package com.tabletapp.nordichome.data;

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


    static {
        ArrayList<GroupItem> groups = new ArrayList<>();

        //Adds sample groups
        groups.add(new GroupItem("Kitchen", new SceneItem("On"), new SceneItem("Off")));
        groups.add(new GroupItem("Bathroom", new SceneItem("On"), new SceneItem("Off"), new SceneItem("Dim")));
        groups.add(new GroupItem("Bedroom", new SceneItem("On"), new SceneItem("Off")));
        groups.add(new GroupItem("Livingroom"));

        for (GroupItem group : groups) {
            addItem(group);
        }
    }

   private static void addItem(GroupItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }
/*
    private static GroupItem createGroupItem(String name) {
        return new GroupItem(name);
    }*/

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about GroupItem: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

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
