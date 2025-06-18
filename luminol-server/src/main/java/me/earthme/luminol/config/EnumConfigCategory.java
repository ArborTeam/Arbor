package me.earthme.luminol.config;

public enum EnumConfigCategory {
    OPTIMIZATIONS("optimizations"),
    FIXES("fixes"),
    MISC("misc"),
    GAMEPLAY("gameplay"),
    EXPERIMENT("experiment"),
    REMOVED("removed");

    private final String baseKeyName;

    EnumConfigCategory(String baseKeyName) {
        this.baseKeyName = baseKeyName;
    }

    public String getBaseKeyName() {
        return this.baseKeyName;
    }
}