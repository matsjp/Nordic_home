package com.tabletapp.nordichome.data;

/**
 * Class to represent a specific scene that will belong to a Group within the Nordic Home
 * @author Sunniva Mathea Runde
 */

public class SceneItem {

    private String address;
    private String name;
    private int number;

    public SceneItem(String name, String address, int number) {
        this.name = name;
        this.address = address;
        this.number = number;
    }

    public String getName(){
        return name;
    }

    public int getNumber() { return number; }

    public String getAddress() { return address; }
}

