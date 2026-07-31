# 2D Java Game

2D Java Game is a 2D action RPG built with Java Swing for an Object-Oriented Programming course project. The original version was created by a team of three students. This branch/fork continues the project as a solo refactor focused on cleaner OOP design, clearer responsibility boundaries, and expanded RPG gameplay systems.

## Project Goals

- Apply object-oriented design principles to a playable 2D game with multiple interacting systems.
- Gradually split large classes into smaller, easier-to-read and easier-to-test modules.
- Build an RPG-style gameplay loop: exploration, combat, rewards, leveling, weapon switching, and progress saving.
- Keep the project running with plain Java, without relying on an external game engine.

## Key Features

- Dynamic tile/chunk-based map loading.
- Four-direction player movement with collision against tiles, objects, NPCs, and monsters.
- Realtime combat with windup, active, recover, cooldown, knockback, and invulnerability frames.
- Monster AI with wander, chase, and aggro-switch behaviors.
- Multiple monster types: Slime, Red Slime, Bat, Orc, and Skeleton Lord.
- Boss behavior with an enraged phase at low HP.
- Level, EXP, HP, ATK, and DEF progression.
- Weapons with individual timing, hitboxes, and damage multipliers.
- Loot drops, potions, keys, portals, doors, and weapon pickups.
- NPC dialogue.
- UI for main menu, pause menu, game over, HP, monster HP, EXP, and messages.
- JSON save/load using Gson.
- Plain Java smoke tests for important core contracts.

## Controls

| Key | Action |
| --- | --- |
| `W A S D` | Move |
| `J` | Attack |
| `E` | Talk / advance dialogue |
| `F` | Pick up item / interact with object |
| `F5` | Save game |
| `ESC` | Open pause menu |
| `Enter` | Select menu option |
| `Arrow Up/Down/Left/Right` | Navigate menu |

## Save / Load

The default save file is located at:

```text
saves/savegame.json
```

The save system currently stores:

- player position;
- current map;
- HP and max HP;
- level and EXP;
- equipped weapon;
- collected key count;
- remaining world objects;
- monster state by spawn slot, including whether each monster is alive or dead.

Load Game is available from the main menu. If the save file becomes corrupted, delete `saves/savegame.json` and create a new save from inside the game.

## Project Structure

```text
src/
  ai/movement/          AI movement strategies: wander, chase, aggro switch
  combat/               Attack phases, hit resolution, damage formula
  entity/               Entity core: placement, size, stats, sprites, combat state
  entity_manager/       Player, monster, NPC, and object spawn/restore management
  game_data/            Save/load DTOs, mapper, repository, and restorer
  input_manager/        Keyboard input and game-state commands
  interact_manager/     NPC/object/item/weapon/portal/door interactions
  main/                 GamePanel, game loop, renderer, config, and entry point
  monster_data/         Monster classes, factory, loot, death result, monster types
  npc_data/             NPCs and dialogue contracts
  object_data/          World objects, items, weapons, portals, doors, spawn plans
  player_manager/       Player, movement, combat input, and progression
  sound_manager/        Music and sound effects
  tile/                 Chunk/tile loading and map queries
  ui/                   HUD, menus, pause overlay, dialogue, messages, health bars

resource/               Runtime images, maps, sounds, and other assets
libraries/              External libraries, currently Gson
test/                   Plain Java smoke tests
scripts/                Quick compile/test scripts
docs/                   Manual testing checklist
```

## Requirements

- JDK 24, based on the current NetBeans project configuration in `nbproject/project.properties`.
- Gson `libraries/gson-2.10.1.jar`.
- NetBeans or IntelliJ IDEA. Either IDE can run the project as long as Gson and the resource folder are on the classpath.

## Run With NetBeans

1. Open NetBeans.
2. Select `File -> Open Project`.
3. Choose this project folder.
4. Check the Gson library:
   - right-click the project;
   - select `Properties`;
   - open `Libraries`;
   - add `libraries/gson-2.10.1.jar` if the IDE has not detected it.
5. Run the project.

Main class:

```text
main.Main
```

## Run With PowerShell

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

## Build With Ant

This project includes NetBeans/Ant configuration:

```powershell
ant clean jar
```

After building, the JAR is generated at:

```text
dist/2DJavaGame.jar
```

## Run Smoke Tests

The smoke test script compiles both `src` and `test`, then runs the plain Java smoke tests:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1
```

Current smoke tests cover:

- game size configuration;
- direction and collision geometry;
- entity stats, damage formula, knockback, and invulnerability;
- object and monster type lookup;
- weapon factory lookup;
- player progression;
- loot drop policy;
- save repository and save manager;
- missing-asset contract in the asset loader.

## Refactor Notes

This branch has been heavily refactored compared with the original course-project version:

- reduced public mutable fields;
- moved `GamePanel` closer to a composition-root role;
- separated combat, movement, rendering, save/load, and object interaction into smaller classes;
- introduced small interfaces/boundaries for world queries, collision, and rendering context;
- added smoke tests to keep behavior stable during future refactors;
- adjusted RPG gameplay balance, including weapon damage, damage mitigation, monster timing, safer respawn behavior, and saved progress.

## Future Improvements

- Add a full inventory/equipment system instead of relying mainly on `currentWeapon` and `keyCount`.
- Add a quest state machine and quest log.
- Move monster, weapon, drop, and spawn configuration into data files for easier balancing.
- Add character sheet and inventory UI.
- Expand save/load support for quest progress and advanced world flags.
