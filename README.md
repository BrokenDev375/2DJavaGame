# 2D Java Game

2D Java Game là một game action-RPG 2D được xây dựng bằng Java Swing cho đồ án môn Lập trình hướng đối tượng. Phiên bản gốc được thực hiện theo nhóm 3 người. Sau đó, mình tiếp tục phát triển một mình trên nhánh/fork này để refactor code theo hướng OOP sạch hơn, tách trách nhiệm rõ hơn và bổ sung thêm các hệ thống gameplay.

## Mục Tiêu Dự Án

- Áp dụng các nguyên lý OOP vào một game 2D có nhiều hệ thống tương tác.
- Tách dần các lớp lớn thành các module nhỏ hơn, dễ đọc và dễ kiểm thử.
- Xây dựng gameplay loop kiểu RPG: khám phá, chiến đấu, nhận thưởng, lên cấp, đổi vũ khí và lưu tiến trình.
- Giữ project chạy được bằng Java thuần, không phụ thuộc game engine ngoài.

## Tính Năng Chính

- Bản đồ tile/chunk load động.
- Player di chuyển 4 hướng, va chạm với tile, object, NPC và monster.
- Combat realtime với windup, active, recover, cooldown, knockback và invulnerability frame.
- Monster AI gồm wander, chase và aggro switch.
- Nhiều loại monster: Slime, Red Slime, Bat, Orc, Skeleton Lord.
- Boss có phase enraged khi HP xuống thấp.
- Hệ thống level/EXP và chỉ số HP/ATK/DEF.
- Vũ khí có timing, hitbox và damage multiplier riêng.
- Loot drop, potion, key, portal, door và weapon pickup.
- Dialogue với NPC.
- UI cho main menu, pause, game over, HP, monster HP, EXP và message.
- Save/load JSON bằng Gson.
- Smoke test Java thuần cho các core contract quan trọng.

## Điều Khiển

| Phím | Chức năng |
| --- | --- |
| `W A S D` | Di chuyển |
| `J` | Tấn công |
| `E` | Nói chuyện / chuyển dialogue |
| `F` | Nhặt item / tương tác object |
| `F5` | Lưu game |
| `ESC` | Mở pause menu |
| `Enter` | Chọn trong menu |
| `Arrow Up/Down/Left/Right` | Điều hướng menu |

## Save / Load

File save mặc định nằm tại:

```text
saves/savegame.json
```

Save hiện lưu:

- vị trí player;
- map hiện tại;
- HP / max HP;
- level / EXP;
- vũ khí đang trang bị;
- số key đã nhặt;
- trạng thái object còn tồn tại trong world;
- trạng thái monster theo spawn slot, bao gồm monster đã chết hoặc còn sống.

Load Game có thể dùng từ main menu. Nếu file save bị hỏng JSON, hãy xóa `saves/savegame.json` rồi save lại từ trong game.

## Cấu Trúc Dự Án

```text
src/
  ai/movement/          AI movement strategy: wander, chase, aggro switch
  combat/               Attack phase, hit resolution, damage formula
  entity/               Entity core: placement, size, stats, sprite, combat state
  entity_manager/       Quản lý player, monster, NPC, object spawn/restore
  game_data/            Save/load DTO, mapper, repository, restorer
  input_manager/        Keyboard input và command theo game state
  interact_manager/     Tương tác NPC/object/item/weapon/portal/door
  main/                 GamePanel, game loop, renderer, config, entry point
  monster_data/         Monster, factory, loot, death result, từng loại quái
  npc_data/             NPC và dialogue contract
  object_data/          WorldObject, item, weapon, portal, door, spawn plan
  player_manager/       Player, movement, combat input, progression
  sound_manager/        Music và sound effect
  tile/                 Chunk/tile loading và map query
  ui/                   HUD, menu, pause, dialogue, message, health bar

resource/               Ảnh, map, âm thanh và asset runtime
libraries/              Thư viện ngoài, hiện có Gson
test/                   Smoke test Java thuần
scripts/                Script compile/test nhanh
docs/                   Checklist test thủ công
```

## Yêu Cầu Môi Trường

- JDK 24 theo cấu hình NetBeans hiện tại trong `nbproject/project.properties`.
- Gson `libraries/gson-2.10.1.jar`.
- NetBeans hoặc IntelliJ IDEA đều chạy được nếu classpath có Gson và resource folder.

## Cách Chạy Bằng NetBeans

1. Mở NetBeans.
2. Chọn `File -> Open Project`.
3. Chọn thư mục project này.
4. Kiểm tra thư viện Gson:
   - chuột phải project;
   - chọn `Properties`;
   - vào `Libraries`;
   - thêm `libraries/gson-2.10.1.jar` nếu IDE chưa nhận.
5. Run project. Main class là:

```text
main.Main
```

## Cách Chạy Bằng PowerShell

Compile:

```powershell
$classes = "out\classes"
New-Item -ItemType Directory -Force -Path $classes | Out-Null
$sources = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -cp "libraries\gson-2.10.1.jar" -d $classes $sources
```

Run:

```powershell
java -cp "out\classes;libraries\gson-2.10.1.jar;resource" main.Main
```

## Build Bằng Ant

Project có cấu hình NetBeans/Ant:

```powershell
ant clean jar
```

Sau khi build, JAR nằm ở:

```text
dist/2DJavaGame.jar
```

## Chạy Smoke Test

Smoke test compile toàn bộ `src` và `test`, sau đó chạy các test Java thuần:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1
```

Các test hiện kiểm tra những phần như:

- config kích thước game;
- direction, collision geometry;
- entity stats, damage formula, knockback, invulnerability;
- object/monster type lookup;
- weapon factory lookup;
- player progression;
- loot drop policy;
- save repository và save manager;
- asset loader missing asset contract.

## Ghi Chú Refactor

Nhánh này đã được refactor lại nhiều phần so với bản đồ án ban đầu:

- giảm public mutable field;
- tách `GamePanel` dần về vai trò composition root;
- tách combat, movement, rendering, save/load, object interaction thành các class nhỏ hơn;
- dùng interface/boundary nhỏ hơn cho world query, collision và render context;
- thêm smoke test để giữ behavior ổn định khi refactor tiếp;
- điều chỉnh gameplay RPG: weapon damage, damage mitigation, monster timing, safe respawn và save progress.

## Hướng Phát Triển Tiếp

- Tách inventory/equipment đầy đủ thay cho `currentWeapon` và `keyCount`.
- Thêm quest state machine và quest log.
- Đưa monster/weapon/drop/spawn config sang data file để balance nhanh hơn.
- Thêm UI character sheet/inventory.
- Mở rộng save/load cho quest progress và world flags nâng cao.
