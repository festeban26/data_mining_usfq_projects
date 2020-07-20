package com.festeban26;

public class Tuple {
    private String mKey;
    private int mValue;

    public Tuple(String key, int value) {
        mKey = key;
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public String getKey() {
        return mKey;
    }
}
