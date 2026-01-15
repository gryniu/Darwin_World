package agh.ics.oop.model;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public enum MapDirection {
    // UWAGA, pola muszą być ustawione zgodnie z ruchem wskazówek zegara
    NORTH,
    EAST_NORTH,
    EAST,
    EAST_SOUTH,
    SOUTH,
    WEST_SOUTH,
    WEST,
    WEST_NORTH;

    public MapDirection next(int steps) {
        MapDirection[] directions = MapDirection.values();
        return directions[(this.ordinal() + steps) % directions.length];
    }

    public static MapDirection getRandomDirection(){
        MapDirection[] directions = MapDirection.values();
        return directions[
                ThreadLocalRandom.current().nextInt(directions.length)
                ];
    }

    public MapDirection opposite() {
        MapDirection[] directions = MapDirection.values();
        return directions[(this.ordinal() + directions.length/2) % directions.length];
    }

    public Vector2d toUnitVector() {
        return switch(this){
            case EAST -> new Vector2d(1, 0);
            case NORTH -> new Vector2d(0, 1);
            case WEST -> new Vector2d(-1, 0);
            case EAST_SOUTH -> new Vector2d(1, -1);
            case SOUTH -> new Vector2d(0, -1);
            case WEST_SOUTH -> new Vector2d(-1, -1);
            case WEST_NORTH -> new Vector2d(-1, 1);
            case EAST_NORTH -> new Vector2d(1, 1);
        };
    }

    @Override
    public String toString(){
        return switch(this){
            case EAST -> "E";
            case NORTH -> "N";
            case WEST ->"W";
            case EAST_SOUTH -> "ES";
            case SOUTH -> "S";
            case WEST_SOUTH -> "WS";
            case WEST_NORTH -> "WN";
            case EAST_NORTH -> "EN";
        };
    }

    public String dataToString() {
        return String.valueOf(ordinal());
    }


}