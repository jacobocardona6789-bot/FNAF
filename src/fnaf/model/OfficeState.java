package fnaf.model;

import fnaf.audio.SoundManager;
import java.awt.Rectangle;
import java.util.Random;

/**
 * Encapsulates the office view state: panning, door animations, lights, fan, and blackout sequence.
 */
public class OfficeState {
    public static final int VIEW_WIDTH = 1280;
    public static final int VIEW_HEIGHT = 720;
    public static final int OFFICE_WIDTH = 1600;
    public static final int MAX_PAN = OFFICE_WIDTH - VIEW_WIDTH; // 320 px

    // Panning
    private double panOffset = MAX_PAN / 2.0; // Centered at start
    private double panTarget = MAX_PAN / 2.0;

    // Doors & Lights
    private boolean leftDoorClosed = false;
    private boolean rightDoorClosed = false;
    private double leftDoorProgress = 0.0; // 0.0 = open, 1.0 = fully closed
    private double rightDoorProgress = 0.0;
    private boolean leftLightOn = false;
    private boolean rightLightOn = false;
    private boolean leftJammed = false;
    private boolean rightJammed = false;

    // Monitor
    private boolean monitorOpen = false;
    private double monitorFlipProgress = 0.0; // 0.0 = down, 1.0 = fully up

    // Desk fan
    private double fanAngle = 0.0;

    // Poster Easter egg ("Celebrate!" poster Freddy nose)
    // Coords relative to office canvas:
    private final Rectangle posterNoseBounds = new Rectangle(790, 240, 25, 25);

    // Blackout sequence
    public enum BlackoutPhase {
        NONE,
        SILENT_INITIAL,
        FREDDY_SINGING,
        DARK_FOOTSTEPS,
        JUMPSCARE_READY
    }

    private BlackoutPhase blackoutPhase = BlackoutPhase.NONE;
    private double blackoutTimer = 0.0;
    private double singingDuration = 8.0;
    private double footstepDuration = 3.0;
    private double singingFlickerTimer = 0.0;
    private boolean freddyFaceLit = false;

    // Hallucinations
    private double hallucinationTimer = 0.0;
    private boolean hallucinationActive = false;

    private final Random rand = new Random();

    public void reset() {
        panOffset = MAX_PAN / 2.0;
        panTarget = MAX_PAN / 2.0;
        leftDoorClosed = false;
        rightDoorClosed = false;
        leftDoorProgress = 0.0;
        rightDoorProgress = 0.0;
        leftLightOn = false;
        rightLightOn = false;
        leftJammed = false;
        rightJammed = false;
        monitorOpen = false;
        monitorFlipProgress = 0.0;
        fanAngle = 0.0;
        blackoutPhase = BlackoutPhase.NONE;
        blackoutTimer = 0.0;
        hallucinationActive = false;
        hallucinationTimer = 0.0;
    }

    public void update(double dtSeconds, PowerSystem powerSystem, SoundManager soundManager) {
        // Update fan
        if (!powerSystem.isBlackout()) {
            fanAngle += dtSeconds * 35.0; // Fast spin
            soundManager.startFanHum();
        } else {
            soundManager.stopFanHum();
        }

        // Panning smooth interpolation (lerp)
        panOffset += (panTarget - panOffset) * Math.min(1.0, dtSeconds * 10.0);
        panOffset = Math.max(0, Math.min(MAX_PAN, panOffset));

        // Smooth door animations (open/close sliding)
        double doorSpeed = 4.0; // Closes or opens in ~0.25 seconds
        if (leftDoorClosed) {
            leftDoorProgress = Math.min(1.0, leftDoorProgress + dtSeconds * doorSpeed);
        } else {
            leftDoorProgress = Math.max(0.0, leftDoorProgress - dtSeconds * doorSpeed);
        }

        if (rightDoorClosed) {
            rightDoorProgress = Math.min(1.0, rightDoorProgress + dtSeconds * doorSpeed);
        } else {
            rightDoorProgress = Math.max(0.0, rightDoorProgress - dtSeconds * doorSpeed);
        }

        // Smooth monitor flip animation
        double flipSpeed = 5.0; // Flips in ~0.2s
        if (monitorOpen) {
            monitorFlipProgress = Math.min(1.0, monitorFlipProgress + dtSeconds * flipSpeed);
        } else {
            monitorFlipProgress = Math.max(0.0, monitorFlipProgress - dtSeconds * flipSpeed);
        }

        // Door light sound management
        if (leftLightOn || rightLightOn) {
            soundManager.startLightBuzz();
        } else {
            soundManager.stopLightBuzz();
        }

        // Blackout progression
        if (powerSystem.isBlackout()) {
            updateBlackout(dtSeconds, soundManager);
        }

        // Random hallucination flicker
        hallucinationTimer += dtSeconds;
        if (hallucinationTimer >= 5.0) {
            hallucinationTimer = 0.0;
            if (rand.nextInt(15) == 0) {
                hallucinationActive = true;
            } else {
                hallucinationActive = false;
            }
        }
    }

    private void updateBlackout(double dtSeconds, SoundManager soundManager) {
        // Force doors open and lights off
        if (leftDoorClosed || rightDoorClosed) {
            leftDoorClosed = false;
            rightDoorClosed = false;
            soundManager.playDoorMove();
        }
        leftLightOn = false;
        rightLightOn = false;
        monitorOpen = false;

        blackoutTimer += dtSeconds;

        if (blackoutPhase == BlackoutPhase.NONE) {
            blackoutPhase = BlackoutPhase.SILENT_INITIAL;
            blackoutTimer = 0.0;
        } else if (blackoutPhase == BlackoutPhase.SILENT_INITIAL) {
            // 2 to 4 seconds of silence in the dark
            if (blackoutTimer >= 3.0) {
                blackoutPhase = BlackoutPhase.FREDDY_SINGING;
                blackoutTimer = 0.0;
                singingDuration = 6.0 + rand.nextDouble() * 10.0; // 6 to 16 seconds
                soundManager.startToreadorMarch();
            }
        } else if (blackoutPhase == BlackoutPhase.FREDDY_SINGING) {
            // Flash Freddy's glowing eyes in sync with the song
            singingFlickerTimer += dtSeconds;
            if (singingFlickerTimer >= 0.15) {
                singingFlickerTimer = 0.0;
                freddyFaceLit = rand.nextDouble() > 0.3;
            }

            if (blackoutTimer >= singingDuration) {
                blackoutPhase = BlackoutPhase.DARK_FOOTSTEPS;
                blackoutTimer = 0.0;
                freddyFaceLit = false;
                soundManager.stopToreadorMarch();
                footstepDuration = 2.0 + rand.nextDouble() * 4.0;
                soundManager.playFootsteps();
            }
        } else if (blackoutPhase == BlackoutPhase.DARK_FOOTSTEPS) {
            if (blackoutTimer >= footstepDuration) {
                blackoutPhase = BlackoutPhase.JUMPSCARE_READY;
            }
        }
    }

    // --- User interactions ---

    public void updateMousePan(int mouseX) {
        // Pan left when mouse on left 20%, right when on right 20%
        double leftThreshold = VIEW_WIDTH * 0.22;
        double rightThreshold = VIEW_WIDTH * 0.78;

        if (mouseX < leftThreshold) {
            double factor = 1.0 - (mouseX / leftThreshold);
            panTarget = Math.max(0.0, panTarget - factor * 22.0);
        } else if (mouseX > rightThreshold) {
            double factor = (mouseX - rightThreshold) / (VIEW_WIDTH - rightThreshold);
            panTarget = Math.min(MAX_PAN, panTarget + factor * 22.0);
        }
    }

    public void toggleLeftDoor(SoundManager soundManager) {
        if (leftJammed) {
            soundManager.playButtonJam();
            return;
        }
        leftDoorClosed = !leftDoorClosed;
        soundManager.playDoorMove();
        soundManager.playButtonClick();
    }

    public void toggleRightDoor(SoundManager soundManager) {
        if (rightJammed) {
            soundManager.playButtonJam();
            return;
        }
        rightDoorClosed = !rightDoorClosed;
        soundManager.playDoorMove();
        soundManager.playButtonClick();
    }

    public void toggleLeftLight(SoundManager soundManager) {
        if (leftJammed) {
            soundManager.playButtonJam();
            return;
        }
        leftLightOn = !leftLightOn;
        if (leftLightOn) rightLightOn = false; // FNAF 1: usually only one light at a time
        soundManager.playButtonClick();
    }

    public void toggleRightLight(SoundManager soundManager) {
        if (rightJammed) {
            soundManager.playButtonJam();
            return;
        }
        rightLightOn = !rightLightOn;
        if (rightLightOn) leftLightOn = false;
        soundManager.playButtonClick();
    }

    public void toggleMonitor(SoundManager soundManager) {
        monitorOpen = !monitorOpen;
        soundManager.playCameraFlip();
        if (monitorOpen) {
            // Turning on monitor turns off door lights
            leftLightOn = false;
            rightLightOn = false;
        }
    }

    public void setLeftLight(boolean on) {
        if (!leftJammed) this.leftLightOn = on;
    }

    public void setRightLight(boolean on) {
        if (!rightJammed) this.rightLightOn = on;
    }

    public boolean checkPosterNoseClick(int officeX, int officeY, SoundManager soundManager) {
        if (posterNoseBounds.contains(officeX, officeY)) {
            soundManager.playNoseHonk();
            return true;
        }
        return false;
    }

    // --- Getters & Setters ---
    public double getPanOffset() { return panOffset; }
    public boolean isLeftDoorClosed() { return leftDoorClosed; }
    public boolean isRightDoorClosed() { return rightDoorClosed; }
    public double getLeftDoorProgress() { return leftDoorProgress; }
    public double getRightDoorProgress() { return rightDoorProgress; }
    public boolean isLeftLightOn() { return leftLightOn; }
    public boolean isRightLightOn() { return rightLightOn; }
    public boolean isMonitorOpen() { return monitorOpen; }
    public double getMonitorFlipProgress() { return monitorFlipProgress; }
    public double getFanAngle() { return fanAngle; }
    public boolean isLeftJammed() { return leftJammed; }
    public void setLeftJammed(boolean j) { this.leftJammed = j; }
    public boolean isRightJammed() { return rightJammed; }
    public void setRightJammed(boolean j) { this.rightJammed = j; }
    public BlackoutPhase getBlackoutPhase() { return blackoutPhase; }
    public boolean isFreddyFaceLit() { return freddyFaceLit; }
    public boolean isHallucinationActive() { return hallucinationActive; }
}
