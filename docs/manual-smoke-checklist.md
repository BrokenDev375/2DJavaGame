# Manual Smoke Checklist

Run the automated smoke test before manual play:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\smoke-test.ps1
```

Record the date, branch, commit, and any local changes before starting.

## Gameplay Flow

- Start a new game from the main menu.
- Move in all directions and verify wall, tile, object, NPC, and monster collision.
- Attack a monster until death; verify HP, knockback, EXP, level-up behavior, and dropped loot.
- Pick up key, potion, and weapon objects; verify inventory/HP/equipment behavior.
- Talk to an NPC with the interaction key and verify the prompt hides when leaving range.
- Use portal and door transitions; verify map index and player position after teleport.
- Save with the in-game save command, quit to menu, then load the save.
- Trigger game over, restart, and return to menu.
- Pause/resume and toggle sound while gameplay is active.

Expected result: no crash, no unexpected missing-asset placeholder, no stuck input state, and no lost save data.
