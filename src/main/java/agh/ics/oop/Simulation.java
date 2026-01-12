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
        int animalsCount;
        while (true){
            worldMap.removeDeadAnimals();
            animalsCount = 0;
            for (Animal animal: worldMap.getAllAnimals()){
                animal.decreaseDailyEnergy();
                animal.rotate();
                worldMap.move(animal);
                animalsCount++;
            }
            if (animalsCount==0) {
                break;
            }

            worldMap.eatAllPossiblePlants();
            worldMap.reproducePopulation(day);
            worldMap.createNewPlants();

            day++;
            System.out.println("Day " + day + " completed"); // todo: do wyejabania sout
            System.out.println(worldMap);
        }
    }

}
