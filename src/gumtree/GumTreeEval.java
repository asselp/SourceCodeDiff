package src.gumtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GumTreeEval {
    private static String nameFile(String filename) {
        return "src/testres4/" + filename;
    }

    public static void main(String[] args) {
        Scanner file;
        PrintWriter writer;

        try {
            File path = new File("src/testres2");
            File [] files = path.listFiles();
            for (File gtres : files) {
                file = new Scanner(new File(gtres.getPath()));
                writer = new PrintWriter(nameFile(gtres.getName()));

                while (file.hasNext()) {
                    String line = file.nextLine();
                    if (!line.isEmpty()) {
                        writer.write(line);
                        writer.write("\n");
                    }
                }

                file.close();
                writer.close();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
