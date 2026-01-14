package agh.ics.oop;

import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable{
    private final AbstractWorldMap worldMap;
    private int day = 0;
    private final int simulationSpeed;
    private final List<Listener> mapChangeListeners = new ArrayList<>();
    private boolean paused = true;  // na starcie zatrzymana
    private boolean running = true;
    private final Object lock = new Object();

    public Simulation(RealWorldMap worldMap, int simulationSpeed) {
        this.worldMap = worldMap;
        this.simulationSpeed = simulationSpeed;
        worldMap.createAnimalsOnRandomPositions(day);
    }

    @Override
    public void run() {
        System.out.println("🚀 Symulacja rozpoczęta");

        try {
            while (isRunning()) {
                waitIfPaused();

                if (!isRunning()) break;

                boolean canContinue = nextDay();
                if (!canContinue) {
                    System.out.println("Brak zwierząt - koniec symulacji");
                    stopSimulation();
                    break;
                }

                System.out.println("Dzień " + day + " zakończony");

                Thread.sleep(simulationSpeed);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Symulacja przerwana");
        }

        System.out.println("Wątek symulacji zakończony");
    }

    private void waitIfPaused() throws InterruptedException {
        synchronized (lock) {
            while (paused && running) {
                lock.wait();
                System.out.println("Wątek obudzony");
            }
        }
    }

    private boolean nextDay() {
        if (worldMap.getAnimalsCount() == 0) {
            return false;
        }

        worldMap.removeDeadAnimals();
        worldMap.moveAllAnimals();
        worldMap.eatAllPossiblePlants();
        worldMap.reproducePopulation(day);
        worldMap.decreaseEnergyAllAnimals();
        worldMap.createNewPlants();

        day++;

        Platform.runLater(() -> {
            notifyListeners("Dzień " + day + " zakończony");
        });

        return true;
    }

    private void notifyListeners(String message) {
        // Użyj kopii listy dla bezpieczeństwa
        List<Listener> listenersCopy;
        synchronized (lock) {
            listenersCopy = new ArrayList<Listener>(mapChangeListeners);
        }

        for (Listener listener : listenersCopy) {
            listener.change(worldMap, message);
        }
    }

    public void addMapChangeListener(Listener listener) {
        synchronized (lock) {
            mapChangeListeners.add(listener);
        }
    }

    public void setPausedSimulation(boolean isPaused) {
        synchronized (lock) {
            boolean wasPaused = this.paused;
            this.paused = isPaused;

            System.out.println("Zmiana pauzy: " + wasPaused + " -> " + isPaused);


            if (wasPaused && !isPaused) {
                lock.notify();
            }
        }
    }

    public boolean isPaused() {
        synchronized (lock) {
            return paused;
        }
    }

    public boolean isRunning() {
        synchronized (lock) {
            return running;
        }
    }

    private void setRunning(boolean running) {
        synchronized (lock) {
            this.running = running;
        }
    }

    public void stopSimulation() {
        synchronized (lock) {
            this.running = false;
            this.paused = false;
            lock.notifyAll();
        }
    }

    public int getCurrentDay() {
        return day;
    }
}