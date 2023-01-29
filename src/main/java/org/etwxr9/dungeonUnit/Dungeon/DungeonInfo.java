package org.etwxr9.dungeonUnit.Dungeon;

import com.alibaba.fastjson2.annotation.JSONField;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonInfo {
    private String id = "";
    private String world = "";
    //用于形容该地牢
    private List<String> tags;
    //源点（最小点）位置
    private Vector origin;
    //以房间为单位的坐标系大小
    private Vector size;
    //以方块为单位的房间大小
    private Vector roomSize;

    //该DI包含的所有房间类型
    private Map<String, RoomInfo> roomInfos;
    //当前被占用的位置
    @JSONField(serialize = false)
    private Map<Vector, String> roomUnits;
    //当前没有被占用的位置
    @JSONField(serialize = false)
    private ArrayList<Vector> emptyRoomList;


    public DungeonInfo(String id, String world, List<String> tags, Vector origin, Vector size, Vector roomSize, Map<String, RoomInfo> roomInfos) {
        this.id = id;
        this.world = world;
        this.tags = tags;
        this.origin = origin;
        this.size = size;
        this.roomSize = roomSize;
        this.roomInfos = roomInfos;
        initEmptyRoomList();
    }

    public String getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }

    public List<String> getTags() {
        return tags;
    }

    public Vector getOrigin() {
        return origin;
    }

    public Vector getSize() {
        return size;
    }

    public Vector getRoomSize() {
        return roomSize;
    }

    public Map<Vector, String> getRoomUnits() {
        return roomUnits;
    }

    public ArrayList<Vector> getEmptyRoomList() {
        return emptyRoomList;
    }

    public Map<String, RoomInfo> getRoomInfos() {
        return roomInfos;
    }

    /**
     * 填充空房间列表
     */
    private void initEmptyRoomList() {
        roomUnits = new HashMap<>();
        roomInfos.forEach((s, ri) -> {
            ri.getRooms().forEach(v -> {
                roomUnits.put(v, ri.getId());
            });
        });
        emptyRoomList = new ArrayList<>();
        for (int z = 0; z < size.getZ(); z++) {
            for (int y = 0; y < size.getY(); y++) {
                for (int x = 0; x < size.getX(); x++) {
                    var pos = new Vector(x, y, z);
                    emptyRoomList.add(pos);
                }
            }
        }
        emptyRoomList.removeAll(roomUnits.keySet());
    }


    /**
     * 返回指定id的房间信息
     *
     * @param id
     * @return
     */
    public RoomInfo GetRoomInfo(String id) {
        return roomInfos.get(id);
    }


}
