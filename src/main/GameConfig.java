package main;

public final class GameConfig {
    private final int originalTileSize;
    private final int scale;
    private final int maxScreenCol;
    private final int maxScreenRow;
    private final int maxWorldCol;
    private final int maxWorldRow;
    private final int chunkSize;
    private final int numMaps;

    public static GameConfig defaults() {
        return new GameConfig(16, 3, 25, 14, 32 * 3, 32 * 3, 32, 3);
    }

    public GameConfig(
            int originalTileSize,
            int scale,
            int maxScreenCol,
            int maxScreenRow,
            int maxWorldCol,
            int maxWorldRow,
            int chunkSize,
            int numMaps
    ) {
        this.originalTileSize = requirePositive(originalTileSize, "originalTileSize");
        this.scale = requirePositive(scale, "scale");
        this.maxScreenCol = requirePositive(maxScreenCol, "maxScreenCol");
        this.maxScreenRow = requirePositive(maxScreenRow, "maxScreenRow");
        this.maxWorldCol = requirePositive(maxWorldCol, "maxWorldCol");
        this.maxWorldRow = requirePositive(maxWorldRow, "maxWorldRow");
        this.chunkSize = requirePositive(chunkSize, "chunkSize");
        this.numMaps = requirePositive(numMaps, "numMaps");
    }

    private static int requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive: " + value);
        }
        return value;
    }

    public int originalTileSize() {
        return originalTileSize;
    }

    public int scale() {
        return scale;
    }

    public int tileSize() {
        return originalTileSize * scale;
    }

    public int maxScreenCol() {
        return maxScreenCol;
    }

    public int maxScreenRow() {
        return maxScreenRow;
    }

    public int screenWidth() {
        return tileSize() * maxScreenCol;
    }

    public int screenHeight() {
        return tileSize() * maxScreenRow;
    }

    public int maxWorldCol() {
        return maxWorldCol;
    }

    public int maxWorldRow() {
        return maxWorldRow;
    }

    public int chunkSize() {
        return chunkSize;
    }

    public int numMaps() {
        return numMaps;
    }
}
