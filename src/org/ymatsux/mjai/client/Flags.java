package org.ymatsux.mjai.client;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Flags {

    private static final Pattern ARG_PATTERN = Pattern.compile("--(.+)=(.+)");

    private static Map<String, String> values;

    public static void parse(String[] args) {
        values = new HashMap<String, String>();
        for (String arg : args) {
            Matcher matcher = ARG_PATTERN.matcher(arg);
            if (matcher.matches()) {
                String name = matcher.group(1);
                String value = matcher.group(2);
                values.put(name, value);
            }
        }
    }

    public static String get(Flag flag) {
        if (values.containsKey(flag.getName())) {
            return values.get(flag.getName());
        }
        return flag.getDefaultValue();
    }

    public static final Flag SERVER = new Flag("server", "");
    public static final Flag PORT = new Flag("port", "");
    public static final Flag ROOM = new Flag("room", "default");
}
