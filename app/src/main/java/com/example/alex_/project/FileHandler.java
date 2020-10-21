package com.example.alex_.project;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public abstract class FileHandler {
  public static final String DEFAULT_FILENAME = "AppEntry";

  /**
   * Reads file {@code filename} and returns its contents as a string
   *
   * @param context - the application context to find the file from
   * @param fileName - the name of the file to read
   *
   * @return the contents of the file in the format of a String
   */
  public static String readFile(Context context, String fileName) {
    File dir = context.getFilesDir();
    File file = new File(dir, fileName + ".txt");

    try {
      if (file.exists()) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
          sb.append(line);
        }

        br.close();

        return sb.toString();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

	/**
	 * Writes the given string to a file of file {@code fileName}. If this file doesn't exist,
	 * it creates it.
	 *
	 * @param context - the application context
	 * @param fileName - the name of the file to write to
	 * @param toWrite - the string to write to the file
	 */
  public static void writeToFile(Context context, String fileName, String toWrite) {
    try {
      OutputStreamWriter os =
          new OutputStreamWriter(context.openFileOutput(fileName + ".txt", Context.MODE_PRIVATE));
      os.write(toWrite);
      os.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Used to check the contents of a file are equal to the given string.
   *
   * @param context - the application context
   * @param string - the string to compare the contents of the file to
   * @param fileName - the name of the file to read
   * @return - true if string is the same as the contents of the file
   */
  public static boolean checkContents(Context context, String string, String fileName) {
    return string.equals(readFile(context, fileName));
  }
}
