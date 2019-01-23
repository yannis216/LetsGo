package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.List;

public class Material implements Serializable {
    private String title;
    private boolean getsConsumed;
    private List<String> shoppingLinks;

    public Material(String title, boolean getsConsumed, List<String> shoppingLinks) {
        this.title = title;
        this.getsConsumed = getsConsumed;
        this.shoppingLinks = shoppingLinks;
    }

    public Material() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isGetsConsumed() {
        return getsConsumed;
    }

    public void setGetsConsumed(boolean getsConsumed) {
        this.getsConsumed = getsConsumed;
    }

    public List<String> getShoppingLinks() {
        return shoppingLinks;
    }

    public void setShoppingLinks(List<String> shoppingLinks) {
        this.shoppingLinks = shoppingLinks;
    }
}
