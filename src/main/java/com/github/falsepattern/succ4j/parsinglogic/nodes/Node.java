package com.github.falsepattern.succ4j.parsinglogic.nodes;

import com.github.falsepattern.succ4j.parsinglogic.ParsingLogicExtensions;
import com.github.falsepattern.succ4j.style.FileStyle;
import com.github.falsepattern.succ4j.datafiles.abstractions.ReadableWritableDataFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.falsepattern.util.FalseUtil.trimEnd;

/**
 * Represents a line of text in a SUCC file that contains data.
 */
public abstract class Node extends Line {
    public abstract String getValue();
    public abstract void setValue(String value);
    public NodeChildrenType childNodeType = NodeChildrenType.none;

    private final List<Line> childLines = new ArrayList<>();
    private final List<Node> childNodes = new ArrayList<>();
    public synchronized List<Line> getChildLines() {
        return Collections.unmodifiableList(childLines);
    }

    public synchronized List<Node> getChildNodes() {
        return Collections.unmodifiableList(childNodes);
    }

    // This is here so that nodes can access the style of their file. For nodes part of a ReadOnlyDataFile, it is null.
    // We reference a DataFile rather than a FileStyle because if a user changes the Style of the File, that change is automatically seen by all its nodes.
    public final ReadableWritableDataFile file;

    protected synchronized FileStyle getStyle() {
        if (file != null) {
            return file.style;
        } else {
            throw new NullPointerException("Tried to get the style of a node without a file.");
        }
    }

    public Node(String rawText, ReadableWritableDataFile file) {
        super(rawText);
        this.file = file;
    }

    public Node(int indentation, ReadableWritableDataFile file) {
        this.setIndentationLevel(indentation);
        this.file = file;
        this.unAppliedStyle = true;
    }

    protected boolean unAppliedStyle = false;

    public KeyNode getChildAddressedByName(String name) {
        ensureProperChildType(NodeChildrenType.key);
        for (Node node: childNodes) {
            KeyNode keyNode = (KeyNode) node;
            if (keyNode.getKey().equals(name)) {
                return keyNode;
            }
        }

        return createKeyNode(name);
    }

    private KeyNode createKeyNode(String key) {
        KeyNode newNode = new KeyNode(getProperChildIndentation(), key, file);
        addChild(newNode);
        return newNode;
    }

    public ListNode getChildAddressedByListNumber(int number) {
        ensureProperChildType(NodeChildrenType.list);

        int indentation = getProperChildIndentation();
        for (int i = childNodes.size(); i <= number; i++) {
            ListNode newNode = new ListNode(indentation, file);
            addChild(newNode);
        }

        return (ListNode)childNodes.get(number);
    }

    public MultiLineStringNode getChildAddressedByStringLineNumber(int number) {
        ensureProperChildType(NodeChildrenType.multiLineString);

        // ensure proper number of child string nodes exist
        int indentation = getProperChildIndentation();
        for (int i = childNodes.size(); i <= number; i++) {
            MultiLineStringNode newNode = new MultiLineStringNode(indentation, file);
            addChild(newNode);
        }

        return (MultiLineStringNode)childNodes.get(number);
    }

    private int getProperChildIndentation() {
        int indentation;
        if (this.childNodes.size() > 0) {
            indentation = this.childNodes.get(0).getIndentationLevel(); // if we already have a child, match new indentation level to that child
        } else {
            indentation = this.getIndentationLevel() + getStyle().getIndentationInterval(); // otherwise, increase the indentation level in accordance with the FileStyle
        }
        return indentation;
    }

    private void ensureProperChildType(NodeChildrenType expectedType) {
        if (expectedType != NodeChildrenType.multiLineString && !(getValue() == null || getValue().equals(""))) {
            throw new IllegalArgumentException("Node has a value, which means it can't have children: " + getValue());
        }

        if (childNodeType != expectedType) {
            if (childNodes.size() == 0) {
                childNodeType = expectedType;
            } else {
                throw new IllegalArgumentException("Can't get child from this node. Expected type was "
                        + expectedType.name() + ", but node children are of type " + childNodeType.name());
            }
        }
    }

    public boolean containsChildNode(String key) {
        return Arrays.asList(getChildKeys()).contains(key);
    }

    public void clearChildren() {
        clearChildren(null);
    }

    public void clearChildren(NodeChildrenType newChildrenType) {
        childLines.clear();
        childNodes.clear();
        if (newChildrenType != null) {
            childNodeType = newChildrenType;
        }
    }

    public void addChild(Line newLine) {
        childLines.add(newLine);

        try {
            Node newNode = (Node) newLine;
            childNodes.add(newNode);
        } catch (ClassCastException ignored) {}
    }

    public void removeChild(String key) {
        for (Node node: childNodes) {
            try {
                KeyNode keyNode = (KeyNode) node;
                if (keyNode.getKey().equals(key)) {
                    childNodes.remove(node);
                    childLines.remove(node);
                    return;
                }
            } catch (ClassCastException ignored) {}
        }
    }

    public void capChildCount(int count) {
        if (count < 0) {
            throw new IndexOutOfBoundsException("Stop it");
        }

        for (int i = childNodes.size() - 1; i >= count; i--) {
            Node removeThis = childNodes.remove(i);
            childLines.remove(removeThis);
        }
    }

    public String[] getChildKeys() {
        return childNodes.stream().map((childNode) -> ((KeyNode) childNode).getKey()).toArray(String[]::new);
    }

    public String getDataText() {
        if (ParsingLogicExtensions.isWhitespace(rawText)) {
            return "";
        }

        return rawText.substring(getDataStartIndex(), getDataEndIndex()).replace("\\#", "#");
    }

    public void setDataText(String newData) {
        rawText = rawText.substring(0, getDataStartIndex())
                + newData.replace("#", "\\#")
                + rawText.substring(getDataEndIndex());
    }

    private int getDataStartIndex() {
        return getIndentationLevel();
    }

    private int getDataEndIndex() {
        String text = rawText;
        if (ParsingLogicExtensions.isWhitespace(text)) {
            return text.length();
        }

        int poundSignIndex = text.indexOf('#');
        while (poundSignIndex > 0 && text.charAt(poundSignIndex - 1) == '\\') {
            poundSignIndex = text.indexOf('#', poundSignIndex + 1);
        }

        if (poundSignIndex > 0) {
            text = text.substring(0, poundSignIndex);
        }

        text = trimEnd(text);

        return text.length();
    }
}
