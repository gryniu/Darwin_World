package agh.ics.oop.model.map;

public interface WorldMapListener<T> {
    void change(T field, int count);
}
