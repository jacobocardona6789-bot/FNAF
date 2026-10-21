# Five Nights at Freddy's 1 (From Scratch in Java)

A complete, accurate recreation of the original **Five Nights at Freddy's (FNAF 1)** built from scratch in pure Java with 60 FPS double-buffered rendering and real-time procedural audio synthesis. Zero external dependencies required.

---

## 🎮 How to Run

### Option 1: Quick Terminal Run
```bash
./run.sh
```

### Option 2: Run Executable JAR
```bash
java -jar FNAF.jar
```

### Option 3: IntelliJ IDEA
1. Open this directory (`/home/jaco/IdeaProjects/FNAF`) in IntelliJ IDEA.
2. Open [`src/Main.java`](file:///home/jaco/IdeaProjects/FNAF/src/Main.java).
3. Click the green **Run** button (or press `Shift + F10`).

---

## 🕹️ Controls

| Action | Mouse | Keyboard Shortcut |
| :--- | :--- | :--- |
| **Pan Office View** | Move mouse towards left/right edge | - |
| **Toggle Left Door** | Click red/green button next to left door | <kbd>A</kbd> |
| **Toggle Left Light** | Click white/yellow button next to left door | <kbd>Q</kbd> |
| **Toggle Right Door** | Click red/green button next to right door | <kbd>D</kbd> |
| **Toggle Right Light** | Click white/yellow button next to right door | <kbd>E</kbd> |
| **Flip Monitor Up / Down** | Hover / click the bottom flip bar | <kbd>Space</kbd> or <kbd>S</kbd> |
| **Switch Cameras** | Click camera buttons on minimap | <kbd>1</kbd> to <kbd>9</kbd>, <kbd>0</kbd> |
| **Mute Phone Call** | Click red "MUTE CALL" button at top left | - |
| **Honk Freddy's Nose** | Click Freddy's nose on the "CELEBRATE!" poster | - |
| **Main Menu / Pause** | - | <kbd>Esc</kbd> |
| **Debug Minimap Overlay** | - | <kbd>Ctrl</kbd> + <kbd>D</kbd> |
| **Skip Hour (Testing)** | - | <kbd>Ctrl</kbd> + <kbd>H</kbd> |
| **Trigger Golden Freddy** | - | <kbd>Ctrl</kbd> + <kbd>G</kbd> |

---

## 🐻 Core Mechanics & Animatronic AI

### 1. Security Office & Perspective
- **Panoramic Pan**: Smooth panoramic panning as you move your mouse to screen edges.
- **Sliding Blast Doors**: Smooth mechanical steel doors with hazard stripes.
- **Doorway Lights**: Illuminates hallway blind spots.
- **Button Jamming**: If Bonnie or Chica slip inside while the monitor is raised, door and light buttons become jammed!
- **Easter Eggs**: Clickable squeaking nose on the "CELEBRATE!" poster.

### 2. Camera System (11 Cameras)
- **CAM 1A (Show Stage)**: Freddy, Bonnie, and Chica starting positions.
- **CAM 1B (Dining Area)**: Dining tables and roaming animatronics.
- **CAM 1C (Pirate Cove)**: Foxy's lair. Shows progression stages (closed, peeking, outside, gone!). Stalled by camera usage.
- **CAM 2A (West Hall)**: Perspective hallway. Displays Foxy's animated sprint towards the office!
- **CAM 2B (West Hall Corner)**: Corner view outside left door. Golden Freddy poster Easter egg chance.
- **CAM 3 (Supply Closet)**: Narrow closet with mop and cleaning supplies.
- **CAM 4A (East Hall)**: Hallway with party streamers.
- **CAM 4B (East Hall Corner)**: Corner outside right door. Freddy and Chica blind spot.
- **CAM 5 (Backstage)**: Table with spare heads and Endoskeleton skull staring into the camera.
- **CAM 6 (Kitchen)**: Authentic **Camera Disabled - Audio Only** feed! Oscilloscope waveform visualizer reacting to Chica's clattering pots and Freddy's music box.
- **CAM 7 (Restrooms)**: Restroom corridor.

### 3. Animatronics AI
- **Bonnie**: Moves every ~4.97s on d20 <= AI level. Path: 1A -> 1B/5 -> 2A/3 -> 2B -> Left Doorway. Blocked by Left Door; sneaks in and jams buttons if open.
- **Chica**: Moves every ~4.98s on d20 <= AI level. Path: 1A -> 1B -> 7/6 (Kitchen) -> 4A -> 4B -> Right Window. Visible in window with Right Light. Jams buttons if door open.
- **Foxy**: Pirate Cove stages 0-3. Progresses every ~5.01s. Checking cameras stalls him. In Stage 3, sprints down CAM 2A! If Left Door is shut in time, bangs on door and drains power (1%, 6%, 11%, etc.). If open, jumpscares!
- **Freddy Fazbear**: Active on Night 3+. Sneaky AI: **freezes when actively watched on cameras**. Laughs every time he moves. At CAM 4B, sneaks into office if Right Door is open when cameras are checked.
- **Golden Freddy**: Rare Easter egg on CAM 2B. Spawns in office with "IT'S ME" hallucinations. Banish by flipping monitor back up!

### 4. Power & Blackout System
- 100% starting battery. 1-5 usage bars.
- Realistic FNAF drain rates (1 bar = ~0.104%/s, up to 5 bars = ~0.526%/s).
- **Power Outage (0% Power)**:
  - Total blackout! Office lights cut out, doors force open, fan sound dies.
  - Freddy appears in left doorway flashing glowing eyes and mouth to the **Toreador March** music box.
  - Music stops, footstep sounds in total darkness, leading to pitch-black jumpscare (unless 6 AM saves you in time!).

### 5. Night Progression & Paychecks
- **Nights 1 to 5**: Progressive difficulty with Phone Guy calls.
- **Night 6**: Overtime Night unlocked after beating Night 5.
- **Custom Night (Night 7)**: Unlocked after Night 6. Full 0-20 AI sliders for all 4 animatronics.
- **Star Rating System**:
  - ⭐ **1 Star**: Beat Night 5 ($120.00 Paycheck).
  - ⭐⭐ **2 Stars**: Beat Night 6 ($120.50 Overtime Paycheck).
  - ⭐⭐⭐ **3 Stars**: Beat 20/20/20/20 Custom Night ("Notice of Termination" Pink Slip).

### 6. Procedural Audio Engine
Synthesizes 16-bit 44.1kHz PCM audio in-memory for all sound effects:
- Low-frequency office fan motor hum loop
- Heavy mechanical door slide and slam
- 60Hz neon light buzz
- Crisp button switches and jammed relay clicks
- Pneumatic camera flip swoosh & CRT switch blips
- Mechanical footsteps and deep Freddy robotic laughs
- Kitchen pots & pans clattering
- Pirate sea shanty song
- Toreador March music box melody
- Piercing FM-modulated jumpscare screeches
- 6:00 AM clock tower chimes and crowd cheering
- Freddy nose squeak honk

---

## 📁 Project Architecture

```
src/
├── Main.java                        # Entry point & 60 FPS double-buffered game loop
└── fnaf/
    ├── GameEngine.java              # Central game controller & state machine
    ├── GameState.java               # Enum of game states
    ├── FNAFMechanicsTest.java       # Complete automated mechanics test suite
    ├── audio/
    │   └── SoundManager.java        # Procedural PCM audio synthesizer & clips
    ├── model/
    │   ├── Animatronic.java         # AI logic for Freddy, Bonnie, Chica, Foxy, Golden Freddy
    │   ├── AnimatronicType.java     # Character types
    │   ├── RoomId.java              # 11 camera rooms & office positions
    │   ├── PowerSystem.java         # Battery drain, usage bars, blackout
    │   ├── ClockSystem.java         # 12 AM - 6 AM timer & night won logic
    │   ├── OfficeState.java         # Panning, door positions, buttons, fan, blackout phase
    │   └── PhoneGuy.java            # Phone calls, transcripts, mute button
    ├── render/
    │   ├── ArtGenerator.java        # Procedural graphics, portraits, textures, lighting
    │   ├── CameraRenderer.java      # Minimap, CRT scanlines, camera feeds
    │   └── JumpscareRenderer.java   # Shaking multi-frame animated jumpscares
    ├── ui/
    │   ├── MainMenuScreen.java      # Title menu with twitching Freddy & stars
    │   ├── CustomNightScreen.java   # Night 7 AI selector (0-20)
    │   ├── NewspaperScreen.java     # Help Wanted newspaper clipping
    │   ├── NightIntroScreen.java    # 12:00 AM loading screen
    │   ├── WinScreen.java           # 6:00 AM celebration sequence
    │   ├── PaycheckScreen.java      # $120.00 check, $120.50 check, Pink slip
    │   └── GameOverScreen.java      # CRT static game over screen
    └── save/
        └── SaveManager.java         # Saves unlocked nights & stars
```
