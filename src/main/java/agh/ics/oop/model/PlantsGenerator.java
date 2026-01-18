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
        shuffle();
    }


    @Override
    public Iterator<Vector2d> iterator() {
        return new PlantsGeneratorIterator();
    }

    public void returnPlant(Vector2d position){
        int x = position.getX();
        int y = position.getY();

        if(y >= jungleStarts && y < jungleEnds){
            jungleArea.addLast(new Vector2d(x, y));
        }else{
            normalArea.addLast(new Vector2d(x, y));
        }
        returnedCounter++;
    }
    private void shuffle(){
        returnedCounter = 0;

        var list = new ArrayList<>(jungleArea);
        Collections.shuffle(list);
        jungleArea = new ArrayDeque<>(list);

        list = new ArrayList<>(normalArea);
        Collections.shuffle(list);
        normalArea = new ArrayDeque<>(list);
    }

    public void reShuffle(){
        if (returnedCounter == 0) return;
        shuffle();
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
