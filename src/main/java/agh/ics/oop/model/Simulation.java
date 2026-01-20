package agh.ics.oop.model;

import java.util.*;

public class Simulation implements Runnable{
    //symulacja
    private final RealWorldMap worldMap;
    private final int simulationSpeed;

    //logika asynchroniczna
    private Thread simulationThread;
    private final List<SimulationListener> simulationChangeListeners = new ArrayList<>();
    private boolean paused = true;
    private boolean running = true;
    private final Object lock = new Object();

    //cofanie
    private int rewindedDays = 0;
    private WorldStatistics worldStatistics;

    public Simulation(RealWorldMap worldMap, int simulationSpeed) {
        this.worldMap = worldMap;
        this.simulationSpeed = simulationSpeed;
        this.worldStatistics = new WorldStatistics(worldMap);
    }

    @Override
    public void run() {
        System.out.println("Symulacja rozpoczęta");
        notifyListeners();

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

                System.out.println("Dzień " + worldMap.getDay() + " zakończony");

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
        worldMap.reproducePopulation();
        worldMap.decreaseEnergyAllAnimals();
        worldMap.createNewPlants();

        notifyListeners();
        return true;
    }

    private void notifyListeners() {
        synchronized (lock) {
            for (SimulationListener listener : simulationChangeListeners) {
                listener.change(worldMap, worldStatistics.getMapStats(), worldMap.getDay(), true);
            }
        }
    }

    public void addSimulationChangeListener(SimulationListener listener) {
        synchronized (lock) {
            simulationChangeListeners.add(listener);
        }
    }

    public void removeSimulationChangeListener(SimulationListener listener) {
        synchronized (lock) {
            boolean removed = simulationChangeListeners.remove(listener);
            if(!removed) throw new RuntimeException("Nie udało się usunąć listenera");
        }
    }

    public void setPausedSimulation(boolean isPaused) {
        synchronized (lock) {
            boolean wasPaused = paused;
            paused = isPaused;

            System.out.println("Zmiana pauzy: " + wasPaused + " -> " + isPaused);

            if (wasPaused && !isPaused) {
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

    public void startSimulation(){
        simulationThread = new Thread(this);
        simulationThread.setDaemon(true);
        simulationThread.start();

        running = true;
        paused = false;
    }

    public void stopSimulation() {
        synchronized (lock) {
            running = false;
            paused = false;
            lock.notifyAll();
        }
        if (simulationThread != null && simulationThread.isAlive()) {
            simulationThread.interrupt();
        }
    }

    public MapStats getMapStats() {return worldStatistics.getMapStats();};
    public int getEnergyPercentile(int percentile) {
        return worldStatistics.getEnergyPercentile(percentile);
    }
    public FieldCategory getFieldCategory(Vector2d position) {
        return worldStatistics.getFieldCategory(position);
    }
}