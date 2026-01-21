package agh.ics.oop.model.simulation;

import agh.ics.oop.model.filesystem.HistoryFileHandler;
import agh.ics.oop.model.map.FakeWorldMap;
import agh.ics.oop.model.map.MapStats;
import agh.ics.oop.model.map.RealWorldMap;
import agh.ics.oop.model.map.WorldMap;

import java.util.ArrayList;
import java.util.List;

public class SimulationHistory {
    private final List<MapStats> simulationStats = new ArrayList<>();
    private final List<SimulationListener> mapChangeListeners = new ArrayList<>();
    private final RealWorldMap worldMap;
    private int maxDay;

    public SimulationHistory(RealWorldMap worldMap){
        this.worldMap = worldMap;
    }

    public void goBackTo(int day) {
        if(day < 0 || day > maxDay) return;
        FakeWorldMap fakeWorldMap = new FakeWorldMap(worldMap.getId(), day, worldMap.getWidth(), worldMap.getHeight());
        notifyListeners(day, fakeWorldMap);
    }

    private void notifyListeners(int day, FakeWorldMap fakeWorldMap) {
        for (SimulationListener listener : mapChangeListeners) {
            listener.change(fakeWorldMap, simulationStats.get(day), day, false);
        }
    }

    public void addSimulationChangeListener(SimulationListener listener) {
        mapChangeListeners.add(listener);
    }

    public void update(WorldMap worldMap, MapStats mapStats, int day, boolean isLive){
        maxDay = day;
        if (isLive && worldMap instanceof RealWorldMap realWorldMap) {
            HistoryFileHandler.writeToFile("/%s-%d-animals.txt".formatted(realWorldMap.getId(), day), worldMap.getAllAnimals());
            HistoryFileHandler.writeToFile("/%s-%d-plants.txt".formatted(realWorldMap.getId(), day), worldMap.getPlants());
        }
        simulationStats.add(mapStats);
    }
}
