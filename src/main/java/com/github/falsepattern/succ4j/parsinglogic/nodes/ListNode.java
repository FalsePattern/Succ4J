package com.github.falsepattern.succ4j.parsinglogic.nodes;

import com.github.falsepattern.succ4j.datafiles.abstractions.ReadableWritableDataFile;
import com.github.falsepattern.succ4j.parsinglogic.ParsingLogicExtensions;

import static com.github.falsepattern.util.FalseUtil.trimStart;
import static com.github.falsepattern.succ4j.parsinglogic.ParsingLogicExtensions.addSpaces;

/**
 * Represents a line of text in a SUCC file that contains data in a list.
 */
public class ListNode extends Node {
    public ListNode(String rawText, ReadableWritableDataFile file) {
        super(rawText, file);
    }

    public ListNode(int indentation, ReadableWritableDataFile file) {
        super(indentation, file);
        rawText += "-";
    }

    @Override
    public String getValue() {
        String text = getDataText();
        int dashIndex = getDashIndex(text);

        text = text.substring(dashIndex + 1);
        text = trimStart(text);
        return text;
        // note that trailing spaces are already trimmed in GetDataText()
    }

    @Override
    public void setValue(String value) {
        if (this.unAppliedStyle) {
            setDataText(addSpaces("-", getStyle().getSpacesAfterDash()) + value);
            this.unAppliedStyle = false;
            return;
        }

        String text = getDataText();
        int dashIndex = getDashIndex(text);

        String afterDash = text.substring(dashIndex + 1);
        int spacesAfterDash = ParsingLogicExtensions.getIndentationLevel(afterDash);

        setDataText(text.substring(0, dashIndex + spacesAfterDash + 1) + value);
    }

    private int getDashIndex(String text) {
        int dashIndex = text.indexOf('-');
        if (dashIndex < 0) {
            throw new StringIndexOutOfBoundsException("List node comprised of the following text: " + rawText + " did not contain the character ':'");
        }

        return dashIndex;
    }
}
