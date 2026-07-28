package world;

import java.awt.Rectangle;

public interface WorldBody {
    int getWorldX();

    int getWorldY();

    int getWidth();

    int getHeight();

    Rectangle getSolidArea();

    default Rectangle getSolidAreaAt(int worldX, int worldY) {
        Rectangle area = getSolidArea();
        return new Rectangle(
                worldX + area.x,
                worldY + area.y,
                area.width,
                area.height
        );
    }

    default Rectangle getSolidAreaWorld() {
        return getSolidAreaAt(getWorldX(), getWorldY());
    }
}
