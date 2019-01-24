package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.List;

public class Element implements Serializable {

    private String title;
    private String shortDesc;
    //What do you achieve with it, what do you use it for? E.G. Lower Back for Deadlift
    private List<String> usedFor;
    private String pictureUrl;
    private String videoId;
    //How may people do you need min. for successfully doing this element?
    private int minNumberOfHumans;
    //TODO To be set true when an Element is very basic (Like Pushups) and reviewed + edited by staff/(admins?). From then it can only be updated by admins
    private boolean isFixedBasic;
    private List<Material> neededMaterials;
    //TODO Add List of SubElements

    //TODO Does this need a short written description?

    public Element(String title) {
        this.title = title;
    }

    public Element() {
    }

    public Element(String title, String shortDesc, List<String> usedFor, String pictureUrl, String videoId, int minNumberOfHumans,  List<Material> neededMaterials) {
        this.title = title;
        this.shortDesc =shortDesc;
        this.usedFor = usedFor;
        this.pictureUrl = pictureUrl;
        this.videoId = videoId;
        this.minNumberOfHumans = minNumberOfHumans;
        this.neededMaterials = neededMaterials;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
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

    public boolean isFixedBasic() {
        return isFixedBasic;
    }

    public void setFixedBasic(boolean fixedBasic) {
        isFixedBasic = fixedBasic;
    }

    public List<Material> getNeededMaterials() {
        return neededMaterials;
    }

    public void setNeededMaterials(List<Material> neededMaterials) {
        this.neededMaterials = neededMaterials;
    }



}
