package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.List;

public class Element implements Serializable {

    private String elementId;
    private String creatorId;
    private String title;
    private String shortDesc;
    //What do you achieve with it, what do you use it for? E.G. Lower Back for Deadlift
    private List<String> usedFor;
    private String pictureUrl;
    //How may people do you need min. for successfully doing this element?
    private int minNumberOfHumans;
    //TODO To be set true when an Element is very basic (Like Pushups) and reviewed + edited by staff/(admins?). From then it can only be updated by admins
    private boolean isFixedBasic;
    //TODO REconsinder making this a List<Material> -> Having the data denormalized
    private List<String> neededMaterialsIds;

    //TODO Add List of SubElements
    private String timeCreated;

    //For easy itemSelection in Recyclerviews
    private boolean isSelected;

    //TODO Does this need a short written description?

    public Element(String title) {
        this.title = title;
    }

    public Element() {
    }

    public Element(String title, String shortDesc, List<String> usedFor, int minNumberOfHumans, List<String> neededMaterialsIds, String timeCreated, String creatorId) {
        this.title = title;
        this.shortDesc =shortDesc;
        this.usedFor = usedFor;
        this.minNumberOfHumans = minNumberOfHumans;
        this.neededMaterialsIds = neededMaterialsIds;
        this.timeCreated = timeCreated;
        this.creatorId = creatorId;
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

    public List<String> getNeededMaterialsIds() {
        return neededMaterialsIds;
    }

    public void setNeededMaterialsIds(List<String> neededMaterialsIds) {
        this.neededMaterialsIds = neededMaterialsIds;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    //Copy function so that on creation of subclasses its not always necessary to input every single variable
    public void copy(Element element){
        this.title = element.getTitle();
        this.shortDesc = element.getShortDesc();
        this.usedFor = element.getUsedFor();
        this.pictureUrl = element.getPictureUrl();
        this.minNumberOfHumans = element.getMinNumberOfHumans();
        this.isFixedBasic = element.isFixedBasic();
        this.timeCreated = element.getTimeCreated();
        this.neededMaterialsIds = element.getNeededMaterialsIds();
        this.creatorId = element.getCreatorId();
        this.elementId = element.getElementId();

    }


}
