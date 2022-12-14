package src.scriptsize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EvaluationScriptSize {
    public static long countLineBufferedReader(String fileName) {

        long lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines ;

    }

    public static void main(String[] args) {

        File path = new File("src/ourES");
        File [] files = path.listFiles();
        for (File gtres : files) {
            long i = countLineBufferedReader("src/ourES/" + gtres.getName());
            System.out.println(i);
        }
    }
}
