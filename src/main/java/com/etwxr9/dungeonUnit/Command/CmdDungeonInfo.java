package com.etwxr9.dungeonUnit.Command;

import com.etwxr9.dungeonUnit.Dungeon.DungeonManager;
import com.etwxr9.dungeonUnit.DungeonAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

//查看指定名称的地牢信息，或者指定Id的房间信息
public class CmdDungeonInfo implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 2) {
            var di = DungeonAPI.GetDungeonInfo(args[1]);
            Player p = (Player) sender;
            if (di == null) {
                p.sendMessage(MessageFormat.format("不存在地牢：{0}", args[1]));
                return true;
            }
            p.sendMessage(MessageFormat.format("查看地牢信息：{0}", di.getId()));
            p.sendMessage(MessageFormat.format("原点坐标：{0}", di.getOrigin().toString()));
            p.sendMessage(MessageFormat.format("地牢大小：{0}", di.getSize().toString()));
            p.sendMessage(MessageFormat.format("单元大小：{0}", di.getRoomSize().toString()));
            p.sendMessage(MessageFormat.format("房间数量：{0}", di.getRoomUnits().size()));
            p.sendMessage(MessageFormat.format("空位数量：{0}", di.getEmptyRoomList().size()));
            di.getRoomInfos().forEach((s, ri) -> p.sendMessage(MessageFormat.format("房间名：{0}，房间数量：{1}",
                    ri.getId(), ri.getRooms().size())));
            return true;
        } else if (args.length == 3) {
            var di = DungeonAPI.GetDungeonInfo(args[1]);
            Player p = (Player) sender;
            if (di == null) {
                p.sendMessage(MessageFormat.format("不存在地牢：{0}", args[1]));
                return true;
            }
            var ri = di.GetRoomInfo(args[2]);
            if (ri == null) {
                p.sendMessage(MessageFormat.format("不存在房间：{0}", args[2]));
                return true;
            }
            p.sendMessage(MessageFormat.format("查看房间信息：{0}", ri.getId()));
            p.sendMessage(MessageFormat.format("玩家传送点：{0}", ri.getPlayerPosition().toString()));
            p.sendMessage(MessageFormat.format("房间副本数量：{0}", ri.getRooms().size()));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            var names = new ArrayList<String>();
            DungeonManager.getDIList().forEach(d -> names.add(d.getId()));
            return names;
        } else if (args.length == 3) {
            var di = DungeonAPI.GetDungeonInfo(args[1]);
            if (di == null) {
                return null;
            }
            var names = new ArrayList<String>();
            di.getRoomInfos().forEach((s, roomInfo) -> names.add(s));
            return names;
        }
        return null;

    }

}
