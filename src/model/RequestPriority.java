package model;

/**
 * Enum representing urgency level of an NGO's food request.
 * Higher priority requests are processed first using PriorityQueue.
 */
public enum RequestPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4);

    private final int level;

    RequestPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
