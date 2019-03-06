package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.List;

public class Modul implements Serializable {

    private String title;

    private String creatorUid;
    //TODO Insert String creatorName -> Denormalized, has to get updated on Creatorname changed, saves Document reads
    private int creationTimestamp;
    //TODO Save non-originals in Collection Associated with creatorUid and save Originals also there AND in Main Mouduls List
    private int editTimeStamp;
    //TODO If it has an originalCreatorUid it is an original that is saved also in Main Module List
    private String originalCreatorUid;
    private List<ModulElement> modulElements;


    public Modul(String title, String creatorUid,  List<ModulElement> modulElements) {
        this.title = title;
        this.creatorUid = creatorUid;
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

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public int getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(int creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public int getEditTimeStamp() {
        return editTimeStamp;
    }

    public void setEditTimeStamp(int editTimeStamp) {
        this.editTimeStamp = editTimeStamp;
    }

    public String getOriginalCreatorUid() {
        return originalCreatorUid;
    }

    public void setOriginalCreatorUid(String originalCreatorUid) {
        this.originalCreatorUid = originalCreatorUid;
    }

}
