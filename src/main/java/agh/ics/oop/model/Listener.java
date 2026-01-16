package agh.ics.oop.model;

@FunctionalInterface
public interface Listener {
    void change(WorldMap worldMap, String message);
}
