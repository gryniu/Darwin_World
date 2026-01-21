package agh.ics.oop.model.animal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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

    public Gen(String genString){
        this.lenOfGen = genString.length();
        if (lenOfGen==0){
            throw new IllegalArgumentException("lenOfGen must be > 0");
        }
        this.genList = genString.chars()
                .mapToObj(c -> Character.getNumericValue((char) c))
                .collect(Collectors.toList());
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Itr();
    }

    public class Itr implements Iterator<Integer>{
        private int currIndex;

        public Itr() {
            currIndex = ThreadLocalRandom.current().nextInt(-1,lenOfGen-2);
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

    public void randomize(int minMutationNum, int maxMutationNum){
        int mutationNum = ThreadLocalRandom.current().nextInt(minMutationNum, maxMutationNum + 1);
        for (int i = 0; i<mutationNum; i++)
            setRandomElementInGenList();
    }

    public int getLenOfGen() {
        return lenOfGen;
    }

    public static Gen mixGens(Animal firstPartner, Animal secondPartner) {
        boolean isSideLeft = ThreadLocalRandom.current().nextBoolean();
        Animal strongerAnimal = firstPartner.getEnergy() > secondPartner.getEnergy() ? firstPartner : secondPartner;
        Animal weakerAnimal = firstPartner.getEnergy() > secondPartner.getEnergy() ? secondPartner : firstPartner;

        List<Integer> kidGenList = new ArrayList<>();
        int kidGenLen = firstPartner.getGen().getLenOfGen();

        double strongerEnergy = strongerAnimal.getEnergy();
        double weakerEnergy = weakerAnimal.getEnergy();
        double totalEnergy = strongerEnergy + weakerEnergy;


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

    @Override
    public String toString(){
        return genList.stream().map(Object::toString).collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Gen integers = (Gen) o;
        return lenOfGen == integers.lenOfGen && Objects.equals(genList, integers.genList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genList, lenOfGen);
    }


}
