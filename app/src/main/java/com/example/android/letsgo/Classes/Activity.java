package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.List;

public class Activity implements Serializable {

    //Added the modul related data not as a full modul becauce I dont want the updateable data
    //(Description of module) to be denormalized. If the user needs more detailed information
    // about a modul he needs to click on it and data gets pulled from the original modul id
    private int modulId;
    private String modulTitle;
    private List<Element> modulElements;
    //TODO Add denormalized way to store user info
    private int uId;
    private long startTime;
    private long endTime;
    //TODO Add ability to track duration of every Element

    public Activity() {
    }

    public Activity(int modulId, String modulTitle, List<Element> modulElements, int uId, long startTime) {
        this.modulId = modulId;
        this.modulTitle = modulTitle;
        this.modulElements = modulElements;
        this.uId = uId;
        this.startTime = startTime;
    }

    public int getModulId() {
        return modulId;
    }

    public void setModulId(int modulId) {
        this.modulId = modulId;
    }

    public String getModulTitle() {
        return modulTitle;
    }

    public void setModulTitle(String modulTitle) {
        this.modulTitle = modulTitle;
    }

    public List<Element> getModulElements() {
        return modulElements;
    }

    public void setModulElements(List<Element> modulElements) {
        this.modulElements = modulElements;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
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

}
