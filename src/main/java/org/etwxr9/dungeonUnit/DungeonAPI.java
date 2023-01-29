package org.etwxr9.dungeonUnit;

import org.etwxr9.dungeonUnit.Dungeon.DungeonInfo;
import org.etwxr9.dungeonUnit.Dungeon.DungeonManager;
import org.etwxr9.dungeonUnit.Dungeon.RoomInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.text.MessageFormat;
import java.util.List;

public class DungeonAPI {

    /**
     * 返回一个世界内的所有DungeonInfo
     *
     * @param worldName
     * @return
     */
    public static List<DungeonInfo> getWorldDI(String worldName) {
        return DungeonManager.getDIList().stream().filter(dungeonInfo -> dungeonInfo.getWorld().equals(worldName)).toList();
    }

    /**
     * 返回指定DungeonInfo，没有则返回null
     *
     * @param dungeonId
     * @return
     */
    public static DungeonInfo GetDungeonInfo(String dungeonId) {
        // Main.getInstance().getLogger().info("准备遍历DI查找" + worldName);
        for (DungeonInfo d : DungeonManager.getDIList()) {
            // Main.getInstance().getLogger().info("遍历DI中：" + d.Id);
            if (dungeonId.equals(d.getId())) {
                // Main.getInstance().getLogger().info("遍历DI" + d.Id + " 匹配");
                return d;
            }
        }
        Main.getInstance().getLogger().info("未能查找到地牢" + dungeonId);
        return null;
    }

    /**
     * 返回指定DungeonInfo的指定RoomInfo，没有则返回null
     *
     * @param dungeonId
     * @param roomId
     * @return
     */
    public static RoomInfo GetRoomInfo(String dungeonId, String roomId) {
        var di = GetDungeonInfo(dungeonId);
        if (di == null) return null;
        return di.GetRoomInfo(roomId);
    }

    public static RoomInfo GetRoomInfo(DungeonInfo di, String roomId) {
        return di.getRoomInfos().get(roomId);
    }

    /**
     * 取得指定房间的副本数量
     *
     * @param ri
     * @return
     */
    public static int getRoomInstanceAmount(RoomInfo ri) {
        return ri.getRooms().size();
    }

    /**
     * 返回指定房间的地牢系坐标，指定序号副本的地牢系坐标，如果序号溢出则返回null
     *
     * @param ri
     * @param index
     * @return
     */
    public static Vector getRoomPos(RoomInfo ri, int index) {
        if (ri.getRooms().size() <= index) return null;
        return ri.getRooms().get(index);
    }

    /**
     * 将玩家传送到指定的房间,传送相对位置是RoomInfo.playerPosition
     *
     * @param dungeon
     * @param p
     * @param room
     * @param index
     */
    public static void tpPlayer(DungeonInfo dungeon, Player p, RoomInfo room, int index) {
        if (index >= room.getRooms().size()) return;
        var point = DungeonAPI.GetPoint(room, index, room.getPlayerPosition());
        var world = Bukkit.getWorld(dungeon.getWorld());
        //如果是op则提供后台信息
        if (p.isOp()) {
            p.sendMessage(MessageFormat.format("准备传送至地牢：{0}， 房间Id：{1}， 序号：{2}",
                    dungeon.getId(), room.getId(), index));

            p.sendMessage(MessageFormat.format("y的地牢原点：{0}，房间规模 {1}， 房间位置{2}， 出生点{3}",
                    dungeon.getOrigin().toString(), dungeon.getRoomSize().toString(),
                    room.getRooms().get(index).toString(), room.getPlayerPosition().toString()));
        }
        if (p.teleport(new Location(world, point.getBlockX() + 0.5, point.getBlockY() + 0.5,
                point.getBlockZ() + 0.5))) {
            if (p.isOp()) {
                p.sendMessage(MessageFormat.format("准备传送至：{0}",
                        point.toString()));
                p.sendMessage("传送成功");
            }
        } else {
            p.sendMessage("因未知原因传送失败");
        }
    }

    /**
     * 返回指定房间副本内指定点的坐标，如果序号溢出则返回null
     *
     * @param ri
     * @param index     该房间副本的序号
     * @param roomPoint 房间内相对坐标
     * @return
     */
    public static Vector GetPoint(RoomInfo ri, int index, Vector roomPoint) {
        var di = GetDungeonInfo(ri.getDungeonId());
        var ori = di.getOrigin().clone();
        if (ri.getRooms().size() <= index) return null;
        return ori.add(di.getRoomSize().clone().multiply(ri.getRooms().get(index)).add(roomPoint));
    }


}
