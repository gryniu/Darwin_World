package agh.ics.oop.model;

@FunctionalInterface
public interface SimulationListener {
    void change(WorldMap worldMap, int day, boolean isLive);
}
