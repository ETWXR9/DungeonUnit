package org.etwxr9.dungeonUnit.Command;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.etwxr9.dungeonUnit.DungeonAPI;
import org.etwxr9.dungeonUnit.Main;
import org.etwxr9.dungeonUnit.Dungeon.DungeonManager;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

//查看当前地牢当前房间信息
public class CmdRoomInfo implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player p = (Player) sender;
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("请进入一个房间");
            return true;
        }
//        if (dm.currentIndex == null) {
//            p.sendMessage("请进入一个房间");
//            return true;
//        }
        var di = dm.currentDungeon;
        var ri = dm.currentRoom;

        p.sendMessage(MessageFormat.format("§b查看房间信息：所属地牢：{0}， Id：{1}, 序号：{2}", di.getId(), ri.getId(),
                ri.getRooms().indexOf(dm.currentIndex)));
        p.sendMessage(MessageFormat.format("单元大小：{0}", di.getRoomSize().toString()));
        p.sendMessage(MessageFormat.format("房间副本数量：{0}", ri.getRooms().size()));
        p.sendMessage("房间Tag：");
        ri.getTags().forEach(t -> {
            p.sendMessage("    " + t);
        });
        p.sendMessage("特殊点：");
        ri.getSpecialPositions().forEach((k, v) -> {
            p.sendMessage("    " + k.toString() + ":" + v);
        });
        p.sendMessage(MessageFormat.format("玩家传送点：{0}", ri.getPlayerPosition().toString()));
        p.sendMessage("§b房间信息打印完毕");
        //特殊点粒子显示 蓝色是特殊点 红色是出生点
        new BukkitRunnable() {
            private int count = 20;

            @Override
            public void run() {
                count--;

                ri.getSpecialPositions().forEach((k, v) -> {
                    var pos = DungeonAPI.GetPoint(ri, ri.getRooms().indexOf(dm.currentIndex), v);
                    p.getWorld().spawnParticle(Particle.REDSTONE,
                            new Location(p.getWorld(), pos.getBlockX() + 0.5, pos.getBlockY() + 0.5, pos.getBlockZ() + 0.5),
                            10, 0.1, 0.1, 0.1,
                            new DustOptions(Color.AQUA, 1));
                });
                var pos = DungeonAPI.GetPoint(ri, ri.getRooms().indexOf(dm.currentIndex), ri.getPlayerPosition());
                p.getWorld().spawnParticle(Particle.REDSTONE,
                        new Location(p.getWorld(), pos.getBlockX() + 0.5, pos.getBlockY() + 0.5, pos.getBlockZ() + 0.5),
                        10, 0.1, 0.1, 0.1,
                        new DustOptions(Color.RED, 1));
                if (count == 0) {
                    cancel();
                }
            }

        }.runTaskTimer(Main.getPlugin(Main.class), 0, 10);
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