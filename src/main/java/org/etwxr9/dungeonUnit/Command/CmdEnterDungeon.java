package org.etwxr9.dungeonUnit.Command;

import java.util.ArrayList;
import java.util.List;

import org.etwxr9.dungeonUnit.Dungeon.DungeonManager;

import org.etwxr9.dungeonUnit.DungeonAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//玩家进入指定地牢，默认进入序号为0的房间
public class CmdEnterDungeon implements CommandInterface {

    //enterDungeon di ri roomIndex
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        if (args.length < 2) {
            return false;
        }
        var di = DungeonAPI.GetDungeonInfo(args[1]);
        var dm = DungeonManager.GetDMbyPlayer(p);

        if (di == null) {
            p.sendMessage("地牢 " + args[1] + " 不存在");
            return true;
        }
        p.sendMessage("准备进入地牢" + args[1] + " " + di);
//        if (dm == null) {
//            dm = DungeonManager.setDungeonManager(p, di);
//        }
        //如果没有指明房间序号，则直接传送到第一个房间副本
        if (args.length == 2) {
            DungeonManager.setDungeonManager(p,di );
//            dm.TeleportPlayerToRoom(dm.currentDungeon, dm.currentRoom);
        } else if (args.length == 3) {
            var ri = di.GetRoomInfo(args[2]);
            if (ri == null) {
                p.sendMessage("指定房间 " + args[2] + " 不存在");
                return true;
            }
            DungeonManager.setDungeonManager(p, di,ri);
//            dm.TeleportPlayerToRoom(di, ri);
        }
//        else if (args.length == 4) {
//            var ri = di.GetRoomInfo(args[2]);
//            if (ri == null) {
//                p.sendMessage("指定房间 " + args[2] + " 不存在");
//                return true;
//            }
//            int index;
//            try {
//                index = Integer.parseInt(args[3]);
//            } catch (Exception e) {
//                return false;
//            }
//            if (ri.getRooms().size() <= index) {
//                p.sendMessage("该房间只有 " + ri.getRooms().size() + " 个副本");
//                return true;
//            }
//            dm.TeleportPlayerToRoom(di, ri, index);
//            return true;
//        }
        else {
            p.sendMessage("指定地牢不存在");
            return true;

        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            var dis = DungeonManager.getDIList();
            // sender.sendMessage("tab enterdungeon dis is null " + (dis == null));
            // sender.sendMessage("tab enterdungeon " + dis.size());
            // sender.sendMessage("tab enterdungeon " + dis.get(0));
            // sender.sendMessage("tab enterdungeon " + dis.get(0).World);
            var names = new ArrayList<String>();
            dis.forEach(d -> names.add(d.getId()));
            return names;
        } else if (args.length == 3) {
            var di = DungeonAPI.GetDungeonInfo(args[1]);
            if (di != null) {
                var names = new ArrayList<String>();
                di.getRoomInfos().forEach((s, roomInfo) -> names.add(roomInfo.getId()));
                return names;
            }
        }
//        else if (args.length == 4) {
//            var _di = DungeonAPI.GetDungeonInfo(args[1]);
//            if (_di != null) {
//                var ri = _di.GetRoomInfo(args[2]);
//                // Main.getInstance().getLogger().info("enterdungeon参数四:"+ri.Id);
//                if (ri != null) {
//                    return Arrays.asList(Integer.toString(ri.getRooms().size()));
//                }
//            }
//
//        }
        return null;
    }

}
