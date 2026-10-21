package fnaf.render;

import fnaf.audio.SoundManager;
import fnaf.model.*;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;

/**
 * Handles monitor rendering, switching between 11 cameras, the interactive minimap,
 * and CRT scanlines/static overlay.
 */
public class CameraRenderer {
    private RoomId activeCamera = RoomId.STAGE_1A;
    private final Map<RoomId, Rectangle> cameraButtonBounds = new HashMap<>();

    private double staticBurstTimer = 0.0;
    private boolean debugOverlay = false;

    // Minimap coordinates offset (lower right of 1280x720)
    private static final int MAP_X = 860;
    private static final int MAP_Y = 320;
    private static final int MAP_W = 380;
    private static final int MAP_H = 340;

    public CameraRenderer() {
        initButtonBounds();
    }

    private void initButtonBounds() {
        // Layout buttons based on actual FNAF 1 pizzeria layout
        int bw = 65;
        int bh = 36;

        cameraButtonBounds.put(RoomId.STAGE_1A, new Rectangle(MAP_X + 130, MAP_Y + 20, bw, bh));
        cameraButtonBounds.put(RoomId.DINING_1B, new Rectangle(MAP_X + 115, MAP_Y + 85, bw, bh));
        cameraButtonBounds.put(RoomId.PIRATE_COVE_1C, new Rectangle(MAP_X + 45, MAP_Y + 125, bw, bh));
        cameraButtonBounds.put(RoomId.BACKSTAGE_5, new Rectangle(MAP_X + 10, MAP_Y + 70, bw, bh));
        cameraButtonBounds.put(RoomId.RESTROOMS_7, new Rectangle(MAP_X + 285, MAP_Y + 85, bw, bh));
        cameraButtonBounds.put(RoomId.KITCHEN_6, new Rectangle(MAP_X + 285, MAP_Y + 175, bw, bh));
        cameraButtonBounds.put(RoomId.WEST_HALL_2A, new Rectangle(MAP_X + 75, MAP_Y + 195, bw, bh));
        cameraButtonBounds.put(RoomId.SUPPLY_CLOSET_3, new Rectangle(MAP_X + 5, MAP_Y + 205, bw, bh));
        cameraButtonBounds.put(RoomId.WEST_CORNER_2B, new Rectangle(MAP_X + 75, MAP_Y + 260, bw, bh));
        cameraButtonBounds.put(RoomId.EAST_HALL_4A, new Rectangle(MAP_X + 215, MAP_Y + 195, bw, bh));
        cameraButtonBounds.put(RoomId.EAST_CORNER_4B, new Rectangle(MAP_X + 215, MAP_Y + 260, bw, bh));
    }

    public void update(double dtSeconds) {
        if (staticBurstTimer > 0) {
            staticBurstTimer -= dtSeconds;
        }
    }

    public boolean handleMouseClick(int mouseX, int mouseY, SoundManager soundManager) {
        for (Map.Entry<RoomId, Rectangle> entry : cameraButtonBounds.entrySet()) {
            if (entry.getValue().contains(mouseX, mouseY)) {
                if (activeCamera != entry.getKey()) {
                    activeCamera = entry.getKey();
                    staticBurstTimer = 0.25; // 0.25s static burst on camera switch
                    soundManager.playCameraSwitch();
                }
                return true;
            }
        }
        return false;
    }

    public void selectCamera(RoomId room, SoundManager soundManager) {
        if (room != null && room.isCamera() && activeCamera != room) {
            activeCamera = room;
            staticBurstTimer = 0.25;
            soundManager.playCameraSwitch();
        }
    }

    public void render(Graphics2D g,
                       Animatronic freddy,
                       Animatronic bonnie,
                       Animatronic chica,
                       Animatronic foxy,
                       Animatronic goldenFreddy,
                       PowerSystem powerSystem,
                       ClockSystem clockSystem,
                       double flipProgress,
                       int screenW,
                       int screenH) {
        if (flipProgress <= 0.0) return;

        // Render camera feed inside animated flip container
        int displayH = (int) (screenH * flipProgress);
        int topY = screenH - displayH;

        Shape oldClip = g.getClip();
        g.setClip(0, topY, screenW, displayH);

        // Draw camera scene
        ArtGenerator.drawCameraFeed(g, activeCamera, freddy, bonnie, chica, foxy,
                goldenFreddy.getCurrentRoom() == RoomId.WEST_CORNER_2B && goldenFreddy.isGoldenFreddyActive(),
                screenW, screenH);

        // Minimap
        drawMinimap(g, freddy, bonnie, chica, foxy);

        // Power & Usage HUD (also shown on camera monitor in FNAF 1)
        drawPowerHUD(g, powerSystem, screenH);

        // Clock & Night HUD
        drawClockHUD(g, clockSystem);

        // CRT scanlines
        ArtGenerator.drawCRTScanlines(g, screenW, screenH);

        // Static overlay (heavy on switch, light continuous)
        float noiseOpacity = (staticBurstTimer > 0) ? 0.75f : 0.08f;
        ArtGenerator.drawStaticNoise(g, screenW, screenH, noiseOpacity);

        // Monitor CRT frame border
        g.setColor(new Color(25, 25, 30));
        g.setStroke(new BasicStroke(16f));
        g.drawRect(8, 8, screenW - 16, screenH - 16);

        g.setClip(oldClip);
    }

    private void drawMinimap(Graphics2D g, Animatronic freddy, Animatronic bonnie, Animatronic chica, Animatronic foxy) {
        // Semi-transparent background for map area
        g.setColor(new Color(15, 20, 25, 180));
        g.fillRoundRect(MAP_X - 10, MAP_Y - 10, MAP_W + 20, MAP_H + 20, 16, 16);

        // Map layout corridor lines
        g.setColor(new Color(90, 100, 120));
        g.setStroke(new BasicStroke(3f));

        // Outline corridors linking rooms
        g.drawRect(MAP_X + 100, MAP_Y + 75, 120, 80);  // Dining
        g.drawLine(MAP_X + 160, MAP_Y + 45, MAP_X + 160, MAP_Y + 75); // Stage to Dining
        g.drawLine(MAP_X + 70, MAP_Y + 115, MAP_X + 100, MAP_Y + 115); // Pirate cove
        g.drawLine(MAP_X + 45, MAP_Y + 70, MAP_X + 100, MAP_Y + 70); // Backstage
        g.drawLine(MAP_X + 220, MAP_Y + 95, MAP_X + 285, MAP_Y + 95); // Restrooms
        g.drawLine(MAP_X + 220, MAP_Y + 140, MAP_X + 285, MAP_Y + 175); // Kitchen
        // West Hall
        g.drawLine(MAP_X + 110, MAP_Y + 155, MAP_X + 110, MAP_Y + 280);
        g.drawLine(MAP_X + 40, MAP_Y + 215, MAP_X + 75, MAP_Y + 215); // Supply closet
        // East Hall
        g.drawLine(MAP_X + 245, MAP_Y + 155, MAP_X + 245, MAP_Y + 280);
        // Office Box at bottom
        g.setColor(new Color(60, 65, 80));
        g.fillRect(MAP_X + 130, MAP_Y + 260, 70, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("YOU", MAP_X + 152, MAP_Y + 290);

        // Camera Buttons
        for (Map.Entry<RoomId, Rectangle> entry : cameraButtonBounds.entrySet()) {
            RoomId room = entry.getKey();
            Rectangle r = entry.getValue();
            boolean isActive = (room == activeCamera);

            // Button body
            g.setColor(isActive ? new Color(20, 160, 60) : new Color(40, 44, 52));
            g.fillRect(r.x, r.y, r.width, r.height);

            // Button border
            g.setColor(isActive ? new Color(60, 255, 120) : new Color(80, 85, 95));
            g.setStroke(new BasicStroke(2f));
            g.drawRect(r.x, r.y, r.width, r.height);

            // Button text (e.g. CAM 1A)
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 12));
            g.drawString(room.getCode(), r.x + 8, r.y + 22);

            // DEBUG OVERLAY: Show animatronic location dots on minimap!
            if (debugOverlay) {
                drawAnimatronicDebugDot(g, freddy, room, r.x + 6, r.y + 8, new Color(130, 80, 40));
                drawAnimatronicDebugDot(g, bonnie, room, r.x + 20, r.y + 8, new Color(120, 90, 200));
                drawAnimatronicDebugDot(g, chica, room, r.x + 34, r.y + 8, new Color(220, 200, 40));
                drawAnimatronicDebugDot(g, foxy, room, r.x + 48, r.y + 8, new Color(220, 50, 40));
            }
        }
    }

    private void drawAnimatronicDebugDot(Graphics2D g, Animatronic a, RoomId room, int x, int y, Color c) {
        if (a.getCurrentRoom() == room) {
            g.setColor(c);
            g.fillOval(x, y, 9, 9);
            g.setColor(Color.WHITE);
            g.drawOval(x, y, 9, 9);
        }
    }

    private void drawPowerHUD(Graphics2D g, PowerSystem powerSystem, int screenH) {
        int y = screenH - 55;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.drawString("Power left: " + (int) Math.ceil(powerSystem.getPower()) + "%", 50, y);

        // Usage text & green/yellow/red battery bars
        g.drawString("Usage: ", 50, y + 26);
        int bars = powerSystem.getUsageBars();
        int bx = 135;
        for (int i = 1; i <= 5; i++) {
            if (i <= bars) {
                if (i <= 2) g.setColor(new Color(40, 220, 60)); // Green
                else if (i <= 3) g.setColor(new Color(240, 210, 40)); // Yellow
                else g.setColor(new Color(230, 40, 40)); // Red
            } else {
                g.setColor(new Color(50, 50, 60)); // Dim/empty bar
            }
            g.fillRect(bx + (i - 1) * 16, y + 11, 12, 16);
            g.setColor(Color.BLACK);
            g.drawRect(bx + (i - 1) * 16, y + 11, 12, 16);
        }
    }

    private void drawClockHUD(Graphics2D g, ClockSystem clockSystem) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        g.drawString(clockSystem.getDisplayHour() + " AM", 1120, 60);
    }

    public RoomId getActiveCamera() { return activeCamera; }
    public void setActiveCamera(RoomId r) { this.activeCamera = r; }
    public boolean isDebugOverlay() { return debugOverlay; }
    public void toggleDebugOverlay() { this.debugOverlay = !debugOverlay; }
}
