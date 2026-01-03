package com.photogram.backup;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvLogs, tvStats;
    private Button btnRunBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        tvLogs = findViewById(R.id.tvLogs);
        tvStats = findViewById(R.id.tvStats);
        btnRunBackup = findViewById(R.id.btnRunBackup);

        btnRunBackup.setOnClickListener(v -> {
            dbHelper.log("USER", "Backup started manually");

            // Schedule BackupWorker
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(BackupWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
        });

        refreshStats();
        showLogs();
    }

    private void refreshStats() {
        int total = dbHelper.getTotalBackupCount();
        tvStats.setText(total + " files marked as uploaded");
    }

    private void showLogs() {
        ArrayList<String> logs = dbHelper.getLogs();
        StringBuilder sb = new StringBuilder();
        for (String s : logs) sb.append(s).append("\n");
        tvLogs.setText(sb.toString());
    }
}