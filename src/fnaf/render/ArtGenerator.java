package fnaf.render;

import fnaf.model.Animatronic;
import fnaf.model.AnimatronicType;
import fnaf.model.RoomId;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Procedural artist that draws the retro-horror visuals for FNAF 1:
 * Office, animatronic portraits, camera views, and atmospheric lighting.
 */
public class ArtGenerator {
    private static final Random RAND = new Random();

    // ==========================================
    // OFFICE RENDERING
    // ==========================================

    public static void drawOffice(Graphics2D g,
                                  double panOffset,
                                  double leftDoorProgress,
                                  double rightDoorProgress,
                                  boolean leftLightOn,
                                  boolean rightLightOn,
                                  double fanAngle,
                                  boolean bonnieAtDoor,
                                  boolean chicaAtWindow,
                                  boolean goldenFreddyInOffice,
                                  boolean blackout,
                                  boolean freddySingingFaceLit) {
        // Save transform
        AffineTransform oldTx = g.getTransform();
        // Shift by negative panOffset
        g.translate(-panOffset, 0);

        int W = 1600;
        int H = 720;

        if (blackout) {
            // Pitch black room during blackout
            g.setColor(new Color(6, 6, 8));
            g.fillRect(0, 0, W, H);

            // If Freddy is singing Toreador March in left doorway:
            if (freddySingingFaceLit) {
                drawFreddyGlowingFace(g, 160, 260, 1.2);
            }
            g.setTransform(oldTx);
            return;
        }

        // --- Back Wall ---
        // Subtle vertical wood/metal paneling
        GradientPaint wallGrad = new GradientPaint(0, 0, new Color(38, 42, 48), 0, 480, new Color(20, 22, 26));
        g.setPaint(wallGrad);
        g.fillRect(0, 0, W, 480);

        // Wall stripes/panel lines
        g.setColor(new Color(15, 17, 20, 140));
        for (int x = 0; x < W; x += 60) {
            g.drawLine(x, 0, x, 480);
        }

        // Center poster: "CELEBRATE!"
        drawCelebratePoster(g, 720, 140);

        // Hanging wires and cables from ceiling
        drawHangingWires(g);

        // --- Checkered Floor with Perspective ---
        drawCheckeredFloor(g, W, H);

        // --- Left Doorway & Wall ---
        drawLeftDoorway(g, leftDoorProgress, leftLightOn, bonnieAtDoor);

        // --- Right Doorway, Window & Wall ---
        drawRightDoorway(g, rightDoorProgress, rightLightOn, chicaAtWindow);

        // --- Office Desk & Clutter ---
        drawDesk(g, fanAngle);

        // --- Golden Freddy Slumped on Floor (if active) ---
        if (goldenFreddyInOffice) {
            drawGoldenFreddyOffice(g, 620, 360);
        }

        // --- Ambient Lighting & Vignette ---
        drawOfficeLighting(g, W, H, leftLightOn, rightLightOn);

        g.setTransform(oldTx);
    }

    private static void drawHangingWires(Graphics2D g) {
        g.setColor(new Color(15, 15, 18));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Wire 1
        Path2D w1 = new Path2D.Double();
        w1.moveTo(400, 0);
        w1.curveTo(420, 80, 450, 120, 480, 70);
        w1.curveTo(510, 20, 530, 0, 560, 0);
        g.draw(w1);

        // Wire 2
        Path2D w2 = new Path2D.Double();
        w2.moveTo(980, 0);
        w2.curveTo(1010, 110, 1060, 130, 1100, 0);
        g.draw(w2);

        // Dangling wire end
        Path2D w3 = new Path2D.Double();
        w3.moveTo(1250, 0);
        w3.curveTo(1260, 60, 1240, 110, 1245, 140);
        g.draw(w3);
    }

    private static void drawCheckeredFloor(Graphics2D g, int W, int H) {
        int floorY = 460;
        int rows = 12;
        int cols = 32;

        for (int r = 0; r < rows; r++) {
            double y1 = floorY + (H - floorY) * Math.pow((double) r / rows, 1.8);
            double y2 = floorY + (H - floorY) * Math.pow((double) (r + 1) / rows, 1.8);

            for (int c = 0; c < cols; c++) {
                double x1_t = (double) c / cols * W;
                double x2_t = (double) (c + 1) / cols * W;

                boolean isDark = (r + c) % 2 == 0;
                int baseVal = isDark ? 22 : 65;
                // Darken towards distance
                double depthShade = (double) r / rows;
                int finalVal = (int) (baseVal * (0.4 + 0.6 * depthShade));
                g.setColor(new Color(finalVal, finalVal, finalVal));

                Polygon poly = new Polygon();
                poly.addPoint((int) x1_t, (int) y1);
                poly.addPoint((int) x2_t, (int) y1);
                poly.addPoint((int) x2_t, (int) y2);
                poly.addPoint((int) x1_t, (int) y2);
                g.fillPolygon(poly);
            }
        }
    }

    private static void drawCelebratePoster(Graphics2D g, int x, int y) {
        int w = 170;
        int h = 230;

        // Poster paper background with worn edges
        g.setColor(new Color(230, 225, 210));
        g.fillRect(x, y, w, h);
        g.setColor(new Color(40, 40, 45));
        g.drawRect(x, y, w, h);

        // Title banner
        g.setColor(new Color(210, 30, 30));
        g.setFont(new Font("Impact", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        String title = "CELEBRATE!";
        g.drawString(title, x + (w - fm.stringWidth(title)) / 2, y + 30);

        // Colorful confetti
        g.setColor(new Color(0, 180, 220));
        g.fillRect(x + 15, y + 10, 6, 6);
        g.setColor(new Color(240, 200, 20));
        g.fillRect(x + 145, y + 15, 6, 6);
        g.setColor(new Color(220, 50, 180));
        g.fillRect(x + 25, y + 40, 5, 5);

        // Bonnie on Left
        g.setColor(new Color(85, 75, 140)); // Lavender bunny
        g.fillOval(x + 20, y + 90, 45, 65);
        // Ears
        g.fillRoundRect(x + 22, y + 55, 12, 40, 8, 8);
        g.fillRoundRect(x + 48, y + 55, 12, 40, 8, 8);
        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(x + 28, y + 105, 10, 10);
        g.fillOval(x + 44, y + 105, 10, 10);
        g.setColor(Color.MAGENTA);
        g.fillOval(x + 31, y + 108, 4, 4);
        g.fillOval(x + 47, y + 108, 4, 4);
        // Red Bowtie
        g.setColor(Color.RED);
        int[] bx = {x + 36, x + 44, x + 40};
        int[] by = {y + 145, y + 145, y + 150};
        g.fillPolygon(bx, by, 3);

        // Chica on Right
        g.setColor(new Color(230, 195, 30)); // Yellow chicken
        g.fillOval(x + 105, y + 90, 45, 65);
        // Beak
        g.setColor(new Color(230, 110, 20));
        int[] chx = {x + 120, x + 135, x + 127};
        int[] chy = {y + 115, y + 115, y + 128};
        g.fillPolygon(chx, chy, 3);
        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(x + 112, y + 102, 10, 10);
        g.fillOval(x + 132, y + 102, 10, 10);
        g.setColor(new Color(140, 40, 200));
        g.fillOval(x + 115, y + 105, 4, 4);
        g.fillOval(x + 135, y + 105, 4, 4);

        // Freddy in Center
        g.setColor(new Color(110, 65, 35)); // Brown bear head
        g.fillOval(x + 55, y + 75, 60, 75);
        // Bear ears
        g.fillOval(x + 50, y + 68, 20, 20);
        g.fillOval(x + 100, y + 68, 20, 20);
        // Black top hat
        g.setColor(new Color(20, 20, 20));
        g.fillRect(x + 70, y + 42, 30, 32);
        g.fillRect(x + 60, y + 70, 50, 8);
        // Hat red ribbon
        g.setColor(new Color(180, 20, 20));
        g.fillRect(x + 70, y + 65, 30, 5);

        // Freddy Muzzle
        g.setColor(new Color(165, 115, 75));
        g.fillOval(x + 67, y + 110, 36, 26);
        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(x + 66, y + 95, 14, 14);
        g.fillOval(x + 90, y + 95, 14, 14);
        g.setColor(new Color(40, 120, 220));
        g.fillOval(x + 71, y + 99, 6, 6);
        g.fillOval(x + 93, y + 99, 6, 6);

        // FREDDY'S CLICKABLE NOSE!
        g.setColor(new Color(20, 20, 20));
        g.fillOval(x + 79, y + 112, 12, 9);
        g.setColor(new Color(220, 220, 220, 180));
        g.fillOval(x + 81, y + 113, 3, 2); // Nose shine
    }

    private static void drawLeftDoorway(Graphics2D g, double doorProgress, boolean lightOn, boolean bonnieAtDoor) {
        int doorX = 80;
        int doorY = 60;
        int doorW = 210;
        int doorH = 470;

        // Dark doorway opening
        g.setColor(new Color(10, 10, 14));
        g.fillRect(doorX, doorY, doorW, doorH);

        // If left light is on:
        if (lightOn) {
            // Bright hallway light cone
            GradientPaint lightBeam = new GradientPaint(doorX, doorY, new Color(255, 250, 220, 200),
                    doorX + doorW, doorY + doorH, new Color(200, 190, 140, 100));
            g.setPaint(lightBeam);
            g.fillRect(doorX, doorY, doorW, doorH);

            // If Bonnie is at the left door:
            if (bonnieAtDoor) {
                drawBonnieAtDoor(g, doorX + 35, doorY + 60);
            }
        }

        // Sliding metal blast door
        if (doorProgress > 0.0) {
            int curH = (int) (doorH * doorProgress);
            g.setColor(new Color(45, 48, 55));
            g.fillRect(doorX, doorY, doorW, curH);

            // Steel door panel lines & rivets
            g.setColor(new Color(30, 32, 38));
            for (int dy = doorY + 40; dy < doorY + curH; dy += 45) {
                g.drawLine(doorX, dy, doorX + doorW, dy);
            }

            // Hazard warning stripes at bottom of door
            if (curH > 35) {
                int stripeY = doorY + curH - 35;
                g.setColor(new Color(200, 160, 20));
                g.fillRect(doorX, stripeY, doorW, 35);
                g.setColor(Color.BLACK);
                for (int sx = doorX - 30; sx < doorX + doorW; sx += 40) {
                    Polygon stripe = new Polygon();
                    stripe.addPoint(sx, stripeY);
                    stripe.addPoint(sx + 20, stripeY);
                    stripe.addPoint(sx + 35, stripeY + 35);
                    stripe.addPoint(sx + 15, stripeY + 35);
                    g.fillPolygon(stripe);
                }
            }

            // Door border outline
            g.setColor(new Color(25, 27, 32));
            g.setStroke(new BasicStroke(4f));
            g.drawRect(doorX, doorY, doorW, curH);
        }

        // Door frame surround
        g.setColor(new Color(25, 28, 33));
        g.setStroke(new BasicStroke(14f));
        g.drawRect(doorX - 7, doorY - 7, doorW + 14, doorH + 14);

        // Control button panel on wall to right of left door
        drawControlPanel(g, doorX + doorW + 20, 260, doorProgress > 0.5, lightOn);
    }

    private static void drawRightDoorway(Graphics2D g, double doorProgress, boolean lightOn, boolean chicaAtWindow) {
        int doorX = 1310;
        int doorY = 60;
        int doorW = 210;
        int doorH = 470;

        // Dark doorway opening
        g.setColor(new Color(10, 10, 14));
        g.fillRect(doorX, doorY, doorW, doorH);

        // Sliding metal blast door
        if (doorProgress > 0.0) {
            int curH = (int) (doorH * doorProgress);
            g.setColor(new Color(45, 48, 55));
            g.fillRect(doorX, doorY, doorW, curH);

            // Door panel lines
            g.setColor(new Color(30, 32, 38));
            for (int dy = doorY + 40; dy < doorY + curH; dy += 45) {
                g.drawLine(doorX, dy, doorX + doorW, dy);
            }

            // Hazard warning stripes
            if (curH > 35) {
                int stripeY = doorY + curH - 35;
                g.setColor(new Color(200, 160, 20));
                g.fillRect(doorX, stripeY, doorW, 35);
                g.setColor(Color.BLACK);
                for (int sx = doorX - 30; sx < doorX + doorW; sx += 40) {
                    Polygon stripe = new Polygon();
                    stripe.addPoint(sx, stripeY);
                    stripe.addPoint(sx + 20, stripeY);
                    stripe.addPoint(sx + 35, stripeY + 35);
                    stripe.addPoint(sx + 15, stripeY + 35);
                    g.fillPolygon(stripe);
                }
            }

            // Outline
            g.setColor(new Color(25, 27, 32));
            g.setStroke(new BasicStroke(4f));
            g.drawRect(doorX, doorY, doorW, curH);
        }

        // Door frame
        g.setColor(new Color(25, 28, 33));
        g.setStroke(new BasicStroke(14f));
        g.drawRect(doorX - 7, doorY - 7, doorW + 14, doorH + 14);

        // Right Security Window (between office and right doorway)
        int winX = 1170;
        int winY = 140;
        int winW = 110;
        int winH = 260;

        g.setColor(new Color(8, 8, 12));
        g.fillRect(winX, winY, winW, winH);

        // If right light is on:
        if (lightOn) {
            g.setColor(new Color(245, 240, 200, 180));
            g.fillRect(winX, winY, winW, winH);

            // If Chica is at the right window:
            if (chicaAtWindow) {
                drawChicaAtWindow(g, winX + 10, winY + 30);
            }
        }

        // Window wire mesh & frame
        g.setColor(new Color(50, 50, 55, 120));
        g.setStroke(new BasicStroke(1.5f));
        for (int wx = winX + 15; wx < winX + winW; wx += 18) {
            g.drawLine(wx, winY, wx, winY + winH);
        }
        for (int wy = winY + 15; wy < winY + winH; wy += 18) {
            g.drawLine(winX, wy, winX + winW, wy);
        }
        g.setColor(new Color(25, 28, 33));
        g.setStroke(new BasicStroke(8f));
        g.drawRect(winX, winY, winW, winH);

        // Control button panel on wall to left of right door
        drawControlPanel(g, 1105, 260, doorProgress > 0.5, lightOn);
    }

    private static void drawControlPanel(Graphics2D g, int x, int y, boolean doorClosed, boolean lightOn) {
        int w = 45;
        int h = 100;

        // Panel metal casing
        g.setColor(new Color(55, 60, 68));
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(new Color(20, 22, 26));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, 8, 8);

        // DOOR BUTTON (Top button)
        int btn1Y = y + 12;
        g.setColor(doorClosed ? new Color(200, 30, 30) : new Color(30, 180, 50));
        g.fillOval(x + 10, btn1Y, 25, 25);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.drawString("DOOR", x + 10, btn1Y + 35);

        // LIGHT BUTTON (Bottom button)
        int btn2Y = y + 54;
        g.setColor(lightOn ? new Color(255, 240, 80) : new Color(190, 195, 205));
        g.fillOval(x + 10, btn2Y, 25, 25);
        if (lightOn) {
            // Glowing halo
            g.setColor(new Color(255, 240, 80, 80));
            g.fillOval(x + 5, btn2Y - 5, 35, 35);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.drawString("LIGHT", x + 8, btn2Y + 36);
    }

    private static void drawBonnieAtDoor(Graphics2D g, int x, int y) {
        // Bonnie's terrifying purple head staring through doorway
        g.setColor(new Color(90, 80, 140)); // Lavender body
        g.fillOval(x, y + 60, 140, 170);

        // Long rabbit ears
        g.fillRoundRect(x + 15, y - 50, 30, 120, 15, 15);
        g.fillRoundRect(x + 95, y - 50, 30, 120, 15, 15);
        // Inner ears
        g.setColor(new Color(130, 120, 180));
        g.fillRoundRect(x + 22, y - 40, 16, 100, 10, 10);
        g.fillRoundRect(x + 102, y - 40, 16, 100, 10, 10);

        // Bonnie Muzzle
        g.setColor(new Color(120, 110, 170));
        g.fillOval(x + 25, y + 130, 90, 60);

        // Mouth & Endoskeleton Teeth
        g.setColor(new Color(20, 20, 25));
        g.fillOval(x + 35, y + 155, 70, 35);
        g.setColor(Color.WHITE);
        for (int i = 0; i < 7; i++) {
            g.fillRect(x + 42 + i * 8, y + 160, 6, 8);
        }

        // Sinister wide eyes with glowing pink/red pupils
        g.setColor(Color.WHITE);
        g.fillOval(x + 25, y + 80, 36, 36);
        g.fillOval(x + 79, y + 80, 36, 36);
        // Glowing magenta/red pupils
        g.setColor(new Color(255, 20, 80));
        g.fillOval(x + 38, y + 92, 12, 12);
        g.fillOval(x + 92, y + 92, 12, 12);
        g.setColor(Color.WHITE);
        g.fillOval(x + 42, y + 94, 4, 4);
        g.fillOval(x + 96, y + 94, 4, 4);
    }

    private static void drawChicaAtWindow(Graphics2D g, int x, int y) {
        // Chica's yellow feathered face and gaping beak peering through window
        g.setColor(new Color(230, 195, 30));
        g.fillOval(x, y + 30, 90, 110);

        // Head tufts
        g.fillOval(x + 30, y + 10, 15, 30);
        g.fillOval(x + 45, y + 8, 15, 35);

        // Wide staring purple eyes
        g.setColor(Color.WHITE);
        g.fillOval(x + 10, y + 45, 30, 30);
        g.fillOval(x + 50, y + 45, 30, 30);
        g.setColor(new Color(150, 40, 210));
        g.fillOval(x + 18, y + 53, 12, 12);
        g.fillOval(x + 58, y + 53, 12, 12);
        g.setColor(Color.WHITE);
        g.fillOval(x + 22, y + 55, 3, 3);
        g.fillOval(x + 62, y + 55, 3, 3);

        // Gaping Orange Beak
        g.setColor(new Color(235, 115, 20));
        g.fillOval(x + 15, y + 75, 60, 45);
        // Inside mouth & Endoskeleton teeth
        g.setColor(new Color(25, 25, 30));
        g.fillOval(x + 25, y + 85, 40, 30);
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            g.fillRect(x + 30 + i * 6, y + 92, 4, 6);
        }

        // "LET'S EAT!" Bib visible below
        g.setColor(Color.WHITE);
        g.fillRoundRect(x + 10, y + 130, 70, 50, 10, 10);
        g.setColor(Color.RED);
        g.setFont(new Font("Impact", Font.BOLD, 10));
        g.drawString("LET'S", x + 25, y + 148);
        g.setColor(Color.YELLOW);
        g.drawString("EAT!!!", x + 23, y + 162);
    }

    private static void drawFreddyGlowingFace(Graphics2D g, int x, int y, double scale) {
        // Freddy's iconic Toreador March glowing eyes & teeth in the dark
        int eyeW = (int) (24 * scale);
        int eyeH = (int) (24 * scale);

        // Blue/White glowing eyes
        g.setColor(new Color(210, 240, 255));
        g.fillOval(x, y, eyeW, eyeH);
        g.fillOval(x + (int) (60 * scale), y, eyeW, eyeH);

        // Eye glow halos
        g.setColor(new Color(100, 200, 255, 90));
        g.fillOval(x - 8, y - 8, eyeW + 16, eyeH + 16);
        g.fillOval(x + (int) (60 * scale) - 8, y - 8, eyeW + 16, eyeH + 16);

        // Glowing teeth in the dark
        g.setColor(new Color(230, 235, 240, 220));
        int teethY = y + (int) (55 * scale);
        for (int i = 0; i < 7; i++) {
            g.fillRect(x + (int) (12 * scale + i * 9 * scale), teethY, (int) (7 * scale), (int) (10 * scale));
        }

        // Faint outline of bear ears & top hat in shadows
        g.setColor(new Color(30, 25, 20, 80));
        g.fillOval(x - 20, y - 30, (int) (35 * scale), (int) (35 * scale));
        g.fillOval(x + (int) (65 * scale), y - 30, (int) (35 * scale), (int) (35 * scale));
        g.fillRect(x + (int) (22 * scale), y - (int) (50 * scale), (int) (35 * scale), (int) (35 * scale));
    }

    private static void drawGoldenFreddyOffice(Graphics2D g, int x, int y) {
        // Golden Freddy sitting slumped in the center of the office
        g.setColor(new Color(195, 155, 30)); // Golden yellow suit
        // Slumped body
        g.fillOval(x + 20, y + 90, 160, 180);

        // Head tilted slightly
        g.fillOval(x + 40, y + 10, 120, 120);
        // Ears
        g.fillOval(x + 25, y, 35, 35);
        g.fillOval(x + 140, y, 35, 35);

        // Black Top Hat & Bowtie
        g.setColor(new Color(10, 10, 15));
        g.fillRect(x + 75, y - 35, 50, 45);
        g.fillRect(x + 60, y + 5, 80, 10);
        // Bowtie
        g.fillOval(x + 85, y + 125, 30, 20);

        // Empty black eye sockets with tiny pinpoint white dots
        g.setColor(Color.BLACK);
        g.fillOval(x + 60, y + 45, 28, 28);
        g.fillOval(x + 112, y + 45, 28, 28);
        // Pinpoint white glowing pupils
        g.setColor(Color.WHITE);
        g.fillOval(x + 72, y + 56, 4, 4);
        g.fillOval(x + 124, y + 56, 4, 4);

        // Hanging jaw
        g.setColor(new Color(170, 130, 25));
        g.fillOval(x + 65, y + 75, 70, 45);
        g.setColor(Color.BLACK);
        g.fillOval(x + 75, y + 90, 50, 25);
    }

    private static void drawDesk(Graphics2D g, double fanAngle) {
        // Large security guard desk in foreground
        int deskY = 520;

        // Desk top surface
        GradientPaint deskGrad = new GradientPaint(0, deskY, new Color(48, 52, 58), 0, 720, new Color(25, 28, 32));
        g.setPaint(deskGrad);
        g.fillRect(250, deskY, 1100, 200);

        // Desk front lip
        g.setColor(new Color(18, 20, 23));
        g.fillRect(250, deskY, 1100, 12);

        // Small CRT Monitor on desk (left side of desk)
        int crtX = 420;
        int crtY = 440;
        g.setColor(new Color(35, 38, 42));
        g.fillRoundRect(crtX, crtY, 120, 90, 8, 8);
        g.setColor(new Color(15, 25, 20)); // Dim green screen
        g.fillRect(crtX + 10, crtY + 10, 100, 70);
        // Scanlines on mini monitor
        g.setColor(new Color(30, 70, 40, 80));
        for (int sy = crtY + 14; sy < crtY + 75; sy += 6) {
            g.drawLine(crtX + 10, sy, crtX + 110, sy);
        }

        // Soda cup with straw (FNAF staple)
        int cupX = 990;
        int cupY = 475;
        g.setColor(new Color(220, 35, 35)); // Red plastic cup
        int[] cupPx = {cupX, cupX + 35, cupX + 30, cupX + 5};
        int[] cupPy = {cupY, cupY, cupY + 55, cupY + 55};
        g.fillPolygon(cupPx, cupPy, 4);
        // White cup stripes
        g.setColor(Color.WHITE);
        g.drawLine(cupX + 2, cupY + 25, cupX + 33, cupY + 25);
        // Red plastic straw bent
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(3f));
        g.drawLine(cupX + 18, cupY + 10, cupX + 22, cupY - 20);
        g.drawLine(cupX + 22, cupY - 20, cupX + 35, cupY - 25);

        // Crumpled paper balls on desk
        g.setColor(new Color(200, 200, 195));
        g.fillOval(580, 525, 18, 15);
        g.fillOval(920, 532, 22, 17);
        g.fillOval(945, 528, 14, 13);

        // Electric Fan (Center desk)
        drawElectricFan(g, 770, 420, fanAngle);
    }

    private static void drawElectricFan(Graphics2D g, int x, int y, double fanAngle) {
        // Fan base & stand
        g.setColor(new Color(30, 32, 36));
        g.fillOval(x + 20, y + 105, 50, 18);
        g.fillRect(x + 40, y + 65, 10, 45);

        // Fan motor housing (center)
        g.setColor(new Color(40, 44, 50));
        g.fillOval(x + 35, y + 35, 20, 20);

        // Fan wire cage
        int cageRadius = 45;
        int cx = x + 45;
        int cy = y + 45;
        g.setColor(new Color(75, 80, 88));
        g.setStroke(new BasicStroke(2f));
        g.drawOval(cx - cageRadius, cy - cageRadius, cageRadius * 2, cageRadius * 2);
        g.drawOval(cx - cageRadius / 2, cy - cageRadius / 2, cageRadius, cageRadius);

        // Spinning blades
        g.setColor(new Color(130, 135, 145, 180));
        for (int i = 0; i < 3; i++) {
            double angle = fanAngle + i * (2 * Math.PI / 3);
            int bx = (int) (cx + Math.cos(angle) * 36);
            int by = (int) (cy + Math.sin(angle) * 36);
            g.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(cx, cy, bx, by);
        }

        // Center silver cap
        g.setColor(new Color(180, 185, 195));
        g.fillOval(cx - 7, cy - 7, 14, 14);
    }

    private static void drawOfficeLighting(Graphics2D g, int W, int H, boolean leftLightOn, boolean rightLightOn) {
        // Moody office ceiling light (dim overhead cone centered around desk)
        Point2D center = new Point2D.Float(800, 280);
        float radius = 680;
        float[] dist = {0.0f, 0.45f, 1.0f};
        Color[] colors = {
                new Color(255, 245, 210, 45),
                new Color(20, 25, 30, 160),
                new Color(5, 5, 8, 240)
        };
        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        g.setPaint(p);
        g.fillRect(0, 0, W, H);
    }

    // ==========================================
    // CAMERA VIEWS RENDERING (11 ROOMS)
    // ==========================================

    public static void drawCameraFeed(Graphics2D g,
                                      RoomId room,
                                      Animatronic freddy,
                                      Animatronic bonnie,
                                      Animatronic chica,
                                      Animatronic foxy,
                                      boolean goldenFreddyPoster,
                                      int screenW,
                                      int screenH) {
        // Base dark room background
        g.setColor(new Color(12, 14, 18));
        g.fillRect(0, 0, screenW, screenH);

        switch (room) {
            case STAGE_1A:
                drawCam1A_Stage(g, freddy, bonnie, chica, screenW, screenH);
                break;
            case DINING_1B:
                drawCam1B_Dining(g, freddy, bonnie, chica, screenW, screenH);
                break;
            case PIRATE_COVE_1C:
                drawCam1C_PirateCove(g, foxy, screenW, screenH);
                break;
            case WEST_HALL_2A:
                drawCam2A_WestHall(g, bonnie, foxy, screenW, screenH);
                break;
            case WEST_CORNER_2B:
                drawCam2B_WestCorner(g, bonnie, goldenFreddyPoster, screenW, screenH);
                break;
            case SUPPLY_CLOSET_3:
                drawCam3_SupplyCloset(g, bonnie, screenW, screenH);
                break;
            case EAST_HALL_4A:
                drawCam4A_EastHall(g, chica, freddy, screenW, screenH);
                break;
            case EAST_CORNER_4B:
                drawCam4B_EastCorner(g, chica, freddy, screenW, screenH);
                break;
            case BACKSTAGE_5:
                drawCam5_Backstage(g, bonnie, screenW, screenH);
                break;
            case KITCHEN_6:
                drawCam6_Kitchen(g, chica, freddy, screenW, screenH);
                break;
            case RESTROOMS_7:
                drawCam7_Restrooms(g, chica, freddy, screenW, screenH);
                break;
            default:
                drawGenericRoom(g, room.getDisplayName(), screenW, screenH);
                break;
        }

        // Camera timestamp & recording overlay
        drawCameraHUD(g, room, screenW, screenH);
    }

    private static void drawCam1A_Stage(Graphics2D g, Animatronic freddy, Animatronic bonnie, Animatronic chica, int W, int H) {
        // Stage red curtains & brick wall
        g.setColor(new Color(130, 20, 25));
        g.fillRect(0, 0, 180, H);
        g.fillRect(W - 180, 0, 180, H);
        g.fillRect(0, 0, W, 100);

        // Brick wall backdrop
        g.setColor(new Color(50, 45, 55));
        g.fillRect(180, 100, W - 360, H - 240);

        // Wood stage platform
        g.setColor(new Color(60, 40, 25));
        g.fillRect(140, H - 180, W - 280, 180);

        // Spotlight cone
        GradientPaint spot = new GradientPaint(W / 2f, 80, new Color(255, 255, 230, 70), W / 2f, H - 100, new Color(0, 0, 0, 0));
        g.setPaint(spot);
        g.fillPolygon(new int[]{W / 2, 100, W - 100}, new int[]{50, H, H}, 3);

        boolean freddyOnStage = freddy.getCurrentRoom() == RoomId.STAGE_1A;
        boolean bonnieOnStage = bonnie.getCurrentRoom() == RoomId.STAGE_1A;
        boolean chicaOnStage = chica.getCurrentRoom() == RoomId.STAGE_1A;

        // Bonnie (Left)
        if (bonnieOnStage) {
            drawStageBonnie(g, W / 2 - 240, H - 380);
        }
        // Freddy (Center)
        if (freddyOnStage) {
            drawStageFreddy(g, W / 2 - 70, H - 410);
        }
        // Chica (Right)
        if (chicaOnStage) {
            drawStageChica(g, W / 2 + 110, H - 380);
        }
    }

    private static void drawStageFreddy(Graphics2D g, int x, int y) {
        // Freddy with top hat & mic on stage
        g.setColor(new Color(105, 65, 35));
        g.fillOval(x + 15, y + 100, 110, 170); // Torso
        g.fillOval(x + 25, y + 20, 90, 90);    // Head
        g.fillOval(x + 15, y + 10, 30, 30);    // Ears
        g.fillOval(x + 95, y + 10, 30, 30);

        // Top hat
        g.setColor(new Color(15, 15, 18));
        g.fillRect(x + 50, y - 25, 40, 45);
        g.fillRect(x + 35, y + 15, 70, 10);

        // Muzzle & Eyes
        g.setColor(new Color(155, 110, 70));
        g.fillOval(x + 40, y + 60, 60, 40);
        g.setColor(Color.WHITE);
        g.fillOval(x + 42, y + 42, 20, 20);
        g.fillOval(x + 78, y + 42, 20, 20);
        g.setColor(new Color(40, 130, 240));
        g.fillOval(x + 48, y + 47, 8, 8);
        g.fillOval(x + 84, y + 47, 8, 8);

        // Microphone in hand
        g.setColor(Color.LIGHT_GRAY);
        g.fillOval(x + 105, y + 140, 16, 20);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x + 110, y + 155, 6, 30);
    }

    private static void drawStageBonnie(Graphics2D g, int x, int y) {
        g.setColor(new Color(85, 75, 140));
        g.fillOval(x + 15, y + 90, 100, 160);
        g.fillOval(x + 25, y + 20, 80, 80);
        // Long rabbit ears
        g.fillRoundRect(x + 20, y - 60, 22, 90, 10, 10);
        g.fillRoundRect(x + 80, y - 60, 22, 90, 10, 10);

        // Muzzle & Eyes
        g.setColor(new Color(125, 115, 175));
        g.fillOval(x + 35, y + 60, 60, 35);
        g.setColor(Color.WHITE);
        g.fillOval(x + 38, y + 40, 18, 18);
        g.fillOval(x + 74, y + 40, 18, 18);
        g.setColor(Color.MAGENTA);
        g.fillOval(x + 44, y + 45, 7, 7);
        g.fillOval(x + 80, y + 45, 7, 7);

        // Red Electric Guitar
        g.setColor(new Color(220, 30, 40));
        g.fillOval(x + 30, y + 150, 70, 40);
        g.setColor(Color.WHITE);
        g.fillRect(x - 20, y + 110, 80, 8);
    }

    private static void drawStageChica(Graphics2D g, int x, int y) {
        g.setColor(new Color(230, 195, 30));
        g.fillOval(x + 15, y + 90, 100, 160);
        g.fillOval(x + 25, y + 20, 80, 80);

        // Beak & Eyes
        g.setColor(new Color(230, 110, 20));
        g.fillOval(x + 40, y + 60, 50, 30);
        g.setColor(Color.WHITE);
        g.fillOval(x + 38, y + 38, 18, 18);
        g.fillOval(x + 74, y + 38, 18, 18);
        g.setColor(new Color(140, 40, 200));
        g.fillOval(x + 44, y + 43, 7, 7);
        g.fillOval(x + 80, y + 43, 7, 7);

        // "LET'S EAT" Bib
        g.setColor(Color.WHITE);
        g.fillRoundRect(x + 35, y + 115, 60, 50, 8, 8);
        g.setColor(Color.RED);
        g.setFont(new Font("Impact", Font.BOLD, 12));
        g.drawString("LET'S", x + 48, y + 135);
        g.drawString("EAT!", x + 50, y + 152);

        // Cupcake on plate (in hand)
        g.setColor(Color.PINK);
        g.fillOval(x + 95, y + 140, 24, 20);
        g.setColor(Color.YELLOW);
        g.fillRect(x + 105, y + 130, 4, 12); // Candle
    }

    private static void drawCam1B_Dining(Graphics2D g, Animatronic freddy, Animatronic bonnie, Animatronic chica, int W, int H) {
        // Dining area with long tables, party hats, checkered cloths
        g.setColor(new Color(25, 28, 35));
        g.fillRect(0, 0, W, H);

        // Tables stretching back
        for (int row = 0; row < 4; row++) {
            int ty = 280 + row * 90;
            int tw = 380 + row * 180;
            int tx = (W - tw) / 2;

            g.setColor(new Color(180, 180, 185));
            g.fillRect(tx, ty, tw, 25 + row * 6);
            // Party hats on tables
            g.setColor(new Color(220, 40, 40));
            int[] phx = {tx + 60, tx + 75, tx + 67};
            int[] phy = {ty, ty, ty - 25};
            g.fillPolygon(phx, phy, 3);
            g.setColor(new Color(40, 160, 220));
            int[] ph2x = {tx + tw - 80, tx + tw - 65, tx + tw - 72};
            int[] ph2y = {ty, ty, ty - 25};
            g.fillPolygon(ph2x, ph2y, 3);
        }

        // Animatronic silhouettes if in dining room
        if (bonnie.getCurrentRoom() == RoomId.DINING_1B) {
            g.setColor(new Color(45, 40, 75));
            g.fillOval(280, 220, 100, 140);
            g.fillRoundRect(295, 140, 18, 90, 8, 8);
            g.fillRoundRect(345, 140, 18, 90, 8, 8);
            g.setColor(Color.MAGENTA);
            g.fillOval(315, 250, 8, 8);
            g.fillOval(345, 250, 8, 8);
        }
        if (chica.getCurrentRoom() == RoomId.DINING_1B) {
            g.setColor(new Color(120, 100, 20));
            g.fillOval(840, 240, 110, 150);
            g.setColor(new Color(140, 40, 200));
            g.fillOval(870, 275, 8, 8);
            g.fillOval(905, 275, 8, 8);
        }
        if (freddy.getCurrentRoom() == RoomId.DINING_1B) {
            g.setColor(new Color(40, 25, 15));
            g.fillOval(560, 200, 120, 150);
            g.setColor(new Color(50, 150, 255));
            g.fillOval(600, 235, 7, 7);
            g.fillOval(635, 235, 7, 7);
        }
    }

    private static void drawCam1C_PirateCove(Graphics2D g, Animatronic foxy, int W, int H) {
        // Pirate Cove purple starry curtains & "OUT OF ORDER" sign
        int foxyStage = foxy.getFoxyStage();

        // Stage Floor & Back Wall
        g.setColor(new Color(20, 18, 25));
        g.fillRect(0, 0, W, H);

        int coveX = W / 2 - 320;
        int coveY = 80;
        int coveW = 640;
        int coveH = 500;

        // Wood stage platform
        g.setColor(new Color(55, 35, 20));
        g.fillRect(coveX - 40, coveY + coveH - 80, coveW + 80, 120);

        // Purple starry curtains
        g.setColor(new Color(75, 30, 95));

        if (foxyStage == 0) {
            // Stage 0: Fully closed curtains
            g.fillRect(coveX, coveY, coveW, coveH - 80);
            drawCurtainStars(g, coveX, coveY, coveW, coveH - 80);
        } else if (foxyStage == 1) {
            // Stage 1: Curtains slightly parted, Foxy's head peeking out
            g.fillRect(coveX, coveY, coveW / 2 - 60, coveH - 80);
            g.fillRect(coveX + coveW / 2 + 60, coveY, coveW / 2 - 60, coveH - 80);
            drawCurtainStars(g, coveX, coveY, coveW / 2 - 60, coveH - 80);
            drawCurtainStars(g, coveX + coveW / 2 + 60, coveY, coveW / 2 - 60, coveH - 80);

            // Foxy peeking in darkness
            drawFoxyPeeking(g, coveX + coveW / 2 - 45, coveY + 120);
        } else if (foxyStage == 2) {
            // Stage 2: Stepped outside curtains, body leaning forward!
            g.fillRect(coveX, coveY, 140, coveH - 80);
            g.fillRect(coveX + coveW - 140, coveY, 140, coveH - 80);
            drawCurtainStars(g, coveX, coveY, 140, coveH - 80);
            drawCurtainStars(g, coveX + coveW - 140, coveY, 140, coveH - 80);

            // Full Foxy outside cove
            drawFoxyOutside(g, coveX + coveW / 2 - 75, coveY + 80);
        } else {
            // Stage 3: Curtains wide open, Pirate Cove is COMPLETELY EMPTY!
            g.fillRect(coveX, coveY, 70, coveH - 80);
            g.fillRect(coveX + coveW - 70, coveY, 70, coveH - 80);
            // Empty stage shadows
            g.setColor(new Color(10, 8, 12));
            g.fillRect(coveX + 70, coveY, coveW - 140, coveH - 80);
        }

        // Wooden Pirate Sign in foreground
        int signX = coveX + 40;
        int signY = coveY + coveH - 120;
        g.setColor(new Color(110, 80, 45));
        g.fillRect(signX, signY, 160, 80);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Impact", Font.BOLD, 16));
        if (foxyStage >= 2 && RAND.nextBoolean()) {
            g.drawString("IT'S ME", signX + 45, signY + 45);
        } else {
            g.drawString("OUT OF", signX + 45, signY + 35);
            g.drawString("ORDER", signX + 50, signY + 60);
        }
    }

    private static void drawCurtainStars(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(230, 220, 240, 160));
        for (int sx = x + 30; sx < x + w - 20; sx += 60) {
            for (int sy = y + 40; sy < y + h - 20; sy += 70) {
                // 4-point star
                g.drawLine(sx - 6, sy, sx + 6, sy);
                g.drawLine(sx, sy - 6, sx, sy + 6);
            }
        }
    }

    private static void drawFoxyPeeking(Graphics2D g, int x, int y) {
        // Reddish fox head peering between curtains
        g.setColor(new Color(175, 45, 30));
        g.fillOval(x, y, 90, 95);
        // Pointy ears
        int[] e1x = {x + 5, x + 30, x + 15};
        int[] e1y = {y + 15, y + 15, y - 30};
        g.fillPolygon(e1x, e1y, 3);
        int[] e2x = {x + 60, x + 85, x + 75};
        int[] e2y = {y + 15, y + 15, y - 30};
        g.fillPolygon(e2x, e2y, 3);

        // Glowing yellow eye
        g.setColor(new Color(255, 230, 50));
        g.fillOval(x + 20, y + 30, 16, 16);
        // Eyepatch over other eye
        g.setColor(Color.BLACK);
        g.fillOval(x + 55, y + 28, 20, 20);
        g.drawLine(x + 40, y + 25, x + 85, y + 40);

        // Sharp silver teeth
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            int[] tx = {x + 25 + i * 8, x + 31 + i * 8, x + 28 + i * 8};
            int[] ty = {y + 70, y + 70, y + 82};
            g.fillPolygon(tx, ty, 3);
        }
    }

    private static void drawFoxyOutside(Graphics2D g, int x, int y) {
        // Full Foxy leaning forward outside cove
        g.setColor(new Color(175, 45, 30));
        g.fillOval(x + 15, y + 90, 120, 170); // Torso with exposed endoskeleton
        g.fillOval(x + 30, y + 10, 90, 95);   // Head

        // Exposed endoskeleton ribs
        g.setColor(Color.GRAY);
        g.fillRect(x + 50, y + 140, 50, 10);
        g.fillRect(x + 55, y + 160, 40, 10);

        // Pirate Hook hand!
        g.setColor(new Color(210, 215, 220));
        g.setStroke(new BasicStroke(6f));
        Path2D hook = new Path2D.Double();
        hook.moveTo(x + 130, y + 160);
        hook.curveTo(x + 160, y + 140, x + 160, y + 110, x + 135, y + 115);
        g.draw(hook);

        // Gaping jaw
        g.setColor(new Color(130, 30, 20));
        g.fillOval(x + 40, y + 65, 70, 50);
        g.setColor(Color.WHITE);
        for (int i = 0; i < 6; i++) {
            g.fillRect(x + 48 + i * 9, y + 85, 5, 10);
        }

        // Glowing yellow eye
        g.setColor(new Color(255, 230, 50));
        g.fillOval(x + 45, y + 35, 18, 18);
        g.setColor(Color.BLACK);
        g.fillOval(x + 85, y + 35, 20, 20); // Eyepatch
    }

    private static void drawCam2A_WestHall(Graphics2D g, Animatronic bonnie, Animatronic foxy, int W, int H) {
        // Perspective hallway
        g.setColor(new Color(20, 22, 28));
        g.fillRect(0, 0, W, H);

        // Vanishing point in center
        int cx = W / 2;
        int cy = H / 2 - 20;

        g.setColor(new Color(40, 44, 52));
        Polygon ceiling = new Polygon(new int[]{0, W, cx + 80, cx - 80}, new int[]{0, 0, cy - 60, cy - 60}, 4);
        g.fillPolygon(ceiling);

        Polygon floor = new Polygon(new int[]{0, W, cx + 100, cx - 100}, new int[]{H, H, cy + 80, cy + 80}, 4);
        g.setColor(new Color(30, 32, 38));
        g.fillPolygon(floor);

        // Checkered tiles on floor
        g.setColor(new Color(55, 58, 65));
        for (int i = 0; i < 6; i++) {
            int y1 = cy + 80 + i * 35;
            g.drawLine(cx - 100 - i * 35, y1, cx + 100 + i * 35, y1);
        }

        // Foxy SPRINT ANIMATION (CAM 2A)!
        if (foxy.isFoxyRunning()) {
            double sprintProgress = 1.0 - (foxy.getFoxySprintTimer() / 3.2);
            int fx = (int) (cx - 120 + sprintProgress * 40);
            int fy = (int) (cy - 30 + sprintProgress * 140);
            double scale = 0.6 + sprintProgress * 1.4;
            drawFoxySprinting(g, fx, fy, scale);
        } else if (bonnie.getCurrentRoom() == RoomId.WEST_HALL_2A) {
            // Bonnie standing in hallway
            g.setColor(new Color(65, 55, 110));
            g.fillOval(cx - 120, cy - 20, 90, 160);
            g.setColor(Color.MAGENTA);
            g.fillOval(cx - 85, cy + 10, 8, 8);
        }
    }

    private static void drawFoxySprinting(Graphics2D g, int x, int y, double scale) {
        // Violent sprint animation down CAM 2A
        int w = (int) (140 * scale);
        int h = (int) (180 * scale);

        g.setColor(new Color(185, 45, 30));
        g.fillOval(x, y + (int) (40 * scale), w, h); // Body lunging
        g.fillOval(x + (int) (20 * scale), y, (int) (90 * scale), (int) (90 * scale)); // Head

        // Eyepatch flying up, teeth bared
        g.setColor(new Color(255, 235, 40));
        g.fillOval(x + (int) (35 * scale), y + (int) (25 * scale), (int) (18 * scale), (int) (18 * scale));
        g.fillOval(x + (int) (70 * scale), y + (int) (25 * scale), (int) (18 * scale), (int) (18 * scale));

        // Open jaw with rows of sharp teeth
        g.setColor(new Color(10, 10, 15));
        g.fillOval(x + (int) (30 * scale), y + (int) (55 * scale), (int) (70 * scale), (int) (45 * scale));
        g.setColor(Color.WHITE);
        for (int i = 0; i < 6; i++) {
            g.fillRect(x + (int) ((38 + i * 9) * scale), y + (int) (68 * scale), (int) (5 * scale), (int) (12 * scale));
        }

        // Silver hook slashing forward
        g.setColor(Color.LIGHT_GRAY);
        g.setStroke(new BasicStroke((float) (5f * scale)));
        g.drawLine(x + w, y + (int) (70 * scale), x + w + (int) (40 * scale), y + (int) (40 * scale));
    }

    private static void drawCam2B_WestCorner(Graphics2D g, Animatronic bonnie, boolean goldenFreddyPoster, int W, int H) {
        g.setColor(new Color(22, 24, 30));
        g.fillRect(0, 0, W, H);

        // Hallway corner
        g.setColor(new Color(35, 38, 45));
        g.fillRect(380, 0, W - 380, H);
        g.setColor(new Color(15, 17, 22));
        g.setStroke(new BasicStroke(4f));
        g.drawLine(380, 0, 380, H);

        // Poster on wall (Chance for Golden Freddy poster!)
        int postX = 140;
        int postY = 160;
        if (goldenFreddyPoster) {
            // Golden Freddy close-up poster!
            g.setColor(new Color(210, 170, 30));
            g.fillRect(postX, postY, 160, 210);
            g.setColor(Color.BLACK);
            g.fillOval(postX + 30, postY + 40, 35, 35);
            g.fillOval(postX + 95, postY + 40, 35, 35);
            g.setColor(Color.WHITE);
            g.fillOval(postX + 45, postY + 52, 6, 6);
            g.fillOval(postX + 110, postY + 52, 6, 6);
        } else {
            // Normal Freddy poster
            g.setColor(new Color(220, 215, 200));
            g.fillRect(postX, postY, 160, 210);
            g.setColor(new Color(110, 70, 35));
            g.fillOval(postX + 35, postY + 40, 90, 90);
        }

        // Bonnie lurking right in front of camera
        if (bonnie.getCurrentRoom() == RoomId.WEST_CORNER_2B) {
            g.setColor(new Color(85, 75, 140));
            g.fillOval(450, 80, 340, 480); // Giant close-up head
            g.setColor(Color.WHITE);
            g.fillOval(510, 220, 65, 65);
            g.fillOval(660, 220, 65, 65);
            g.setColor(Color.MAGENTA);
            g.fillOval(535, 245, 22, 22);
            g.fillOval(685, 245, 22, 22);
        }
    }

    private static void drawCam3_SupplyCloset(Graphics2D g, Animatronic bonnie, int W, int H) {
        g.setColor(new Color(18, 19, 24));
        g.fillRect(0, 0, W, H);

        // Metal Shelves with chemical bottles and mops
        g.setColor(new Color(45, 48, 55));
        g.fillRect(150, 120, 320, 16);
        g.fillRect(150, 260, 320, 16);
        g.fillRect(150, 400, 320, 16);

        // Bottles
        g.setColor(new Color(40, 180, 140));
        g.fillRect(180, 80, 30, 40);
        g.setColor(new Color(220, 80, 40));
        g.fillRect(230, 70, 25, 50);

        // Mop and bucket
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(780, 380, 80, 70); // Bucket
        g.setColor(new Color(140, 90, 40));
        g.setStroke(new BasicStroke(6f));
        g.drawLine(820, 400, 890, 150); // Mop handle

        // Bonnie lurking in shadows
        if (bonnie.getCurrentRoom() == RoomId.SUPPLY_CLOSET_3) {
            g.setColor(new Color(50, 45, 80));
            g.fillOval(480, 180, 160, 260);
            g.setColor(Color.MAGENTA);
            g.fillOval(530, 250, 10, 10);
            g.fillOval(590, 250, 10, 10);
        }
    }

    private static void drawCam4A_EastHall(Graphics2D g, Animatronic chica, Animatronic freddy, int W, int H) {
        g.setColor(new Color(24, 25, 30));
        g.fillRect(0, 0, W, H);

        // Hallway walls with party streamers
        g.setColor(new Color(210, 40, 40));
        g.drawLine(100, 80, W - 100, 120);
        g.setColor(new Color(40, 180, 220));
        g.drawLine(100, 110, W - 100, 90);

        if (chica.getCurrentRoom() == RoomId.EAST_HALL_4A) {
            g.setColor(new Color(180, 150, 20));
            g.fillOval(W / 2 - 80, H / 2 - 60, 160, 240);
            g.setColor(Color.MAGENTA);
            g.fillOval(W / 2 - 30, H / 2, 10, 10);
            g.fillOval(W / 2 + 20, H / 2, 10, 10);
        }
        if (freddy.getCurrentRoom() == RoomId.EAST_HALL_4A) {
            g.setColor(new Color(45, 28, 16));
            g.fillOval(W / 2 - 70, H / 2 - 70, 140, 220);
            g.setColor(new Color(40, 140, 255));
            g.fillOval(W / 2 - 25, H / 2 - 10, 8, 8);
            g.fillOval(W / 2 + 15, H / 2 - 10, 8, 8);
        }
    }

    private static void drawCam4B_EastCorner(Graphics2D g, Animatronic chica, Animatronic freddy, int W, int H) {
        g.setColor(new Color(20, 22, 26));
        g.fillRect(0, 0, W, H);

        if (chica.getCurrentRoom() == RoomId.EAST_CORNER_4B) {
            // Close up of Chica twitching into lens
            g.setColor(new Color(225, 190, 25));
            g.fillOval(380, 80, 360, 480);
            g.setColor(Color.WHITE);
            g.fillOval(450, 220, 60, 60);
            g.fillOval(610, 220, 60, 60);
            g.setColor(new Color(140, 40, 200));
            g.fillOval(470, 240, 22, 22);
            g.fillOval(630, 240, 22, 22);
            // Gaping beak
            g.setColor(new Color(230, 110, 20));
            g.fillOval(480, 310, 160, 110);
        } else if (freddy.getCurrentRoom() == RoomId.EAST_CORNER_4B) {
            // Freddy lurking in the dark outside your door
            g.setColor(new Color(25, 16, 10));
            g.fillOval(420, 110, 320, 420);
            // Glowing blue eyes in pitch darkness
            g.setColor(new Color(70, 160, 255));
            g.fillOval(500, 250, 16, 16);
            g.fillOval(620, 250, 16, 16);
        }
    }

    private static void drawCam5_Backstage(Graphics2D g, Animatronic bonnie, int W, int H) {
        g.setColor(new Color(22, 24, 28));
        g.fillRect(0, 0, W, H);

        // Workbench table with spare animatronic heads
        g.setColor(new Color(48, 42, 36));
        g.fillRect(180, 380, 780, 160);

        // Bare metal Endoskeleton head staring into camera!
        g.setColor(Color.GRAY);
        g.fillOval(420, 280, 80, 100);
        g.setColor(Color.WHITE);
        g.fillOval(435, 310, 18, 18);
        g.fillOval(465, 310, 18, 18);

        // Spare Freddy head on shelf
        g.setColor(new Color(110, 70, 35));
        g.fillOval(640, 260, 90, 90);

        if (bonnie.getCurrentRoom() == RoomId.BACKSTAGE_5) {
            // Bonnie staring intently into camera
            g.setColor(new Color(85, 75, 140));
            g.fillOval(720, 140, 220, 320);
            g.setColor(Color.WHITE);
            g.fillOval(760, 220, 35, 35);
            g.fillOval(840, 220, 35, 35);
            g.setColor(Color.MAGENTA);
            g.fillOval(775, 235, 12, 12);
            g.fillOval(855, 235, 12, 12);
        }
    }

    private static void drawCam6_Kitchen(Graphics2D g, Animatronic chica, Animatronic freddy, int W, int H) {
        // Kitchen is AUDIO ONLY in FNAF 1!
        g.setColor(new Color(10, 10, 14));
        g.fillRect(0, 0, W, H);

        g.setColor(new Color(220, 40, 40));
        g.setFont(new Font("Impact", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        String msg = "- CAMERA DISABLED -";
        g.drawString(msg, (W - fm.stringWidth(msg)) / 2, H / 2 - 30);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        fm = g.getFontMetrics();
        String sub = "AUDIO ONLY";
        g.drawString(sub, (W - fm.stringWidth(sub)) / 2, H / 2 + 20);

        // Oscilloscope / Audio waveform
        g.setColor(new Color(40, 220, 120));
        g.setStroke(new BasicStroke(2f));
        int waveY = H / 2 + 90;
        int lastY = waveY;
        boolean audioActive = (chica.getCurrentRoom() == RoomId.KITCHEN_6 || freddy.getCurrentRoom() == RoomId.KITCHEN_6);

        for (int x = 200; x < W - 200; x += 12) {
            int curY = waveY + (audioActive ? (int) ((RAND.nextDouble() - 0.5) * 50) : (int) ((RAND.nextDouble() - 0.5) * 6));
            g.drawLine(x - 12, lastY, x, curY);
            lastY = curY;
        }

        if (chica.getCurrentRoom() == RoomId.KITCHEN_6) {
            g.setColor(new Color(255, 230, 50));
            g.setFont(new Font("Monospaced", Font.BOLD, 14));
            g.drawString(">> AUDIO DETECTED: POTS & PANS CLATTERING <<", (W - 380) / 2, H / 2 + 150);
        } else if (freddy.getCurrentRoom() == RoomId.KITCHEN_6) {
            g.setColor(new Color(100, 200, 255));
            g.setFont(new Font("Monospaced", Font.BOLD, 14));
            g.drawString(">> AUDIO DETECTED: FAINT MUSIC BOX CHIMING <<", (W - 390) / 2, H / 2 + 150);
        }
    }

    private static void drawCam7_Restrooms(Graphics2D g, Animatronic chica, Animatronic freddy, int W, int H) {
        g.setColor(new Color(25, 28, 35));
        g.fillRect(0, 0, W, H);

        // Restroom doors with male/female icons
        g.setColor(new Color(45, 50, 60));
        g.fillRect(240, 160, 140, 320);
        g.fillRect(780, 160, 140, 320);

        g.setColor(Color.WHITE);
        g.drawString("MEN", 290, 200);
        g.drawString("WOMEN", 820, 200);

        if (chica.getCurrentRoom() == RoomId.RESTROOMS_7) {
            g.setColor(new Color(180, 150, 20));
            g.fillOval(510, 180, 160, 240);
            g.setColor(Color.MAGENTA);
            g.fillOval(550, 240, 10, 10);
            g.fillOval(610, 240, 10, 10);
        }
        if (freddy.getCurrentRoom() == RoomId.RESTROOMS_7) {
            g.setColor(new Color(40, 25, 15));
            g.fillOval(520, 160, 150, 230);
            g.setColor(new Color(40, 140, 255));
            g.fillOval(560, 220, 8, 8);
            g.fillOval(610, 220, 8, 8);
        }
    }

    private static void drawGenericRoom(Graphics2D g, String name, int W, int H) {
        g.setColor(new Color(20, 22, 28));
        g.fillRect(0, 0, W, H);
        g.setColor(Color.WHITE);
        g.drawString(name, W / 2 - 40, H / 2);
    }

    private static void drawCameraHUD(Graphics2D g, RoomId room, int W, int H) {
        // Red flashing "● REC" indicator
        if (System.currentTimeMillis() % 1000 < 600) {
            g.setColor(new Color(220, 30, 30));
            g.fillOval(50, 45, 18, 18);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.drawString("REC", 78, 60);

        // Camera Title Box (Upper Right)
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        g.drawString(room.getCode() + " - " + room.getDisplayName(), 50, 100);
    }

    // ==========================================
    // CRT SCANLINES & STATIC GENERATOR
    // ==========================================

    public static void drawCRTScanlines(Graphics2D g, int W, int H) {
        g.setColor(new Color(0, 0, 0, 35));
        for (int y = 0; y < H; y += 4) {
            g.drawLine(0, y, W, y);
        }
    }

    public static void drawStaticNoise(Graphics2D g, int W, int H, float opacity) {
        int dots = (int) (W * H * 0.04 * opacity);
        for (int i = 0; i < dots; i++) {
            int x = RAND.nextInt(W);
            int y = RAND.nextInt(H);
            int gray = RAND.nextInt(180) + 75;
            g.setColor(new Color(gray, gray, gray, (int) (opacity * 255)));
            g.fillRect(x, y, 2, 2);
        }
    }
}
