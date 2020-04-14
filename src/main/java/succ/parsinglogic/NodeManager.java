package succ.parsinglogic;

import falsepattern.reflectionhelper.ClassTree;
import succ.Utilities;
import succ.parsinglogic.nodes.MultiLineStringNode;
import succ.parsinglogic.nodes.Node;
import succ.parsinglogic.types.BaseTypes;
import succ.parsinglogic.types.CollectionTypes;
import succ.parsinglogic.types.ComplexTypeShortcuts;
import succ.parsinglogic.types.ComplexTypes;
import succ.style.FileStyle;

import static succ.parsinglogic.ParsingLogicExtensions.containsNewLine;

/**
 * Gets and sets the data encoded by Nodes.
 */
public class NodeManager {

    @SuppressWarnings("UnnecessaryReturnStatement")
    public static <T> void setNodeData(Node node, Object data, ClassTree<?> type, FileStyle style) {
        if (data == null) {
            node.clearChildren();
            node.setValue(Utilities.getNullIndicator());
            return;
        } else if (node.getValue().equals(Utilities.getNullIndicator())) {
            node.setValue("");
        }

        // If we try to save a single-line string and find it is currently saved as a multi-line string, we do NOT remove the multi-line formatting.
        // The reason for this is that there might be comments on the """s, and we want to preserve those comments.
        // Also, this happens in only two cases:
        //     1. A string that is usually single-line is manually switched to multi-line formatting by a user
        //     2. A string is saved as multi-line, then later saved as single-line
        // In case 1, we don't want to piss off the user; keep it how they like it.
        // In case 2, the string is probably going to be saved again later with multiple lines. It doesn't seem necessary to disrupt the structure
        // of the file for something temporary.
        String dataAsString = data.toString();
        if (type.type.equals(String.class) && (containsNewLine(dataAsString) || node.getChildNodes().size() > 0)) {
            BaseTypes.setStringSpecialCase(node, dataAsString, style);
        } else if (BaseTypes.isBaseType(type.type)) {
            BaseTypes.setBaseTypeNode(node, data, type.type, style);
        } else if (CollectionTypes.trySetCollection(node, data, type, style)) {
            return;
        } else {
            ComplexTypes.setComplexNode(node, data, type, style);
        }
    }

    public static <T> T getNodeData(Node node, ClassTree<T> type) {
        if (node.getValue().equals(Utilities.getNullIndicator())) {
            return null;
        }

        try {
            if (String.class.equals(type.type) && node.getValue().equals(MultiLineStringNode.terminator) && node.getChildLines().size() > 0) {
                return type.type.cast(BaseTypes.parseSpecialStringCase(node));
            }

            if (BaseTypes.isBaseType(type.type)) {
                return BaseTypes.parseBaseType(node.getValue(), type.type);
            }

            T collection = type.type.cast(CollectionTypes.tryGetCollection(node, type));

            if (collection != null) {
                return collection;
            }

            String value = node.getValue();
            if (!(value == null || value.equals(""))) {
                return ComplexTypeShortcuts.getFromShortcut(value, type.type);
            }

            return ComplexTypes.retrieveComplexType(node, type);
        } catch (Exception e) {
            throw new RuntimeException("Error getting data of type " + type.toString() + " from node: ", e);
        }
    }
}
