package agh.ics.oop.model;

import java.util.Map;

public enum MapDirection {
    // UWAGA, pola muszą być ustawione zgodnie z ruchem wskazówek zegara
    EAST,
    EAST_SOUTH,
    SOUTH,
    WEST_SOUTH,
    WEST,
    WEST_NORTH,
    NORTH,
    EAST_NORTH;

    public MapDirection next(int steps) {
        MapDirection[] direction = MapDirection.values();
        return direction[(this.ordinal() + steps) % direction.length];
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
    public String toString() {
        return switch (this) {
            case EAST -> "Wschod";
            case NORTH -> "Polnoc";
            case WEST -> "Zachod";
            case EAST_SOUTH -> "Wschód - Południe";
            case SOUTH -> "Wschod";
            case WEST_SOUTH -> "Zachód - Południe";
            case WEST_NORTH -> "Zachód - Północ";
            case EAST_NORTH -> "Wschód - Północ";
        };
    }
}