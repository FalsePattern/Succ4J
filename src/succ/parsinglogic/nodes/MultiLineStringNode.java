package succ.parsinglogic.nodes;

import succ.datafiles.abstractions.ReadableWritableDataFile;

/**
 * Represents a line of text in a SUCC file that contains part of a multi-line string.
 */
public class MultiLineStringNode extends Node {
    public MultiLineStringNode(String rawText, ReadableWritableDataFile file) {
        super(rawText, file);
    }

    public MultiLineStringNode(int indentation, ReadableWritableDataFile file) {
        super(indentation, file);
        this.unappliedStyle = false; // currently, no styles apply to MultiLineStringNodes
    }

    @Override
    public String getValue() {
        return getDataText();
    }

    @Override
    public void setValue(String value) {
        setDataText(value);
    }

    public static final String terminator = "\"\"\"";

    public boolean isTerminator() {
        return getValue().equals(terminator);
    }

    public void makeTerminator() {
        setValue(terminator);
    }

    //private void NO() {
    //  throw new RuntimeException("You can't do that on a multi-line string node!");
    //}
}
