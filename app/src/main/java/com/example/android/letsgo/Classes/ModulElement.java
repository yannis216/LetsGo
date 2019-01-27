package com.example.android.letsgo.Classes;

import java.io.Serializable;
import java.util.List;

public class ModulElement extends Element implements Serializable {
    //TODO Might have to add "SourceElementId" here
    private ElementMultiplier multiplier;
    //Meant to display additional, Modul specific instrcutions for the element.
    // Eg. Something like "If you are running out of stamina here its ok to slow down a little bit"
    // or "As many/fast as as you can"
    private String hint;
    private String sourceElementId;

    public ModulElement(String title, String shortDesc, List<String> usedFor, String pictureUrl,
                        String videoId, int minNumberOfHumans, List<String> neededMaterialsIds,
                        String timeCreated, ElementMultiplier multiplier, String hint, String sourceElementId) {
        super(title, shortDesc, usedFor, pictureUrl, videoId, minNumberOfHumans, neededMaterialsIds, timeCreated);
        this.multiplier = multiplier;
        this.hint = hint;
        this.sourceElementId = sourceElementId;
    }

    public ElementMultiplier getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(ElementMultiplier multiplier) {
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
}
