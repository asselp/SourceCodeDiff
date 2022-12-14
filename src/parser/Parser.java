package src.parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import src.element.GTAction;
import src.matcher.Matcher;
import src.parser.TreeBuilder;
import src.parser.JavaCodeVisitor;
import src.parser.TreeDIff;
import org.eclipse.jdt.core.dom.*;


public class Parser {

    public static String getFileContent(String filePath) throws FileNotFoundException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.lineSeparator());
            line = bufferedReader.readLine();
        }
        return stringBuilder.toString();
    }

    public static void treeString(TreeNode treeNode, StringBuffer stringBuffer) {
        stringBuffer.append(treeNode.toString());
        stringBuffer.append("\n");
        for(TreeNode c : treeNode.children) {
            treeString(c, stringBuffer);
        }
    }

    private static String nameFile(int i) {
        return "src/results/changeTest" + String.valueOf(i) + ".txt";
    }

    public static void main(final String[] args) throws Exception {

//        Matcher matcher = new Matcher(srcFile, dstFile);
//        StringBuffer editScript = matcher.match();
//        System.out.println(editScript);
//        System.out.println(matcher.sizeEditScript());
        File path = new File("changes");

        File [] dirs = path.listFiles();
        int change = 1;

        for (File dir : dirs) {
            if (dir.isDirectory()) {
                File [] oldNewDir = dir.listFiles();

                File [] newDir = oldNewDir[1].listFiles();

                File src = new File(newDir[0].getPath());

                File [] oldDir = oldNewDir[0].listFiles();

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
