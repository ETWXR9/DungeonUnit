package org.etwxr9.dungeonUnit.Command;

import java.util.Arrays;
import java.util.List;

import org.etwxr9.dungeonUnit.Dungeon.DungeonManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdNewRoom implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length != 2) {
            return false;
        }
        if (args[1] == "") {
            return false;
        }
        var p = (Player) sender;
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("请使用enterdungeon进入一个地牢");
            return true;
        }
        if (dm.currentDungeon == null || dm.currentRoom == null) {
            p.sendMessage("请使用enterdungeon进入一个地牢");
            return true;
        }
        if (dm.currentDungeon.getRoomInfos().keySet().stream().anyMatch(id -> id.equals(args[1]))) {
            p.sendMessage("已经存在同名房间");
            return true;
        }
        if (dm.currentDungeon.getEmptyRoomList().size() == 0){
            p.sendMessage("当前地牢已经没有空房间，无法新建房间！");
        }
        DungeonManager.newRoomInfo(p, dm.currentDungeon, args[1]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2) {
            return null;
        }
        return Arrays.asList("<roomid>");
    }

}
