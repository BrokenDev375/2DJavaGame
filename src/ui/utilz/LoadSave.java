package ui.utilz;

import main.AssetLoadException;
import main.AssetLoader;
import main.DebugLog;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Optional;

public class LoadSave {
    private static final AssetLoader ASSET_LOADER = AssetLoader.defaultLoader();
    private static final int FALLBACK_ATLAS_SIZE = 96;

    public static final String PAUSE_BACKGROUND = "pause_background.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png";

    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String BACKGROUND_MENU = "background_menu.png";
    public static final String BUTTON_ATLAS = "button_atlas.png";

    public static Optional<BufferedImage> findSpriteAtlas(String fileName) {
        for (String path : searchPathsFor(fileName)) {
            try {
                Optional<BufferedImage> image = ASSET_LOADER.findImage(path, "LoadSave");
                if (image.isPresent()) {
                    return image;
                }
            } catch (AssetLoadException e) {
                DebugLog.error(e.getMessage(), e);
            }
        }
        return Optional.empty();
    }

    public static BufferedImage GetSpriteAtlas(String fileName) {
        Optional<BufferedImage> image = findSpriteAtlas(fileName);
        if (image.isPresent()) {
            return image.get();
        }

        DebugLog.error("[LoadSave] Missing sprite atlas: " + fileName
                + " searched " + Arrays.toString(searchPathsFor(fileName)), null);
        return AssetLoader.placeholderImage(FALLBACK_ATLAS_SIZE, FALLBACK_ATLAS_SIZE);
    }

    private static String[] searchPathsFor(String fileName) {
        return new String[] {
                "/ui/" + fileName,
                "/object/" + fileName,
                "/player/" + fileName,
                "/" + fileName
        };
    }
}
