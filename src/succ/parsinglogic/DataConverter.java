package succ.parsinglogic;

import falsepattern.FalseUtil;
import falsepattern.Pair;
import succ.Utilities;
import succ.datafiles.DataFile;
import succ.datafiles.abstractions.ReadableDataFile;
import succ.parsinglogic.nodes.*;

import java.util.*;

import static succ.parsinglogic.ParsingLogicExtensions.getIndentationLevel;
import static succ.parsinglogic.ParsingLogicExtensions.splitIntoLines;

public class DataConverter {

    /**
     * Turns a data structure into raw SUCC
     */
    public static String succFromDataStructure(List<Line> lines) {
        StringBuilder succBuilder = new StringBuilder();
        recursivelyBuildLines(lines, succBuilder);
        return FalseUtil.trimEnd(succBuilder.toString());
    }

    private static void recursivelyBuildLines(List<Line> lines, StringBuilder builder) {
        for (Line line : lines) {
            builder.append(line.rawText);
            builder.append(Utilities.getNewLine());

            if (line instanceof Node) {
                Node node = (Node) line;
                recursivelyBuildLines(node.getChildLines(), builder);
            }
        }
    }

    /**
     * Parses a string of SUCC into a data structure.
     */
    public static Pair<List<Line>, Map<String, KeyNode>> dataStructureFromSUCC(String input, ReadableDataFile fileRef) {
        return dataStructureFromSUCC(splitIntoLines(input), fileRef);
    }

    /**
     * Parses lines of SUCC into a data structure.
     */
    public static Pair<List<Line>, Map<String, KeyNode>> dataStructureFromSUCC(String[] lines, ReadableDataFile fileRef) {
        // If the file is empty
        // Do this because otherwise new files are created with a newline at the top
        if (lines.length == 1 && lines[0].equals("")) {
            return new Pair<>(new ArrayList<>(), new HashMap<>());
        }

        List<Line> topLevelLines = new ArrayList<>();
        Map<String, KeyNode> topLevelNodes = new HashMap<>();

        Stack<Node> nestingNodeStack = new Stack<>(); // The top of the stack is the node that new nodes should be children of
        boolean doingMuliLineString = false;

        DataFile file = null; // This will be null if fileRef is a ReadOnlyDataFile
        try {
            file = (DataFile) fileRef;
        } catch (ClassCastException ignored) {}

        // Parse the input line by line
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.contains("\t")) {
                throw new IllegalArgumentException("A SUCC file cannot contain tabs. Please use spaces instead.");
            }

            if (doingMuliLineString) {
                if (nestingNodeStack.peek().childNodeType != NodeChildrenType.multiLineString) {
                    throw new RuntimeException("We were supposed to be doing a multi-line string but the top of the node stack isn't a multi-line string node!");
                }

                MultiLineStringNode newNode = new MultiLineStringNode(line, file);

                nestingNodeStack.peek().addChild(newNode);

                if (newNode.isTerminator()) {
                    doingMuliLineString = false;
                    nestingNodeStack.pop();
                }

                continue;
            }

            if (lineHasData(line)) {
                Node node = getNodeFromLine(line, file);


                while (true) {
                    if (nestingNodeStack.size() == 0) { // If this is a top-level node
                        if (!(node instanceof KeyNode)) {
                            throw new IllegalArgumentException("Top level lines must be key nods. Line " + i + " does not conform to this: '" + line + "'");
                        }
                        topLevelLines.add(node);
                        KeyNode heck = (KeyNode) node;
                        topLevelNodes.put(heck.getKey(), heck);
                    } else { // If this is NOT a top-level node
                        int stackTopIndentation = nestingNodeStack.peek().getIndentationLevel();
                        int lineIndentation = getIndentationLevel(line);

                        if (lineIndentation > stackTopIndentation) { // If this should be a child of the stack top
                            Node newParent = nestingNodeStack.peek();
                            if (newParent.getChildNodes().size() == 0) {// If this is the first child of the parent, assign the parent's child type
                                if (node instanceof KeyNode) {
                                    newParent.childNodeType = NodeChildrenType.key;
                                } else if (node instanceof ListNode) {
                                    newParent.childNodeType = NodeChildrenType.list;
                                } else {
                                    throw new IllegalStateException("What the fuck?");
                                }
                            } else { // If the parent already has children, check for errors with this line
                                checkNewSiblingForErrors(node, newParent);
                            }

                            newParent.addChild(node);
                        } else { // If this should NOT be a child of the stack top
                            nestingNodeStack.pop();
                            continue;
                        }
                    }
                    break;
                }

                if (node.getValue().equals("")) { // If this is a node with children
                    nestingNodeStack.push(node);
                } else if (node.getValue().equals(MultiLineStringNode.terminator)) { // if this is the start of a multi line string
                    nestingNodeStack.push(node);
                    node.childNodeType = NodeChildrenType.multiLineString;
                    doingMuliLineString = true;
                }
            } else { // Line has no data
                Line noDataLine = new Line(line);

                if (nestingNodeStack.size() == 0) {
                    topLevelLines.add(noDataLine);
                } else {
                    nestingNodeStack.peek().addChild(noDataLine);
                }
            }
        }

        return new Pair<>(topLevelLines, topLevelNodes);
    }

    private static boolean lineHasData(String line) {
        line = line.trim();
        return line.length() != 0 && line.charAt(0) != '#';
    }

    private static Node getNodeFromLine(String line, DataFile file) {
        DataLineType dataType = getDataLineType(line);
        Node node;
        switch (dataType) {
            case key:
                node = new KeyNode(line, file);
                break;
            case list:
                node = new ListNode(line, file);
                break;

            default:
                throw new IllegalArgumentException("Format error on line: " + line);
        }

        return node;
    }

    private static void checkNewSiblingForErrors(Node child, Node newParent) {
        Node sibling = newParent.getChildNodes().get(0);
        if (child.getIndentationLevel() != sibling.getIndentationLevel()) { // if there is a mismatch between the new node's indentation and its sibling's
            throw new IllegalArgumentException("Line did not have the same indentation as its assumed sibling. Line was '" + child.rawText + "'; sibling was '" + sibling.rawText + "'");
        }

        if ( // if there is a mismatch between the new node's type and its sibling's
                newParent.childNodeType == NodeChildrenType.key  && !(child instanceof KeyNode)
             || newParent.childNodeType == NodeChildrenType.list && !(child instanceof ListNode)
             || newParent.childNodeType == NodeChildrenType.multiLineString
             || newParent.childNodeType == NodeChildrenType.none
        ) {
            throw new IllegalArgumentException("Line did not match the child type of it's parent. Line was '" + child.rawText + "'; parent was '" + newParent.rawText + "'");
        }
    }

    private enum DataLineType {
        none, key, list
    }

    private static DataLineType getDataLineType(String line) {
        String trimmed = line.trim();
        if (trimmed.length() == 0) return DataLineType.none;
        if (trimmed.charAt(0) == '#') return DataLineType.none;
        if (trimmed.charAt(0) == '-') return DataLineType.list;
        if (trimmed.contains(":")) return DataLineType.key;

        return DataLineType.none;
    }
}
