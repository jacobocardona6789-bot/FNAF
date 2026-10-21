package fnaf.model;

public enum AnimatronicType {
    FREDDY("Freddy Fazbear", 0),
    BONNIE("Bonnie", 1),
    CHICA("Chica", 2),
    FOXY("Foxy the Pirate", 3),
    GOLDEN_FREDDY("Golden Freddy", 4);

    private final String displayName;
    private final int index;

    AnimatronicType(String displayName, int index) {
        this.displayName = displayName;
        this.index = index;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getIndex() {
        return index;
    }
}
