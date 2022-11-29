package src.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.io.File;

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

//    private TreeNode getTreeNode(ASTNode node) {
//        TreeNode treeNode = new TreeNode(getLabel(node), node);
//        if(!nodeStack.isEmpty()){
//            nodeStack.peek().addChild(treeNode);
//        }
//        return treeNode;
//    }

//    public String getLabel(ASTNode astNode) {
//        String label = astNode.getClass().getSimpleName();
//        if (astNode instanceof Assignment) {
//            label += "|#|" + ((Assignment)astNode).getOperator().toString();
//        }
//        if (astNode instanceof BooleanLiteral
//                || astNode instanceof Modifier
//                || astNode instanceof SimpleType
//                || astNode instanceof QualifiedType
//                || astNode instanceof PrimitiveType) {
//            label += "|#|" + astNode.toString();
//        }
//        if(astNode instanceof CharacterLiteral)
//            label += "|#|" + ((CharacterLiteral)astNode).getEscapedValue();
//        if(astNode instanceof NumberLiteral)
//            label += "|#|"  + ((NumberLiteral)astNode).getToken();
//        if(astNode instanceof StringLiteral)
//            label += "|#|"  + ((StringLiteral)astNode).getEscapedValue();
//        if(astNode instanceof InfixExpression)
//            label += "|#|"  + ((InfixExpression)astNode).getOperator().toString();
//        if(astNode instanceof PrefixExpression)
//            label += "|#|"  + ((PrefixExpression)astNode).getOperator().toString();
//        if(astNode instanceof PostfixExpression)
//            label += "|#|"  + ((PostfixExpression)astNode).getOperator().toString();
//        if(astNode instanceof SimpleName)
//            label += "|#|"  + ((SimpleName)astNode).getIdentifier();
//        if(astNode instanceof QualifiedName)
//            label += "|#|"  + ((QualifiedName)astNode).getFullyQualifiedName();
//        if(astNode instanceof MethodInvocation)
//            label += "|#|"  + ((MethodInvocation)astNode).getName().toString();
//        if(astNode instanceof VariableDeclarationFragment)
//            label += "|#|"  + ((VariableDeclarationFragment)astNode).getName().toString();
//        return label;
//    }

    public static void main(final String[] args) throws Exception {

        String filePath = "src/Example.java";
        TreeNode treeNode = TreeBuilder.buildTreeFromFile(filePath);


        File srcFile = new File("src/Example.java");
        File dstFile = new File("src/Example1.java");

        List<GTAction> actions = TreeDIff.diffGumTreeWithGrouping(srcFile, dstFile);
        System.out.println(actions);

//        parser.setSource(fileContent);
//        final CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
//        PackageDeclaration imports = compilationUnit.getPackage();
//        System.out.println(imports);
//        compilationUnit.accept(new ASTVisitor() {

//        });

    }
}
