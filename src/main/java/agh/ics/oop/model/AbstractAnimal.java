package agh.ics.oop.model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.UUID;

public abstract class AbstractAnimal implements WorldElement{
    protected Vector2d position;
    protected MapDirection orientation;
    protected int energy;
    protected Gen gen;
    private final UUID id = UUID.randomUUID();

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
        return position.dataToString() + "," + orientation.dataToString() + "," + energy + "," + gen;
    }

    public int getEnergy(){
        return energy;
    }

    public double getEnergyRatio(int median, int percentile85) {
        if (percentile85 <= median) return 0;

        double k = Math.log(9) / (percentile85 - median);
        return 1.0 / (1.0 + Math.exp(-k * (energy - median)));
    }


    public UUID getId() { return id; }

    public Gen getGen() {
        return gen;
    }

    public Color getEnergyColor(int softCap) {
        if (energy < softCap * .15) return Color.RED;
        if (energy < softCap * .3) return Color.YELLOW;
        if (energy < softCap * .5) return Color.ORANGE;
        if (energy < softCap * .75) return Color.LIMEGREEN;
        return Color.DARKGREEN;
    }
}
