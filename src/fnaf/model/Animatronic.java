package fnaf.model;

import fnaf.audio.SoundManager;
import java.util.Random;

/**
 * Encapsulates the AI and state for an individual animatronic character.
 */
public class Animatronic {
    private final AnimatronicType type;
    private int aiLevel = 0; // 0 to 20
    private RoomId currentRoom;
    private double moveTimer = 0.0;
    private final double moveInterval; // Bonnie: ~4.97s, Chica: ~4.98s, Foxy: ~5.01s, Freddy: ~3.02s

    // Foxy specifics
    private int foxyStage = 0; // 0: Curtain closed, 1: Peeking, 2: Outside cove, 3: Sprinting down hall
    private double foxySprintTimer = 0.0;
    private boolean foxyRunning = false;

    // Office infiltration
    private boolean enteredOffice = false;
    private double officeAttackTimer = 0.0;

    // Golden Freddy specifics
    private boolean goldenFreddyActive = false;
    private double goldenFreddyTimer = 0.0;

    private final Random rand = new Random();

    public Animatronic(AnimatronicType type) {
        this.type = type;
        switch (type) {
            case FREDDY:
                this.moveInterval = 3.02;
                this.currentRoom = RoomId.STAGE_1A;
                break;
            case BONNIE:
                this.moveInterval = 4.97;
                this.currentRoom = RoomId.STAGE_1A;
                break;
            case CHICA:
                this.moveInterval = 4.98;
                this.currentRoom = RoomId.STAGE_1A;
                break;
            case FOXY:
                this.moveInterval = 5.01;
                this.currentRoom = RoomId.PIRATE_COVE_1C;
                break;
            case GOLDEN_FREDDY:
                this.moveInterval = 1.0;
                this.currentRoom = RoomId.WEST_CORNER_2B;
                break;
            default:
                this.moveInterval = 5.0;
                this.currentRoom = RoomId.STAGE_1A;
        }
    }

    public void reset(int ai) {
        this.aiLevel = Math.max(0, Math.min(20, ai));
        this.moveTimer = 0.0;
        this.enteredOffice = false;
        this.officeAttackTimer = 0.0;
        this.foxyStage = 0;
        this.foxySprintTimer = 0.0;
        this.foxyRunning = false;
        this.goldenFreddyActive = false;
        this.goldenFreddyTimer = 0.0;

        switch (type) {
            case FREDDY:
            case BONNIE:
            case CHICA:
                this.currentRoom = RoomId.STAGE_1A;
                break;
            case FOXY:
                this.currentRoom = RoomId.PIRATE_COVE_1C;
                break;
            case GOLDEN_FREDDY:
                this.currentRoom = RoomId.WEST_CORNER_2B;
                break;
        }
    }

    /**
     * Update animatronic AI tick.
     * Returns an AnimatronicType if a jumpscare is triggered, else null.
     */
    public AnimatronicType update(double dtSeconds,
                                  RoomId watchedCamera,
                                  boolean monitorOpen,
                                  boolean leftDoorClosed,
                                  boolean rightDoorClosed,
                                  PowerSystem powerSystem,
                                  SoundManager soundManager) {
        if (aiLevel <= 0 && type != AnimatronicType.GOLDEN_FREDDY) {
            return null;
        }

        switch (type) {
            case BONNIE:
                return updateBonnie(dtSeconds, watchedCamera, monitorOpen, leftDoorClosed, soundManager);
            case CHICA:
                return updateChica(dtSeconds, watchedCamera, monitorOpen, rightDoorClosed, soundManager);
            case FOXY:
                return updateFoxy(dtSeconds, watchedCamera, monitorOpen, leftDoorClosed, powerSystem, soundManager);
            case FREDDY:
                return updateFreddy(dtSeconds, watchedCamera, monitorOpen, rightDoorClosed, soundManager);
            case GOLDEN_FREDDY:
                return updateGoldenFreddy(dtSeconds, watchedCamera, monitorOpen, soundManager);
            default:
                return null;
        }
    }

    // ==========================================
    // BONNIE AI
    // ==========================================
    private AnimatronicType updateBonnie(double dtSeconds, RoomId watchedCamera, boolean monitorOpen,
                                         boolean leftDoorClosed, SoundManager soundManager) {
        // If already entered office:
        if (enteredOffice) {
            officeAttackTimer += dtSeconds;
            // Jumpscares immediately when monitor is lowered or after brief delay
            if (!monitorOpen || officeAttackTimer >= 4.0) {
                return AnimatronicType.BONNIE;
            }
            return null;
        }

        moveTimer += dtSeconds;
        if (moveTimer >= moveInterval) {
            moveTimer = 0.0;
            // Roll d20 (1-20)
            int roll = rand.nextInt(20) + 1;
            if (roll <= aiLevel) {
                // Move!
                advanceBonnie(leftDoorClosed, monitorOpen, soundManager);
            }
        }
        return null;
    }

    private void advanceBonnie(boolean leftDoorClosed, boolean monitorOpen, SoundManager soundManager) {
        switch (currentRoom) {
            case STAGE_1A:
                currentRoom = (rand.nextBoolean()) ? RoomId.DINING_1B : RoomId.BACKSTAGE_5;
                soundManager.playFootsteps();
                break;
            case BACKSTAGE_5:
                currentRoom = (rand.nextBoolean()) ? RoomId.DINING_1B : RoomId.WEST_HALL_2A;
                soundManager.playFootsteps();
                break;
            case DINING_1B:
                currentRoom = (rand.nextBoolean()) ? RoomId.BACKSTAGE_5 : RoomId.WEST_HALL_2A;
                soundManager.playFootsteps();
                break;
            case WEST_HALL_2A:
                currentRoom = (rand.nextBoolean()) ? RoomId.SUPPLY_CLOSET_3 : RoomId.WEST_CORNER_2B;
                soundManager.playFootsteps();
                break;
            case SUPPLY_CLOSET_3:
                currentRoom = (rand.nextBoolean()) ? RoomId.WEST_HALL_2A : RoomId.WEST_CORNER_2B;
                soundManager.playFootsteps();
                break;
            case WEST_CORNER_2B:
                currentRoom = RoomId.LEFT_DOOR_BLIND_SPOT;
                soundManager.playRobotMovement();
                break;
            case LEFT_DOOR_BLIND_SPOT:
                if (leftDoorClosed) {
                    // Blocked! Retreat to dining or west hall
                    currentRoom = (rand.nextBoolean()) ? RoomId.DINING_1B : RoomId.WEST_HALL_2A;
                    soundManager.playFootsteps();
                } else {
                    // Door is open! Bonnie slips into office
                    if (monitorOpen) {
                        enteredOffice = true;
                        currentRoom = RoomId.OFFICE;
                        soundManager.playRobotMovement();
                    } else {
                        // Player caught him in hallway; he lingers or steps in
                        enteredOffice = true;
                        currentRoom = RoomId.OFFICE;
                    }
                }
                break;
            default:
                currentRoom = RoomId.STAGE_1A;
        }
    }

    // ==========================================
    // CHICA AI
    // ==========================================
    private AnimatronicType updateChica(double dtSeconds, RoomId watchedCamera, boolean monitorOpen,
                                        boolean rightDoorClosed, SoundManager soundManager) {
        // If already in office:
        if (enteredOffice) {
            officeAttackTimer += dtSeconds;
            if (!monitorOpen || officeAttackTimer >= 4.0) {
                return AnimatronicType.CHICA;
            }
            return null;
        }

        // Play kitchen sounds if Chica is in kitchen
        if (currentRoom == RoomId.KITCHEN_6 && rand.nextInt(120) == 0) {
            soundManager.playKitchenClatter();
        }

        moveTimer += dtSeconds;
        if (moveTimer >= moveInterval) {
            moveTimer = 0.0;
            int roll = rand.nextInt(20) + 1;
            if (roll <= aiLevel) {
                advanceChica(rightDoorClosed, monitorOpen, soundManager);
            }
        }
        return null;
    }

    private void advanceChica(boolean rightDoorClosed, boolean monitorOpen, SoundManager soundManager) {
        switch (currentRoom) {
            case STAGE_1A:
                currentRoom = RoomId.DINING_1B;
                soundManager.playFootsteps();
                break;
            case DINING_1B:
                currentRoom = (rand.nextBoolean()) ? RoomId.RESTROOMS_7 : RoomId.KITCHEN_6;
                soundManager.playFootsteps();
                break;
            case RESTROOMS_7:
                currentRoom = (rand.nextBoolean()) ? RoomId.KITCHEN_6 : RoomId.EAST_HALL_4A;
                soundManager.playFootsteps();
                break;
            case KITCHEN_6:
                currentRoom = RoomId.EAST_HALL_4A;
                soundManager.playKitchenClatter();
                break;
            case EAST_HALL_4A:
                currentRoom = RoomId.EAST_CORNER_4B;
                soundManager.playFootsteps();
                break;
            case EAST_CORNER_4B:
                currentRoom = RoomId.RIGHT_DOOR_BLIND_SPOT;
                soundManager.playRobotMovement();
                break;
            case RIGHT_DOOR_BLIND_SPOT:
                if (rightDoorClosed) {
                    // Blocked! Retreat to Dining or East Hall
                    currentRoom = (rand.nextBoolean()) ? RoomId.DINING_1B : RoomId.EAST_HALL_4A;
                    soundManager.playFootsteps();
                } else {
                    if (monitorOpen) {
                        enteredOffice = true;
                        currentRoom = RoomId.OFFICE;
                        soundManager.playRobotMovement();
                    } else {
                        enteredOffice = true;
                        currentRoom = RoomId.OFFICE;
                    }
                }
                break;
            default:
                currentRoom = RoomId.STAGE_1A;
        }
    }

    // ==========================================
    // FOXY AI
    // ==========================================
    private AnimatronicType updateFoxy(double dtSeconds, RoomId watchedCamera, boolean monitorOpen,
                                       boolean leftDoorClosed, PowerSystem powerSystem, SoundManager soundManager) {
        // If Foxy is sprinting:
        if (foxyRunning) {
            foxySprintTimer -= dtSeconds;
            if (foxySprintTimer <= 0) {
                foxyRunning = false;
                // Arrived at Left Door!
                if (leftDoorClosed) {
                    // Foxy bangs on the door!
                    soundManager.playFoxyDoorBang();
                    powerSystem.applyFoxyDoorBang();
                    // Reset Foxy to stage 0 or 1
                    foxyStage = (rand.nextBoolean()) ? 0 : 1;
                    currentRoom = RoomId.PIRATE_COVE_1C;
                } else {
                    // Left door was left open: JUMPSCARE!
                    return AnimatronicType.FOXY;
                }
            }
            return null;
        }

        // Camera monitor usage stalls Foxy's countdown!
        if (monitorOpen) {
            // When monitor is open (especially Pirate Cove CAM 1C), Foxy is delayed!
            return null;
        }

        moveTimer += dtSeconds;
        if (moveTimer >= moveInterval) {
            moveTimer = 0.0;
            int roll = rand.nextInt(20) + 1;
            if (roll <= aiLevel) {
                foxyStage++;
                if (foxyStage < 3) {
                    soundManager.playRobotMovement();
                } else if (foxyStage >= 3) {
                    // Stage 3: Empty cove -> Start sprinting!
                    foxyRunning = true;
                    foxySprintTimer = 3.2; // 3.2 seconds to react and shut left door
                    currentRoom = RoomId.WEST_HALL_2A;
                    soundManager.playFootsteps();
                }
            }
        }
        return null;
    }

    // ==========================================
    // FREDDY FAZBEAR AI
    // ==========================================
    private AnimatronicType updateFreddy(double dtSeconds, RoomId watchedCamera, boolean monitorOpen,
                                         boolean rightDoorClosed, SoundManager soundManager) {
        // If Freddy already snuck into office:
        if (enteredOffice) {
            officeAttackTimer += dtSeconds;
            if (!monitorOpen || officeAttackTimer >= 3.0) {
                return AnimatronicType.FREDDY;
            }
            return null;
        }

        // Sneaky rule: Freddy cannot move if the player is actively watching the camera he is in!
        if (monitorOpen && watchedCamera == currentRoom) {
            return null;
        }

        moveTimer += dtSeconds;
        if (moveTimer >= moveInterval) {
            moveTimer = 0.0;
            int roll = rand.nextInt(20) + 1;
            if (roll <= aiLevel) {
                advanceFreddy(rightDoorClosed, monitorOpen, soundManager);
            }
        }
        return null;
    }

    private void advanceFreddy(boolean rightDoorClosed, boolean monitorOpen, SoundManager soundManager) {
        soundManager.playDeepLaugh(rand.nextInt(3));

        switch (currentRoom) {
            case STAGE_1A:
                currentRoom = RoomId.DINING_1B;
                break;
            case DINING_1B:
                currentRoom = RoomId.RESTROOMS_7;
                break;
            case RESTROOMS_7:
                currentRoom = RoomId.KITCHEN_6;
                // Plays music box faintly from kitchen
                soundManager.startToreadorMarch();
                break;
            case KITCHEN_6:
                soundManager.stopToreadorMarch();
                currentRoom = RoomId.EAST_HALL_4A;
                break;
            case EAST_HALL_4A:
                currentRoom = RoomId.EAST_CORNER_4B;
                break;
            case EAST_CORNER_4B:
                // From 4B, if right door is closed: Freddy is blocked and stays on 4B or retreats to 4A
                if (rightDoorClosed) {
                    currentRoom = RoomId.EAST_HALL_4A;
                } else {
                    // Right door open: Freddy enters the office!
                    enteredOffice = true;
                    currentRoom = RoomId.OFFICE;
                }
                break;
            default:
                currentRoom = RoomId.STAGE_1A;
        }
    }

    // ==========================================
    // GOLDEN FREDDY AI
    // ==========================================
    private AnimatronicType updateGoldenFreddy(double dtSeconds, RoomId watchedCamera, boolean monitorOpen,
                                               SoundManager soundManager) {
        if (!goldenFreddyActive) {
            // Chance to trigger when CAM 2B is viewed
            if (monitorOpen && watchedCamera == RoomId.WEST_CORNER_2B && rand.nextInt(3000) == 0) {
                goldenFreddyActive = true;
                soundManager.playHallucination();
            }
            return null;
        }

        // Once triggered: when monitor is lowered, Golden Freddy sits in the office!
        if (!monitorOpen) {
            goldenFreddyTimer += dtSeconds;
            // If monitor not raised within 3 seconds, jumpscare / crash!
            if (goldenFreddyTimer >= 3.0) {
                return AnimatronicType.GOLDEN_FREDDY;
            }
        } else {
            // Raising the monitor banishes him!
            goldenFreddyActive = false;
            goldenFreddyTimer = 0.0;
        }
        return null;
    }

    // --- Getters & Setters ---
    public AnimatronicType getType() { return type; }
    public int getAiLevel() { return aiLevel; }
    public void setAiLevel(int ai) { this.aiLevel = ai; }
    public RoomId getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(RoomId r) { this.currentRoom = r; }
    public boolean hasEnteredOffice() { return enteredOffice; }
    public int getFoxyStage() { return foxyStage; }
    public boolean isFoxyRunning() { return foxyRunning; }
    public double getFoxySprintTimer() { return foxySprintTimer; }
    public boolean isGoldenFreddyActive() { return goldenFreddyActive; }
    public void triggerGoldenFreddy() { this.goldenFreddyActive = true; }
}
