package com.tabletapp.nordichome.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class to represent a specific group in the Nordic Home
 * @author Sunniva Mathea Runde
 */

public class GroupItem {
    public final String id;
    public final String name;

    /**
     * An array containing the scenes that belong to the group
     */
    public static List<SceneItem> scenes = new ArrayList<>();

    public GroupItem(String name, SceneItem... sceneItems) {
        this.id = UUID.randomUUID().toString();
        this.name = name;

        for (SceneItem sceneItem: sceneItems){
            this.addScene(sceneItem);
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    public ArrayList<SceneItem> getScenesList(){
        return (ArrayList<SceneItem>) scenes;
    }

    /**
     * Adds a specific scene to the group
     * @param scene
     */
    public void addScene(SceneItem scene) {
        this.scenes.add(scene);
    }

}
