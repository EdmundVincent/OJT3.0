package com.collaboportal.common.position;

public final class PositionRule {
    private final int permMask;
    private final int defaultMask;

    public PositionRule(int permMask, int defaultMask) {
        this.permMask = permMask;
        this.defaultMask = defaultMask;
    }

    public boolean can(Feature f) {
        return ((permMask >> f.ordinal()) & 1) == 1;
    }

    public boolean show(Feature f) {
        return ((defaultMask >> f.ordinal()) & 1) == 1;
    }

    public int getPermMask() {
        return permMask;
    }

    public int getDefaultMask() {
        return defaultMask;
    }
}
