package agh.ics.oop;

import agh.ics.oop.model.*;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;

import java.util.ArrayList;
import java.util.List;

public class Simulation implements Runnable{
    //symulacja
    private final AbstractWorldMap worldMap;
    private int day = 0;
    private final int simulationSpeed;

    //logika asynchroniczna
    private final List<Listener> mapChangeListeners = new ArrayList<>();
    private boolean paused = true;
    private boolean running = true;
    private final Object lock = new Object();

    //cofanie
    private boolean rewinded = false;
    private int rewindedDays = 0;
    private FakeWorldMap fakeWorldMap = null;

    public Simulation(AbstractWorldMap worldMap, int simulationSpeed) {
        this.worldMap = worldMap;
        this.simulationSpeed = simulationSpeed;
    }

    @Override
    public void run() {
        System.out.println("Symulacja rozpoczęta");

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

        worldMap.removeDeadAnimals(day);
        worldMap.moveAllAnimals();
        worldMap.eatAllPossiblePlants();
        worldMap.reproducePopulation(day);
        worldMap.decreaseEnergyAllAnimals();
        worldMap.createNewPlants();

        day++;

        notifyListeners(String.valueOf(day));

        return true;
    }

    private void notifyListeners(String message) {
        synchronized (lock) {
            for (Listener listener : mapChangeListeners) {
                listener.change(worldMap, message);
            }
        }
    }

    private void notifyListeners(String message, FakeWorldMap otherWorldMap) {
        synchronized (lock) {
            for (Listener listener : mapChangeListeners) {
                listener.change(otherWorldMap, message);
            }
        }
    }

    public void addMapChangeListener(Listener listener) {
        synchronized (lock) {
            mapChangeListeners.add(listener);
        }
    }

    public void setPausedSimulation(boolean isPaused) {
        synchronized (lock) {
            boolean wasPaused = paused;
            paused = isPaused;

            System.out.println("Zmiana pauzy: " + wasPaused + " -> " + isPaused);


            if (wasPaused && !isPaused) {
                rewinded = false;
                rewindedDays = 0;
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


    public void stopSimulation() {
        synchronized (lock) {
            this.running = false;
            this.paused = false;
            lock.notifyAll();
            HistoryFileHandler.deleteHistory(worldMap.getId());
        }
    }

    public int getCurrentDay() {
        return day;
    }

    public void rewind(boolean back) {
        if(!isPaused()) throw new RuntimeException("Can't rewind on play!");
        rewinded = true;
        if(back)
            rewindedDays = Math.min(day-1, rewindedDays + 1);
        else
            rewindedDays = Math.max(0, rewindedDays - 1);

        fakeWorldMap = new FakeWorldMap(worldMap.getId(), day - rewindedDays,  worldMap.getWidth(), worldMap.getHeight());
        notifyListeners(String.valueOf(day - rewindedDays), fakeWorldMap);
    }
}