package main;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public final class AssetLoader {
    private static final AssetLoader DEFAULT = new AssetLoader(new UtilityTool());

    private final UtilityTool utilityTool;

    public AssetLoader(UtilityTool utilityTool) {
        this.utilityTool = Objects.requireNonNull(utilityTool, "utilityTool");
    }

    public static AssetLoader defaultLoader() {
        return DEFAULT;
    }

    public Optional<InputStream> findStream(String resourcePath, String requester) {
        String normalizedPath = normalizeResourcePath(resourcePath);
        return Optional.ofNullable(AssetLoader.class.getResourceAsStream(normalizedPath));
    }

    public InputStream requireStream(String resourcePath, String requester) throws AssetLoadException {
        String normalizedPath = normalizeResourcePath(resourcePath);
        InputStream stream = AssetLoader.class.getResourceAsStream(normalizedPath);
        if (stream == null) {
            throw missing(normalizedPath, requester);
        }
        return stream;
    }

    public Optional<BufferedImage> findImage(String imagePath, String requester) throws AssetLoadException {
        String resourcePath = normalizeImagePath(imagePath);
        try (InputStream stream = AssetLoader.class.getResourceAsStream(resourcePath)) {
            if (stream == null) {
                return Optional.empty();
            }

            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw invalidImage(resourcePath, requester);
            }
            return Optional.of(image);
        } catch (IOException e) {
            throw readFailure(resourcePath, requester, e);
        }
    }

    public BufferedImage requireImage(String imagePath, String requester) throws AssetLoadException {
        String resourcePath = normalizeImagePath(imagePath);
        Optional<BufferedImage> image = findImage(resourcePath, requester);
        if (image.isEmpty()) {
            throw missing(resourcePath, requester);
        }
        return image.get();
    }

    public Optional<BufferedImage> findScaledImage(String imagePath, int width, int height, String requester)
            throws AssetLoadException {
        Optional<BufferedImage> image = findImage(imagePath, requester);
        if (image.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(utilityTool.scaleImage(image.get(), width, height));
    }

    public BufferedImage requireScaledImage(String imagePath, int width, int height, String requester)
            throws AssetLoadException {
        BufferedImage image = requireImage(imagePath, requester);
        return utilityTool.scaleImage(image, width, height);
    }

    public static BufferedImage placeholderImage(int width, int height) {
        int safeWidth = Math.max(1, width);
        int safeHeight = Math.max(1, height);
        BufferedImage image = new BufferedImage(safeWidth, safeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(new Color(48, 48, 48, 220));
        g2.fillRect(0, 0, safeWidth, safeHeight);
        g2.setColor(new Color(255, 0, 255, 210));
        int halfWidth = Math.max(1, safeWidth / 2);
        int halfHeight = Math.max(1, safeHeight / 2);
        g2.fillRect(0, 0, halfWidth, halfHeight);
        g2.fillRect(halfWidth, halfHeight, safeWidth - halfWidth, safeHeight - halfHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, safeWidth - 1, safeHeight - 1);
        g2.dispose();
        return image;
    }

    public static String normalizeImagePath(String imagePath) {
        String normalizedPath = normalizeResourcePath(imagePath);
        return hasFileExtension(normalizedPath) ? normalizedPath : normalizedPath + ".png";
    }

    public static String normalizeResourcePath(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath");
        String normalizedPath = resourcePath.trim().replace('\\', '/');
        if (normalizedPath.isEmpty()) {
            throw new IllegalArgumentException("resourcePath must not be empty");
        }
        return normalizedPath.startsWith("/") ? normalizedPath : "/" + normalizedPath;
    }

    private static boolean hasFileExtension(String resourcePath) {
        int slash = resourcePath.lastIndexOf('/');
        int dot = resourcePath.lastIndexOf('.');
        return dot > slash;
    }

    private static AssetLoadException missing(String resourcePath, String requester) {
        return new AssetLoadException(
                "[AssetLoader] Missing asset requested by " + requester + ": " + resourcePath,
                resourcePath,
                requester
        );
    }

    private static AssetLoadException invalidImage(String resourcePath, String requester) {
        return new AssetLoadException(
                "[AssetLoader] Unsupported image requested by " + requester + ": " + resourcePath,
                resourcePath,
                requester
        );
    }

    private static AssetLoadException readFailure(String resourcePath, String requester, IOException cause) {
        return new AssetLoadException(
                "[AssetLoader] Failed to read asset requested by " + requester + ": " + resourcePath,
                resourcePath,
                requester,
                cause
        );
    }
}
