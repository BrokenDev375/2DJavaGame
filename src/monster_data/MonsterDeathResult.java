package monster_data;

import object_data.ObjectDropRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class MonsterDeathResult {
    private final String monsterName;
    private final int expReward;
    private final LootDropResult healthPotionDrop;
    private final List<ObjectDropRequest> extraDrops;

    private MonsterDeathResult(
            String monsterName,
            int expReward,
            LootDropResult healthPotionDrop,
            List<ObjectDropRequest> extraDrops
    ) {
        this.monsterName = monsterName == null ? "monster" : monsterName;
        this.expReward = Math.max(0, expReward);
        this.healthPotionDrop = healthPotionDrop;
        this.extraDrops = List.copyOf(extraDrops);
    }

    public static MonsterDeathResult of(String monsterName, int expReward, LootDropResult healthPotionDrop) {
        return new MonsterDeathResult(monsterName, expReward, healthPotionDrop, Collections.emptyList());
    }

    public MonsterDeathResult withDrop(ObjectDropRequest drop) {
        Objects.requireNonNull(drop, "drop");
        List<ObjectDropRequest> drops = new ArrayList<>(extraDrops);
        drops.add(drop);
        return new MonsterDeathResult(monsterName, expReward, healthPotionDrop, drops);
    }

    public String monsterName() {
        return monsterName;
    }

    public int expReward() {
        return expReward;
    }

    public String deathLog() {
        return "[DEATH] " + monsterName;
    }

    public Optional<LootDropResult> healthPotionDrop() {
        return Optional.ofNullable(healthPotionDrop);
    }

    public List<ObjectDropRequest> dropRequests() {
        List<ObjectDropRequest> drops = new ArrayList<>();
        if (healthPotionDrop != null) {
            healthPotionDrop.dropRequest().ifPresent(drops::add);
        }
        drops.addAll(extraDrops);
        return Collections.unmodifiableList(drops);
    }
}
