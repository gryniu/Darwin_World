package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Gen implements Iterable<Integer>{
    private List<Integer> genList = new ArrayList<>();;
    private final int lenOfGen;

    public Gen(int lenOfGen) {
        if (lenOfGen<=0){
            throw new IllegalArgumentException("lenOfGen must be > 0");
        }
        this.lenOfGen = lenOfGen;
        for (int i= 0;i<lenOfGen;i++)
            genList.add(ThreadLocalRandom.current().nextInt(8));
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

    private void setRandomElementInGenList(){
        genList.set(ThreadLocalRandom.current().nextInt(lenOfGen), ThreadLocalRandom.current().nextInt(8));
    }

    public void randomize(int mutationNum){
        for (int i = 0; i<mutationNum; i++)
            setRandomElementInGenList();
    }

    public int getLenOfGen() {
        return lenOfGen;
    }

    public static Gen mixGens(Animal firstPartner, Animal secoundPartner) {
        boolean isSideLeft = ThreadLocalRandom.current().nextBoolean();
        Animal strongerAnimal = firstPartner.getEnergy() > secoundPartner.getEnergy() ? firstPartner : secoundPartner;
        Animal weakerAnimal = firstPartner.getEnergy() > secoundPartner.getEnergy() ? secoundPartner : firstPartner;

        List<Integer> kidGenList = new ArrayList<>();
        int kidGenLen = firstPartner.getGen().getLenOfGen();

        double strongerEnergy = strongerAnimal.getEnergy();
        double weakerEnergy = weakerAnimal.getEnergy();
        double totalEnergy = strongerEnergy + weakerEnergy;

        if (totalEnergy == 0) {
            totalEnergy = 1;
            strongerEnergy = 0.5;
            weakerEnergy = 0.5;
        }


        if (isSideLeft) {
            double strongerRatio = strongerEnergy / totalEnergy;
            int splitPoint = (int)(strongerRatio * kidGenLen); // we want to have at least one of each

            splitPoint = Math.max(1, Math.min(splitPoint, kidGenLen - 1));

            kidGenList.addAll(strongerAnimal.getGen().getGenList()
                    .subList(0, splitPoint));

            kidGenList.addAll(weakerAnimal.getGen().getGenList()
                    .subList(splitPoint, kidGenLen));

        } else {
            double weakerRatio = weakerEnergy / totalEnergy;
            int splitPoint = (int)(weakerRatio * kidGenLen); // same

            splitPoint = Math.max(1, Math.min(splitPoint, kidGenLen - 1));

            kidGenList.addAll(weakerAnimal.getGen().getGenList()
                    .subList(0, splitPoint));

            kidGenList.addAll(strongerAnimal.getGen().getGenList()
                    .subList(splitPoint, kidGenLen));
        }

        return new Gen(kidGenList);
    }
}
