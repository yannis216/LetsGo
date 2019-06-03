package com.example.android.letsgo.Classes;

import java.io.Serializable;

public class User implements Serializable {
    private String displayName;
    private String profilePictureUrl;
    private String authId;

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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }
}
