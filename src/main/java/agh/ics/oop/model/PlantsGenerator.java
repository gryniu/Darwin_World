package agh.ics.oop.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class PlantsGenerator implements Iterable<Vector2d>{
    private Deque<Vector2d> normalArea = new ArrayDeque<>();
    private Deque<Vector2d> jungleArea = new ArrayDeque<>();
    private final int jungleStarts;
    private final int jungleEnds;
    private int returnedCounter = 0;

    public PlantsGenerator(int width, int height){
        jungleStarts = (2*height/5);
        jungleEnds = (3*height/5);

        for (int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(j >= jungleStarts && j < jungleEnds)
                    jungleArea.addLast(new Vector2d(i, j));
                else
                    normalArea.addLast(new Vector2d(i, j));
            }
        }
    }


    @Override
    public Iterator iterator() {
        return new PlantsGeneratorIterator();
    }

    @Override
    public void forEach(Consumer action) {
        Iterable.super.forEach(action);
    }

    public void returnPlant(Plant plant){
        int x = plant.position().getX();
        int y = plant.position().getY();

        if(y >= jungleStarts && y < jungleEnds){
            jungleArea.addLast(new Vector2d(x, y));
        }else{
            normalArea.addLast(new Vector2d(x, y));
        }
        returnedCounter++;
    }

    public void reShuffle(){
        if (returnedCounter == 0) return;

        returnedCounter = 0;

        var list = new ArrayList<>(jungleArea);
        Collections.shuffle(list);
        jungleArea = new ArrayDeque<>(list);

        list = new ArrayList<>(jungleArea);
        Collections.shuffle(list);
        normalArea = new ArrayDeque<>(list);
    }

    public class PlantsGeneratorIterator implements Iterator<Vector2d>{
        @Override
        public boolean hasNext() {
            return !jungleArea.isEmpty() || !normalArea.isEmpty();
        }

        @Override
        public Vector2d next() {
            if(normalArea.isEmpty() || (!jungleArea.isEmpty() && ThreadLocalRandom.current().nextInt(10)<8)){
                return jungleArea.removeFirst();
            }else{
                return normalArea.removeFirst();
            }
        }
    }
}
