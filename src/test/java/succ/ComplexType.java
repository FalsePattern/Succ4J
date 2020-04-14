package succ;

import succ.parsinglogic.DoSave;

public class ComplexType {
    public int Integer;
    @DoSave
    private String String;
    @DoSave
    public boolean Boolean;

    // parameter-less constructor required for reflection
    public ComplexType(){}

    public ComplexType(int integer, String text, boolean aBoolean) {
        this.Integer = integer;
        this.String = text;
        this.Boolean = aBoolean;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ComplexType)) {
            return false;
        }

        ComplexType other = (ComplexType) obj;

        return this.Integer == other.Integer
            && this.String.equals(other.String)
            && this.Boolean == other.Boolean;
    }

    @Override
    public java.lang.String toString() {
        return Integer + " | " + String + " | " + Boolean;
    }
}
