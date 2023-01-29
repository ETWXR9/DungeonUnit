package org.etwxr9.dungeonUnit.Dungeon;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import org.etwxr9.dungeonUnit.DungeonAPI;
import org.etwxr9.dungeonUnit.Main;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


//每个该对象和一个玩家绑定。管理所有DungeonInfo。
public class DungeonManager {

    private static final List<DungeonManager> dmList = new ArrayList<DungeonManager>();
    private static final List<DungeonInfo> diList = new ArrayList<DungeonInfo>();
    public DungeonInfo currentDungeon;
    public RoomInfo currentRoom;
    public int currentIndex;
    public Player player;

    /**
     * 取得空房间坐标
     *
     * @param di
     * @return int[] or null
     */
    public static Vector GetEmptyRoom(DungeonInfo di) {

        if (di.getEmptyRoomList().size() == 0) {
            return null;
        }
        return di.getEmptyRoomList().get(0);
    }

    /**
     * 新建DungeonManager地牢编辑器
     *
     * @param p
     * @param di
     * @return
     */
    public static DungeonManager setDungeonManager(Player p, DungeonInfo di) {
        var dm = GetDMbyPlayer(p);
        if (dm != null) {
            dm.currentDungeon = di;
            dm.currentRoom = di.getRoomInfos().values().stream().findFirst().orElse(null);
            return dm;
        }

        dm = new DungeonManager();
        dm.player = p;
        dm.currentDungeon = di;
        dm.currentRoom = di.getRoomInfos().values().stream().findFirst().orElse(null);
        dm.currentIndex = 0;
        dmList.add(dm);
        return dm;
    }


    // 使用JsonIO的函数加载所有json文件并解析为DungeonInfo，加入diList，（可能功能：填充空房间列表），加载世界
//    public static void LoadDungeons() {
//        diList = new ArrayList<DungeonInfo>();
//        File dir = new File(Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/Dungeon/");
//        if (!dir.exists()) dir.mkdirs();
//        //提取文件名
//        var names = Arrays.asList(dir.list()).stream().filter(s -> s.endsWith(".json"))
//                .map(s -> s = s.substring(0, s.length() - 5)).collect(Collectors.toList());
//        Main.getInstance().getLogger().info("读取地牢数据数量" + names.size());
//        names.forEach(n -> {
//            try {
//                Main.getInstance().getLogger().info("读取地牢数据 " + n);
//                LoadDungeon(n);
//            } catch (Exception e) {
//                Main.getInstance().getLogger().info("读取地牢数据出错！ ");
//                e.printStackTrace();
//            }
//        });
//    }

//    public static void LoadDungeon(String id) {
//        // 读取地牢json、房间json、合并
//        DungeonInfo di;
//        try {
//            var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Dungeon/" + id + ".json");
//            if (!Files.exists(path)) return;
//            var dataString = Files.readString(path);
//            Main.getInstance().getLogger().info(("loadDungeon dataString:" + dataString));
//            di = JSON.parseObject(dataString, DungeonInfo.class);
//            Main.getInstance().getLogger().info(("loadDungeon object" + di));
//            Main.getInstance().getLogger().info(("loadDungeon id " + di.getId()));
//            diList.add(di);
//        } catch (Exception e) {
//            Main.getInstance().getLogger().info("读取地牢数据出错！ " + e.getMessage());
//        }
//
//    }

    /**
     * 设定DungeonManager地牢编辑器并将玩家传送至指定房间
     *
     * @param p
     * @param di
     * @return
     */
    public static DungeonManager setDungeonManager(Player p, DungeonInfo di, RoomInfo ri) {
        var dm = GetDMbyPlayer(p);
        //若DM已经存在
        if (dm != null) {
            dm.currentDungeon = di;
            dm.currentRoom = ri;
            dm.currentIndex = 0;
            dm.TeleportPlayerToRoom(di, ri);
            return dm;
        }
        //DM不存在，新建
        dm = new DungeonManager();
        dm.player = p;
        dm.currentDungeon = di;
        dm.currentRoom = ri;
        dm.currentIndex = 0;
        dm.TeleportPlayerToRoom(di, ri);
        dmList.add(dm);
        return dm;
    }

    /**
     * 返回对应玩家的DM
     *
     * @param p
     * @return
     */
    public static DungeonManager GetDMbyPlayer(Player p) {
        var dm = dmList.stream().filter(d -> d.player.getName() == p.getName()).toArray();
        if (dm.length > 0) {
            return (DungeonManager) dm[0];
        } else
            return null;
    }

    /**
     * 给出指定原点坐标和房间大小，填充房间四壁为屏障方块。
     *
     * @param di
     * @param pos
     */
    public static void FillDefaultRoom(DungeonInfo di, Vector pos) {
        var size = di.getRoomSize();
        var w = Bukkit.getWorld(di.getWorld());
        //minPos绝对位置

        var origin = di.getOrigin().clone().add(pos.clone().multiply(size));
        Main.getInstance().getLogger().info("房间源点位置" + pos.clone().multiply(size).toString());
        for (int z = 0; z < size.getBlockZ(); z++) {
            for (int y = 0; y < size.getBlockY(); y++) {
                for (int x = 0; x < size.getBlockX(); x++) {
                    int blockX = origin.getBlockX() + x;
                    int blockY = origin.getBlockY() + y;
                    int blockZ = origin.getBlockZ() + z;
                    if (x * y * z == 0 || x == size.getBlockX() - 1 || y == size.getBlockY() - 1 || z == size.getBlockZ() - 1) {
                        w.getBlockAt(blockX, blockY, blockZ).setType(Material.STONE);
                    } else {
                        w.getBlockAt(blockX, blockY, blockZ).setType(Material.AIR);
                    }
                }
            }
        }
    }

    /**
     * 新建房间并传送玩家，设定DM
     *
     * @param p
     * @param dungeon
     * @param id
     * @return
     */
    public static RoomInfo newRoomInfo(Player p, DungeonInfo dungeon, String id) {
        var point = GetEmptyRoom(dungeon);
        Main.getInstance().getLogger().info("取得空房间" + point.toString());
        if (point == null) return null;
        RoomInfo newRoom = new RoomInfo(dungeon.getId(), id, new ArrayList<String>(),
                new Vector(1, 1, 1), point);
        dungeon.getEmptyRoomList().remove(point);
        dungeon.getRoomInfos().put(id, newRoom);
        dungeon.getRoomUnits().put(point, id);
        FillDefaultRoom(dungeon, point);
        //保存文件
        try {
            DungeonManager.WriteDungeonFile(dungeon);
        } catch (IOException e) {
            p.sendMessage("保存地牢 " + dungeon.getId() + ".json 文件时出错！");
            e.printStackTrace();
        }
        // 为该玩家设定DM
        var dm = DungeonManager.setDungeonManager(p, dungeon, newRoom);
        return newRoom;
    }

    public static List<DungeonManager> getDmList() {
        return dmList;
    }

    public static List<DungeonInfo> getDIList() {
        return diList;
    }

    /**
     * 复制房间
     *
     * @param dm
     * @param count count为-1时为更新房间
     */
    public static void CloneRoom(DungeonManager dm, int count) {
        var p = dm.player;
        var ri = dm.currentRoom;
        var di = dm.currentDungeon;
        var vBegin = DungeonAPI.GetPoint(ri, 0,
                new Vector()).toBlockVector();
        var vEnd = DungeonAPI.GetPoint(ri, 0,
                di.getRoomSize());
        p.sendMessage(count == -1 ? "准备更新 " + (ri.getRooms().size() - 1) + " 个房间" :
                "准备复制 " + count + " 个房间");
        // USE WEAPI
        var v3Begin = BlockVector3.at(vBegin.getBlockX(), vBegin.getBlockY(), vBegin.getBlockZ());
        var v3End = BlockVector3.at(vEnd.getBlockX() - 1, vEnd.getBlockY() - 1, vEnd.getBlockZ() - 1);
        CuboidRegion region = new CuboidRegion(v3Begin, v3End);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(p.getWorld()))) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            try {
                forwardExtentCopy.setCopyingEntities(false);
                Operations.complete(forwardExtentCopy);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }
        p.sendMessage("房间数据进入剪贴版，开始执行");


        if (count == -1) {
            for (int i = 1; i < ri.getRooms().size(); i++) {
                var des = DungeonAPI.GetPoint(dm.currentRoom,
                        i, new Vector());
                p.sendMessage(MessageFormat.format("更新房间序号{0}，位置{1}", i, des.toString()));

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(p.getWorld()))) {
                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(des.getBlockX(), des.getBlockY(), des.getBlockZ())).build();
                    try {
                        Operations.complete(operation);
                    } catch (WorldEditException e) {
                        e.printStackTrace();
                    }
                    p.sendMessage("更新完毕");
                }
            }
            p.sendMessage("全部更新完毕");
        } else {
            for (int i = 0; i < count; i++) {
                var emptyRoomVector = DungeonManager.GetEmptyRoom(di);
                if (emptyRoomVector == null){
                    p.sendMessage("当前地牢已经没有空位，无法继续复制！");
                    return;
                }
                var des = di.getOrigin().clone().add(di.getRoomSize().clone().multiply(emptyRoomVector));
                p.sendMessage(MessageFormat.format("向{0}复制{1}号房间", des.toString(), ri.getRooms().size()));
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(p.getWorld()))) {
                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                            .to(BlockVector3.at(des.getBlockX(), des.getBlockY(), des.getBlockZ())).build();
                    try {
                        Operations.complete(operation);
                        p.sendMessage("复制完毕");
                        di.getEmptyRoomList().remove(emptyRoomVector);
                        ri.getRooms().add(emptyRoomVector);
                        di.getRoomUnits().put(emptyRoomVector, ri.getId());
                    } catch (WorldEditException e) {
                        p.sendMessage("复制出现错误");
                        e.printStackTrace();
                    }
                }
            }

            dm.saveCurrentDungeon();
            p.sendMessage("全部复制完毕");
        }
        Bukkit.getWorld(dm.currentDungeon.getWorld()).save();
        p.sendMessage("世界已保存");


    }


    //
//    public static double[] GetPoint(DungeonInfo di, int[] roomPos, double[] roomPoint) {
//        var x = di.Origin[0] + di.RoomSize[0] * roomPos[0] + roomPoint[0];
//        var y = di.Origin[1] + di.RoomSize[1] * roomPos[1] + roomPoint[1];
//        var z = di.Origin[2] + di.RoomSize[2] * roomPos[2] + roomPoint[2];
//
//        return new double[]{x, y, z};
//    }

    /**
     * 写入地牢数据
     *
     * @param di
     * @throws IOException
     */
    public static void WriteDungeonFile(DungeonInfo di) throws IOException {
        var filename = di.getId();
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Dungeon/" + filename + ".json");
        // 如果不存在目录，创建
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        var sppf = new SimplePropertyPreFilter(Vector.class);
        sppf.getExcludes().add("blockX");
        sppf.getExcludes().add("blockY");
        sppf.getExcludes().add("blockZ");
        sppf.getExcludes().add("normalized");
        var data = JSON.toJSONString(di, sppf);
        if (Files.exists(path)) {
            Files.writeString(path, data);
        }
        Files.writeString(path, data, StandardOpenOption.CREATE);
    }

    /**
     * 删除地牢json文件
     *
     * @param id
     * @return
     */
    public static boolean DeleteDungeonFile(String id) {
        var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Dungeon/" + id + ".json");
        if (!Files.exists(path)) {
            return false;
        }
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 保存
    public boolean saveCurrentDungeon() {
        try {
            DungeonManager.WriteDungeonFile(
                    currentDungeon);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 传送玩家
     *
     * @param dungeon
     * @param room
     */
    public void TeleportPlayerToRoom(DungeonInfo dungeon, RoomInfo room) {
        TeleportPlayerToRoom(dungeon, room, 0);
    }

    /**
     * 传送玩家到指定房间
     *
     * @param dungeon
     * @param room
     * @param index
     */
    public void TeleportPlayerToRoom(DungeonInfo dungeon, RoomInfo room, int index) {
        if (index >= room.getRooms().size()) {
            return;
        }
        var p = player;
        var tpDes = DungeonAPI.GetPoint(room, index, room.getPlayerPosition());
        var world = Bukkit.getWorld(dungeon.getWorld());
        if (player.isOp()) {
            p.sendMessage(MessageFormat.format("准备传送至地牢：{0}， 房间Id：{1}， 序号：{2}",
                    dungeon.getId(), room.getId(), index));

            p.sendMessage(MessageFormat.format("y的地牢原点：{0}，房间规模 {1}， 房间位置{2}， 出生点{3}",
                    dungeon.getOrigin().toString(), dungeon.getRoomSize().toString(),
                    room.getRooms().get(index).toString(), room.getPlayerPosition().toString()));

        }
        if (p.teleport(new Location(world, tpDes.getBlockX() + 0.5, tpDes.getBlockY() + 0.5,
                tpDes.getBlockZ() + 0.5))) {
            p.sendMessage(MessageFormat.format("准备传送至：{0}",
                    tpDes.toString()));
            currentDungeon = dungeon;
            currentRoom = room;
            currentIndex = index;
            p.sendMessage("传送成功");
        } else {
            p.sendMessage("传送失败");
        }
    }

}
