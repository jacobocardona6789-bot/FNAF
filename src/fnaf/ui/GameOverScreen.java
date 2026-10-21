package fnaf.ui;

import fnaf.render.ArtGenerator;

import java.awt.*;
import java.util.Random;

/**
 * Game Over screen with heavy CRT static and the ominous red "GAME OVER".
 */
public class GameOverScreen {
    private double timer = 0.0;
    private final Random rand = new Random();

    public void reset() {
        timer = 0.0;
    }

    public void update(double dtSeconds) {
        timer += dtSeconds;
    }

    public void render(Graphics2D g, int W, int H) {
        g.setColor(new Color(6, 6, 8));
        g.fillRect(0, 0, W, H);

        // Flashing red "GAME OVER"
        if (timer >= 0.8) {
            g.setColor(new Color(220, 20, 20));
            g.setFont(new Font("Impact", Font.BOLD, 72));
            FontMetrics fm = g.getFontMetrics();
            String title = "GAME OVER";
            g.drawString(title, (W - fm.stringWidth(title)) / 2, H / 2);

            if (timer >= 2.0 && (System.currentTimeMillis() % 1200 < 800)) {
                g.setColor(new Color(200, 200, 200));
                g.setFont(new Font("Monospaced", Font.BOLD, 18));
                fm = g.getFontMetrics();
                String prompt = "[ CLICK ANYWHERE TO RETURN TO MENU ]";
                g.drawString(prompt, (W - fm.stringWidth(prompt)) / 2, H / 2 + 70);
            }
        }

        // Heavy static noise and scanlines
        ArtGenerator.drawStaticNoise(g, W, H, 0.4f);
        ArtGenerator.drawCRTScanlines(g, W, H);
    }
}
