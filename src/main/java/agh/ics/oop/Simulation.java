package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.List;

public class Simulation implements Runnable{
    private final RealWorldMap worldMap;
    private final int startEnergy;
    private int day = 0;

    public Simulation(RealWorldMap worldMap, AnimalOptions defaultAnimalOptions) {
        this.worldMap = worldMap;
        this.startEnergy = worldMap.getMapOptions().energyStart();

        worldMap.createAnimalsOnRandomPositions(worldMap.getMapOptions().startingNumOfAnimals(), day);
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
