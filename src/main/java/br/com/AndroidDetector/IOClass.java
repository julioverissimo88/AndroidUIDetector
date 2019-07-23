package br.com.AndroidDetector;

import br.com.UTIL.ApiReader;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class IOClass {
    private static List<String> listIO = new ArrayList<String>();

    public static List<String> getIOClass() {
        listIO.add("PFASQLiteHelper");
        listIO.add("SQLite");
        listIO.add("File");
        listIO.add("Stream");
        listIO.add("Statement");
        listIO.add("Connection");
        listIO.add("WebView");
        listIO.add("Cursor");
        listIO.add("DatabaseViewerInterface");

        loadJSONAPI();

        return listIO;
    }

    public static void loadJSONAPI() {
        Gson gson = new Gson();
        try {
            File file = new File("./resource/api.json");
            JsonReader reader = new JsonReader(new FileReader(file));
            ApiReader[] data = (ApiReader[]) gson.fromJson(reader, ApiReader[].class);

            for (int i = 0; i < data.length; i++) {
                listIO.addAll(data[i].getClassesInterfacesExceptionsEnum());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
