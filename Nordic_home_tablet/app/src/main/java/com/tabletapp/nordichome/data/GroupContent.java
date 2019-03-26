package com.tabletapp.nordichome.data;

import android.transition.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used to preview the information belonging to the groups in the Nordic Home
 * @author Sunniva Mathea Runde
 * @author Heidi Lohne Braekke
 */

public class GroupContent {

    /**
     * An array of group items
     */
    public static final List<GroupItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (group) items, by ID.
     */
    public static final Map<String, GroupItem> ITEM_MAP = new HashMap<>();


    /**
     * Adding dummy data for displaying front-end
     */
    static {
        ArrayList<GroupItem> groups = new ArrayList<>();
        ArrayList<SceneItem> scenes = new ArrayList<>();

        //Adds sample groups
        groups.add(new GroupItem("Kitchen"));
        groups.add(new GroupItem("Bathroom"));
        groups.add(new GroupItem("Bedroom"));
        groups.add(new GroupItem("Livingroom"));

        for (GroupItem group : groups) {
            addItem(group);
        }

        //Add sample scenes
        scenes.add(new SceneItem("On"));
        scenes.add(new SceneItem("Off"));
        scenes.add(new SceneItem("Dimmed"));


        for (GroupItem group : ITEMS) {
            for (SceneItem scene : scenes) {
                group.addScene(scene);
            }
        }


    }

   private static void addItem(GroupItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }
/*
 */
    private static GroupItem createGroupItem(String name) {
        return new GroupItem(name);
    }

/*    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about GroupItem: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }*/

/*    *//**
     * A group item representing a piece of content.
     *//*
    public static class GroupItem {
        public final String id;
        public final String content;
        public final String details;

        public GroupItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }*/
}
