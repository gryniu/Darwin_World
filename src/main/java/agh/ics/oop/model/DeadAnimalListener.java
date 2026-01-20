package agh.ics.oop.model;

public interface DeadAnimalListener extends WorldMapListener<Animal>{
    void change(Animal field, int count);
}
