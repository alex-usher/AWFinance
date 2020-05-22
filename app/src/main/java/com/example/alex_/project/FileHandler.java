package com.example.alex_.project;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;

public class FileHandler {

    public static String readFile(Context context, String fileName){
        try {
            File dir = context.getFilesDir();
            File file = new File(dir, fileName+".txt");
            if(checkFileExists(file)){
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while((line = br.readLine()) != null){
                    sb.append(line);
                }

                br.close();

                return sb.toString();
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static boolean checkFileExists(File file){
        if(file.exists()){
            return true;
        } else {
            return false;
        }
    }

    public static void writeToFile(Context context, String fileName, String toWrite){
        try {
            OutputStreamWriter os = new OutputStreamWriter(context.openFileOutput(fileName+".txt", Context.MODE_PRIVATE));
            os.write(toWrite);
            os.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
