package agh.ics.oop.model.animal;

import agh.ics.oop.model.*;

import java.util.UUID;

public abstract class AbstractAnimal implements WorldElement, HasEnergy {
    protected Vector2d position;
    protected MapDirection orientation;
    protected Gen gen;
    private final UUID id = UUID.randomUUID();

    @Override
    public Vector2d position() {
        return position;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public UUID getId() { return id; }

    public Gen getGen() {
        return gen;
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

    public Vector2d getPosition() {
        return position;
    }
}
