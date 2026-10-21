package fnaf.model;

/**
 * Tracks in-game night time from 12 AM to 6 AM.
 */
public class ClockSystem {
    public static final double DEFAULT_HOUR_DURATION_SEC = 60.0; // 60 seconds per hour (6 mins total night)

    private double hourDuration = DEFAULT_HOUR_DURATION_SEC;
    private double elapsedSeconds = 0.0;
    private int currentHour = 0; // 0 = 12 AM, 1 = 1 AM, ..., 6 = 6 AM
    private boolean nightWon = false;
    private boolean fastForward = false;

    public void reset() {
        elapsedSeconds = 0.0;
        currentHour = 0;
        nightWon = false;
        fastForward = false;
    }

    public void update(double dtSeconds) {
        if (nightWon) return;

        double rate = fastForward ? 10.0 : 1.0;
        elapsedSeconds += dtSeconds * rate;

        int newHour = (int) (elapsedSeconds / hourDuration);
        if (newHour > currentHour) {
            currentHour = Math.min(6, newHour);
            if (currentHour >= 6) {
                nightWon = true;
            }
        }
    }

    public int getDisplayHour() {
        return (currentHour == 0) ? 12 : currentHour;
    }

    public int getCurrentHour() {
        return currentHour;
    }

    public boolean isNightWon() {
        return nightWon;
    }

    public void setFastForward(boolean ff) {
        this.fastForward = ff;
    }

    public boolean isFastForward() {
        return fastForward;
    }

    public void skipToNextHour() {
        if (nightWon) return;
        currentHour = Math.min(6, currentHour + 1);
        elapsedSeconds = currentHour * hourDuration;
        if (currentHour >= 6) {
            nightWon = true;
        }
    }

    public double getProgressFraction() {
        return Math.min(1.0, elapsedSeconds / (hourDuration * 6.0));
    }
}
