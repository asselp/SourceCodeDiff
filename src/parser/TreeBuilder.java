package src.parser;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import java.nio.file.Paths;

import java.io.*;

public class TreeBuilder {

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
	public static TreeNode buildTreeFromFile(String filePath) throws IOException {
		TreeNode tree = buildTreeFromSource(getFileContent(filePath));
		return tree;
	}

	public static TreeNode buildTreeFromSource(String source) throws IOException {
		TreeNode root = new TreeNode();
		CompilationUnit cu = getCompilationUnit(source);
		JavaCodeVisitor visitor = new JavaCodeVisitor(root);
		cu.accept(visitor);

		return root;
	}

	public static CompilationUnit getCompilationUnit(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS14);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(source.toCharArray());
		CompilationUnit cu = (CompilationUnit)parser.createAST(null);

		return cu;
	}

}
