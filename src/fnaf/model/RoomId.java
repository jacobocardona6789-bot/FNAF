package fnaf.model;

/**
 * All 11 camera rooms and office positions in FNAF 1.
 */
public enum RoomId {
    STAGE_1A("CAM 1A", "Show Stage"),
    DINING_1B("CAM 1B", "Dining Area"),
    PIRATE_COVE_1C("CAM 1C", "Pirate Cove"),
    WEST_HALL_2A("CAM 2A", "West Hall"),
    WEST_CORNER_2B("CAM 2B", "West Hall Corner"),
    SUPPLY_CLOSET_3("CAM 3", "Supply Closet"),
    EAST_HALL_4A("CAM 4A", "East Hall"),
    EAST_CORNER_4B("CAM 4B", "East Hall Corner"),
    BACKSTAGE_5("CAM 5", "Backstage"),
    KITCHEN_6("CAM 6", "Kitchen (Audio Only)"),
    RESTROOMS_7("CAM 7", "Restrooms"),

    // Special positions (not cameras)
    LEFT_DOOR_BLIND_SPOT("DOOR_L", "Left Doorway"),
    RIGHT_DOOR_BLIND_SPOT("DOOR_R", "Right Door Window"),
    OFFICE("OFFICE", "Security Office");

    private final String code;
    private final String displayName;

    RoomId(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCamera() {
        return this != LEFT_DOOR_BLIND_SPOT && this != RIGHT_DOOR_BLIND_SPOT && this != OFFICE;
    }
}
