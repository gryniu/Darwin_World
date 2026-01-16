package agh.ics.oop.model;

import javafx.scene.paint.Color;

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

    public int getEnergy(){
        return energy;
    }

    public double getEnergyRatio(int softCap) {
        if (softCap <= 0) return 0;

        double k = 6.0 / softCap; // stromość
        return 1.0 / (1.0 + Math.exp(-k * (energy - softCap / 2.0)));
    }

    public Color getEnergyColor(int softCap) {
        if (energy < softCap * 0.15) return Color.RED;
        if (energy < softCap * 0.5) return Color.YELLOW;
        if (energy < softCap * 0.75) return Color.LIMEGREEN;
        return Color.DARKGREEN;
    }
}
