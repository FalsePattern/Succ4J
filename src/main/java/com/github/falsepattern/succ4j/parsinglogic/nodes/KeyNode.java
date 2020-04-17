package com.github.falsepattern.succ4j.parsinglogic.nodes;

import com.github.falsepattern.util.Out;
import com.github.falsepattern.succ4j.Utilities;
import com.github.falsepattern.succ4j.datafiles.abstractions.ReadableWritableDataFile;
import com.github.falsepattern.succ4j.parsinglogic.ParsingLogicExtensions;

import static com.github.falsepattern.util.FalseUtil.trimEnd;
import static com.github.falsepattern.util.FalseUtil.trimStart;
import static com.github.falsepattern.succ4j.parsinglogic.ParsingLogicExtensions.addSpaces;

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
        if (this.unAppliedStyle) {
            setDataText(getKey() + addSpaces(":", getStyle().getSpacesAfterColon()) + value);
            this.unAppliedStyle = false;
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
