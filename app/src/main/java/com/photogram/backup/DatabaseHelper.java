package com.photogram.backup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "photogram_v7.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("""
            CREATE TABLE history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                file_key TEXT UNIQUE,
                file_path TEXT,
                last_modified LONG,
                uploaded_at LONG,
                device_id TEXT
            )
        """);
        db.execSQL("CREATE INDEX idx_file_key ON history(file_key)");

        db.execSQL("""
            CREATE TABLE logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp LONG,
                type TEXT,
                message TEXT
            )
        """);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS history");
        db.execSQL("DROP TABLE IF EXISTS logs");
        onCreate(db);
    }

    /* =================== FILE KEY =================== */
    public static String makeFileKey(String path, long modified) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update((path + ":" + modified).getBytes());
            byte[] b = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            return path + "_" + modified;
        }
    }

    public boolean isUploaded(String fileKey) {
        try (Cursor c = getReadableDatabase().query(
                "history", new String[]{"id"}, "file_key=?", new String[]{fileKey}, null, null, null)) {
            return c != null && c.moveToFirst();
        }
    }

    public void markUploaded(String fileKey, String path, long modified, String deviceId) {
        ContentValues v = new ContentValues();
        v.put("file_key", fileKey);
        v.put("file_path", path);
        v.put("last_modified", modified);
        v.put("uploaded_at", System.currentTimeMillis());
        v.put("device_id", deviceId);
        getWritableDatabase().insertWithOnConflict("history", null, v, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /* =================== LOGGING =================== */
    public void log(String type, String msg) {
        try {
            ContentValues v = new ContentValues();
            v.put("timestamp", System.currentTimeMillis());
            v.put("type", type);
            v.put("message", msg);
            getWritableDatabase().insert("logs", null, v);
            getWritableDatabase().execSQL("DELETE FROM logs WHERE id NOT IN (SELECT id FROM logs ORDER BY timestamp DESC LIMIT 50)");
        } catch (Exception ignored) {}
    }

    public ArrayList<String> getLogs() {
        ArrayList<String> out = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("logs", null, null, null, null, null, "timestamp DESC")) {
            while (c.moveToNext()) {
                out.add("[" + c.getString(2) + "] " + c.getString(3));
            }
        }
        return out;
    }
}
