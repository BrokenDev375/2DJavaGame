package combat;

import entity.Direction;
import monster_data.Monster;
import player_manager.Player;

public final class KnockbackService {
    private KnockbackService() {}

    // cấu hình nhanh
    private static final int DEFAULT_PLAYER_KB = 3;
    private static final int DEFAULT_MONSTER_KB = 3;
    private static final int MAX_KB = 3;

    private static int clamp(int v, int lo, int hi){ return Math.max(lo, Math.min(hi, v)); }

    // Player → Monster
    public static int[] forPlayerAttack(Player p) {
        if (p == null) return new int[]{0,0};

        int baseForce = (p.getAttackKnockbackForce() > 0)
                ? p.getAttackKnockbackForce()
                : DEFAULT_PLAYER_KB;

        // ATK an toàn khi null
        int atk = 0;
        try { atk = Math.max(0, p.getATK()); } catch (Exception ignore) {}

        // scale nhẹ theo ATK (0..+50%); điều chỉnh tuỳ game
        int scaled = (int) Math.round(baseForce * (1.0 + Math.min(atk, 50) * 0.01));

        Direction dir = Direction.RIGHT;
        try { dir = p.getDirection(); } catch (Exception ignore) {}
        dir = (dir == null) ? Direction.RIGHT : dir;

        int kx = dir.scaledDx(scaled);
        int ky = dir.scaledDy(scaled);

        return new int[]{ clamp(kx, -MAX_KB, MAX_KB), clamp(ky, -MAX_KB, MAX_KB) };
    }

    // Monster → Player (vector m→player; mượt)
    public static int[] forMonsterAttack(Monster m, Player player) {
        if (m == null || m.isDead() || player == null || player.isDead()) return new int[]{0,0};

        int kbForce = (m.getAttackKnockbackForce() > 0)
                ? m.getAttackKnockbackForce()
                : DEFAULT_MONSTER_KB;

        int dx = player.getWorldX() - m.getWorldX();
        int dy = player.getWorldY() - m.getWorldY();

        double len = Math.hypot(dx, dy);
        int kx = (len == 0) ? 0 : (int) Math.round(kbForce * dx / len);
        int ky = (len == 0) ? 0 : (int) Math.round(kbForce * dy / len);

        // đảm bảo có tối thiểu 1px theo hướng đúng khi len nhỏ
        if (kx == 0 && dx != 0) kx = dx > 0 ? 1 : -1;
        if (ky == 0 && dy != 0) ky = dy > 0 ? 1 : -1;

        return new int[]{ clamp(kx, -MAX_KB, MAX_KB), clamp(ky, -MAX_KB, MAX_KB) };
    }
}
