package agh.ics.oop.model;

import java.util.Objects;

public class Vector2d {
    private final int x;
    private final int y;

    public Vector2d(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){return x;}
    public int getY(){return y;}

    @Override
    public String toString() {
        return "(" +
                + x +
                ", " + y +
                ')';
    }


    public boolean precedes(Vector2d other){
        return x <= other.x && y <= other.y;
    }

    public boolean follows(Vector2d other){
        return x >= other.x && y >= other.y;
    }

    public Vector2d add(Vector2d other){
        return new Vector2d(x+other.x, y+other.y);
    }

    public Vector2d subtract(Vector2d other){
        return new Vector2d(x-other.x, y-other.y);
    }

    public Vector2d upperRight(Vector2d other){
        return new Vector2d(Math.max(x,other.x), Math.max(y,other.y));
    }

    public Vector2d lowerLeft(Vector2d other){
        return new Vector2d(Math.min(x,other.x), Math.min(y,other.y));
    }

    public Vector2d opposite(){
        return new Vector2d(-x, -y);
    }

    public static int getDistance(Vector2d v1, Vector2d v2){
        return Math.max(Math.abs(v1.getX() - v2.getX()), Math.abs(v1.getY()- v2.getY()));
    }

    @Override
    public boolean equals(Object other){
        if (other == this) return true;
        if (other == null || this.getClass()!=other.getClass()) return false;
        Vector2d otherVector2d = (Vector2d) other;
        return x == otherVector2d.x && y == otherVector2d.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
