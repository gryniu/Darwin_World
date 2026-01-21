package agh.ics.oop.model.simulation;

import agh.ics.oop.model.map.MapStats;
import agh.ics.oop.model.map.WorldMap;

@FunctionalInterface
public interface SimulationListener {
    void change(WorldMap worldMap, MapStats mapStats, int day, boolean isLive);
}
