package com.example.android.letsgo.Classes;

import java.util.List;

public class Modul {

    private String title;
    private List<ModulElement> modulElements;


    public Modul(String title, List<ModulElement> modulElements) {
        this.title = title;
        this.modulElements = modulElements;
    }

    public Modul() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ModulElement> getModulElements() {
        return modulElements;
    }

    public void setModulElements(List<ModulElement> modulElements) {
        this.modulElements = modulElements;
    }


}
