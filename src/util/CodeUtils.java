package src.util;

import src.element.Hunk;
import src.element.Line;
import src.element.NormalizedHunk;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.*;

public class CodeUtils {

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
	public static CompilationUnit getCompilationUnit(String unitName, String[] classPath, String[] sourcePath, String source){
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setEnvironment(classPath, sourcePath, null, true);
		parser.setUnitName(unitName);
		parser.setResolveBindings(true);
		parser.setSource(source.toCharArray());
		CompilationUnit cu = (CompilationUnit)parser.createAST(null);

		return cu;
	}

	public static CompilationUnit getCompilationUnit(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);
		parser.setCompilerOptions(options);
		parser.setSource(source.toCharArray());
		CompilationUnit cu = (CompilationUnit)parser.createAST(null);

		return cu;
	}

	public static String getTypeName(int type){
		return type == -1 ? "root" : ASTNode.nodeClassForType(type).getSimpleName();
	}

}
