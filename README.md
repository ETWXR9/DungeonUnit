# DungeonUnit-地牢API
## 简介
为了在mc中支持多个实例空间，以实现网络游戏中常见的“副本功能”或roguelike等游戏形式，这个插件通过创建指定大小的“房间阵列”，并提供api来检索副本、传送玩家进出副本，来实现类似的功能。

该插件提供一系列指令，可以创建和编辑地牢，不涉及游戏逻辑实现。

该插件提供API类`org.etwxr9.dungeonUnit.DungeonAPI`，用于开发具体的游戏玩法，不涉及地牢的编辑。

通过指令，插件可以在世界中创建一个指定大小的坐标系，每个坐标是一个指定尺寸的房间，该房间可以是任意样式。再通过指令，可以将房间复制多份，或者将房间的更改应用到这些副本之中。

插件会记录所有坐标系和房间的信息，并提供检索、传送的功能。

## 指令
* 主指令/du
  * `/du createdungeon <id> <dungeon x> <y> <z> <room x> <y> <z> <tags...>`创建一个地牢坐标系 第一个坐标为地牢尺寸，以房间为单位，第二个坐标为房间尺寸，以方块为单位。标签以空格分隔，可以输入多个。创建后会新建默认房间default，并自动进入该房间的编辑模式。
  * `/du enterDungeon id` 进入指定地牢的指定房间的编辑模式。
  * `/du newRoom <id>` 在当前地牢中创建新的房间并进入该房间编辑模式，默认为石头盒子，玩家传送点相对坐标1,1,1
  * `/du dungeonInfo <dungeonid> <roomid>`打印指定地牢和指定房间的信息。
  * `/du roomInfo` 查看当前正在编辑的房间的信息。并生成粒子指示位置。红色粒子为玩家传送点，蓝色为特殊点。
  * `/du copyRoom <count>` 复制当前房间指定数量，tab提示为当前已经存在的数量。
  * `/du updateRoom` 将当前房间的改动应用到该房间的所有副本中，注意，通过指令进入房间时，默认只进入第0个房间，更新房间时，也是以0号房间为蓝本进行复制。
  * `/du setRoomInfo <...>` 重新设置当前房间的属性，tab提示可以选择不同的属性，位置属性会输入当前玩家位置。
  * `/du deleteRoom <count> `删除指定数量的当前房间副本，删多了或者参数为all，则删除整个房间。注意，删除房间并不会实际清空房间区域，只是标记其为空房间，在创建房间时，新的房间会覆盖这片区域。
  * `/du deleteDungeon <id>` 删除指定地牢，但不会在世界中实际清空房间。

## API

### getWorldDI

public static [List](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/List.html "java.util中的类或接口")<org.etwxr9.dungeonUnit.Dungeon.DungeonInfo> getWorldDI([String](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html "java.lang中的类或接口") worldName)

返回一个世界内的所有DungeonInfo

参数:

\`worldName\` -

返回:

*

### GetDungeonInfo

public static org.etwxr9.dungeonUnit.Dungeon.DungeonInfo GetDungeonInfo([String](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html "java.lang中的类或接口") dungeonId)

返回指定DungeonInfo，没有则返回null

参数:

\`dungeonId\` -

返回:

*

### GetRoomInfo

public static org.etwxr9.dungeonUnit.Dungeon.RoomInfo GetRoomInfo([String](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html "java.lang中的类或接口") dungeonId, [String](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html "java.lang中的类或接口") roomId)

返回指定DungeonInfo的指定RoomInfo，没有则返回null

参数:

\`dungeonId\` -

\`roomId\` -

返回:

*

### GetRoomInfo

public static org.etwxr9.dungeonUnit.Dungeon.RoomInfo GetRoomInfo(org.etwxr9.dungeonUnit.Dungeon.DungeonInfo di, [String](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/String.html "java.lang中的类或接口") roomId)

*

### getRoomInstanceAmount

public static int getRoomInstanceAmount(org.etwxr9.dungeonUnit.Dungeon.RoomInfo ri)

取得指定房间的副本数量

参数:

\`ri\` -

返回:

*

### getRoomPos

public static org.bukkit.util.Vector getRoomPos(org.etwxr9.dungeonUnit.Dungeon.RoomInfo ri, int index)

返回指定房间的地牢系坐标，指定序号副本的地牢系坐标，如果序号溢出则返回null

参数:

\`ri\` -

\`index\` -

返回:

*

### tpPlayer

public static void tpPlayer(org.etwxr9.dungeonUnit.Dungeon.DungeonInfo dungeon, org.bukkit.entity.Player p, org.etwxr9.dungeonUnit.Dungeon.RoomInfo room, int index)

将玩家传送到指定的房间,传送相对位置是RoomInfo.playerPosition

参数:

\`dungeon\` -

\`p\` -

\`room\` -

\`index\` -

*

### GetPoint

public static org.bukkit.util.Vector GetPoint(org.etwxr9.dungeonUnit.Dungeon.RoomInfo ri, int index, org.bukkit.util.Vector roomPoint)

返回指定房间副本内指定点的坐标，如果序号溢出则返回null

参数:

\`ri\` -

\`index\` - 该房间副本的序号

\`roomPoint\` - 房间内相对坐标

返回:</section>