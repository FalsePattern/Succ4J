package succ.parsinglogic;

import succ.Utilities;
import succ.parsinglogic.nodes.MultiLineStringNode;
import succ.parsinglogic.nodes.Node;
import succ.parsinglogic.types.BaseTypes;
import succ.parsinglogic.types.CollectionTypes;
import succ.parsinglogic.types.ComplexTypes;
import succ.style.FileStyle;

import static succ.parsinglogic.ParsingLogicExtensions.containsNewLine;

/**
 * Gets and sets the data encoded by Nodes.
 */
public class NodeManager {
    public static <T> void setNodeData(Node node, T data, FileStyle style) {
        setNodeData(node, data, data.getClass(), style);
    }

    public static void setNodeData(Node node, Object data, Class<?> type, FileStyle style) {
        if (data == null) {
            node.clearChildren();
            node.setValue(Utilities.getNullIndicator());
            return;
        } else if (node.getValue().equals(Utilities.getNullIndicator())) {
            node.setValue("");
        }

        // If we try to save a single-line string and find it is currently saved as a multi-line string, we do NOT remove the mutli-line formatting.
        // The reason for this is that there might be comments on the """s, and we want to preserve those comments.
        // Also, this happens in only two cases:
        //     1. A string that is usually single-line is manually switched to multi-line formatting by a user
        //     2. A string is saved as multi-line, then later saved as single-line
        // In case 1, we don't want to piss off the user; keep it how they like it.
        // In case 2, the string is probably going to be saved again later with multiple lines. It doesn't seem necessary to disrupt the structure
        // of the file for something temporary.
        String dataAsString = data.toString();
        if (type.equals(String.class) && (containsNewLine(dataAsString) || node.getChildNodes().size() > 0)) {
            BaseTypes.setStringSpecialCase(node, dataAsString, style);
        } else if (BaseTypes.isBaseType(type)) {
            BaseTypes.setBaseTypeNode(node, data, type, style);
        } else if (CollectionTypes.trySetCollection(node, data, (Class)type, Object.class, style)) {
            return;
        } else {
            ComplexTypes.setComplexNode(node, data, type, style);
        }
    }

    public static <T> T getNodeData(Node node, Class<T> type) {
        if (node.getValue().equals(Utilities.getNullIndicator())) {
            return null;
        }

        try {
            if (String.class.equals(type) && node.getValue().equals(MultiLineStringNode.terminator) && node.getChildLines().size() > 0) {
                return type.cast(BaseTypes.parseSpecialStringCase(node));
            }

            if (BaseTypes.isBaseType(type)) {
                return BaseTypes.parseBaseType(node.getValue(), type);
            }

            T collection = type.cast(CollectionTypes.tryGetCollection(node, type, Object.class));

        }
    }
}
