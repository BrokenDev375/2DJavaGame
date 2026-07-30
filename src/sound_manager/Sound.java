
package sound_manager;

import main.DebugLog;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;


public class Sound {
    // Audio clip object
    private Clip clip;
    public void setFile(URL url) {
        if (url == null) {
            clip = null;
            DebugLog.error("[Sound] Missing audio URL.", null);
            return;
        }

        try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
            Clip loadedClip = AudioSystem.getClip();
            loadedClip.open(ais);
            clip = loadedClip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException
                 | IllegalArgumentException | SecurityException e) {
            clip = null;
            DebugLog.error("[Sound] Failed to load audio file: " + url, e);
        }
    }
    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);// rewind to start
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            clip.setFramePosition(0);// rewind to start
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}

