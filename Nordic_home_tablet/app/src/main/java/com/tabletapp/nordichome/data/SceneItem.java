package com.tabletapp.nordichome.data;

import java.util.UUID;

/**
 * Class to represent a specific scene that will belong to a Group within the Nordic Home
 * @author Sunniva Mathea Runde
 */

public class SceneItem {

    private String id;
    private String name;

    public SceneItem(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
    }
}
