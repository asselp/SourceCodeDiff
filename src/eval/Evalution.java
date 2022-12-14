package src.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Evalution {
    public static long countLineBufferedReader(String fileName) {

        long lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines - 1;

    }

    public static void main(String[] args) {

        File path = new File("src/results");
        File [] files = path.listFiles();
        for (File gtres : files) {
            long i = countLineBufferedReader("src/results/" + gtres.getName());
            if (i == -1) {
                System.out.println(0);
            }
            else {
                System.out.println(i);
            }
        }
    }
}
