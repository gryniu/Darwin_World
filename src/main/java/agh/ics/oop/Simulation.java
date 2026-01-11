package agh.ics.oop.presenter;

import agh.ics.oop.model.*;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable{
    private final RealWorldMap worldMap;
    private final int startEnergy;
    private int day = 0;

    public Simulation(RealWorldMap worldMap, AnimalOptions animalOptions, List<Vector2d> animalsPositions, int startEnergy) {
        this.worldMap = worldMap;
        this.startEnergy = startEnergy;

        for (Vector2d animalPosition : animalsPositions) {
            Animal animalToAdd = new Animal(animalPosition,animalOptions, startEnergy, day);
            this.worldMap.place(animalToAdd);
        }
    }

    @Override
    public void run() {
        worldMap.removeDeadAnimals();
        for (Animal animal: worldMap.getAllAnimals()){
            animal.decreaseDailyEnergy();
            animal.rotate();
            worldMap.move(animal);
        }
        worldMap.eatAllPossiblePlants();
        worldMap.reproducePopulation();
        worldMap.createNewPlants();

        day++;
    }

}
