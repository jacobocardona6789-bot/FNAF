package fnaf;

import fnaf.audio.SoundManager;
import fnaf.model.*;
import fnaf.save.SaveManager;

/**
 * Headless test suite testing all core FNAF 1 mechanics programmatically.
 */
public class FNAFMechanicsTest {
    public static void main(String[] args) {
        System.out.println("=== STARTING FNAF 1 MECHANICS INTEGRATION TEST ===");

        testSaveManager();
        testPowerSystem();
        testClockSystem();
        testOfficeState();
        testAnimatronicAI();
        testSoundManager();

        System.out.println("=== ALL FNAF 1 MECHANICS PASSED SUCCESSFULLY! ===");
    }

    private static void testSaveManager() {
        System.out.print("Testing SaveManager... ");
        SaveManager sm = new SaveManager();
        sm.resetProgress();
        assert sm.getCurrentNight() == 1;
        assert !sm.isNight6Unlocked();
        assert !sm.isCustomNightUnlocked();
        assert sm.getStarsEarned() == 0;

        // Beating Night 5
        sm.recordNightWon(5, false);
        assert sm.isNight6Unlocked() : "Night 6 should be unlocked after night 5";
        assert sm.getStarsEarned() >= 1 : "Should have earned at least 1 star";

        // Beating Night 6
        sm.recordNightWon(6, false);
        assert sm.isCustomNightUnlocked() : "Custom night should be unlocked after night 6";
        assert sm.getStarsEarned() >= 2 : "Should have earned at least 2 stars";

        // Beating 20/20/20/20
        sm.recordNightWon(7, true);
        assert sm.getStarsEarned() == 3 : "Should have 3 stars for beating 20/20/20/20";
        System.out.println("PASSED!");
    }

    private static void testPowerSystem() {
        System.out.print("Testing PowerSystem... ");
        PowerSystem ps = new PowerSystem();
        ps.reset();
        assert ps.getPower() == 100.0;
        assert !ps.isBlackout();
        assert ps.getUsageBars() == 1;

        // 1 second base drain (1 bar = 0.104%/s)
        ps.update(1.0, false, false, false, false, false);
        assert ps.getUsageBars() == 1;
        assert Math.abs(ps.getPower() - 99.896) < 0.05 : "Power drain mismatch";

        // All active: Left Door + Right Door + Left Light + Monitor = 5 bars
        ps.update(1.0, true, true, true, false, true);
        assert ps.getUsageBars() == 5 : "Expected 5 usage bars";

        // Foxy bang power drain escalation
        double before = ps.getPower();
        ps.applyFoxyDoorBang(); // 1st bang: 1%
        assert Math.abs(before - ps.getPower() - 1.0) < 0.01;
        before = ps.getPower();
        ps.applyFoxyDoorBang(); // 2nd bang: 6%
        assert Math.abs(before - ps.getPower() - 6.0) < 0.01;

        // Blackout trigger
        ps.setPower(0.0);
        assert ps.isBlackout() : "Power 0 should trigger blackout";
        assert ps.getUsageBars() == 0;
        System.out.println("PASSED!");
    }

    private static void testClockSystem() {
        System.out.print("Testing ClockSystem... ");
        ClockSystem cs = new ClockSystem();
        cs.reset();
        assert cs.getDisplayHour() == 12;
        assert cs.getCurrentHour() == 0;
        assert !cs.isNightWon();

        // 1 hour passes (60s)
        cs.update(60.1);
        assert cs.getCurrentHour() == 1;
        assert cs.getDisplayHour() == 1;

        // Skip to 6 AM
        for (int i = 0; i < 5; i++) {
            cs.skipToNextHour();
        }
        assert cs.getCurrentHour() == 6;
        assert cs.isNightWon() : "6 AM should trigger night won";
        System.out.println("PASSED!");
    }

    private static void testOfficeState() {
        System.out.print("Testing OfficeState... ");
        OfficeState os = new OfficeState();
        os.reset();
        SoundManager sm = new SoundManager();
        sm.setMuted(true);

        assert !os.isLeftDoorClosed();
        os.toggleLeftDoor(sm);
        assert os.isLeftDoorClosed();

        assert !os.isLeftLightOn();
        os.toggleLeftLight(sm);
        assert os.isLeftLightOn();

        // Button jamming test
        os.setLeftJammed(true);
        os.toggleLeftDoor(sm);
        // Door state should remain unchanged because buttons are jammed
        assert os.isLeftDoorClosed() : "Jammed buttons should not toggle door";

        // Monitor test
        assert !os.isMonitorOpen();
        os.toggleMonitor(sm);
        assert os.isMonitorOpen();
        // Opening monitor should turn off door lights
        assert !os.isLeftLightOn() : "Opening monitor must turn off door lights";

        System.out.println("PASSED!");
    }

    private static void testAnimatronicAI() {
        System.out.print("Testing Animatronic AI... ");
        Animatronic bonnie = new Animatronic(AnimatronicType.BONNIE);
        bonnie.reset(20); // Max AI
        assert bonnie.getCurrentRoom() == RoomId.STAGE_1A;

        Animatronic foxy = new Animatronic(AnimatronicType.FOXY);
        foxy.reset(20);
        assert foxy.getCurrentRoom() == RoomId.PIRATE_COVE_1C;
        assert foxy.getFoxyStage() == 0;

        Animatronic freddy = new Animatronic(AnimatronicType.FREDDY);
        freddy.reset(20);
        assert freddy.getCurrentRoom() == RoomId.STAGE_1A;

        System.out.println("PASSED!");
    }

    private static void testSoundManager() {
        System.out.print("Testing SoundManager... ");
        SoundManager sm = new SoundManager();
        // Play one-shots (verify non-blocking & no exceptions thrown)
        sm.playButtonClick();
        sm.playButtonJam();
        sm.playCameraFlip();
        sm.playCameraSwitch();
        sm.playNoseHonk();
        sm.playFootsteps();
        sm.playDeepLaugh(1);
        sm.playKitchenClatter();
        sm.playFoxyDoorBang();
        sm.playJumpscare();
        sm.play6AMChimes();
        sm.playPhoneRing();
        sm.playHallucination();
        System.out.println("PASSED!");
    }
}
