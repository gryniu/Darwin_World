package agh.ics.oop.model;

public class Animal implements WorldElement {
    public Animal(Vector2d vector2d, AnimalOptions animalOptions){}

    public Vector2d position(){
        return new Vector2d(0, 0);
    }
        public MapDirection getOrientation(){
        return MapDirection.EAST;
    }
    public void setPosition(Vector2d vector2d){

    }

    public void rotate180(){

    }

    public double getEnergy(){
        return 0;
    }
}
