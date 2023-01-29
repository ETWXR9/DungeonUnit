package org.etwxr9.dungeonUnit;

import com.alibaba.fastjson2.JSON;
import org.etwxr9.dungeonUnit.Dungeon.DungeonInfo;
import org.etwxr9.dungeonUnit.Dungeon.DungeonManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.etwxr9.dungeonUnit.Command.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;


public class Main extends JavaPlugin {

    // 获取单例
    private static Main i;

    public static Main getInstance() {
        return i;
    }

    public CommandHandler cmdHandler;

    @Override
    public void onEnable() {
        i = this;
        // 管理配置文件
        saveDefaultConfig();
        //加载DungeonInfo
        File diDir = new File(Main.getInstance().getDataFolder().getAbsolutePath().toString() + "/Dungeon/");
        if (!diDir.exists()) diDir.mkdirs();
        //取得json路径
        var diPathList = Arrays.asList(diDir.listFiles()).stream().filter(s -> s.getAbsolutePath().endsWith(".json")).collect(Collectors.toList());
        Main.getInstance().getLogger().info("读取地牢数据数量" + diPathList.size());
        //读取json内容并转换为DungeonInfo对象，存入DiList
        diPathList.forEach(f -> {
            try {
                var p = f.getAbsolutePath();
                Main.getInstance().getLogger().info("读取地牢数据 " + p);
                var dataString = Files.readString(Path.of(p));
                Main.getInstance().getLogger().info(("loadDungeon dataString:" + dataString));
                var di = JSON.parseObject(dataString, DungeonInfo.class);
                DungeonManager.getDIList().add(di);
            } catch (Exception e) {
                Main.getInstance().getLogger().info("读取地牢数据出错！ ");
                e.printStackTrace();
            }
        });
        // 注册指令
        cmdHandler = new CommandHandler();
        cmdHandler.register("du", new BaseCmd());
        cmdHandler.register("createDungeon", new CmdCreateDungeon());
        cmdHandler.register("deleteWorld", new CmdDeleteWorld());
        cmdHandler.register("deleteRoom", new CmdDeleteRoom());
        cmdHandler.register("enterDungeon", new CmdEnterDungeon());
        cmdHandler.register("dungeonInfo", new CmdDungeonInfo());
        cmdHandler.register("roomInfo", new CmdRoomInfo());
        cmdHandler.register("setRoomInfo", new CmdSetRoomInfo());
        cmdHandler.register("newRoom", new CmdNewRoom());
        cmdHandler.register("copyRoom", new CmdCopyRoom());
        cmdHandler.register("updateRoom", new CmdUpdateRoom());
        this.getCommand("dfmc").setExecutor(cmdHandler);
        this.getCommand("dfmc").setTabCompleter(new BaseTabCompleter());
        // 如果没有配置目录，创建。
        if (!Files.exists(Paths.get(getDataFolder() + "/"))) {
            try {
                Files.createDirectory(Paths.get(getDataFolder() + "/"));
            } catch (Exception e) {
                getLogger().info("插件dfmc创建配置目录出错");
            }
        }
    }

    @Override
    public void onDisable() {
    }
}