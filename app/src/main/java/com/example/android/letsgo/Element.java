package com.example.android.letsgo;

import java.io.Serializable;
import java.util.List;

public class Element implements Serializable {

    private String title;
    //What do you achieve with it, what do you use it for? E.G. Lower Back for Deadlift
    //TODO This will be a List at some point and maybe be (entered and) displayed as chips
    private List<String> usedFor;
    private String pictureUrl;
    private String videoId;
    //How may people do you need min. for successfully doing this element?
    private int minNumberOfHumans;
    //Are any materials needed for doing this element? Should be implemented with Tags/Chips
    private List<String> materialsNeeded;
    //To be set true when an Element is very basic (Like Pushups) and reviewed + edited by staff/(admins?). From then it can only be updated by admins
    private boolean isFixedBasic;
    //TODO Add List of SubElements

    public Element(String title) {
        this.title = title;
    }

    public Element() {
    }

    public Element(String title, List<String> usedFor, String pictureUrl, String videoId, int minNumberOfHumans) {
        this.title = title;
        this.usedFor = usedFor;
        this.videoId = videoId;
        this.minNumberOfHumans = minNumberOfHumans;
        this.pictureUrl =pictureUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getUsedFor() {
        return usedFor;
    }

    public void setUsedFor(List<String> usedFor) {
        this.usedFor = usedFor;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getMinNumberOfHumans() {
        return minNumberOfHumans;
    }

    public void setMinNumberOfHumans(int numberOfHumans) {
        this.minNumberOfHumans = numberOfHumans;
    }

    public List<String> getMaterialsNeeded() {
        return materialsNeeded;
    }

    public void setMaterialsNeeded(List<String> materialsNeeded) {
        this.materialsNeeded = materialsNeeded;
    }

    public boolean isFixedBasic() {
        return isFixedBasic;
    }

    public void setFixedBasic(boolean fixedBasic) {
        isFixedBasic = fixedBasic;
    }



}
