package fnaf.save;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Manages game progress, unlocked nights, and stars earned.
 */
public class SaveManager {
    private static final String SAVE_FILE = "fnaf_save.properties";

    private int currentNight = 1;
    private int highestNightUnlocked = 1;
    private boolean night6Unlocked = false;
    private boolean customNightUnlocked = false;
    private int starsEarned = 0; // 1: Beat Night 5, 2: Beat Night 6, 3: Beat 20/20/20/20

    public SaveManager() {
        load();
    }

    public void load() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return;
        }

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
            currentNight = Integer.parseInt(props.getProperty("currentNight", "1"));
            highestNightUnlocked = Integer.parseInt(props.getProperty("highestNightUnlocked", "1"));
            night6Unlocked = Boolean.parseBoolean(props.getProperty("night6Unlocked", "false"));
            customNightUnlocked = Boolean.parseBoolean(props.getProperty("customNightUnlocked", "false"));
            starsEarned = Integer.parseInt(props.getProperty("starsEarned", "0"));
        } catch (Exception e) {
            System.err.println("Failed to load save file: " + e.getMessage());
        }
    }

    public void save() {
        Properties props = new Properties();
        props.setProperty("currentNight", String.valueOf(currentNight));
        props.setProperty("highestNightUnlocked", String.valueOf(highestNightUnlocked));
        props.setProperty("night6Unlocked", String.valueOf(night6Unlocked));
        props.setProperty("customNightUnlocked", String.valueOf(customNightUnlocked));
        props.setProperty("starsEarned", String.valueOf(starsEarned));

        try (OutputStream out = new FileOutputStream(SAVE_FILE)) {
            props.store(out, "FNAF 1 Save Data");
        } catch (Exception e) {
            System.err.println("Failed to write save file: " + e.getMessage());
        }
    }

    public void recordNightWon(int night, boolean is20Mode) {
        if (night == 5) {
            night6Unlocked = true;
            if (starsEarned < 1) starsEarned = 1;
        } else if (night == 6) {
            customNightUnlocked = true;
            if (starsEarned < 2) starsEarned = 2;
        } else if (night == 7 && is20Mode) {
            if (starsEarned < 3) starsEarned = 3;
        }

        if (night < 5 && night + 1 > highestNightUnlocked) {
            highestNightUnlocked = night + 1;
        }
        currentNight = Math.min(5, Math.max(currentNight, night + 1));
        save();
    }

    public void resetProgress() {
        currentNight = 1;
        highestNightUnlocked = 1;
        night6Unlocked = false;
        customNightUnlocked = false;
        starsEarned = 0;
        save();
    }

    public int getCurrentNight() { return currentNight; }
    public void setCurrentNight(int n) { this.currentNight = n; save(); }
    public int getHighestNightUnlocked() { return highestNightUnlocked; }
    public boolean isNight6Unlocked() { return night6Unlocked; }
    public boolean isCustomNightUnlocked() { return customNightUnlocked; }
    public int getStarsEarned() { return starsEarned; }
}
