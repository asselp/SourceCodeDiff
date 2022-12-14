package src.matcher;

import com.github.gumtreediff.actions.model.Action;
import src.element.GTAction;
import src.parser.TreeBuilder;
import src.parser.TreeNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import static java.util.Arrays.binarySearch;

public class Matcher {
    private final File srcFile;
    private final File dstFile;

    public StringBuffer editScript;

    public int editScriptSize;

    public Matcher(File srcFile, File dstFile) {
        this.srcFile = srcFile;
        this.dstFile = dstFile;
    }

    public TreeNode getTree(File file) throws IOException {
        return TreeBuilder.buildTreeFromFile(file.getPath());
    }


    public void treeString(TreeNode treeNode, StringBuffer stringBuffer) {
        stringBuffer.append(treeNode.toString());
        stringBuffer.append("\n");
        for (TreeNode c : treeNode.children) {
            treeString(c, stringBuffer);
        }
    }


    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public StringBuffer match() throws IOException {
        TreeNode srcTree = getTree(this.srcFile);
        TreeNode dstTree = getTree(this.dstFile);

        StringBuffer stringBuffer = new StringBuffer();
        treeString(srcTree, stringBuffer);

        StringBuffer stringBuffer1 = new StringBuffer();
        treeString(dstTree, stringBuffer1);


        String[] srcFileArray = stringBuffer.toString().split("\n");

        String[] dstFileArray = stringBuffer1.toString().split("\n");

        Map<Integer, List<String>> srcFileMap = new HashMap<Integer, List<String>>();
        Map<Integer, List<String>> dstFileMap = new HashMap<Integer, List<String>>();

//        Pattern keyPattern = Pattern.compile("\\(([^\\)]+)\\)");
        Pattern keyPattern = Pattern.compile("^.*?\\([^\\d]*(\\d+)[^\\d]*\\).*$");
        Pattern valuePattern = Pattern.compile("^[^\\(]+");

        for (String srcFileElement : srcFileArray) {
            java.util.regex.Matcher keyMatcher = keyPattern.matcher(srcFileElement);
            java.util.regex.Matcher valueMatcher = valuePattern.matcher(srcFileElement);

            if (keyMatcher.find() && valueMatcher.find()) {
                int newKey = Integer.parseInt(keyMatcher.group(1));
                String newValue = valueMatcher.group(0);
                if (!srcFileMap.containsKey(newKey)) {
                    ArrayList<String> listValues = new ArrayList<>();
                    listValues.add(newValue);
                    srcFileMap.put(newKey, listValues);
                } else {
                    srcFileMap.get(newKey).add(newValue);
                }
            }
        }

        for (String dstFileElement : dstFileArray) {
            java.util.regex.Matcher keyMatcher = keyPattern.matcher(dstFileElement);
            java.util.regex.Matcher valueMatcher = valuePattern.matcher(dstFileElement);

            if (keyMatcher.find() && valueMatcher.find()) {
                int newKey = Integer.parseInt(keyMatcher.group(1));
                String newValue = valueMatcher.group(0);
                if (!dstFileMap.containsKey(newKey)) {
                    ArrayList<String> listValues = new ArrayList<>();
                    listValues.add(newValue);
                    dstFileMap.put(newKey, listValues);
                } else {
                    dstFileMap.get(newKey).add(newValue);
                }
            }
        }

        List<Map<Integer, List<String>>> unchanged = new ArrayList<>();

        List<Map<Integer, List<String>>> potentialInserts = new ArrayList<>();
        List<Map<Integer, List<String>>> potentialDeletes = new ArrayList<>();
        List<Map<Integer, List<String>>> potentialMoves = new ArrayList<>();
        List<Map<Integer, List<String>>> potentialUpdates = new ArrayList<>();


        List<Map<Integer, List<String>>> unchanged2 = new ArrayList<>();

        for (int key : srcFileMap.keySet()) {
            List<String> currentValue = srcFileMap.get(key);
            if (dstFileMap.containsValue(currentValue)) {
                int keyOfValueDst = getKey(dstFileMap, currentValue);
                if (Math.abs(key - keyOfValueDst) <= 3) {
                    Map<Integer, List<String>> potentialUnchanged = new HashMap<>();
                    potentialUnchanged.put(key, currentValue);
                    unchanged.add(potentialUnchanged);
                }
            } else {
                if (dstFileMap.containsValue(currentValue)) {
                    Map<Integer, List<String>> potentialMove = new HashMap<>();
                    potentialMove.put(key, currentValue);
                    potentialMoves.add(potentialMove);
                } else {
                    for (Map<Integer, List<String>> updates : potentialUpdates) {
                        if (!updates.containsKey(key)) {
                            Map<Integer, List<String>> potentialDelete = new HashMap<>();
                            potentialDelete.put(key, currentValue);
                            potentialDeletes.add(potentialDelete);
                        }
                    }
                }
            }
        }
        for (int key : dstFileMap.keySet()) {
            List<String> currentValue = dstFileMap.get(key);
            if (srcFileMap.containsValue(currentValue)) {
                int keySrc = getKey(srcFileMap, currentValue);
                if (key == keySrc) {
                    Map<Integer, List<String>> potentialUnchanged = new HashMap<>();
                    potentialUnchanged.put(key, currentValue);
                    unchanged.add(potentialUnchanged);
                }

            } else {
                Map<Integer, List<String>> potentialInsert = new HashMap<>();
                potentialInsert.put(key, currentValue);
                potentialInserts.add(potentialInsert);
            }
        }

        for (int key : dstFileMap.keySet()) {
            List<String> currentValue = dstFileMap.get(key);
            if (srcFileMap.containsValue(currentValue)) {
                int keyOfValueSrc = getKey(srcFileMap, currentValue);
                if (Math.abs(key - keyOfValueSrc) <= 3) {
                    Map<Integer, List<String>> potentialUnchanged = new HashMap<>();
                    potentialUnchanged.put(key, currentValue);
                    unchanged2.add(potentialUnchanged);
                }
            } else if (!srcFileMap.containsValue(currentValue) && srcFileMap.containsKey(key)) {
                int currentValueLength = currentValue.size();
                List<String> valueAtKeySrc = srcFileMap.get(key);
                int valueAtKeySrcLength = valueAtKeySrc.size();
                if (currentValueLength == valueAtKeySrcLength) {
                    List<String> differences = new ArrayList<>(currentValue);
                    differences.removeAll(valueAtKeySrc);
                    if (differences.size() == 1) {
                        Map<Integer, List<String>> potentialUpdate = new HashMap<>();
                        potentialUpdate.put(key, differences);
                        potentialUpdates.add(potentialUpdate);
                    }
                }
            } else {
                Map<Integer, List<String>> potentialInsert = new HashMap<>();
                potentialInsert.put(key, currentValue);
                potentialInserts.add(potentialInsert);

            }
        }

//        for (int key : dstFileMap.keySet()) {
//            List<String> currentValue = dstFileMap.get(key);
//            if (srcFileMap.containsValue(currentValue)) {
//                int keyOfValueSrc = getKey(srcFileMap, currentValue);
//                if (Math.abs(key - keyOfValueSrc) <= 3) {
//                    Map<Integer, List<String>> potentialUnchanged = new HashMap<>();
//                    potentialUnchanged.put(key, currentValue);
//                    unchanged2.add(potentialUnchanged);
//                }
//            } else if (!srcFileMap.containsValue(currentValue) && srcFileMap.containsKey(key)) {
//                int currentValueLength = currentValue.size();
//                List<String> valueAtKeySrc = srcFileMap.get(key);
//                int valueAtKeySrcLength = valueAtKeySrc.size();
//                if (currentValueLength == valueAtKeySrcLength) {
//                    List<String> differences = new ArrayList<>(currentValue);
//                    differences.removeAll(valueAtKeySrc);
//                    if (differences.size() == 1) {
//                        Map<Integer, List<String>> potentialUpdate = new HashMap<>();
//                        potentialUpdate.put(key, differences);
//                        potentialUpdates.add(potentialUpdate);
//                    }
//                }
//            } else {
//                Map<Integer, List<String>> potentialInsert = new HashMap<>();
//                potentialInsert.put(key, currentValue);
//                potentialInserts.add(potentialInsert);
//
//            }
//        }


//        for (int key : srcFileMap.keySet()) {
//            List<String> currentValue = srcFileMap.get(key);
//            if (dstFileMap.containsValue(currentValue)) {
//                int keyDst = getKey(dstFileMap, currentValue);
//                if (key == keyDst) {
//                    Map<Integer, List<String>> potentialUnchanged = new HashMap<>();
//                    potentialUnchanged.put(key, currentValue);
//                    unchanged2.add(potentialUnchanged);
//                }
////                else {
////
////                    if (Math.abs(key - keyDst) <= 3) {
////                        Map<Integer, List<String>> potentialMove = new HashMap<>();
////                        potentialMove.put(key, currentValue);
////                        potentialMoves.add(potentialMove);
////                    }
////                }
//            } else {
//                Map<Integer, List<String>> potentialDelete = new HashMap<>();
//                potentialDelete.put(key, currentValue);
//                potentialDeletes.add(potentialDelete);
//            }
//        }

        System.out.println();
        StringBuffer stringBuffer2 = new StringBuffer();

        for (
                Map<Integer, List<String>> map : potentialUpdates) {
            for (List<String> list : map.values()) {
                stringBuffer2.append("Update: " + list.get(0));
                stringBuffer2.append("\n");
            }
        }
        for (
                Map<Integer, List<String>> map : potentialInserts) {
            for (List<String> list : map.values()) {
                stringBuffer2.append("Insert: " + list.get(0));
                stringBuffer2.append("\n");
            }
        }
        for (
                Map<Integer, List<String>> map : potentialMoves) {
            for (List<String> list : map.values()) {
                stringBuffer2.append("Move: " + list.get(0));
                stringBuffer2.append("\n");
            }
        }

        for (
                Map<Integer, List<String>> map : potentialDeletes) {
            for (List<String> list : map.values()) {
                stringBuffer2.append("Delete: " + list.get(0));
                stringBuffer2.append("\n");
            }
        }

//        stringBuffer2.append(potentialDeletes.toString());
//        System.out.println(stringBuffer2);
        this.editScript = stringBuffer2;
//        System.out.println(editScript);
//        System.out.println("Unchanged: " + unchanged.size());
//        System.out.println("Potential moves: " + potentialMoves.size());
//        System.out.println("Potential deletes: " + potentialDeletes.size());
//        System.out.println("Potential updates: " + potentialUpdates.size());
//        System.out.println("Potential inserts: " + potentialInserts.size());
        this.editScriptSize = potentialMoves.size() + potentialDeletes.size() + potentialInserts.size() + potentialUpdates.size();
        return editScript;
    }

    public int sizeEditScript() {
        return editScriptSize;
    }
}
