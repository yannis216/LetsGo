package com.example.android.letsgo.Classes;

public class ElementMultiplier {

    private int timesMultiplied;
    private String type;

    public ElementMultiplier(int timesMultiplied, String type) {
        this.timesMultiplied = timesMultiplied;
        this.type = type;
    }

    public ElementMultiplier() {
    }

    public int getTimesMultiplied() {
        return timesMultiplied;
    }

    public void setTimesMultiplied(int timesMultiplied) {
        this.timesMultiplied = timesMultiplied;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
