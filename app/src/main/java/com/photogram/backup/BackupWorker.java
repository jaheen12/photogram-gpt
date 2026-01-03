package com.photogram.backup;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;

public class BackupWorker extends Worker {

    private DatabaseHelper db;

    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        db = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        db.log("INFO", "BackupWorker started");

        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (picturesDir.exists() && picturesDir.isDirectory()) {
            scanFolder(picturesDir);
        }

        db.log("INFO", "BackupWorker finished");
        return Result.success();
    }

    private void scanFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                scanFolder(f);
            } else {
                String key = DatabaseHelper.makeFileKey(f.getAbsolutePath(), f.lastModified());
                if (!db.isUploaded(key)) {
                    db.markUploaded(key, f.getAbsolutePath(), f.lastModified(), "device_1");
                    db.log("UPLOAD", "Marked uploaded: " + f.getName());
                }
            }
        }
    }
}