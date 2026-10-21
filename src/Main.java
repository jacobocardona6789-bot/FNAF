import fnaf.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * Five Nights at Freddy's (FNAF 1) - Complete Recreation from Scratch
 * 
 * Mechanics implemented:
 * - Title screen with twitching Freddy Fazbear and Star rating system (1-3 stars)
 * - Save system persisting unlocked nights & stars
 * - New Game Newspaper Intro
 * - 12:00 AM Night Intro
 * - Complete Office with smooth panoramic mouse panning (edge-scrolling)
 * - Left & Right Doors with animated sliding blast doors
 * - Left & Right Door Lights with hallway blind spot illumination
 * - Working Button Jamming when animatronics enter the office
 * - "CELEBRATE!" poster with clickable Freddy nose honk Easter egg
 * - Electric desk fan with spinning blades and ambient motor drone
 * - Complete Security Camera Monitor with 11 cameras and CRT flip up/down animation
 * - Interactive Minimap with active camera highlighting and switching
 * - Audio-only CAM 6 (Kitchen) with oscilloscope visualizer
 * - Accurate Power System (100% to 0%, 1-5 usage bars, realistic drain formulas)
 * - Complete Power Outage Blackout sequence: doors forced open, Toreador March music box,
 *   flashing Freddy face in the doorway, delay in pitch darkness, blackout jumpscare
 * - Full Clock System (12 AM to 6 AM, 6:00 AM celebration bells and cheering)
 * - Complete Animatronic AI:
 *     * Bonnie (Show Stage -> Dining / Backstage -> West Hall -> Supply Closet -> Doorway)
 *     * Chica (Show Stage -> Dining -> Restrooms -> Kitchen pots/pans -> East Hall -> Window)
 *     * Foxy (Pirate Cove stages 1-4, sprint down West Hall CAM 2A, door pounding power drain,
 *             stalled by camera usage)
 *     * Freddy Fazbear (Active Night 3+, sneaky rules: freezes when watched on cameras,
 *                       deep echoing laughs on movement, sneaks through East Hall 4B into office)
 *     * Golden Freddy (Rare CAM 2B poster hallucination -> office spawn -> banish with camera)
 * - Multi-frame animated procedural jumpscares with screen shake and horrific screech audio
 * - Procedural Sound Engine (no external files required - synthesizes 16-bit PCM WAV audio for
 *   screams, fan, doors, lights, switches, laughter, kitchen clatter, Toreador March, and chimes)
 * - Phone Guy calls (Nights 1-5 transcripts & subtitle audio) with functional "MUTE CALL" button
 * - Night 6 (Unlocked after Night 5)
 * - Custom Night (Night 7 with 0-20 AI sliders for all 4 animatronics & 20/20/20/20 mode)
 * - Paycheck Screens ($120.00 for Night 5, $120.50 for Night 6, Pink Slip for Custom Night)
 * - Keyboard shortcuts: A/D (Doors), Q/E (Lights), Space/S (Monitor), 1-0 (Cameras),
 *   Ctrl+D (Debug overlay), Ctrl+H (Skip hour), Ctrl+G (Golden Freddy), Esc (Menu)
 */
public class Main {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Five Nights at Freddy's");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            Canvas canvas = new Canvas();
            canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
            canvas.setFocusable(true);

            frame.add(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Create icon
            BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D ig = icon.createGraphics();
            ig.setColor(new Color(110, 65, 35));
            ig.fillOval(4, 4, 24, 24);
            ig.setColor(new Color(15, 15, 20));
            ig.fillRect(10, 0, 12, 10);
            ig.dispose();
            frame.setIconImage(icon);

            // Initialize Game Engine
            GameEngine engine = new GameEngine();
            canvas.addKeyListener(engine);
            canvas.addMouseListener(engine);
            canvas.addMouseMotionListener(engine);
            canvas.requestFocus();

            // Set up double-buffered strategy
            canvas.createBufferStrategy(2);
            BufferStrategy bs = canvas.getBufferStrategy();

            // Game Loop Thread (Target 60 FPS)
            Thread gameLoop = new Thread(() -> {
                final long targetNs = 1_000_000_000L / 60; // ~16.66 ms per frame
                long lastTime = System.nanoTime();

                while (frame.isDisplayable()) {
                    long now = System.nanoTime();
                    long elapsedNs = now - lastTime;
                    lastTime = now;

                    double dt = Math.min(0.1, elapsedNs / 1_000_000_000.0);

                    // Update game logic
                    engine.update(dt);

                    // Render game frame
                    Graphics2D g = null;
                    try {
                        g = (Graphics2D) bs.getDrawGraphics();
                        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                        engine.render(g);
                    } finally {
                        if (g != null) g.dispose();
                    }

                    if (!bs.contentsLost()) {
                        bs.show();
                    }

                    // Precise frame rate limiter
                    long frameTime = System.nanoTime() - now;
                    long sleepMs = (targetNs - frameTime) / 1_000_000L;
                    if (sleepMs > 1) {
                        try {
                            Thread.sleep(sleepMs);
                        } catch (InterruptedException ignored) {}
                    }
                }
            });

            gameLoop.setName("FNAF-GameLoop");
            gameLoop.setDaemon(true);
            gameLoop.start();
        });
    }
}
