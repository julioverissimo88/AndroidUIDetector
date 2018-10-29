package AndroidDetector;

import java.util.ArrayList;
import java.util.List;

public class IOClass {
    private static  List<String> listIO = new ArrayList<String>();

    public static List<String> getIOClass(){
        listIO.add("PFASQLiteHelper");
        listIO.add("SQLite");
        listIO.add("File");
        listIO.add("Stream");
        listIO.add("Statement");
        listIO.add("Connection");

        return listIO;
    }
}
