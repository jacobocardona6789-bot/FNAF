package fnaf.ui;

import fnaf.render.ArtGenerator;
import fnaf.save.SaveManager;

import java.awt.*;
import java.util.Random;

/**
 * FNAF 1 Main Menu:
 * Dark flickering title screen with twitching Freddy Fazbear,
 * menu options (New Game, Continue, Night 6, Custom Night),
 * and star tracker.
 */
public class MainMenuScreen {
    private int selectedOption = 0; // 0: New Game, 1: Continue, 2: Night 6, 3: Custom Night
    private double twitchTimer = 0.0;
    private boolean isTwitching = false;
    private final Random rand = new Random();

    // Menu bounds
    private final Rectangle newGameRect = new Rectangle(120, 420, 260, 40);
    private final Rectangle continueRect = new Rectangle(120, 475, 260, 40);
    private final Rectangle night6Rect = new Rectangle(120, 530, 260, 40);
    private final Rectangle customNightRect = new Rectangle(120, 585, 260, 40);

    public void update(double dtSeconds) {
        twitchTimer += dtSeconds;
        if (twitchTimer >= 0.1) {
            twitchTimer = 0.0;
            // 8% chance to twitch
            isTwitching = rand.nextInt(12) == 0;
        }
    }

    public void render(Graphics2D g, SaveManager saveManager, int W, int H) {
        // Deep dark background
        g.setColor(new Color(6, 6, 10));
        g.fillRect(0, 0, W, H);

        // Twitching Freddy Fazbear on the right side
        drawMenuFreddy(g, W, H);

        // Title Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 52));
        g.drawString("Five", 120, 140);
        g.drawString("Nights", 120, 195);
        g.drawString("at", 120, 250);
        g.drawString("Freddy's", 120, 315);

        // Stars earned (Beat Night 5 = 1, Night 6 = 2, 20/20/20/20 = 3)
        int stars = saveManager.getStarsEarned();
        g.setColor(new Color(255, 225, 40));
        for (int i = 0; i < stars; i++) {
            drawStar(g, 125 + i * 40, 350, 16);
        }

        // Menu Options
        g.setFont(new Font("Monospaced", Font.BOLD, 26));

        // 1. New Game
        drawOption(g, "New Game", newGameRect, selectedOption == 0);

        // 2. Continue (shows current night)
        String contText = "Continue (Night " + saveManager.getCurrentNight() + ")";
        drawOption(g, contText, continueRect, selectedOption == 1);

        // 3. Night 6 (unlocked after beating night 5)
        if (saveManager.isNight6Unlocked()) {
            drawOption(g, "6th Night", night6Rect, selectedOption == 2);
        }

        // 4. Custom Night (unlocked after beating night 6)
        if (saveManager.isCustomNightUnlocked()) {
            drawOption(g, "Custom Night", customNightRect, selectedOption == 3);
        }

        // Version & Copyright footer
        g.setColor(new Color(120, 120, 130));
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g.drawString("v1.0 - Remade from scratch - Scott Cawthon / FNAF 1 Tribute", 120, H - 35);
        g.drawString("[Controls: Mouse/WASD to Pan & Toggle Doors, Space/S for Monitor, Esc for Menu]", 120, H - 18);

        // CRT scanlines & static noise
        ArtGenerator.drawCRTScanlines(g, W, H);
        ArtGenerator.drawStaticNoise(g, W, H, isTwitching ? 0.35f : 0.08f);
    }

    private void drawOption(Graphics2D g, String text, Rectangle r, boolean isSelected) {
        if (isSelected) {
            g.setColor(new Color(255, 255, 255));
            g.drawString(">> " + text, r.x, r.y + 26);
        } else {
            g.setColor(new Color(160, 160, 175));
            g.drawString(text, r.x, r.y + 26);
        }
    }

    private void drawStar(Graphics2D g, int x, int y, int radius) {
        Polygon star = new Polygon();
        for (int i = 0; i < 10; i++) {
            double angle = i * Math.PI / 5 - Math.PI / 2;
            double r = (i % 2 == 0) ? radius : radius / 2.2;
            star.addPoint((int) (x + Math.cos(angle) * r), (int) (y + Math.sin(angle) * r));
        }
        g.fillPolygon(star);
    }

    private void drawMenuFreddy(Graphics2D g, int W, int H) {
        int fx = W - 480;
        int fy = 80;

        if (isTwitching) {
            fx += rand.nextInt(20) - 10;
            fy += rand.nextInt(20) - 10;
        }

        // Freddy silhouette / head in shadows
        g.setColor(new Color(90, 55, 30));
        g.fillOval(fx, fy + 80, 360, 420); // Head

        // Bear Ears
        g.fillOval(fx - 20, fy + 50, 110, 110);
        g.fillOval(fx + 270, fy + 50, 110, 110);

        // Top Hat
        g.setColor(new Color(15, 15, 20));
        g.fillRect(fx + 110, fy - 60, 140, 150);
        g.fillRect(fx + 60, fy + 65, 240, 30);

        // Muzzle & Mouth
        g.setColor(new Color(140, 95, 60));
        g.fillOval(fx + 55, fy + 240, 250, 170);

        // Eyes (Twitch between unlit and piercing blue/white)
        boolean eyesLit = isTwitching || (rand.nextInt(5) == 0);
        if (eyesLit) {
            g.setColor(Color.WHITE);
            g.fillOval(fx + 75, fy + 160, 65, 65);
            g.fillOval(fx + 220, fy + 160, 65, 65);
            g.setColor(new Color(40, 140, 255));
            g.fillOval(fx + 95, fy + 175, 25, 25);
            g.fillOval(fx + 240, fy + 175, 25, 25);
        } else {
            g.setColor(new Color(15, 15, 20));
            g.fillOval(fx + 75, fy + 160, 65, 65);
            g.fillOval(fx + 220, fy + 160, 65, 65);
            // Tiny glowing pinpricks
            g.setColor(new Color(255, 255, 255, 120));
            g.fillOval(fx + 105, fy + 185, 6, 6);
            g.fillOval(fx + 250, fy + 185, 6, 6);
        }

        // Endoskeleton Teeth in open jaw
        g.setColor(new Color(10, 10, 15));
        g.fillOval(fx + 85, fy + 310, 190, 90);
        g.setColor(new Color(200, 205, 215));
        for (int i = 0; i < 7; i++) {
            g.fillRect(fx + 105 + i * 22, fy + 330, 15, 25);
        }
    }

    public int handleMouseClick(int mx, int my, SaveManager saveManager) {
        if (newGameRect.contains(mx, my)) return 0;
        if (continueRect.contains(mx, my)) return 1;
        if (saveManager.isNight6Unlocked() && night6Rect.contains(mx, my)) return 2;
        if (saveManager.isCustomNightUnlocked() && customNightRect.contains(mx, my)) return 3;
        return -1;
    }

    public void handleMouseMove(int mx, int my, SaveManager saveManager) {
        if (newGameRect.contains(mx, my)) selectedOption = 0;
        else if (continueRect.contains(mx, my)) selectedOption = 1;
        else if (saveManager.isNight6Unlocked() && night6Rect.contains(mx, my)) selectedOption = 2;
        else if (saveManager.isCustomNightUnlocked() && customNightRect.contains(mx, my)) selectedOption = 3;
    }

    public int getSelectedOption() { return selectedOption; }
}
