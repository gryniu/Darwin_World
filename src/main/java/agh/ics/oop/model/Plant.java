package agh.ics.oop.model;

import java.util.Objects;

public record Plant(Vector2d position) implements WorldElement {

    @Override
    public String toString() {
        return "*";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Plant grass = (Plant) o;
        return Objects.equals(position, grass.position);
    }
}
