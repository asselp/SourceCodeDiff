package src.parser;

import java.io.*;

import src.matcher.Matcher;


public class Parser {

    private static String nameFile(int i) {
        return "src/ourES/changeTest" + String.valueOf(i) + ".txt";
    }

    public static void main(final String[] args) throws Exception {

        File path = new File("changes");

        File[] dirs = path.listFiles();
        int change = 1;

        for (File dir : dirs) {
            if (dir.isDirectory()) {
                File[] oldNewDir = dir.listFiles();

                File[] newDir = oldNewDir[1].listFiles();

                File src = new File(newDir[0].getPath());

                File[] oldDir = oldNewDir[0].listFiles();

                File dst = new File(oldDir[0].getPath());

                Matcher matcher = new Matcher(src, dst);
                StringBuffer editScript = matcher.match();
                FileWriter fileWriter = new FileWriter(nameFile(change));
                fileWriter.write(editScript.toString());
                fileWriter.close();
                System.out.print("change passed ");
                System.out.println(change);
                change = change + 1;
            }
        }
    }
}
