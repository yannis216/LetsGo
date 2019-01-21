package com.example.android.letsgo;

import java.util.List;

public class Element {

    private String title;
    //What do you achieve with it, what do you use it for? E.G. Lower Back for Deadlift
    private List<String> usedFor;
    private String thumbnailUrl;
    private String videoUrl;
    //How may people do you need min. for successfully doing this element?
    private int minNumberOfHumans;
    //Are any materials needed for doing this element? Should be implemented with Tags/Chips
    private List<String> materialsNeeded;
    //To be set true when an Element is very basic (Like Pushups) and reviewed + edited by staff/(admins?). From then it can only be updated by admins
    private boolean isFixedBasic;
    //TODO Add List of Subelements

    public Element(String title) throws Exception {
        this.title = title;
    }

    public Element() {
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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
