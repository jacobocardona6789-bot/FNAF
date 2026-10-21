package fnaf.model;

/**
 * Accurately models FNAF 1 battery power and usage bars.
 */
public class PowerSystem {
    private double power = 100.0;
    private int usageBars = 1; // 1 to 5
    private boolean blackout = false;

    // Foxy bang counter for escalating power drain
    private int foxyBangCount = 0;

    public void reset() {
        power = 100.0;
        usageBars = 1;
        blackout = false;
        foxyBangCount = 0;
    }

    public void update(double dtSeconds, boolean leftDoorClosed, boolean rightDoorClosed,
                       boolean leftLightOn, boolean rightLightOn, boolean monitorOpen) {
        if (blackout) {
            usageBars = 0;
            return;
        }

        // Calculate usage bars:
        // 1 (base fan + office)
        // +1 if left door closed
        // +1 if right door closed
        // +1 if left light on
        // +1 if right light on
        // +1 if monitor open
        int bars = 1;
        if (leftDoorClosed) bars++;
        if (rightDoorClosed) bars++;
        if (leftLightOn) bars++;
        if (rightLightOn) bars++;
        if (monitorOpen) bars++;

        this.usageBars = Math.min(5, bars);

        // FNAF 1 exact drain rates:
        // 1 bar = ~0.104 % / sec
        // 2 bars = ~0.208 % / sec
        // 3 bars = ~0.312 % / sec
        // 4 bars = ~0.416 % / sec
        // 5 bars = ~0.526 % / sec
        double drainRate;
        switch (usageBars) {
            case 2: drainRate = 0.208; break;
            case 3: drainRate = 0.312; break;
            case 4: drainRate = 0.416; break;
            case 5: drainRate = 0.526; break;
            default: drainRate = 0.104; break;
        }

        power -= drainRate * dtSeconds;
        if (power <= 0.0) {
            power = 0.0;
            blackout = true;
        }
    }

    public void applyFoxyDoorBang() {
        if (blackout) return;
        foxyBangCount++;
        // 1st bang: 1%, 2nd bang: 6%, 3rd bang: 11%, 4th: 16%
        double drainPercent = 1.0 + (foxyBangCount - 1) * 5.0;
        power = Math.max(0.0, power - drainPercent);
        if (power <= 0.0) {
            power = 0.0;
            blackout = true;
        }
    }

    public double getPower() {
        return Math.max(0.0, power);
    }

    public int getUsageBars() {
        return usageBars;
    }

    public boolean isBlackout() {
        return blackout;
    }

    public void setPower(double p) {
        this.power = p;
        if (power <= 0) {
            blackout = true;
            usageBars = 0;
        }
    }
}
