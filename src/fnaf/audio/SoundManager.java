package fnaf.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.Random;

/**
 * Procedural audio synthesizer and sound effect manager for FNAF 1.
 * Synthesizes authentic sound effects into in-memory PCM clips without needing external files.
 */
public class SoundManager {
    private static final int SAMPLE_RATE = 44100;
    private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

    private Clip fanClip;
    private Clip lightClip;
    private Clip staticClip;
    private Clip toreadorClip;
    private Clip foxySongClip;

    private boolean muted = false;
    private final Random rand = new Random();

    public SoundManager() {
        initLoops();
    }

    private void initLoops() {
        try {
            fanClip = createClip(generateFanHum());
            lightClip = createClip(generateLightBuzz());
            staticClip = createClip(generateStaticHum());
            toreadorClip = createClip(generateToreadorMarch());
            foxySongClip = createClip(generateFoxySong());
        } catch (Exception e) {
            System.err.println("Could not initialize loop audio clips: " + e.getMessage());
        }
    }

    private Clip createClip(byte[] data) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(FORMAT, data, 0, data.length);
            return clip;
        } catch (Exception e) {
            System.err.println("Error creating audio clip: " + e.getMessage());
            return null;
        }
    }

    private void playOnce(byte[] data, float volumeGainDb) {
        if (muted) return;
        new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(FORMAT, data, 0, data.length);
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), volumeGainDb)));
                }
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
            } catch (Exception ignored) {}
        }).start();
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            stopFanHum();
            stopLightBuzz();
            stopStaticHum();
            stopToreadorMarch();
            stopFoxySong();
        }
    }

    public boolean isMuted() {
        return muted;
    }

    // --- Fan Loop ---
    public void startFanHum() {
        if (muted || fanClip == null) return;
        if (!fanClip.isRunning()) {
            fanClip.setFramePosition(0);
            fanClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopFanHum() {
        if (fanClip != null && fanClip.isRunning()) {
            fanClip.stop();
        }
    }

    // --- Light Buzz Loop ---
    public void startLightBuzz() {
        if (muted || lightClip == null) return;
        if (!lightClip.isRunning()) {
            lightClip.setFramePosition(0);
            lightClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopLightBuzz() {
        if (lightClip != null && lightClip.isRunning()) {
            lightClip.stop();
        }
    }

    // --- Static Buzz Loop ---
    public void startStaticHum() {
        if (muted || staticClip == null) return;
        if (!staticClip.isRunning()) {
            staticClip.setFramePosition(0);
            staticClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopStaticHum() {
        if (staticClip != null && staticClip.isRunning()) {
            staticClip.stop();
        }
    }

    // --- Toreador March Music Box (Blackout / Freddy in Kitchen) ---
    public void startToreadorMarch() {
        if (muted || toreadorClip == null) return;
        if (!toreadorClip.isRunning()) {
            toreadorClip.setFramePosition(0);
            toreadorClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopToreadorMarch() {
        if (toreadorClip != null && toreadorClip.isRunning()) {
            toreadorClip.stop();
        }
    }

    // --- Foxy Song ---
    public void startFoxySong() {
        if (muted || foxySongClip == null) return;
        if (!foxySongClip.isRunning()) {
            foxySongClip.setFramePosition(0);
            foxySongClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopFoxySong() {
        if (foxySongClip != null && foxySongClip.isRunning()) {
            foxySongClip.stop();
        }
    }

    // --- One-Shot Sound Effects ---

    public void playDoorMove() {
        playOnce(generateDoorMove(), 0.0f);
    }

    public void playButtonClick() {
        playOnce(generateButtonClick(), -3.0f);
    }

    public void playButtonJam() {
        playOnce(generateButtonJam(), 0.0f);
    }

    public void playCameraFlip() {
        playOnce(generateCameraFlip(), -2.0f);
    }

    public void playCameraSwitch() {
        playOnce(generateCameraSwitch(), -5.0f);
    }

    public void playNoseHonk() {
        playOnce(generateNoseHonk(), 2.0f);
    }

    public void playFootsteps() {
        playOnce(generateFootstep(), -2.0f);
    }

    public void playDeepLaugh(int pitch) {
        playOnce(generateDeepLaugh(pitch), 0.0f);
    }

    public void playKitchenClatter() {
        playOnce(generateKitchenClatter(), -4.0f);
    }

    public void playFoxyDoorBang() {
        playOnce(generateFoxyDoorBang(), 4.0f);
    }

    public void playJumpscare() {
        playOnce(generateJumpscareScreech(), 6.0f);
    }

    public void play6AMChimes() {
        playOnce(generate6AMChimes(), 2.0f);
    }

    public void playPhoneRing() {
        playOnce(generatePhoneRing(), -2.0f);
    }

    public void playHallucination() {
        playOnce(generateHallucination(), 0.0f);
    }

    public void playRobotMovement() {
        playOnce(generateRobotMovement(), -3.0f);
    }

    // ==========================================
    // PROCEDURAL AUDIO SYNTHESIZERS
    // ==========================================

    private byte[] generateFanHum() {
        int durationSec = 2;
        int totalSamples = SAMPLE_RATE * durationSec;
        byte[] data = new byte[totalSamples * 2];
        double filterState = 0;

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            // Fan low motor hum (96Hz + 192Hz + 288Hz)
            double hum = Math.sin(2 * Math.PI * 96 * t) * 0.4
                    + Math.sin(2 * Math.PI * 192 * t) * 0.2
                    + Math.sin(2 * Math.PI * 288 * t) * 0.1;
            // Air turbulence (smoothed pink-like noise)
            double white = (rand.nextDouble() * 2.0 - 1.0) * 0.25;
            filterState += (white - filterState) * 0.15;
            double sampleVal = (hum + filterState) * 0.25;

            // Soft looping fade at edges
            if (i < 2000) sampleVal *= (double) i / 2000;
            if (i > totalSamples - 2000) sampleVal *= (double) (totalSamples - i) / 2000;

            short s = (short) Math.max(-32767, Math.min(32767, sampleVal * 32767));
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateLightBuzz() {
        int durationSec = 1;
        int totalSamples = SAMPLE_RATE * durationSec;
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            // 60Hz mains hum with sharp harmonics and slight jitter
            double buzz = Math.sin(2 * Math.PI * 60 * t) * 0.35
                    + Math.sin(2 * Math.PI * 120 * t) * 0.3
                    + Math.sin(2 * Math.PI * 180 * t) * 0.2
                    + Math.sin(2 * Math.PI * 240 * t) * 0.1
                    + (rand.nextDouble() * 0.08 - 0.04);
            short s = (short) (buzz * 0.4 * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateStaticHum() {
        int durationSec = 2;
        int totalSamples = SAMPLE_RATE * durationSec;
        byte[] data = new byte[totalSamples * 2];
        double filter = 0;

        for (int i = 0; i < totalSamples; i++) {
            double noise = rand.nextDouble() * 2.0 - 1.0;
            filter += (noise - filter) * 0.4;
            double sampleVal = filter * 0.15;
            short s = (short) (sampleVal * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateDoorMove() {
        // Heavy metallic pneumatic sliding down and clank
        double duration = 0.55;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double progress = (double) i / totalSamples;
            double sampleVal;

            if (progress < 0.75) {
                // Motor gear slide
                double freq = 120 - progress * 40;
                double motor = Math.sin(2 * Math.PI * freq * t) * 0.4
                        + (rand.nextDouble() * 2.0 - 1.0) * 0.2;
                sampleVal = motor * 0.7;
            } else {
                // Heavy metallic impact thud
                double slamT = (progress - 0.75) / 0.25;
                double slamEnv = Math.exp(-slamT * 12.0);
                double slamFreq = 65.0 * Math.exp(-slamT * 4.0);
                double metallicRing = Math.sin(2 * Math.PI * 450 * t) * Math.exp(-slamT * 20.0) * 0.3;
                double bassImpact = Math.sin(2 * Math.PI * slamFreq * t) * 0.8;
                sampleVal = (bassImpact + metallicRing) * slamEnv;
            }

            short s = (short) Math.max(-32767, Math.min(32767, sampleVal * 32767));
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateButtonClick() {
        double duration = 0.05;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double env = Math.exp(-t * 120.0);
            double freq = 1400.0 - t * 15000.0;
            double sampleVal = Math.sin(2 * Math.PI * Math.max(200, freq) * t) * env;
            short s = (short) (sampleVal * 32767 * 0.6);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateButtonJam() {
        // Quick broken metallic double-click buzz
        double duration = 0.18;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double click1 = Math.exp(-t * 90.0) * Math.sin(2 * Math.PI * 220 * t);
            double click2 = (t > 0.08) ? Math.exp(-(t - 0.08) * 90.0) * Math.sin(2 * Math.PI * 180 * t) : 0;
            double sampleVal = (click1 + click2) * 0.7;
            short s = (short) (sampleVal * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateCameraFlip() {
        // Pneumatic CRT monitor flipping swoosh
        double duration = 0.28;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double p = (double) i / totalSamples;
            double env = Math.sin(p * Math.PI);
            double noise = rand.nextDouble() * 2.0 - 1.0;
            double swooshFreq = 1800.0 * (1.0 - p) + 300.0;
            double tone = Math.sin(2 * Math.PI * swooshFreq * t);
            double sampleVal = (noise * 0.4 + tone * 0.6) * env * 0.5;
            short s = (short) (sampleVal * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateCameraSwitch() {
        // CRT camera switch blip / pop
        double duration = 0.08;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double env = Math.exp(-t * 60.0);
            double noise = rand.nextDouble() * 2.0 - 1.0;
            double blip = Math.sin(2 * Math.PI * 1200 * t) * 0.5;
            double sampleVal = (noise * 0.6 + blip) * env * 0.5;
            short s = (short) (sampleVal * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateNoseHonk() {
        // Cute toy rubber squeak honk
        double duration = 0.22;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double p = (double) i / totalSamples;
            double env = Math.sin(p * Math.PI);
            double freq = 900 + p * 600;
            double sampleVal = (Math.sin(2 * Math.PI * freq * t) + 0.3 * Math.sin(2 * Math.PI * freq * 2 * t)) * env * 0.7;
            short s = (short) (sampleVal * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateFootstep() {
        // Heavy animatronic thud step
        double duration = 0.4;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double env = Math.exp(-t * 14.0);
            double freq = 95.0 * Math.exp(-t * 8.0);
            double sampleVal = (Math.sin(2 * Math.PI * freq * t) + (rand.nextDouble() - 0.5) * 0.2) * env * 0.8;
            short s = (short) (sampleVal * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateRobotMovement() {
        // Mechanical servo movement sound
        double duration = 0.35;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double env = Math.sin(((double) i / totalSamples) * Math.PI);
            double freq = 300.0 + Math.sin(t * 40.0) * 80.0;
            double servo = Math.sin(2 * Math.PI * freq * t) * 0.5 + (rand.nextDouble() - 0.5) * 0.2;
            short s = (short) (servo * env * 32767 * 0.5);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateDeepLaugh(int pitchVariant) {
        // Resonant Freddy Fazbear deep echoing robotic chuckle
        double duration = 1.3;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];
        double baseFreq = 85.0 - (pitchVariant % 3) * 12.0;

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            // 3-pulse chuckle
            double pulses = Math.max(0, Math.sin(t * 14.0));
            double env = pulses * Math.exp(-t * 2.0);
            double laugh = Math.sin(2 * Math.PI * baseFreq * t) * 0.6
                    + Math.sin(2 * Math.PI * baseFreq * 2.2 * t) * 0.3
                    + (rand.nextDouble() - 0.5) * 0.15;
            short s = (short) Math.max(-32767, Math.min(32767, laugh * env * 32767 * 0.9));
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateKitchenClatter() {
        // Dropping pots, pans, and metallic cookware in kitchen
        double duration = 1.2;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        // Synthesize several metallic hits at staggered offsets
        double[] hitTimes = {0.05, 0.22, 0.38, 0.65, 0.88};
        double[] hitFreqs = {440.0, 720.0, 310.0, 890.0, 520.0};

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double val = 0;
            for (int h = 0; h < hitTimes.length; h++) {
                if (t >= hitTimes[h]) {
                    double dt = t - hitTimes[h];
                    double env = Math.exp(-dt * 15.0);
                    val += (Math.sin(2 * Math.PI * hitFreqs[h] * dt) * 0.4
                            + Math.sin(2 * Math.PI * (hitFreqs[h] * 1.58) * dt) * 0.3
                            + (rand.nextDouble() - 0.5) * 0.2) * env;
                }
            }
            short s = (short) Math.max(-32767, Math.min(32767, val * 32767 * 0.5));
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateFoxySong() {
        // "Dum dum dum dum..." pirate sea shanty humming
        double noteDuration = 0.45;
        // Simple pirate tune: A3, C4, B3, A3, G#3, A3
        double[] notes = {220.0, 261.63, 246.94, 220.0, 207.65, 220.0, 0, 220.0, 261.63, 246.94, 220.0, 0};
        int totalSamples = (int) (SAMPLE_RATE * noteDuration * notes.length);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            int noteIdx = (int) (t / noteDuration);
            double noteT = t % noteDuration;
            double freq = notes[Math.min(notes.length - 1, noteIdx)];
            double val = 0;
            if (freq > 0) {
                double env = Math.sin((noteT / noteDuration) * Math.PI);
                // Vocal formant hum
                val = (Math.sin(2 * Math.PI * freq * noteT) * 0.5
                        + Math.sin(2 * Math.PI * freq * 2 * noteT) * 0.25) * env * 0.4;
            }
            short s = (short) (val * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateFoxyDoorBang() {
        // Violent heavy metallic banging on door
        double duration = 1.1;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];
        double[] bangTimes = {0.05, 0.3, 0.55, 0.8};

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double val = 0;
            for (double bangTime : bangTimes) {
                if (t >= bangTime) {
                    double dt = t - bangTime;
                    double env = Math.exp(-dt * 20.0);
                    double boom = Math.sin(2 * Math.PI * 75 * dt) * 0.7;
                    double metal = Math.sin(2 * Math.PI * 340 * dt) * 0.3;
                    val += (boom + metal) * env;
                }
            }
            short s = (short) Math.max(-32767, Math.min(32767, val * 32767 * 0.7));
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateToreadorMarch() {
        // Carmen's Toreador Song - Music Box melody (Bizet)
        // Authentic music box chime synth
        // Notes: F4, D4, F4, G4, F4, E4, D4, C#4, D4, E4, F4, D4, F4, A4, G4, F4, E4, D4
        double beat = 0.32;
        double[] melody = {
                349.23, beat,      // F4
                293.66, beat * 0.5, // D4
                349.23, beat * 0.5, // F4
                392.00, beat * 0.5, // G4
                349.23, beat * 0.5, // F4
                329.63, beat * 0.5, // E4
                293.66, beat * 0.5, // D4
                277.18, beat,      // C#4
                293.66, beat * 0.5, // D4
                329.63, beat * 0.5, // E4
                349.23, beat,      // F4
                293.66, beat * 0.5, // D4
                349.23, beat * 0.5, // F4
                440.00, beat * 0.5, // A4
                392.00, beat * 0.5, // G4
                349.23, beat * 0.5, // F4
                329.63, beat * 0.5, // E4
                293.66, beat * 1.5, // D4
                0.0,    beat * 0.8  // Rest
        };

        double totalDur = 0;
        for (int i = 1; i < melody.length; i += 2) {
            totalDur += melody[i];
        }

        int totalSamples = (int) (SAMPLE_RATE * totalDur);
        byte[] data = new byte[totalSamples * 2];

        double currentNoteStart = 0;
        for (int m = 0; m < melody.length; m += 2) {
            double freq = melody[m];
            double dur = melody[m + 1];
            int startSample = (int) (currentNoteStart * SAMPLE_RATE);
            int noteSamples = (int) (dur * SAMPLE_RATE);

            if (freq > 0) {
                for (int i = 0; i < noteSamples && (startSample + i) < totalSamples; i++) {
                    double t = (double) i / SAMPLE_RATE;
                    // Music box chime timbre: fundamental + bright high harmonics with fast decay
                    double env = Math.exp(-t * 5.0);
                    double chime = Math.sin(2 * Math.PI * freq * t) * 0.5
                            + Math.sin(2 * Math.PI * freq * 2.0 * t) * 0.25
                            + Math.sin(2 * Math.PI * freq * 3.01 * t) * 0.15
                            + Math.sin(2 * Math.PI * freq * 4.02 * t) * 0.08;
                    int idx = (startSample + i) * 2;
                    short existing = (short) ((data[idx] & 0xFF) | (data[idx + 1] << 8));
                    short s = (short) Math.max(-32767, Math.min(32767, existing + chime * env * 32767 * 0.4));
                    data[idx] = (byte) (s & 0xFF);
                    data[idx + 1] = (byte) ((s >> 8) & 0xFF);
                }
            }
            currentNoteStart += dur;
        }
        return data;
    }

    private byte[] generateJumpscareScreech() {
        // Iconic horrifying FNAF 1 screech: harsh FM modulation, rapid frequency wobble, distorted screech!
        double duration = 1.35;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double p = (double) i / totalSamples;
            double env = Math.min(1.0, t * 40.0) * (1.0 - p * 0.35);

            // Carrier modulated by multiple modulators
            double mod1 = Math.sin(2 * Math.PI * 135.0 * t) * 350.0;
            double mod2 = Math.sin(2 * Math.PI * 45.0 * t) * 200.0;
            double carrier1 = Math.sin(2 * Math.PI * (1150.0 + mod1) * t);
            double carrier2 = Math.sin(2 * Math.PI * (820.0 + mod2) * t);
            double harshNoise = (rand.nextDouble() * 2.0 - 1.0) * 0.35;

            double mixed = (carrier1 * 0.45 + carrier2 * 0.45 + harshNoise) * env;
            // Heavy overdrive clipping for authentic mechanical scream distortion
            double clipped = Math.tanh(mixed * 2.2);

            short s = (short) (clipped * 32767 * 0.95);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generate6AMChimes() {
        // 6:00 AM clock tower chimes + cheering sound
        double duration = 5.0;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        // 4 Westminster bell chimes: G#4, E4, F#4, B3
        double[] chimeTimes = {0.2, 0.9, 1.6, 2.3};
        double[] chimeFreqs = {415.30, 329.63, 369.99, 246.94};

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double val = 0;

            // Bell chimes
            for (int c = 0; c < chimeTimes.length; c++) {
                if (t >= chimeTimes[c]) {
                    double dt = t - chimeTimes[c];
                    double env = Math.exp(-dt * 2.2);
                    double bell = Math.sin(2 * Math.PI * chimeFreqs[c] * dt) * 0.5
                            + Math.sin(2 * Math.PI * chimeFreqs[c] * 2.0 * dt) * 0.25
                            + Math.sin(2 * Math.PI * chimeFreqs[c] * 3.01 * dt) * 0.15;
                    val += bell * env * 0.4;
                }
            }

            // Cheering crowd starts around 2.6s
            if (t > 2.6) {
                double dt = t - 2.6;
                double cheerEnv = Math.min(1.0, dt * 2.0) * Math.max(0, 1.0 - (dt - 1.0) * 0.7);
                double cheerNoise = (rand.nextDouble() * 2.0 - 1.0);
                double voice1 = Math.sin(2 * Math.PI * (520.0 + Math.sin(dt * 8.0) * 40.0) * dt);
                double voice2 = Math.sin(2 * Math.PI * (640.0 + Math.sin(dt * 6.0) * 30.0) * dt);
                val += (cheerNoise * 0.25 + voice1 * 0.2 + voice2 * 0.15) * cheerEnv * 0.5;
            }

            short s = (short) Math.max(-32767, Math.min(32767, val * 32767));
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generatePhoneRing() {
        // Dual-tone rotary phone ring (440Hz + 480Hz)
        double duration = 1.6;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double val = 0;
            // 2 bursts of ringing
            if ((t >= 0.1 && t <= 0.6) || (t >= 0.8 && t <= 1.3)) {
                val = (Math.sin(2 * Math.PI * 440 * t) + Math.sin(2 * Math.PI * 480 * t)) * 0.35;
            }
            short s = (short) (val * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }

    private byte[] generateHallucination() {
        // Eerie whispered robotic sound
        double duration = 1.0;
        int totalSamples = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[totalSamples * 2];

        for (int i = 0; i < totalSamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            double env = Math.sin(((double) i / totalSamples) * Math.PI);
            double whisper = (rand.nextDouble() - 0.5) * Math.sin(2 * Math.PI * 300 * t);
            double sampleVal = whisper * env * 0.5;
            short s = (short) (sampleVal * 32767);
            data[i * 2] = (byte) (s & 0xFF);
            data[i * 2 + 1] = (byte) ((s >> 8) & 0xFF);
        }
        return data;
    }
}
