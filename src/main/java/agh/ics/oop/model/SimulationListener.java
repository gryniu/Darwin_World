package agh.ics.oop.model;

@FunctionalInterface
public interface SimulationListener {
    void change(WorldMap worldMap, MapStats mapStats, int day, boolean isLive);
}
