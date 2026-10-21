package fnaf.ui;

import fnaf.render.ArtGenerator;

import java.awt.*;

/**
 * Classic FNAF 1 "HELP WANTED" newspaper clipping shown at the start of a New Game.
 */
public class NewspaperScreen {
    private double timer = 0.0;

    public void reset() {
        timer = 0.0;
    }

    public void update(double dtSeconds) {
        timer += dtSeconds;
    }

    public void render(Graphics2D g, int W, int H) {
        g.setColor(new Color(15, 15, 18));
        g.fillRect(0, 0, W, H);

        // Center newspaper clipping
        int nw = 700;
        int nh = 500;
        int nx = (W - nw) / 2;
        int ny = (H - nh) / 2;

        // Vintage sepia paper
        g.setColor(new Color(230, 222, 198));
        g.fillRect(nx, ny, nw, nh);
        g.setColor(new Color(60, 55, 45));
        g.setStroke(new BasicStroke(3f));
        g.drawRect(nx, ny, nw, nh);

        // Header: "HELP WANTED"
        g.setColor(new Color(25, 25, 25));
        g.setFont(new Font("Impact", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "HELP WANTED";
        g.drawString(title, nx + (nw - fm.stringWidth(title)) / 2, ny + 65);

        // Subtitle: "Freddy Fazbear's Pizza"
        g.setFont(new Font("Serif", Font.BOLD, 26));
        fm = g.getFontMetrics();
        String sub = "Freddy Fazbear's Pizza";
        g.drawString(sub, nx + (nw - fm.stringWidth(sub)) / 2, ny + 105);

        // Divider
        g.drawLine(nx + 40, ny + 120, nx + nw - 40, ny + 120);

        // Body text
        g.setFont(new Font("Serif", Font.PLAIN, 20));
        int textY = ny + 160;
        int lineSpacing = 32;

        g.drawString("Family pizzeria looking for security guard to work the night-shift.", nx + 50, textY);
        g.drawString("12:00 am to 6:00 am.", nx + 50, textY + lineSpacing);
        g.drawString("Monitor security cameras, ensure safety of equipment and", nx + 50, textY + lineSpacing * 2);
        g.drawString("animatronic characters.", nx + 50, textY + lineSpacing * 3);
        g.drawString("Not responsible for injury / dismemberment.", nx + 50, textY + lineSpacing * 4);
        g.drawString("$120 a week.", nx + 50, textY + lineSpacing * 5);

        // Contact info
        g.setFont(new Font("Impact", Font.BOLD, 22));
        g.drawString("To apply call: 1-888-FAZ-FAZBEAR", nx + 50, textY + lineSpacing * 7);

        // Bottom prompt
        if (System.currentTimeMillis() % 1200 < 800) {
            g.setColor(new Color(200, 200, 200));
            g.setFont(new Font("Monospaced", Font.BOLD, 16));
            String prompt = "[ CLICK ANYWHERE OR PRESS SPACE TO START ]";
            fm = g.getFontMetrics();
            g.drawString(prompt, (W - fm.stringWidth(prompt)) / 2, H - 45);
        }

        ArtGenerator.drawCRTScanlines(g, W, H);
        ArtGenerator.drawStaticNoise(g, W, H, 0.05f);
    }
}
