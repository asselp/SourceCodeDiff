package src.gumtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GumTreeEval {
    /*
    just to delete empty lines to make it easy to read
     */
    private static String nameFile(String filename) {
        return "src/gumtreeES/" + filename;
    }

    public static void main(String[] args) {
        Scanner file;
        PrintWriter writer;

        try {
            File path = new File("src/gumtreeESfirst");
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
