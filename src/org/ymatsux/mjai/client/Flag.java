package org.ymatsux.mjai.client;

public class Flag {

    private String name;
    private String defaultValue;

    public Flag(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
