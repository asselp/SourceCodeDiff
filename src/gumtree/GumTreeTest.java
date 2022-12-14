package src.gumtree;

import src.element.GTAction;
import src.parser.TreeDIff;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class GumTreeTest {

    private static String nameFile(int i) {
        return "src/gumtreeESfirst/changeTest" + String.valueOf(i) + ".txt";
    }
    public static void main(String[] args) throws Exception {

        File path = new File("changes");

        File [] dirs = path.listFiles();
        int change = 1;

        for (File dir : dirs) {
            if (dir.isDirectory()) {
                File [] oldNewDir = dir.listFiles();
                // new, old

                File [] newDir = oldNewDir[1].listFiles();

                File src = new File(newDir[0].getPath());

                File [] oldDir = oldNewDir[0].listFiles();

                File dst = new File(oldDir[0].getPath());

                List<GTAction> actions = TreeDIff.diffGumTreeWithGrouping(src, dst);
                FileWriter fileWriter = new FileWriter(nameFile(change));
                fileWriter.write(actions.toString());
                fileWriter.close();
                System.out.print("change passed ");
                System.out.println(change);
                change = change + 1;
            }
        }
    }
}
