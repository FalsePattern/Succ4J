package succ;

/**
 * Rules for various aspects of how generated SUCC data is formatted.
 * This only affects new data. If SUCC is modifying existing data, it will do its best to keep the formatting intact.
 */
public class FileStyle {

    /**
     * If you do not specify a FileStyle for your DataFile, this will be used.
     */
    public static FileStyle defaultStyle = new FileStyle();

    /**
     * Creates a new FileStyle.
     */

    public FileStyle() {}

    /**
     * SUCC strings can optionally be surrounded by "quotes". If this is true, they will be quoted even when not necessary.
     */
    public boolean alwaysQuoteStrings = false;

    /**
     * SUCC can store dictionaries as KeyValuePair arrays if the key type is complex. If this is true, dictionaries will always be stored like that.
     */
    public boolean alwaysArrayDictionaries = false;

    /**
     * SUCC can read booleans in several different ways. The BoolStyle specifies which of those ways to save them in.
     */
    public BoolStyle boolStyle = BoolStyle.true_false;

    /**
     * SUCC can read enums in a couple different ways. The EnumStyle specifies which of those ways to save them in.
     */
    public EnumStyle enumStyle = EnumStyle.name;

    private int indentationInterval = 4;
    private int spacesAfterColon = 1;
    private int spacesAfterDash = 1;

    /**
     * The number of spaces used to indent a child line under its parent. Must be at least 1.
     */
    public int getIndentationInterval() {
        return indentationInterval;
    }

    /**
     * The number of spaces used to indent a child line under its parent. Must be at least 1.
     */
    public void setIndentationInterval(int value) {
        if (value < 1) {
            throw new IndexOutOfBoundsException("IndentationInterval must be at least 1. You tried to set it to " + value);
        } else {
            indentationInterval = value;
        }
    }

    /**
     * The number of spaces between the colon and the value in a key node. Must be at least 0.
     */
    public int getSpacesAfterColon() {
        return spacesAfterColon;
    }

    /**
     * The number of spaces between the colon and the value in a key node. Must be at least 0.
     */
    public void setSpacesAfterColon(int value) {

    }

    /**
     * The number of spaces between the dash and the value in a list node. Must be at least 0.
     */
    public int getSpacesAfterDash() {
        return spacesAfterDash;
    }

    /**
     * The number of spaces between the dash and the value in a list node. Must be at least 0.
     */
    public void setSpacesAfterDash(int value) {
        this.spacesAfterDash = spacesAfterDash;
    }
}
