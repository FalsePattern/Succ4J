package falsepattern;

/**
 * Used exclusively to emulate c# (out T param) behaviour.
 */
public class Out<T> {
    public T value;
    public final Class<T> type;
    public Out(Class<T> type) {
        this.type = type;
    }
}
