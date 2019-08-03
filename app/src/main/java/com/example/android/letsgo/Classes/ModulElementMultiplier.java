package com.example.android.letsgo.Classes;

import java.io.Serializable;

public class ModulElementMultiplier implements Serializable, Cloneable {

    private int timesMultiplied;
    private String type;

    public ModulElementMultiplier(int timesMultiplied, String type) {
        this.timesMultiplied = timesMultiplied;
        this.type = type;
    }

    public ModulElementMultiplier() {
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

    @Override
    public ModulElementMultiplier clone() {
        try{
            return (ModulElementMultiplier) super.clone();
        }catch (CloneNotSupportedException e){
            throw new AssertionError();
        }
    }


}
