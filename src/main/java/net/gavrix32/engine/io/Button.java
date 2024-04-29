package net.gavrix32.engine.io;

public enum Button {
    LAST(7),
    LEFT(0),
    RIGHT(1),
    MIDDLE(2);

    private int id;

    Button(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}