package succ.parsinglogic.types;

import java.util.HashMap;

public class BaseTypeConversionHashMap extends HashMap<Class<?>, BaseTypes.CustomMethod<?>> {
    @SuppressWarnings("unchecked")
    public <T, K extends BaseTypes.CustomMethod<T>> K get(Class<T> type) {
        return (K)super.get(type);
    }
}
