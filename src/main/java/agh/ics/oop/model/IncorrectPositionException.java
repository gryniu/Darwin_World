package agh.ics.oop.model;

public class IncorrectPositionException extends RuntimeException {

    public IncorrectPositionException(String message) {
        super(message);
    }

    public IncorrectPositionException(Vector2d position, Boundary bounds) {
        super(String.format("Position %s is outside the map boundaries %s",
                position, bounds));
    }

    public IncorrectPositionException(Vector2d position) {
        super(String.format("Position %s is not valid for this operation", position));
    }
}