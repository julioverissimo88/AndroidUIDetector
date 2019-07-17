package UTIL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLOC {
    public static void main(String[] args) throws IOException, InterruptedException{
        String command = "cloc /Users/rafaeldurelli/Desktop/Repositorio01/Android-PreferencesManager";
        Process proc = Runtime.getRuntime().exec(command);

        // Read the output
        BufferedReader reader =  new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = "";
        while((line = reader.readLine()) != null) {
            System.out.print(line + "\n");
            if (line.contains("SUM:")){
                String[] array = line.split(" ");
                System.out.print("Tamanho do projeto é " + array[array.length - 1] + "\n");
            }
        }

        proc.waitFor();
    }
}
