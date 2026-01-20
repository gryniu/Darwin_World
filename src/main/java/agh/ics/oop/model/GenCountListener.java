package agh.ics.oop.model;

public interface GenCountListener extends WorldMapListener<Gen> {
    void change(Gen gen, int count);

}
