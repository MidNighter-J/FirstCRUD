package com.novokren.exceptions;

public class RegionNotFoundException extends Exception {
    private final long id;

    public RegionNotFoundException(long id) {
        this.id = id;
    }

    public RegionNotFoundException(String message, long id) {
        super(message);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
