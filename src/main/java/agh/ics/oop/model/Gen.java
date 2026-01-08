package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.Random;

public class Gen implements Iterable<Integer>{
    private final List<Integer> gen = new ArrayList<>();;
    private final int lenOfGen;

    public Gen(int lenOfGen) {
        if (lenOfGen<=0){
            throw new IllegalArgumentException("lenOfGen must be > 0");
        }
        this.lenOfGen = lenOfGen;
        Random random = new Random();
        for (int i= 0;i<lenOfGen;i++)
            gen.add(random.nextInt(8));
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Itr();
    }

    public class Itr implements Iterator<Integer>{
        private int currIndex;

        public Itr() {
            currIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Integer next() {
            currIndex = (currIndex+1) % lenOfGen;
            return gen.get(currIndex);
        }

    }

}
