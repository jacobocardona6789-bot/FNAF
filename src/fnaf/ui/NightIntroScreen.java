package fnaf.ui;

import fnaf.render.ArtGenerator;

import java.awt.*;

/**
 * Screen showing the 12:00 AM start and current night number (e.g. "1st Night").
 */
public class NightIntroScreen {
    private int night = 1;
    private double timer = 0.0;
    private static final double DURATION = 2.5;

    public void start(int night) {
        this.night = night;
        this.timer = 0.0;
    }

    public boolean update(double dtSeconds) {
        timer += dtSeconds;
        return timer >= DURATION;
    }

    public void render(Graphics2D g, int W, int H) {
        g.setColor(new Color(6, 6, 8));
        g.fillRect(0, 0, W, H);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();

        // "12:00 AM"
        String timeStr = "12:00 AM";
        g.drawString(timeStr, (W - fm.stringWidth(timeStr)) / 2, H / 2 - 20);

        // Night number (e.g. "1st Night")
        g.setFont(new Font("Monospaced", Font.BOLD, 32));
        fm = g.getFontMetrics();
        String nightStr;
        switch (night) {
            case 1: nightStr = "1st Night"; break;
            case 2: nightStr = "2nd Night"; break;
            case 3: nightStr = "3rd Night"; break;
            case 4: nightStr = "4th Night"; break;
            case 5: nightStr = "5th Night"; break;
            case 6: nightStr = "6th Night"; break;
            case 7: nightStr = "Custom Night"; break;
            default: nightStr = night + "th Night"; break;
        }
        g.drawString(nightStr, (W - fm.stringWidth(nightStr)) / 2, H / 2 + 50);

        ArtGenerator.drawCRTScanlines(g, W, H);
        ArtGenerator.drawStaticNoise(g, W, H, 0.06f);
    }
}
