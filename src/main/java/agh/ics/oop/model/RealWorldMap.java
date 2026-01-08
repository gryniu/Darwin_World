package agh.ics.oop.model;

import java.util.HashMap;
import java.util.Map;

public class RealWorldMap extends AbstractWorldMap{
    private final Map<Vector2d, Grass> grasses = new HashMap<>();

    public RealWorldMap(){

    }


    @Override
    public Boundary getCurrentBounds() {
        Vector2d lowerLeft = new Vector2d(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2d upperRight = new Vector2d(Integer.MIN_VALUE, Integer.MIN_VALUE);

        for(Vector2d key: animals.getPositions()){
            upperRight = upperRight.upperRight(key);
            lowerLeft = lowerLeft.lowerLeft(key);
        }
        for(Vector2d key: grasses.keySet()){
            upperRight = upperRight.upperRight(key);
            lowerLeft = lowerLeft.lowerLeft(key);
        }
        return new Boundary(lowerLeft, upperRight);
    }
}
