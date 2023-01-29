package com.etwxr9.dungeonUnit.Command;

import com.etwxr9.dungeonUnit.Dungeon.DungeonInfo;
import com.etwxr9.dungeonUnit.Dungeon.DungeonManager;
import com.etwxr9.dungeonUnit.DungeonAPI;
import com.etwxr9.dungeonUnit.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CmdCreateDungeon implements CommandInterface {

    // /rl createdungeon <id> <dungeon x> <y> <z> <room x> <y> <z> <tags...>
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 8)
            return false;

        Player p = (Player) sender;
        // 判断参数格式
        String id;
        var loc = p.getLocation();
        var ori = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        var dungeonSize = new Vector();
        var roomSize = new Vector();
        var tags = new ArrayList<String>();
        try {
            id = args[1];
            dungeonSize.setX(Integer.parseInt(args[2]));
            dungeonSize.setY(Integer.parseInt(args[3]));
            dungeonSize.setZ(Integer.parseInt(args[4]));
            roomSize.setX(Integer.parseInt(args[5]));
            roomSize.setY(Integer.parseInt(args[6]));
            roomSize.setZ(Integer.parseInt(args[7]));
            if (args.length > 8) {
                for (int i = 8; i < args.length; i++) {
                    tags.add(args[i]);
                }
            }
        } catch (Exception e) {
            p.sendMessage("参数格式错误");
            return false;
        }
        // 检查size超限
        if (ori.getBlockY() + dungeonSize.getBlockY() * roomSize.getBlockX() > 319) {
            p.sendMessage("最大高度超过319");
            return true;
        }

        //TODO 这里之后添加判断地牢重叠
        if (DungeonAPI.GetDungeonInfo(id) != null) {
            p.sendMessage("已经存在id为 " + id + " 的地牢！");
        }
        // 创建配置

            var path = Paths.get(Main.getInstance().getDataFolder().getAbsolutePath() + "/Dungeon/" + id + ".json");
            // 如果已经存在，返回false
            if (Files.exists(path)) {
                p.sendMessage("id为" + id + "的地牢已经存在");
                return true;
            }
            var dungeonInfo = new DungeonInfo(id, p.getWorld().getName(),
                    tags, ori, dungeonSize, roomSize,  new HashMap<>());
            p.sendMessage("创建成功!");
            DungeonManager.getDIList().add(dungeonInfo);
            // 在源点创建一个新房间
            // 传送玩家,设定当前房间
            DungeonManager.newRoomInfo(p, DungeonAPI.GetDungeonInfo(id), "default");
        try {
            DungeonManager.WriteDungeonFile(dungeonInfo);
        } catch (Exception e) {
            p.sendMessage("写入文件出错： " + e.getMessage());
            e.printStackTrace();
            return true;
        }
//        p.sendMessage("开始读取刚创建的文件！");
//        DungeonManager.LoadDungeon(id);
        return true;
    }

    // /rl createdungeon <id> <dungeon x> <y> <z> <room x> <y> <z> <tags...>
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 0:
                return null;
            case 1:
                return null;
            case 2:
                return Arrays.asList("<id>");
            case 3:
                return Arrays.asList("<dungeon x>");
            case 4:
                return Arrays.asList("<dungeon y>");
            case 5:
                return Arrays.asList("<dungeon z>");
            case 6:
                return Arrays.asList("<room x>");
            case 7:
                return Arrays.asList("<room y>");
            case 8:
                return Arrays.asList("<room z>");
            default:
                return Arrays.asList("<tags...>");
        }
    }

}
