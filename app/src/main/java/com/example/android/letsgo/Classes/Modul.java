package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Modul implements Serializable {

    private String title;
    private String Id;
    private String creatorUid;
    private String editorUid;
    //TODO Insert editorName Denormalized, has to get updated on Creatorname changed
    private String editorName;
    private long creationTimestamp;
    //TODO Save non-originals in Collection Associated with editorUid and save Originals also there AND in Main Mouduls List
    private long editTimeStamp;
    //TODO Add lastDateDone = timestamp when some user did it the last time.
    // Moduls that havent been done for time x can be deleted
    private String pictureUrl;


    private List<ModulElement> modulElements;
    private ArrayList<String> elementIds;


    public Modul(String title, String editorUid, List<ModulElement> modulElements) {
        this.title = title;
        this.editorUid = editorUid;
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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public List<ModulElement> getModulElements() {
        return modulElements;
    }

    public void setModulElements(List<ModulElement> modulElements) {
        this.modulElements = modulElements;
    }

    public String getEditorUid() {
        return editorUid;
    }

    public void setEditorUid(String editorUid) {
        this.editorUid = editorUid;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public long getEditTimeStamp() {
        return editTimeStamp;
    }

    public void setEditTimeStamp(long editTimeStamp) {
        this.editTimeStamp = editTimeStamp;
    }

    public String getCreatorUid() {
        return creatorUid;
    }

    public void setCreatorUid(String creatorUid) {
        this.creatorUid = creatorUid;
    }

    public String getEditorName() {
        return editorName;
    }

    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public ArrayList<String> getElementIds() {
        return elementIds;
    }

    public void setElementIds(ArrayList<String> elementIds) {
        this.elementIds = elementIds;
    }
}
