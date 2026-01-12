package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.List;

public class Simulation implements Runnable{
    private final RealWorldMap worldMap;
    private int day = 0;

    public Simulation(MapOptions mapOptions, AnimalOptions defaultAnimalOptions) {
        this.worldMap = new RealWorldMap(mapOptions, defaultAnimalOptions);
        worldMap.createAnimalsOnRandomPositions(day);
    }

    @Override
    public void run() {
        while (nextDay()){
            System.out.println("Day " + day + " completed"); // todo: do wyejabania sout
            System.out.println(worldMap);
        };
    }

    private boolean nextDay(){
        if (worldMap.getAnimalsCount() == 0) return false;

        worldMap.removeDeadAnimals();
        worldMap.moveAllAnimals();
        worldMap.eatAllPossiblePlants();
        worldMap.reproducePopulation(day);
        worldMap.createNewPlants();

        day++;
        return true;
    }
}
