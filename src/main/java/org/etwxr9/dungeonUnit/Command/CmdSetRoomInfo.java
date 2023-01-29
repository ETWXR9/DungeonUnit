package org.etwxr9.dungeonUnit.Command;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.etwxr9.dungeonUnit.Dungeon.DungeonManager;

import org.etwxr9.dungeonUnit.DungeonAPI;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

//设置当前房间的配置，敌人位置有set/unset/clear三种
public class CmdSetRoomInfo implements CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 2) {
            return false;
        }
        var p = (Player) sender;
        var dm = DungeonManager.GetDMbyPlayer(p);
        if (dm == null) {
            p.sendMessage("无当前地牢");
            return true;
        }
        if (dm.currentRoom == null) {
            p.sendMessage("无当前房间");
            return true;
        }

        var loc = p.getLocation();
        var roomOrigin = DungeonAPI.GetPoint(dm.currentRoom,
                dm.currentIndex, new Vector(0, 0, 0));
        //玩家相对位置
        var playerRelativePos = new Vector(loc.getBlockX() - roomOrigin.getBlockX(), loc.getBlockY() - roomOrigin.getBlockY(),
                loc.getBlockZ() - roomOrigin.getBlockZ());
        switch (args[1]) {
            case "playerPosition":
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
                    switch (args[2]) {
                        case "set":
                            // 判断玩家是否在房间内
                            if ((playerRelativePos.getBlockX() >= dm.currentDungeon.getRoomSize().getBlockX() || playerRelativePos.getBlockX() < 0)
                                    || (playerRelativePos.getBlockY() >= dm.currentDungeon.getRoomSize().getBlockY() || playerRelativePos.getBlockY() < 0)
                                    || (playerRelativePos.getBlockZ() >= dm.currentDungeon.getRoomSize().getBlockZ() || playerRelativePos.getBlockZ() < 0)) {
                                p.sendMessage("当前位置超出房间范围");
                                break;
                            }
                            if (dm.currentRoom.getPlayerPosition().equals(playerRelativePos)) {
                                p.sendMessage("该位置已经存在");
                            } else {
                                dm.currentRoom.setPlayerPosition(playerRelativePos);
                                p.sendMessage(MessageFormat.format("该位置已经设置：{0}", playerRelativePos.toString()));
                            }
                            break;

                        case "clear":
                            dm.currentRoom.setPlayerPosition(new Vector());
                            p.sendMessage("已将当前房间出生点重置");
                            break;
                    }
                }
                break;
            case "id":// 这里需要做重复检查+目录更改
                if (args.length != 3) {
                    return false;
                }
                var newId = args[2];
                if (dm.currentDungeon.GetRoomInfo(newId) != null) {
                    p.sendMessage("id已经存在！");
                    break;
                }
                dm.currentRoom.setId(newId);
                break;
            case "tags":
                if (args.length != 4) {
                    return false;
                }
                switch (args[2]) {
                    case "set":
                        if (dm.currentRoom.getTags().contains(args[3])) {
                            p.sendMessage("该Tag已经存在");
                        } else {
                            dm.currentRoom.getTags().add(args[3]);
                            p.sendMessage(MessageFormat.format("设置了Tag：{0}", args[3]));
                        }
                        break;

                    case "clear":
                        dm.currentRoom.getTags().clear();
                        p.sendMessage("已将当前房间所有Tags清除");
                        break;
                    case "unset":
                        if (!dm.currentRoom.getTags().contains(args[3])) {
                            p.sendMessage("该Tag不存在");
                        } else {
                            dm.currentRoom.getTags().remove(args[3]);
                            p.sendMessage(MessageFormat.format("清除了Tag：{0}", args[3]));
                        }
                        break;
                }
                break;
            case "specialPosition":
                if (args.length < 3) {
                    return false;
                }
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
                    switch (args[2]) {
                        case "set":
                            if (args.length != 4) {
                                p.sendMessage("请打出该点的名称");
                                return false;
                            }
                            if (args[3].equals("")) {
                                p.sendMessage("请打出该点的名称");
                                return false;
                            }
                            // 判断玩家是否在房间内
                            if ((playerRelativePos.getBlockX() >= dm.currentDungeon.getRoomSize().getBlockX() || playerRelativePos.getBlockX() < 0)
                                    || (playerRelativePos.getBlockY() >= dm.currentDungeon.getRoomSize().getBlockY() || playerRelativePos.getBlockY() < 0)
                                    || (playerRelativePos.getBlockZ() >= dm.currentDungeon.getRoomSize().getBlockZ() || playerRelativePos.getBlockZ() < 0)) {
                                p.sendMessage("当前位置超出房间范围");
                                break;
                            }
                            if (dm.currentRoom.getSpecialPositions().values().stream()
                                    .anyMatch(sp -> sp.equals(playerRelativePos))) {
                                p.sendMessage("该位置已经存在");
                            } else {
                                dm.currentRoom.getSpecialPositions().put(args[3], playerRelativePos);
                                p.sendMessage(MessageFormat.format("该位置已经设置：{0}:{1}",
                                        args[3], playerRelativePos.toString()));
                            }
                            break;

                        case "clear":
                            dm.currentRoom.getSpecialPositions().clear();
                            p.sendMessage("已将当前房间所有特殊点重置");
                            break;
                        case "unset":
                            var unsetPos = dm.currentRoom.getSpecialPositions().entrySet().stream()
                                    .filter(entry -> entry.getValue().equals(playerRelativePos)).findFirst();
                            if (unsetPos.isPresent()) {
                                dm.currentRoom.getSpecialPositions().remove(unsetPos.get().getKey());
                                p.sendMessage(MessageFormat.format("该特殊点已移除：{0}:{1}", unsetPos.get().getKey(),
                                        playerRelativePos.toString()));
                            }
                            break;
                    }
                }
                break;
            default:
                break;
        }
        dm.saveCurrentDungeon();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("playerPosition", "tags", "id", "specialPosition");
        } else if (args.length == 3) {
            List<String> items;
            switch (args[1]) {
                case "playerPosition":
                case "specialPosition":
                case "tags":
                    items = Arrays.asList("set", "unset", "clear");
                    return items;
                default:
                    break;
            }
        } else if (args.length == 4 && args[1].equals("specialPosition")) {
            return List.of("<posId>");

        }

        return null;

    }

}
