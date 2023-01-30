package org.etwxr9.dungeonUnit.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.etwxr9.dungeonUnit.Dungeon.DungeonManager;
import org.etwxr9.dungeonUnit.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdDeleteDungeon implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        if (args.length != 2)
            return false;
        var id = args[1];
        if (DungeonManager.DeleteDungeonFile(id)) {
            p.sendMessage("地牢文件已经删除！");
        } else {
            p.sendMessage("地牢文件删除失败！");
        }

        p.sendMessage("删除结束");
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2)
            return null;
        var names = new ArrayList<String>();
        DungeonManager.getDIList().forEach(d -> names.add(d.getId()));
        return names;
    }

}
