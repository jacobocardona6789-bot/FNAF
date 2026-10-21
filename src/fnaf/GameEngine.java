package fnaf;

import fnaf.audio.SoundManager;
import fnaf.model.*;
import fnaf.render.*;
import fnaf.save.SaveManager;
import fnaf.ui.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Main game coordinator managing states, updates at 60 FPS,
 * user input, and screen transitions.
 */
public class GameEngine implements KeyListener, MouseListener, MouseMotionListener {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;

    // Subsystems
    private final SoundManager soundManager = new SoundManager();
    private final SaveManager saveManager = new SaveManager();
    private final PowerSystem powerSystem = new PowerSystem();
    private final ClockSystem clockSystem = new ClockSystem();
    private final OfficeState officeState = new OfficeState();
    private final CameraRenderer cameraRenderer = new CameraRenderer();
    private final JumpscareRenderer jumpscareRenderer = new JumpscareRenderer();

    // UI Screens
    private final MainMenuScreen mainMenuScreen = new MainMenuScreen();
    private final CustomNightScreen customNightScreen = new CustomNightScreen();
    private final NewspaperScreen newspaperScreen = new NewspaperScreen();
    private final NightIntroScreen nightIntroScreen = new NightIntroScreen();
    private final GameOverScreen gameOverScreen = new GameOverScreen();
    private final WinScreen winScreen = new WinScreen();
    private final PaycheckScreen paycheckScreen = new PaycheckScreen();

    // Animatronics
    private final Animatronic freddy = new Animatronic(AnimatronicType.FREDDY);
    private final Animatronic bonnie = new Animatronic(AnimatronicType.BONNIE);
    private final Animatronic chica = new Animatronic(AnimatronicType.CHICA);
    private final Animatronic foxy = new Animatronic(AnimatronicType.FOXY);
    private final Animatronic goldenFreddy = new Animatronic(AnimatronicType.GOLDEN_FREDDY);

    // Phone Guy
    private PhoneGuy phoneGuy;

    // State
    private GameState currentState = GameState.MAIN_MENU;
    private int selectedNight = 1;

    // Monitor flip bar hit area (bottom center)
    private final Rectangle monitorFlipBar = new Rectangle(SCREEN_WIDTH / 2 - 250, SCREEN_HEIGHT - 60, 500, 55);

    // Mute call button (top left)
    private final Rectangle muteCallButton = new Rectangle(40, 30, 110, 32);

    // Door & Light hit areas in Office panorama:
    // Left controls:
    private final Rectangle leftDoorButtonHit = new Rectangle(80 + 210 + 25, 270, 35, 30);
    private final Rectangle leftLightButtonHit = new Rectangle(80 + 210 + 25, 312, 35, 30);
    // Right controls:
    private final Rectangle rightDoorButtonHit = new Rectangle(1110, 270, 35, 30);
    private final Rectangle rightLightButtonHit = new Rectangle(1110, 312, 35, 30);

    private int mouseX = SCREEN_WIDTH / 2;
    private int mouseY = SCREEN_HEIGHT / 2;
    private final Random rand = new Random();

    public GameEngine() {
        soundManager.startStaticHum();
    }

    // ==========================================
    // NIGHT INITIALIZATION
    // ==========================================

    public void startNight(int night) {
        this.selectedNight = night;
        powerSystem.reset();
        clockSystem.reset();
        officeState.reset();
        phoneGuy = new PhoneGuy(night);

        // Configure AI levels according to FNAF 1 rules
        int fAi = 0, bAi = 0, cAi = 0, fxAi = 0;
        switch (night) {
            case 1:
                bAi = 0; cAi = 0; fxAi = 0; fAi = 0;
                break;
            case 2:
                bAi = 3; cAi = 1; fxAi = 1; fAi = 0;
                break;
            case 3:
                bAi = 0; cAi = 5; fxAi = 2; fAi = 1;
                break;
            case 4:
                bAi = 2; cAi = 4; fxAi = 6; fAi = 1;
                break;
            case 5:
                bAi = 5; cAi = 7; fxAi = 5; fAi = 3;
                break;
            case 6:
                bAi = 10; cAi = 12; fxAi = 16; fAi = 4;
                break;
            case 7:
                // Custom night AI levels
                fAi = customNightScreen.getFreddyAI();
                bAi = customNightScreen.getBonnieAI();
                cAi = customNightScreen.getChicaAI();
                fxAi = customNightScreen.getFoxyAI();
                break;
        }

        freddy.reset(fAi);
        bonnie.reset(bAi);
        chica.reset(cAi);
        foxy.reset(fxAi);
        goldenFreddy.reset(1);

        nightIntroScreen.start(night);
        currentState = GameState.NIGHT_INTRO;
    }

    // ==========================================
    // UPDATE TICK (60 FPS)
    // ==========================================

    public void update(double dtSeconds) {
        switch (currentState) {
            case MAIN_MENU:
                mainMenuScreen.update(dtSeconds);
                break;
            case NEWSPAPER:
                newspaperScreen.update(dtSeconds);
                break;
            case NIGHT_INTRO:
                if (nightIntroScreen.update(dtSeconds)) {
                    currentState = GameState.PLAYING;
                    officeState.reset();
                    soundManager.playPhoneRing();
                }
                break;
            case PLAYING:
                updatePlaying(dtSeconds);
                break;
            case JUMPSCARE:
                if (jumpscareRenderer.update(dtSeconds)) {
                    soundManager.stopFanHum();
                    soundManager.stopLightBuzz();
                    soundManager.stopToreadorMarch();
                    gameOverScreen.reset();
                    currentState = GameState.GAME_OVER;
                }
                break;
            case WIN_6AM:
                if (winScreen.update(dtSeconds)) {
                    saveManager.recordNightWon(selectedNight,
                            selectedNight == 7 && customNightScreen.is20Mode());
                    if (selectedNight >= 5) {
                        paycheckScreen.start(selectedNight);
                        currentState = GameState.PAYCHECK;
                    } else {
                        currentState = GameState.MAIN_MENU;
                        soundManager.startStaticHum();
                    }
                }
                break;
            case GAME_OVER:
                gameOverScreen.update(dtSeconds);
                break;
            case CUSTOM_NIGHT:
                // Custom night screen
                break;
            case PAYCHECK:
                // Paycheck screen
                break;
        }
    }

    private void updatePlaying(double dtSeconds) {
        // Update panning based on mouse position (when monitor is lowered)
        if (!officeState.isMonitorOpen()) {
            officeState.updateMousePan(mouseX);
        }

        // Clock update
        clockSystem.update(dtSeconds);
        applyTimeMilestoneAIBuffs();

        // Phone Guy update
        if (phoneGuy != null) {
            phoneGuy.update(dtSeconds);
        }

        // Power system update
        powerSystem.update(dtSeconds,
                officeState.isLeftDoorClosed(),
                officeState.isRightDoorClosed(),
                officeState.isLeftLightOn(),
                officeState.isRightLightOn(),
                officeState.isMonitorOpen());

        // Office state update
        officeState.update(dtSeconds, powerSystem, soundManager);

        // Check button jamming (if Bonnie or Chica have entered office)
        if (bonnie.hasEnteredOffice()) {
            officeState.setLeftJammed(true);
        }
        if (chica.hasEnteredOffice()) {
            officeState.setRightJammed(true);
        }

        // Camera renderer update
        cameraRenderer.update(dtSeconds);

        // Blackout jumpscare trigger
        if (officeState.getBlackoutPhase() == OfficeState.BlackoutPhase.JUMPSCARE_READY) {
            triggerJumpscare(AnimatronicType.FREDDY);
            return;
        }

        // Animatronics AI update
        if (!powerSystem.isBlackout()) {
            RoomId watchedCam = officeState.isMonitorOpen() ? cameraRenderer.getActiveCamera() : null;
            boolean leftClosed = officeState.isLeftDoorClosed();
            boolean rightClosed = officeState.isRightDoorClosed();
            boolean monitorOpen = officeState.isMonitorOpen();

            AnimatronicType scare = null;

            // Bonnie
            if (scare == null) {
                scare = bonnie.update(dtSeconds, watchedCam, monitorOpen, leftClosed, rightClosed, powerSystem, soundManager);
            }
            // Chica
            if (scare == null) {
                scare = chica.update(dtSeconds, watchedCam, monitorOpen, leftClosed, rightClosed, powerSystem, soundManager);
            }
            // Foxy
            if (scare == null) {
                scare = foxy.update(dtSeconds, watchedCam, monitorOpen, leftClosed, rightClosed, powerSystem, soundManager);
            }
            // Freddy
            if (scare == null) {
                scare = freddy.update(dtSeconds, watchedCam, monitorOpen, leftClosed, rightClosed, powerSystem, soundManager);
            }
            // Golden Freddy
            if (scare == null) {
                scare = goldenFreddy.update(dtSeconds, watchedCam, monitorOpen, leftClosed, rightClosed, powerSystem, soundManager);
            }

            if (scare != null) {
                triggerJumpscare(scare);
                return;
            }
        }

        // Check 6 AM victory!
        if (clockSystem.isNightWon()) {
            soundManager.stopFanHum();
            soundManager.stopLightBuzz();
            soundManager.stopToreadorMarch();
            winScreen.start(soundManager);
            currentState = GameState.WIN_6AM;
        }
    }

    private void applyTimeMilestoneAIBuffs() {
        int hour = clockSystem.getCurrentHour();
        // At 2 AM: Bonnie +1
        if (hour >= 2 && bonnie.getAiLevel() > 0 && selectedNight <= 5) {
            // Slight boost
        }
        // At 3 AM & 4 AM: Bonnie, Chica, Foxy get buffs
    }

    private void triggerJumpscare(AnimatronicType type) {
        officeState.reset();
        soundManager.stopFanHum();
        soundManager.stopLightBuzz();
        soundManager.stopToreadorMarch();
        jumpscareRenderer.start(type, soundManager);
        currentState = GameState.JUMPSCARE;
    }

    // ==========================================
    // RENDERING
    // ==========================================

    public void render(Graphics2D g) {
        switch (currentState) {
            case MAIN_MENU:
                mainMenuScreen.render(g, saveManager, SCREEN_WIDTH, SCREEN_HEIGHT);
                break;
            case NEWSPAPER:
                newspaperScreen.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
                break;
            case NIGHT_INTRO:
                nightIntroScreen.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
                break;
            case PLAYING:
                renderPlaying(g);
                break;
            case JUMPSCARE:
                jumpscareRenderer.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
                break;
            case WIN_6AM:
                winScreen.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
                break;
            case GAME_OVER:
                gameOverScreen.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
                break;
            case CUSTOM_NIGHT:
                customNightScreen.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
                break;
            case PAYCHECK:
                paycheckScreen.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
                break;
        }
    }

    private void renderPlaying(Graphics2D g) {
        // 1. Render Office
        boolean bonnieAtDoor = bonnie.getCurrentRoom() == RoomId.LEFT_DOOR_BLIND_SPOT;
        boolean chicaAtWindow = chica.getCurrentRoom() == RoomId.RIGHT_DOOR_BLIND_SPOT;
        boolean goldenFreddyInOffice = goldenFreddy.isGoldenFreddyActive() && !officeState.isMonitorOpen();
        boolean blackout = powerSystem.isBlackout();
        boolean freddyFaceLit = officeState.isFreddyFaceLit();

        ArtGenerator.drawOffice(g,
                officeState.getPanOffset(),
                officeState.getLeftDoorProgress(),
                officeState.getRightDoorProgress(),
                officeState.isLeftLightOn(),
                officeState.isRightLightOn(),
                officeState.getFanAngle(),
                bonnieAtDoor,
                chicaAtWindow,
                goldenFreddyInOffice,
                blackout,
                freddyFaceLit);

        // 2. Render Hallucinations if active
        if (officeState.isHallucinationActive() && !blackout && !officeState.isMonitorOpen()) {
            drawHallucinationOverlay(g);
        }

        // 3. Render Phone Guy Subtitles & Mute Button
        if (phoneGuy != null && phoneGuy.isActive()) {
            drawPhoneGuyUI(g);
        }

        // 4. Render Monitor Camera Feed (if flipped up or animating)
        cameraRenderer.render(g, freddy, bonnie, chica, foxy, goldenFreddy,
                powerSystem, clockSystem, officeState.getMonitorFlipProgress(),
                SCREEN_WIDTH, SCREEN_HEIGHT);

        // 5. Render Office HUD (Power & Clock when monitor is down)
        if (!officeState.isMonitorOpen() && officeState.getMonitorFlipProgress() < 0.2) {
            drawOfficeHUD(g);
        }

        // 6. Monitor Flip Bar at bottom
        if (!blackout) {
            drawMonitorFlipBar(g);
        }

        // 7. Ambient Scanlines
        ArtGenerator.drawCRTScanlines(g, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void drawHallucinationOverlay(Graphics2D g) {
        g.setColor(new Color(255, 255, 255, 180));
        g.setFont(new Font("Impact", Font.BOLD, 100));
        FontMetrics fm = g.getFontMetrics();
        String str = "IT'S ME";
        g.drawString(str, (SCREEN_WIDTH - fm.stringWidth(str)) / 2, SCREEN_HEIGHT / 2 + 35);
    }

    private void drawPhoneGuyUI(Graphics2D g) {
        // "MUTE CALL" Button (Top Left)
        g.setColor(new Color(180, 30, 30));
        g.fillRoundRect(muteCallButton.x, muteCallButton.y, muteCallButton.width, muteCallButton.height, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString("MUTE CALL", muteCallButton.x + 18, muteCallButton.y + 20);

        // Subtitle bar
        String sub = phoneGuy.getCurrentSubtitle();
        if (!sub.isEmpty()) {
            g.setColor(new Color(10, 12, 16, 210));
            g.fillRoundRect(160, 25, SCREEN_WIDTH - 360, 42, 8, 8);
            g.setColor(new Color(255, 240, 160));
            g.setFont(new Font("Monospaced", Font.BOLD, 14));
            g.drawString("PHONE: " + sub, 175, 52);
        }
    }

    private void drawOfficeHUD(Graphics2D g) {
        // Power (Bottom Left)
        int y = SCREEN_HEIGHT - 65;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        g.drawString("Power left: " + (int) Math.ceil(powerSystem.getPower()) + "%", 45, y);

        // Usage bars
        g.drawString("Usage: ", 45, y + 28);
        int bars = powerSystem.getUsageBars();
        int bx = 140;
        for (int i = 1; i <= 5; i++) {
            if (i <= bars) {
                if (i <= 2) g.setColor(new Color(40, 220, 60));
                else if (i <= 3) g.setColor(new Color(240, 210, 40));
                else g.setColor(new Color(230, 40, 40));
            } else {
                g.setColor(new Color(50, 50, 60));
            }
            g.fillRect(bx + (i - 1) * 16, y + 12, 12, 17);
            g.setColor(Color.BLACK);
            g.drawRect(bx + (i - 1) * 16, y + 12, 12, 17);
        }

        // Time & Night (Top Right)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 36));
        g.drawString(clockSystem.getDisplayHour() + " AM", SCREEN_WIDTH - 180, 60);

        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        String nStr = (selectedNight == 7) ? "Custom Night" : "Night " + selectedNight;
        g.drawString(nStr, SCREEN_WIDTH - 180, 95);
    }

    private void drawMonitorFlipBar(Graphics2D g) {
        // Transparent flip trigger bar with arrow
        boolean hovered = monitorFlipBar.contains(mouseX, mouseY);
        g.setColor(hovered ? new Color(255, 255, 255, 120) : new Color(255, 255, 255, 45));
        g.fillRoundRect(monitorFlipBar.x, monitorFlipBar.y, monitorFlipBar.width, monitorFlipBar.height, 12, 12);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(monitorFlipBar.x, monitorFlipBar.y, monitorFlipBar.width, monitorFlipBar.height, 12, 12);

        // Arrow icon and text
        g.setFont(new Font("Monospaced", Font.BOLD, 15));
        FontMetrics fm = g.getFontMetrics();
        String txt = officeState.isMonitorOpen() ? "▼ [FLIP DOWN MONITOR] ▼" : "▲ [FLIP UP MONITOR] ▲";
        g.drawString(txt, monitorFlipBar.x + (monitorFlipBar.width - fm.stringWidth(txt)) / 2, monitorFlipBar.y + 34);
    }

    // ==========================================
    // INPUT LISTENERS
    // ==========================================

    @Override
    public void mousePressed(MouseEvent e) {
        int mx = e.getX();
        int my = e.getY();

        if (currentState == GameState.MAIN_MENU) {
            int opt = mainMenuScreen.handleMouseClick(mx, my, saveManager);
            if (opt == 0) { // New Game
                newspaperScreen.reset();
                currentState = GameState.NEWSPAPER;
            } else if (opt == 1) { // Continue
                startNight(saveManager.getCurrentNight());
            } else if (opt == 2) { // Night 6
                startNight(6);
            } else if (opt == 3) { // Custom Night
                currentState = GameState.CUSTOM_NIGHT;
            }
        } else if (currentState == GameState.NEWSPAPER) {
            startNight(1);
        } else if (currentState == GameState.GAME_OVER) {
            currentState = GameState.MAIN_MENU;
            soundManager.startStaticHum();
        } else if (currentState == GameState.PAYCHECK) {
            currentState = GameState.MAIN_MENU;
            soundManager.startStaticHum();
        } else if (currentState == GameState.CUSTOM_NIGHT) {
            if (customNightScreen.isReadyClicked(mx, my)) {
                startNight(7);
            } else if (customNightScreen.isBackClicked(mx, my)) {
                currentState = GameState.MAIN_MENU;
            } else {
                customNightScreen.handleMouseClick(mx, my, soundManager);
            }
        } else if (currentState == GameState.PLAYING) {
            handlePlayingClick(mx, my);
        }
    }

    private void handlePlayingClick(int mx, int my) {
        // Monitor flip bar
        if (monitorFlipBar.contains(mx, my)) {
            officeState.toggleMonitor(soundManager);
            return;
        }

        // Mute call
        if (phoneGuy != null && phoneGuy.isActive() && muteCallButton.contains(mx, my)) {
            phoneGuy.mute();
            soundManager.playButtonClick();
            return;
        }

        // If monitor is open: check minimap camera buttons
        if (officeState.isMonitorOpen()) {
            cameraRenderer.handleMouseClick(mx, my, soundManager);
            return;
        }

        // When monitor is closed: check office interactables
        double pan = officeState.getPanOffset();
        int officeX = (int) (mx + pan);
        int officeY = my;

        // Check Freddy nose honk
        if (officeState.checkPosterNoseClick(officeX, officeY, soundManager)) {
            return;
        }

        // Left controls (Door & Light buttons)
        if (leftDoorButtonHit.contains(officeX, officeY)) {
            officeState.toggleLeftDoor(soundManager);
            return;
        }
        if (leftLightButtonHit.contains(officeX, officeY)) {
            officeState.toggleLeftLight(soundManager);
            return;
        }

        // Right controls (Door & Light buttons)
        if (rightDoorButtonHit.contains(officeX, officeY)) {
            officeState.toggleRightDoor(soundManager);
            return;
        }
        if (rightLightButtonHit.contains(officeX, officeY)) {
            officeState.toggleRightLight(soundManager);
            return;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.mouseX = e.getX();
        this.mouseY = e.getY();

        if (currentState == GameState.MAIN_MENU) {
            mainMenuScreen.handleMouseMove(mouseX, mouseY, saveManager);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Universal Escape to Menu
        if (code == KeyEvent.VK_ESCAPE) {
            soundManager.stopFanHum();
            soundManager.stopLightBuzz();
            soundManager.stopToreadorMarch();
            currentState = GameState.MAIN_MENU;
            soundManager.startStaticHum();
            return;
        }

        if (currentState == GameState.NEWSPAPER && code == KeyEvent.VK_SPACE) {
            startNight(1);
            return;
        }

        if (currentState == GameState.PLAYING) {
            // A: Left Door
            if (code == KeyEvent.VK_A) {
                officeState.toggleLeftDoor(soundManager);
            }
            // Q: Left Light
            else if (code == KeyEvent.VK_Q) {
                officeState.toggleLeftLight(soundManager);
            }
            // D: Right Door
            else if (code == KeyEvent.VK_D) {
                officeState.toggleRightDoor(soundManager);
            }
            // E: Right Light
            else if (code == KeyEvent.VK_E) {
                officeState.toggleRightLight(soundManager);
            }
            // Space or S: Toggle Monitor
            else if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_S) {
                officeState.toggleMonitor(soundManager);
            }
            // Quick camera keys (1-9, 0)
            else if (code == KeyEvent.VK_1) {
                cameraRenderer.selectCamera(RoomId.STAGE_1A, soundManager);
            } else if (code == KeyEvent.VK_2) {
                cameraRenderer.selectCamera(RoomId.DINING_1B, soundManager);
            } else if (code == KeyEvent.VK_3) {
                cameraRenderer.selectCamera(RoomId.PIRATE_COVE_1C, soundManager);
            } else if (code == KeyEvent.VK_4) {
                cameraRenderer.selectCamera(RoomId.WEST_HALL_2A, soundManager);
            } else if (code == KeyEvent.VK_5) {
                cameraRenderer.selectCamera(RoomId.WEST_CORNER_2B, soundManager);
            } else if (code == KeyEvent.VK_6) {
                cameraRenderer.selectCamera(RoomId.SUPPLY_CLOSET_3, soundManager);
            } else if (code == KeyEvent.VK_7) {
                cameraRenderer.selectCamera(RoomId.EAST_HALL_4A, soundManager);
            } else if (code == KeyEvent.VK_8) {
                cameraRenderer.selectCamera(RoomId.EAST_CORNER_4B, soundManager);
            } else if (code == KeyEvent.VK_9) {
                cameraRenderer.selectCamera(RoomId.BACKSTAGE_5, soundManager);
            } else if (code == KeyEvent.VK_0) {
                cameraRenderer.selectCamera(RoomId.KITCHEN_6, soundManager);
            }
            // Ctrl+D: Toggle Debug Overlay
            else if (e.isControlDown() && code == KeyEvent.VK_D) {
                cameraRenderer.toggleDebugOverlay();
            }
            // Ctrl+H: Skip to next hour (testing tool)
            else if (e.isControlDown() && code == KeyEvent.VK_H) {
                clockSystem.skipToNextHour();
            }
            // Ctrl+G: Trigger Golden Freddy hallucination (testing tool)
            else if (e.isControlDown() && code == KeyEvent.VK_G) {
                goldenFreddy.triggerGoldenFreddy();
                soundManager.playHallucination();
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
