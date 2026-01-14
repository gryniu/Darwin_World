package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.List;

public class Simulation implements Runnable{
    private final AbstractWorldMap worldMap;
    private int day = 0;

    public Simulation(MapOptions mapOptions, AnimalOptions defaultAnimalOptions, SeasonsOptions seasonsOptions) {
        this.worldMap = new SeasonalWorldMap(mapOptions, defaultAnimalOptions, seasonsOptions);
        worldMap.createAnimalsOnRandomPositions(day);
    }

    @Override
    public void run() {
        while (nextDay()){
            System.out.println("Day " + day + " completed"); // todo: do wyejabania sout
            System.out.println(worldMap);
            if(day==21) break;
        };
    }

    private boolean nextDay(){
        if (worldMap.getAnimalsCount() == 0) return false;

        worldMap.removeDeadAnimals();
        worldMap.moveAllAnimals();
        worldMap.eatAllPossiblePlants();
        worldMap.reproducePopulation(day);
        worldMap.decreaseEnergyAllAnimals();
        worldMap.createNewPlants();

        day++;
        return true;
    }
}
