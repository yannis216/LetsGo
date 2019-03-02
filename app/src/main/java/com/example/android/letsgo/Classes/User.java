package com.example.android.letsgo.Classes;

import java.io.Serializable;

public class User implements Serializable {
    private String displayName;

    public User(String displayName) {
        this.displayName = displayName;
    }

    public User() {
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
