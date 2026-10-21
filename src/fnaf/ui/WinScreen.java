package fnaf.ui;

import fnaf.audio.SoundManager;
import fnaf.render.ArtGenerator;

import java.awt.*;

/**
 * 6:00 AM victory sequence:
 * Numbers shift from 5:59 to 6:00 AM while clock bells ring and children cheer.
 */
public class WinScreen {
    private double timer = 0.0;
    private static final double DURATION = 6.0;
    private boolean soundPlayed = false;

    public void start(SoundManager soundManager) {
        this.timer = 0.0;
        this.soundPlayed = false;
        soundManager.play6AMChimes();
    }

    public boolean update(double dtSeconds) {
        timer += dtSeconds;
        return timer >= DURATION;
    }

    public void render(Graphics2D g, int W, int H) {
        g.setColor(new Color(6, 6, 8));
        g.fillRect(0, 0, W, H);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 64));
        FontMetrics fm = g.getFontMetrics();

        // 5 shifts to 6 at timer >= 1.5s
        String hourStr = (timer < 1.5) ? "5" : "6";
        String timeStr = hourStr + " AM";
        g.drawString(timeStr, (W - fm.stringWidth(timeStr)) / 2, H / 2);

        ArtGenerator.drawCRTScanlines(g, W, H);
        ArtGenerator.drawStaticNoise(g, W, H, 0.05f);
    }
}
