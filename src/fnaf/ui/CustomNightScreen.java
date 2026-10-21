package fnaf.ui;

import fnaf.audio.SoundManager;
import fnaf.render.ArtGenerator;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * FNAF 1 Custom Night (Night 7):
 * Lets the player configure AI levels from 0 to 20 for
 * Freddy Fazbear, Bonnie, Chica, and Foxy.
 */
public class CustomNightScreen {
    private int freddyAI = 20;
    private int bonnieAI = 20;
    private int chicaAI = 20;
    private int foxyAI = 20;

    private final Rectangle readyButton = new Rectangle(540, 580, 200, 50);
    private final Rectangle backButton = new Rectangle(100, 580, 160, 50);
    private final Rectangle preset20Button = new Rectangle(820, 580, 160, 50);

    // AI Adjustment Button bounds [character][0: left arrow, 1: right arrow]
    private final Rectangle[][] arrowButtons = new Rectangle[4][2];

    public CustomNightScreen() {
        int startX = 140;
        int cardW = 230;
        int gap = 30;
        int y = 460;

        for (int i = 0; i < 4; i++) {
            int cx = startX + i * (cardW + gap);
            arrowButtons[i][0] = new Rectangle(cx + 30, y, 40, 40);  // Left arrow (<)
            arrowButtons[i][1] = new Rectangle(cx + 160, y, 40, 40); // Right arrow (>)
        }
    }

    public void render(Graphics2D g, int W, int H) {
        g.setColor(new Color(10, 10, 14));
        g.fillRect(0, 0, W, H);

        // Header Title
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 46));
        FontMetrics fm = g.getFontMetrics();
        String title = "CUSTOM NIGHT";
        g.drawString(title, (W - fm.stringWidth(title)) / 2, 70);

        // Subtitle
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(new Color(180, 180, 190));
        String sub = "SET ANIMATRONIC A.I. LEVELS (0 - 20)";
        fm = g.getFontMetrics();
        g.drawString(sub, (W - fm.stringWidth(sub)) / 2, 105);

        // 4 Character Cards
        int startX = 140;
        int cardW = 230;
        int gap = 30;

        drawCharacterCard(g, startX, 140, cardW, "Freddy Fazbear", freddyAI, 0, new Color(110, 65, 35));
        drawCharacterCard(g, startX + (cardW + gap), 140, cardW, "Bonnie", bonnieAI, 1, new Color(85, 75, 140));
        drawCharacterCard(g, startX + (cardW + gap) * 2, 140, cardW, "Chica", chicaAI, 2, new Color(220, 185, 25));
        drawCharacterCard(g, startX + (cardW + gap) * 3, 140, cardW, "Foxy", foxyAI, 3, new Color(185, 45, 30));

        // READY Button
        g.setColor(new Color(20, 160, 60));
        g.fillRoundRect(readyButton.x, readyButton.y, readyButton.width, readyButton.height, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 28));
        fm = g.getFontMetrics();
        g.drawString("READY", readyButton.x + (readyButton.width - fm.stringWidth("READY")) / 2, readyButton.y + 36);

        // BACK Button
        g.setColor(new Color(55, 60, 70));
        g.fillRoundRect(backButton.x, backButton.y, backButton.width, backButton.height, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 22));
        fm = g.getFontMetrics();
        g.drawString("BACK", backButton.x + (backButton.width - fm.stringWidth("BACK")) / 2, backButton.y + 34);

        // 20/20/20/20 Preset Button
        g.setColor(new Color(160, 30, 30));
        g.fillRoundRect(preset20Button.x, preset20Button.y, preset20Button.width, preset20Button.height, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 20));
        fm = g.getFontMetrics();
        g.drawString("20/20/20/20", preset20Button.x + (preset20Button.width - fm.stringWidth("20/20/20/20")) / 2, preset20Button.y + 33);

        // Scanlines & static
        ArtGenerator.drawCRTScanlines(g, W, H);
        ArtGenerator.drawStaticNoise(g, W, H, 0.06f);
    }

    private void drawCharacterCard(Graphics2D g, int x, int y, int w, String name, int ai, int charIndex, Color primaryColor) {
        int h = 400;

        // Card border & background
        g.setColor(new Color(25, 28, 35));
        g.fillRoundRect(x, y, w, h, 14, 14);
        g.setColor(new Color(50, 55, 65));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, 14, 14);

        // Character portrait box
        g.setColor(new Color(15, 16, 20));
        g.fillRect(x + 15, y + 15, w - 30, 190);

        // Stylized character head
        drawPortrait(g, charIndex, x + (w - 30) / 2 - 25, y + 40, primaryColor);

        // Character Name
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(name, x + (w - fm.stringWidth(name)) / 2, y + 240);

        // A.I. LEVEL label
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        g.setColor(new Color(160, 165, 175));
        String aiLabel = "A.I. LEVEL";
        fm = g.getFontMetrics();
        g.drawString(aiLabel, x + (w - fm.stringWidth(aiLabel)) / 2, y + 270);

        // AI Level Number
        g.setFont(new Font("Impact", Font.BOLD, 48));
        g.setColor(new Color(255, 220, 50));
        String num = String.valueOf(ai);
        fm = g.getFontMetrics();
        g.drawString(num, x + (w - fm.stringWidth(num)) / 2, y + 325);

        // Arrow Buttons (< and >)
        Rectangle leftArrow = arrowButtons[charIndex][0];
        Rectangle rightArrow = arrowButtons[charIndex][1];

        // Left button (<)
        g.setColor(new Color(45, 50, 60));
        g.fillRoundRect(leftArrow.x, leftArrow.y, leftArrow.width, leftArrow.height, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 24));
        g.drawString("<", leftArrow.x + 14, leftArrow.y + 28);

        // Right button (>)
        g.setColor(new Color(45, 50, 60));
        g.fillRoundRect(rightArrow.x, rightArrow.y, rightArrow.width, rightArrow.height, 8, 8);
        g.setColor(Color.WHITE);
        g.drawString(">", rightArrow.x + 14, rightArrow.y + 28);
    }

    private void drawPortrait(Graphics2D g, int charIndex, int px, int py, Color c) {
        g.setColor(c);
        g.fillOval(px - 15, py, 110, 120);

        if (charIndex == 0) { // Freddy
            // Top hat
            g.setColor(new Color(15, 15, 20));
            g.fillRect(px + 20, py - 30, 40, 35);
            g.fillRect(px + 10, py + 2, 60, 8);
            // Blue pupils
            g.setColor(Color.WHITE);
            g.fillOval(px + 10, py + 35, 20, 20);
            g.fillOval(px + 50, py + 35, 20, 20);
            g.setColor(new Color(40, 140, 255));
            g.fillOval(px + 16, py + 41, 8, 8);
            g.fillOval(px + 56, py + 41, 8, 8);
        } else if (charIndex == 1) { // Bonnie
            // Ears
            g.fillRoundRect(px + 5, py - 40, 20, 50, 8, 8);
            g.fillRoundRect(px + 55, py - 40, 20, 50, 8, 8);
            // Eyes
            g.setColor(Color.WHITE);
            g.fillOval(px + 10, py + 35, 20, 20);
            g.fillOval(px + 50, py + 35, 20, 20);
            g.setColor(Color.MAGENTA);
            g.fillOval(px + 16, py + 41, 8, 8);
            g.fillOval(px + 56, py + 41, 8, 8);
        } else if (charIndex == 2) { // Chica
            // Beak
            g.setColor(new Color(230, 110, 20));
            g.fillOval(px + 15, py + 50, 50, 30);
            g.setColor(Color.WHITE);
            g.fillOval(px + 10, py + 30, 20, 20);
            g.fillOval(px + 50, py + 30, 20, 20);
            g.setColor(new Color(140, 40, 200));
            g.fillOval(px + 16, py + 36, 8, 8);
            g.fillOval(px + 56, py + 36, 8, 8);
        } else if (charIndex == 3) { // Foxy
            // Pointy ears
            int[] e1x = {px - 5, px + 15, px + 5};
            int[] e1y = {py + 10, py + 10, py - 25};
            g.fillPolygon(e1x, e1y, 3);
            int[] e2x = {px + 65, px + 85, px + 75};
            int[] e2y = {py + 10, py + 10, py - 25};
            g.fillPolygon(e2x, e2y, 3);
            // Yellow eye & eyepatch
            g.setColor(new Color(255, 230, 50));
            g.fillOval(px + 15, py + 35, 18, 18);
            g.setColor(Color.BLACK);
            g.fillOval(px + 50, py + 35, 18, 18);
        }
    }

    public boolean handleMouseClick(int mx, int my, SoundManager soundManager) {
        // Ready button
        if (readyButton.contains(mx, my)) {
            soundManager.playButtonClick();
            return true; // Start night!
        }

        // Back button
        if (backButton.contains(mx, my)) {
            soundManager.playButtonClick();
            return false;
        }

        // 20/20/20/20 Preset
        if (preset20Button.contains(mx, my)) {
            freddyAI = 20;
            bonnieAI = 20;
            chicaAI = 20;
            foxyAI = 20;
            soundManager.playButtonClick();
            return false;
        }

        // Check arrows
        for (int i = 0; i < 4; i++) {
            if (arrowButtons[i][0].contains(mx, my)) {
                adjustAI(i, -1);
                soundManager.playButtonClick();
                return false;
            }
            if (arrowButtons[i][1].contains(mx, my)) {
                adjustAI(i, 1);
                soundManager.playButtonClick();
                return false;
            }
        }
        return false;
    }

    private void adjustAI(int charIndex, int delta) {
        switch (charIndex) {
            case 0: freddyAI = Math.max(0, Math.min(20, freddyAI + delta)); break;
            case 1: bonnieAI = Math.max(0, Math.min(20, bonnieAI + delta)); break;
            case 2: chicaAI = Math.max(0, Math.min(20, chicaAI + delta)); break;
            case 3: foxyAI = Math.max(0, Math.min(20, foxyAI + delta)); break;
        }
    }

    public boolean isBackClicked(int mx, int my) {
        return backButton.contains(mx, my);
    }

    public boolean isReadyClicked(int mx, int my) {
        return readyButton.contains(mx, my);
    }

    public int getFreddyAI() { return freddyAI; }
    public int getBonnieAI() { return bonnieAI; }
    public int getChicaAI() { return chicaAI; }
    public int getFoxyAI() { return foxyAI; }
    public boolean is20Mode() { return freddyAI == 20 && bonnieAI == 20 && chicaAI == 20 && foxyAI == 20; }
}
