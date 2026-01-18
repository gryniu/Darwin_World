package agh.ics.oop.model;

public record Vector2d(int x, int y) {

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public Vector2d add(Vector2d other) {
        return new Vector2d(x + other.x, y + other.y);
    }

    public static int getDistance(Vector2d v1, Vector2d v2) {
        return Math.max(Math.abs(v1.x() - v2.x()), Math.abs(v1.y() - v2.y()));
    }

    public String dataToString() {
        return x + ";" + y;
    }
}
