package com.photogram.backup;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TelegramHelper {

    private final File registryFile;

    public TelegramHelper(Context ctx) {
        File dir = ctx.getFilesDir();
        registryFile = new File(dir, "topic_registry.json");
        if (!registryFile.exists()) {
            try (FileWriter fw = new FileWriter(registryFile)) {
                fw.write("{}");
            } catch (Exception ignored) {}
        }
    }

    /* ====== Topic Registry ====== */
    public Map<String, String> getTopicRegistry() {
        Map<String, String> map = new HashMap<>();
        try (FileReader fr = new FileReader(registryFile)) {
            char[] buf = new char[(int) registryFile.length()];
            fr.read(buf);
            JSONObject obj = new JSONObject(new String(buf));
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String k = keys.next();
                map.put(k, obj.getString(k));
            }
        } catch (Exception ignored) {}
        return map;
    }

    public void saveTopicRegistry(Map<String, String> map) {
        try (FileWriter fw = new FileWriter(registryFile)) {
            JSONObject obj = new JSONObject();
            for (Map.Entry<String, String> e : map.entrySet()) {
                obj.put(e.getKey(), e.getValue());
            }
            fw.write(obj.toString());
        } catch (Exception ignored) {}
    }
}
