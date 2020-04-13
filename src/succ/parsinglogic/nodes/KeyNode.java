package succ.parsinglogic.nodes;

import falsepattern.Out;
import succ.Utilities;
import succ.datafiles.abstractions.ReadableWritableDataFile;
import succ.parsinglogic.ParsingLogicExtensions;

import static falsepattern.FalseUtil.trimEnd;
import static falsepattern.FalseUtil.trimStart;
import static succ.parsinglogic.ParsingLogicExtensions.addSpaces;

/**
 * Represents a line of text in a SUCC file that contains data addressed by key.
 */
public class KeyNode extends Node {
    public KeyNode(String rawText, ReadableWritableDataFile file) {
        super(rawText, file);
    }

    public KeyNode(int indentation, String key, ReadableWritableDataFile file) {
        super(indentation, file);

        {
            Out<String> whyNot = new Out<>(String.class);
            if (!Utilities.isValidKey(key, whyNot)) {
                throw new IllegalArgumentException(whyNot.value);
            }
        }

        rawText += key + ":";
    }

    public String getKey() {
        String text = getDataText();
        int colonIndex = getColonIndex(text);

        text = text.substring(0, colonIndex);
        text = trimEnd(text);
        return text;
    }

    @Override
    public String getValue() {
        String text = getDataText();
        int colonIndex = getColonIndex(text);
        text = text.substring(colonIndex + 1);
        text = trimStart(text);
        return text;
    }

    @Override
    public void setValue(String value) {
        if (this.unappliedStyle) {
            setDataText(getKey() + addSpaces(":", getStyle().getSpacesAfterColon()) + value);
            this.unappliedStyle = false;
            return;
        }

        String text = getDataText();
        int colonIndex = getColonIndex(text);

        String afterColon = text.substring(colonIndex + 1);
        int spacesAfterColon = ParsingLogicExtensions.getIndentationLevel(afterColon);

        setDataText(text.substring(0, colonIndex + spacesAfterColon + 1) + value);
    }

    private int getColonIndex(String text) {
        int colonIndex = text.indexOf(':');

        if (colonIndex < 0) {
            throw new StringIndexOutOfBoundsException("Key node comprised of the following text: " + rawText + " did not contain the character ':'");
        }
        return colonIndex;
    }
}
