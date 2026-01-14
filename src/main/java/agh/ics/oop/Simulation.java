package agh.ics.oop;

import agh.ics.oop.model.*;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable{
    private final WorldMap worldMap;
    private int day = 0;
    private int simulationSpeed;
    private final List<Listener> mapChangeListeners = new ArrayList<>();
    private boolean paused;

    public Simulation(WorldMap worldMap, int simulationSpeed) {
        this.worldMap = worldMap;
        // todo: dodać wszystkich obserwatorów dla mapy
        // this.worldMap.addSubscriber();
        this.simulationSpeed = simulationSpeed;
        this.paused = true;
        worldMap.createAnimalsOnRandomPositions(day);
    }

    @Override
    public void run() {
        while (paused && nextDay()){
            System.out.println("Day " + day + " completed"); // todo: do wywalenia sout, zamienic na listenerow
            System.out.println(worldMap);
            try {
                Thread.sleep(simulationSpeed); // kontrola predkosci
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
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
        notifyListeners(worldMap+ "Day " + day + " finished");

        return true;
    }

    private void notifyListeners(String s) {
        for (Listener listener : mapChangeListeners) {
            listener.change(worldMap, s);
        }
    }

    public void addMapChangeListener(Listener listener) {
        this.mapChangeListeners.add(listener);
    }

    public void pauseSimulation(){
        paused = false;
    }

}
