package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Gen implements Iterable<Integer>{
    private List<Integer> genList = new ArrayList<>();;
    private final int lenOfGen;
    private final Random random = new Random();

    public Gen(int lenOfGen) {
        if (lenOfGen<=0){
            throw new IllegalArgumentException("lenOfGen must be > 0");
        }
        this.lenOfGen = lenOfGen;
        for (int i= 0;i<lenOfGen;i++)
            genList.add(random.nextInt(8));
    }


    public Gen(List<Integer> gen) {
        this.lenOfGen = gen.size();
        if (lenOfGen==0){
            throw new IllegalArgumentException("lenOfGen must be > 0");
        }
        this.genList = gen;
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
            return genList.get(currIndex);
        }

    }

    public List<Integer> getGenList() {
        return genList;
    }

    public void setRandomElementInGenList(){
        genList.set(random.nextInt(lenOfGen), random.nextInt(8));
    }
}
