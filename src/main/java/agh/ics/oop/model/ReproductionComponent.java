package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;

public class ReproductionComponent {
    private int numOfKids = 0;
    private final List<Animal> children = new ArrayList<>();

    public int getNumOfKids() { return numOfKids; }
    public void increaseNumOfKids() { numOfKids++; }
    public void addChild(Animal child) { children.add(child); }
    public int getNumOfDescendants() {
        int res = numOfKids;
        for (Animal c : children) res += c.getNumOfDescendants();
        return res;
    }
}
