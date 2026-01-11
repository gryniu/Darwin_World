package agh.ics.oop.model;

import java.util.concurrent.ThreadLocalRandom;

public record Boundary(Vector2d lowerLeft, Vector2d upperRight) {
    public Vector2d getRandomPosition() {
        int x = ThreadLocalRandom.current().nextInt(
                lowerLeft.getX(),
                upperRight.getX() + 1  // nextInt exclusive na górnej granicy
        );
        int y = ThreadLocalRandom.current().nextInt(
                lowerLeft.getY(),
                upperRight.getY() + 1
        );
        return new Vector2d(x, y);
    }

    public int width() {
        return upperRight.getX() - lowerLeft.getX() + 1;
    }

    public int height() {
        return upperRight.getY() - lowerLeft.getY() + 1;
    }
}
