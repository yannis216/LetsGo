package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.List;

public class Activity implements Serializable {


    private String id;
    //Added the modul related data not as a full modul becauce I dont want the updateable data
    //(Description of module) to be denormalized. If the user needs more detailed information
    // about a modul he needs to click on it and data gets pulled from the original modul id
    private String modulId;
    private String modulTitle;
    private List<ModulElement> modulElements;
    //TODO Add denormalized way to store user info
    private String uId;
    private long startTime;
    private long endTime;
       //TODO Add ability to track duration of every Element
    private int currentPosition;

    public Activity() {
    }

    public Activity(String modulId, String modulTitle, List<ModulElement> modulElements, String uId, long startTime, int currentPosition) {
        this.modulId = modulId;
        this.modulTitle = modulTitle;
        this.modulElements = modulElements;
        this.uId = uId;
        this.startTime = startTime;
        this.currentPosition = currentPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getModulId() {
        return modulId;
    }

    public void setModulId(String modulId) {
        this.modulId = modulId;
    }

    public String getModulTitle() {
        return modulTitle;
    }

    public void setModulTitle(String modulTitle) {
        this.modulTitle = modulTitle;
    }

    public List<ModulElement> getModulElements() {
        return modulElements;
    }

    public void setModulElements(List<ModulElement> modulElements) {
        this.modulElements = modulElements;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

}
