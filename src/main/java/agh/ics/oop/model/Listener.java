package agh.ics.oop.model;

@FunctionalInterface
public interface Listener {
    void change(AbstractWorldMap worldMap, String message);
}
