package agh.ics.oop.model;

public interface WorldMapListener<T> {
    void change(T field, int count);
}
