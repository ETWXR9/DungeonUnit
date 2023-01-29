package com.etwxr9.dungeonUnit.Command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.etwxr9.dungeonUnit.Dungeon.DungeonManager;
import com.etwxr9.dungeonUnit.Main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdDeleteWorld implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        if (args.length != 2)
            return false;
        var defalutWorld = Main.getInstance().getConfig().getString("DefaultWorld");
        if (defalutWorld == null) {
            p.sendMessage("默认世界未设置！在默认世界使用/rl setdefaultworld进行设置！");
            return true;
        }

        var id = args[1];
        var worldFolder = Main.getInstance().getServer().getWorld(id).getWorldFolder();

        // 传送玩家至默认世界
        List<Player> players = Main.getInstance().getServer().getWorld(id).getPlayers();
        players.forEach((e) -> e.teleport(Main.getInstance().getServer().getWorld(defalutWorld).getSpawnLocation()));
        // p.teleport(Main.getInstance().getServer().getWorld(defalutWorld).getSpawnLocation());
        // 卸载世界
        Main.getInstance().getServer().unloadWorld(id, false);
        // 删除
        if (!deleteWorld(worldFolder))
            p.sendMessage("删除世界过程出错！");

        // var path = new File(Main.getInstance().getDataFolder().getAbsolutePath() +
        // "/Dungeon/" + id + ".json");
        if (DungeonManager.DeleteDungeonFile(id)) {
            p.sendMessage("地牢文件已经删除！");
        } else {
            p.sendMessage("地牢文件删除失败！");
        }

        p.sendMessage("删除结束");
        return true;
    }

    private boolean deleteWorld(File path) {
        if (path.exists()) {
            File files[] = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 2)
            return null;
        var worldNameList = new ArrayList<String>();
        Main.getInstance().getServer().getWorlds().forEach(w -> worldNameList.add(w.getName()));
        return worldNameList;
    }

}
