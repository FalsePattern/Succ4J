package com.github.falsepattern.succ4j.parsinglogic.nodes;

import com.github.falsepattern.succ4j.parsinglogic.ParsingLogicExtensions;

/**
 * Represents a single line of text in a SUCC file.
 */
public class Line {
    public Line() {
        this.rawText = "";
    }
    public Line(String rawText) {
        this.rawText = rawText;
    }

    public String rawText;
    public int getIndentationLevel() {
        return ParsingLogicExtensions.getIndentationLevel(rawText);
    }

    public void setIndentationLevel(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Node indents must be at least 0. You tried to set it to " + value);
        }

        int indent = getIndentationLevel();
        if (value == indent) {
            return;
        }

        int diff = value - indent;
        if (diff > 0) {
            rawText = ParsingLogicExtensions.addIndent(rawText, diff);
        } else {
            rawText = rawText.substring(-diff);
        }
    }
}
