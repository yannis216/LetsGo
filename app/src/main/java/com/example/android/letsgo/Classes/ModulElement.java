package com.example.android.letsgo.Classes;

import java.io.Serializable;

public class ModulElement extends Element implements Serializable, Cloneable {
    //TODO Might have to add "SourceElementId" here
    private ModulElementMultiplier multiplier;
    //Meant to display additional, Modul specific instructions for the element.
    // Eg. Something like "If you are running out of stamina here its ok to slow down a little bit"
    // or "As many/fast as as you can"
    private String hint;
    private String sourceElementId;
    private int orderInModul;

    public ModulElement(Element baseElement){
        super.copy(baseElement);
    }

    public ModulElement() {
    }

    public ModulElement(Element baseElement, ModulElementMultiplier multiplier, String hint, String sourceElementId, int orderInModul) {
        super.copy(baseElement);
        this.multiplier = multiplier;
        this.hint = hint;
        this.sourceElementId = sourceElementId;
        this.orderInModul = orderInModul;
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

    @Override
    public ModulElement clone() {
        try{
            return (ModulElement) super.clone();
        }catch (CloneNotSupportedException e){
            throw new AssertionError();
        }
    }

}
