package agh.ics.oop.model;

public interface PlantGrowListener extends WorldMapListener<Vector2d>{
    void change(Vector2d position, int count);
}
