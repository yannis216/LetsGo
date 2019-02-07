package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.List;

public class ModulElement extends Element implements Serializable {
    //TODO Might have to add "SourceElementId" here
    private ModulElementMultiplier multiplier;
    //Meant to display additional, Modul specific instructions for the element.
    // Eg. Something like "If you are running out of stamina here its ok to slow down a little bit"
    // or "As many/fast as as you can"
    private String hint;
    private String sourceElementId;

    //TODO Possible that I will never need this orderInModul!
    private int orderInModul;

    public ModulElement(String title, String shortDesc, List<String> usedFor, String pictureUrl,
                        String videoId, int minNumberOfHumans, List<String> neededMaterialsIds,
                        String timeCreated, ModulElementMultiplier multiplier, String hint, String sourceElementId, int orderInModul) {
        super(title, shortDesc, usedFor, pictureUrl, videoId, minNumberOfHumans, neededMaterialsIds, timeCreated);
        this.multiplier = multiplier;
        this.hint = hint;
        this.sourceElementId = sourceElementId;
        this.orderInModul = orderInModul;
    }

    public ModulElement(Element baseElement, ModulElementMultiplier multiplier, String hint, String sourceElementId, int orderInModul){
        super.copy(baseElement);
        this.multiplier = multiplier;
        this.hint = hint;
        this.sourceElementId = sourceElementId;
        this.orderInModul = orderInModul;
    }

    //TODO Experimental
    public ModulElement(Element baseElement){
        super.copy(baseElement);
    }

    public ModulElement() {
    }

    public ModulElementMultiplier getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(ModulElementMultiplier multiplier) {
        this.multiplier = multiplier;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getSourceElementId() {
        return sourceElementId;
    }

    public void setSourceElementId(String sourceElementId) {
        this.sourceElementId = sourceElementId;
    }

    public int getOrderInModul() {
        return orderInModul;
    }

    public void setOrderInModul(int orderInModul) {
        this.orderInModul = orderInModul;
    }

}
