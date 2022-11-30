package src.parser;

import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import src.element.GTAction;
import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.gen.Generators;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import src.util.CodeUtils;

public class TreeDIff {

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

    public static List<GTAction> diffGumTreeWithGrouping(File srcFile, File dstFile) throws Exception {
        List<GTAction> gtActions = new ArrayList<>();
        com.github.gumtreediff.client.Run.initGenerators();
        ITree src = Generators.getInstance().getTree(srcFile.getAbsolutePath()).getRoot();
        ITree dst = Generators.getInstance().getTree(dstFile.getAbsolutePath()).getRoot();
        Matcher m = Matchers.getInstance().getMatcher(src, dst); // retrieve the default matcher
        m.match();
        ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
        g.generate();
        List<Action> actions = g.getActions();
        CompilationUnit srcCu = CodeUtils.getCompilationUnit(getFileContent(srcFile.getAbsolutePath()));
        CompilationUnit dstCu = CodeUtils.getCompilationUnit(getFileContent(dstFile.getAbsolutePath()));
        //Group actions.
        while(actions.size() > 0){
            Action action = actions.get(0);
            GTAction gtAction = new GTAction(action, srcCu, dstCu);
            gtAction = attachActions(gtAction, actions, srcCu, dstCu);
            gtActions.add(gtAction);
        }
        return gtActions;
    }

    private static GTAction attachActions(
            GTAction gtAction, List<Action> actions, CompilationUnit srcCu, CompilationUnit dstCu) {
        GTAction root = gtAction;

        //Bottom-up search to find a root action.
        GTAction parent;
        ITree parentNode;
        do {
            parent = null;
            parentNode = root.action.getNode().getParent();
            if (parentNode != null) {
                for (Action action : actions) {
                    if (action.getNode().getId() == parentNode.getId()
                            && GTAction.getActionType(action).equals(root.actionType)) {
                        parent = new GTAction(action, srcCu, dstCu);
                        break;
                    }
                }
            }
            //Switch the root.
            if(parent != null){
                root = parent;
            }
        } while (parent != null);

        //Top-down search for children.
        List<GTAction> targetActions = new ArrayList<>();
        List<GTAction> attachedActions = new ArrayList<>();
        targetActions.add(root);
        actions.remove(root.action);
        do {
            targetActions.addAll(attachedActions);
            attachedActions.clear();
            //Find children of each target.
            for(GTAction target : targetActions){
                for (ITree child : target.action.getNode().getChildren()) {
                    for (Action action : actions) {
                        if (action.getNode().getId() == child.getId()
                                && target.actionType.equals(GTAction.getActionType(action))) {
                            GTAction gta = new GTAction(action, srcCu, dstCu);
                            target.children.add(gta);
                            attachedActions.add(gta);
                            break;
                        }
                    }
                }
            }
            //Remove all attached actions.
            for(GTAction gta : attachedActions){
                actions.remove(gta.action);
            }
        } while (attachedActions.size() > 0 && actions.size() > 0);

        return root;
    }
}
