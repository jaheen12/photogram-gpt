package com.photogram.backup;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class LogsActivity extends AppCompatActivity {

    private ListView listLogs;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        listLogs = findViewById(R.id.listLogs);
        db = new DatabaseHelper(this);

        loadLogs();
    }

    private void loadLogs() {
        ArrayList<String> logs = db.getLogs();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, logs);
        listLogs.setAdapter(adapter);
    }
}