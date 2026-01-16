package agh.ics.oop.model;

public abstract class AbstractAnimal implements WorldElement{
    protected Vector2d position;
    protected MapDirection orientation;
    protected int energy;

    @Override
    public Vector2d position() {
        return position;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return switch (orientation) {
            case NORTH -> "↑";
            case EAST -> "→";
            case SOUTH -> "↓";
            case WEST -> "←";
            case EAST_NORTH -> "↗";
            case EAST_SOUTH -> "↘";
            case WEST_SOUTH -> "↙";
            case WEST_NORTH -> "↖";
        };
    }

    @Override
    public String dataToString() {
        return position.dataToString() + "," + orientation.dataToString() + "," + energy;
    }
}
