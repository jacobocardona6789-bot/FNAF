package fnaf.render;

import fnaf.audio.SoundManager;
import fnaf.model.AnimatronicType;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Handles the animated multi-frame procedural jumpscares for all animatronics.
 */
public class JumpscareRenderer {
    private AnimatronicType activeJumpscare = null;
    private double timer = 0.0;
    private static final double JUMPSCARE_DURATION = 1.35;
    private final Random rand = new Random();

    public void start(AnimatronicType type, SoundManager soundManager) {
        this.activeJumpscare = type;
        this.timer = 0.0;
        soundManager.playJumpscare();
    }

    public boolean update(double dtSeconds) {
        if (activeJumpscare == null) return false;
        timer += dtSeconds;
        if (timer >= JUMPSCARE_DURATION) {
            activeJumpscare = null;
            return true; // Jumpscare finished -> trigger GAME_OVER!
        }
        return false;
    }

    public void render(Graphics2D g, int W, int H) {
        if (activeJumpscare == null) return;

        double progress = timer / JUMPSCARE_DURATION;
        AffineTransform oldTx = g.getTransform();

        // Screen shake
        int shakeX = rand.nextInt(25) - 12;
        int shakeY = rand.nextInt(25) - 12;
        g.translate(shakeX, shakeY);

        // Flash background
        boolean flash = (rand.nextInt(3) == 0);
        g.setColor(flash ? new Color(180, 20, 20) : new Color(10, 10, 15));
        g.fillRect(-50, -50, W + 100, H + 100);

        switch (activeJumpscare) {
            case BONNIE:
                drawBonnieJumpscare(g, W, H, progress);
                break;
            case CHICA:
                drawChicaJumpscare(g, W, H, progress);
                break;
            case FOXY:
                drawFoxyJumpscare(g, W, H, progress);
                break;
            case FREDDY:
                drawFreddyJumpscare(g, W, H, progress);
                break;
            case GOLDEN_FREDDY:
                drawGoldenFreddyJumpscare(g, W, H, progress);
                break;
        }

        // Heavy static noise and scanlines
        ArtGenerator.drawStaticNoise(g, W, H, 0.45f);
        ArtGenerator.drawCRTScanlines(g, W, H);

        g.setTransform(oldTx);
    }

    private void drawBonnieJumpscare(Graphics2D g, int W, int H, double progress) {
        // Bonnie lunging up from bottom left into camera
        double scale = 1.0 + progress * 1.6;
        int bx = (int) (W * 0.25 + (rand.nextDouble() - 0.5) * 40);
        int by = (int) (H * 0.9 - progress * H * 0.7);

        g.setColor(new Color(85, 75, 140));
        g.fillOval(bx - (int)(180 * scale), by - (int)(200 * scale), (int)(360 * scale), (int)(420 * scale));

        // Long shaking ears
        g.fillRoundRect(bx - (int)(140 * scale), by - (int)(380 * scale), (int)(60 * scale), (int)(220 * scale), 20, 20);
        g.fillRoundRect(bx + (int)(80 * scale), by - (int)(380 * scale), (int)(60 * scale), (int)(220 * scale), 20, 20);

        // Gaping mouth with exposed endoskeleton jaw
        g.setColor(new Color(15, 15, 20));
        g.fillOval(bx - (int)(120 * scale), by + (int)(40 * scale), (int)(240 * scale), (int)(160 * scale));
        // Endoskeleton teeth
        g.setColor(Color.WHITE);
        for (int i = 0; i < 9; i++) {
            g.fillRect(bx - (int)(90 * scale) + (int)(i * 20 * scale), by + (int)(60 * scale), (int)(12 * scale), (int)(20 * scale));
            g.fillRect(bx - (int)(90 * scale) + (int)(i * 20 * scale), by + (int)(120 * scale), (int)(12 * scale), (int)(20 * scale));
        }

        // Glaring eyes with vibrating red pupils
        g.setColor(Color.WHITE);
        g.fillOval(bx - (int)(100 * scale), by - (int)(80 * scale), (int)(80 * scale), (int)(80 * scale));
        g.fillOval(bx + (int)(20 * scale), by - (int)(80 * scale), (int)(80 * scale), (int)(80 * scale));
        g.setColor(new Color(255, 20, 60));
        g.fillOval(bx - (int)(75 * scale), by - (int)(60 * scale), (int)(32 * scale), (int)(32 * scale));
        g.fillOval(bx + (int)(45 * scale), by - (int)(60 * scale), (int)(32 * scale), (int)(32 * scale));
    }

    private void drawChicaJumpscare(Graphics2D g, int W, int H, double progress) {
        // Chica lunging from bottom right with unhinged beak
        double scale = 1.0 + progress * 1.6;
        int cx = (int) (W * 0.7 + (rand.nextDouble() - 0.5) * 40);
        int cy = (int) (H * 0.9 - progress * H * 0.65);

        g.setColor(new Color(230, 195, 30));
        g.fillOval(cx - (int)(190 * scale), cy - (int)(210 * scale), (int)(380 * scale), (int)(420 * scale));

        // Huge wide gaping orange beak
        g.setColor(new Color(235, 115, 20));
        g.fillOval(cx - (int)(150 * scale), cy, (int)(300 * scale), (int)(200 * scale));
        g.setColor(new Color(15, 15, 20));
        g.fillOval(cx - (int)(110 * scale), cy + (int)(30 * scale), (int)(220 * scale), (int)(140 * scale));

        // Rows of teeth inside mouth
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 8; i++) {
            g.fillRect(cx - (int)(85 * scale) + (int)(i * 22 * scale), cy + (int)(55 * scale), (int)(14 * scale), (int)(22 * scale));
        }

        // Bulging purple eyes
        g.setColor(Color.WHITE);
        g.fillOval(cx - (int)(130 * scale), cy - (int)(100 * scale), (int)(95 * scale), (int)(95 * scale));
        g.fillOval(cx + (int)(35 * scale), cy - (int)(100 * scale), (int)(95 * scale), (int)(95 * scale));
        g.setColor(new Color(150, 30, 220));
        g.fillOval(cx - (int)(95 * scale), cy - (int)(70 * scale), (int)(32 * scale), (int)(32 * scale));
        g.fillOval(cx + (int)(70 * scale), cy - (int)(70 * scale), (int)(32 * scale), (int)(32 * scale));
    }

    private void drawFoxyJumpscare(Graphics2D g, int W, int H, double progress) {
        // Foxy screaming through left doorway
        double scale = 1.2 + progress * 1.4;
        int fx = (int) (W * 0.35 + (rand.nextDouble() - 0.5) * 35);
        int fy = (int) (H * 0.5 + (rand.nextDouble() - 0.5) * 35);

        g.setColor(new Color(185, 45, 30));
        g.fillOval(fx - (int)(160 * scale), fy - (int)(180 * scale), (int)(320 * scale), (int)(360 * scale));

        // Sharp fox muzzle & jaw
        g.setColor(new Color(140, 30, 20));
        g.fillOval(fx - (int)(120 * scale), fy + (int)(10 * scale), (int)(240 * scale), (int)(170 * scale));
        g.setColor(new Color(15, 15, 20));
        g.fillOval(fx - (int)(90 * scale), fy + (int)(40 * scale), (int)(180 * scale), (int)(120 * scale));

        // Sharp golden/silver teeth
        g.setColor(Color.WHITE);
        for (int i = 0; i < 9; i++) {
            int[] tx = {fx - (int)(70 * scale) + (int)(i * 16 * scale), fx - (int)(60 * scale) + (int)(i * 16 * scale), fx - (int)(65 * scale) + (int)(i * 16 * scale)};
            int[] ty = {fy + (int)(50 * scale), fy + (int)(50 * scale), fy + (int)(85 * scale)};
            g.fillPolygon(tx, ty, 3);
        }

        // Glowing yellow eye & flipped eyepatch
        g.setColor(new Color(255, 235, 40));
        g.fillOval(fx - (int)(90 * scale), fy - (int)(70 * scale), (int)(50 * scale), (int)(50 * scale));
        g.fillOval(fx + (int)(40 * scale), fy - (int)(70 * scale), (int)(50 * scale), (int)(50 * scale));
    }

    private void drawFreddyJumpscare(Graphics2D g, int W, int H, double progress) {
        // Freddy lunging from right side
        double scale = 1.1 + progress * 1.5;
        int mx = (int) (W * 0.5 + (rand.nextDouble() - 0.5) * 35);
        int my = (int) (H * 0.55 + (rand.nextDouble() - 0.5) * 35);

        g.setColor(new Color(110, 65, 35));
        g.fillOval(mx - (int)(170 * scale), my - (int)(190 * scale), (int)(340 * scale), (int)(380 * scale));

        // Top Hat
        g.setColor(new Color(15, 15, 20));
        g.fillRect(mx - (int)(50 * scale), my - (int)(260 * scale), (int)(100 * scale), (int)(80 * scale));
        g.fillRect(mx - (int)(75 * scale), my - (int)(190 * scale), (int)(150 * scale), (int)(20 * scale));

        // Muzzle and jaw
        g.setColor(new Color(160, 110, 70));
        g.fillOval(mx - (int)(110 * scale), my + (int)(10 * scale), (int)(220 * scale), (int)(140 * scale));
        g.setColor(new Color(15, 15, 20));
        g.fillOval(mx - (int)(80 * scale), my + (int)(40 * scale), (int)(160 * scale), (int)(90 * scale));

        // Teeth
        g.setColor(Color.WHITE);
        for (int i = 0; i < 7; i++) {
            g.fillRect(mx - (int)(60 * scale) + (int)(i * 18 * scale), my + (int)(55 * scale), (int)(12 * scale), (int)(20 * scale));
        }

        // Eyes with bright glowing blue pupils
        g.setColor(Color.WHITE);
        g.fillOval(mx - (int)(90 * scale), my - (int)(70 * scale), (int)(65 * scale), (int)(65 * scale));
        g.fillOval(mx + (int)(25 * scale), my - (int)(70 * scale), (int)(65 * scale), (int)(65 * scale));
        g.setColor(new Color(50, 160, 255));
        g.fillOval(mx - (int)(70 * scale), my - (int)(50 * scale), (int)(26 * scale), (int)(26 * scale));
        g.fillOval(mx + (int)(45 * scale), my - (int)(50 * scale), (int)(26 * scale), (int)(26 * scale));
    }

    private void drawGoldenFreddyJumpscare(Graphics2D g, int W, int H, double progress) {
        // Giant inverted Golden Freddy face filling screen + "IT'S ME" flashes
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, W, H);

        if (rand.nextBoolean()) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Impact", Font.BOLD, 120));
            FontMetrics fm = g.getFontMetrics();
            String itsMe = "IT'S ME";
            g.drawString(itsMe, (W - fm.stringWidth(itsMe)) / 2, H / 2 + 40);
        } else {
            // Golden Freddy close-up
            g.setColor(new Color(220, 180, 30));
            g.fillOval(W / 2 - 320, H / 2 - 320, 640, 640);
            g.setColor(Color.BLACK);
            g.fillOval(W / 2 - 190, H / 2 - 120, 130, 130);
            g.fillOval(W / 2 + 60, H / 2 - 120, 130, 130);
            g.setColor(Color.WHITE);
            g.fillOval(W / 2 - 140, H / 2 - 70, 15, 15);
            g.fillOval(W / 2 + 110, H / 2 - 70, 15, 15);
        }
    }

    public boolean isActive() {
        return activeJumpscare != null;
    }

    public AnimatronicType getActiveJumpscare() {
        return activeJumpscare;
    }
}
