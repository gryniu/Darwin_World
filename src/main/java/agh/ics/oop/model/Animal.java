package agh.ics.oop.model;

public class Animal implements WorldElement {
    public Vector2d position(){
        return new Vector2d(0, 0);
    }
        public MapDirection getOrientation(){
        return MapDirection.EAST;
    }
    public void setPosition(Vector2d vector2d){

    }
}
