package com.festeban26;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;

public class Splitter {

    public static ArrayList<String> split(Path source, int numberOfParagraphsPerFile, Charset sourceCharset) {

        ArrayList<String> ficheros = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(source.toFile())) {
            InputStreamReader isr = new InputStreamReader(fis, sourceCharset);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;
            StringBuilder builder = new StringBuilder();
            int lineCounter = 1;
            int paragraphsCounter = 1;
            boolean wasLastLineBlank = false;
            boolean exit = false;
            while ((line = bufferedReader.readLine()) != null & !exit) {

                if (line.isEmpty() & !wasLastLineBlank) {
                    if (paragraphsCounter % numberOfParagraphsPerFile == 0) {
                        ficheros.add(builder.toString().trim());
                        builder.setLength(0);
                    }
                    paragraphsCounter++;
                }
                wasLastLineBlank = line.isEmpty();
                builder.append(line).append(System.getProperty("line.separator"));
            }
            if (!builder.toString().trim().isEmpty())
                ficheros.add(builder.toString().trim());

            return ficheros;

        } catch (FileNotFoundException e) {
            System.out.println("ERROR! The file: " + source.toString() + " does not exists.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
