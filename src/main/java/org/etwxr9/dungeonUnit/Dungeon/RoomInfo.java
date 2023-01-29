package org.etwxr9.dungeonUnit.Dungeon;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoomInfo {
    private String dungeonId;
    private String id;
    private List<String> tags;
    private Vector playerPosition;
    private Map<String, Vector> specialPositions;
    private List<Vector> rooms;

    public RoomInfo(String dungeonId, String id, List<String> tags, Vector playerPosition, Map<String, Vector> specialPositions, List<Vector> rooms) {
        this.dungeonId = dungeonId;
        this.id = id;
        this.tags = tags == null ? new ArrayList<>() : tags;
        this.playerPosition = playerPosition;
        this.specialPositions = specialPositions == null ? new HashMap<>() : specialPositions;
        this.rooms = rooms;
    }


    public RoomInfo(String dungeonId, String id, List<String> tags, Vector playerPosition, Vector firstRoomPos) {
        this.dungeonId = dungeonId;
        this.id = id;
        this.tags = tags;
        this.playerPosition = playerPosition;
        specialPositions = new HashMap<>();
        rooms = new ArrayList<>();
        rooms.add(firstRoomPos);
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public void setDungeonId(String dungeonId) {
        this.dungeonId = dungeonId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Vector getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(Vector playerPosition) {
        this.playerPosition = playerPosition;
    }

    public Map<String, Vector> getSpecialPositions() {
        return specialPositions;
    }

    public void setSpecialPositions(Map<String, Vector> specialPositions) {
        this.specialPositions = specialPositions;
    }

    public List<Vector> getRooms() {
        return rooms;
    }

    public void setRooms(List<Vector> rooms) {
        this.rooms = rooms;
    }
}