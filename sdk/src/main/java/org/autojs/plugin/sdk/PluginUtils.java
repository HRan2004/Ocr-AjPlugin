package org.autojs.plugin.sdk;

import java.util.Map;

public class PluginUtils {

    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getNotNull(Map<String, ?> args, String key) {
        T value = (T) args.get(key);
        if (value == null)
            throw new NullPointerException(key + " is null in args: " + args);
        return value;
    }
}
