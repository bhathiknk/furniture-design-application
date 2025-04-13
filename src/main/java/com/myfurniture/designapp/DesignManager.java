package com.myfurniture.designapp;

public class DesignManager {
    private RoomDesign currentDesign;

    public RoomDesign getCurrentDesign() {
        return currentDesign;
    }

    public void setCurrentDesign(RoomDesign design) {
        this.currentDesign = design;
    }
}
