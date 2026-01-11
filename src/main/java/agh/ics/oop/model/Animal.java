package agh.ics.oop.model;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

public class Animal implements WorldElement{
    private Vector2d position;
    private MapDirection orientation;
    private int energy;
    private final Gen gen;
    private final int mutationNum;
    private final int dayOfBirth;
    private int numOfKids = 0;

    private final int lenOfGen;
    private final Iterator<Integer> genIterator;
    private final EnergyOptions energyOptions;
    private final AnimalOptions animalOptions;

    public Animal(Vector2d position, AnimalOptions animalOptions, int energyStart, int dayOfBirth) {
        this(position,animalOptions, new AnimalData(new Gen(animalOptions.lenOfGen()), energyStart, dayOfBirth));
    }
    public Animal(Vector2d position, AnimalOptions animalOptions, AnimalData animalData) {
        this.dayOfBirth = animalData.dayOfBirth(); //todo: tymczasowe - zmienic facotry, po stworzeniu simuation DONE
        this.orientation = MapDirection.getRandomDirection();
        this.position = position;
        this.gen = animalData.gen();
        this.genIterator = gen.iterator();
        this.mutationNum = animalOptions.mutationNum();
        this.lenOfGen = animalOptions.lenOfGen();
        this.animalOptions = animalOptions;
        this.energyOptions = animalOptions.energyOptions();
        this.energy = animalData.energyStart();
    }


    public int giveEnergyToKid(){
        int energyGiven = Math.min(energy, energyOptions.energyToKid());
        energy -= energyGiven;
        return energyGiven;
    }

    public Optional<AnimalData> sex(Animal partner){
        if (!canReproduce(this,partner))
            return Optional.empty();
        int kidStartingEnergy = giveEnergyToKid() + partner.giveEnergyToKid();

        Gen kidGen = mixGens(partner);
        kidGen.mixGen(mutationNum);

        increaseNumOfKids();
        partner.increaseNumOfKids();

        return Optional.of(new AnimalData(kidGen, kidStartingEnergy, dayOfBirth+1));
    }

    private Gen mixGens(Animal partner) {
        boolean isSideLeft = ThreadLocalRandom.current().nextBoolean();
        Animal strongerAnimal = getEnergy() > partner.getEnergy() ? this : partner;
        Animal weakerAnimal = getEnergy() > partner.getEnergy() ? partner : this;

        List<Integer> kidGenList = new ArrayList<>();
        int kidGenLen = this.getGen().getLenOfGen();

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

    public void decreaseDailyEnergy(){
        energy = Math.max(0,energy - energyOptions.dailyEnergyLoss());
    }

    public Gen getGen() {
        return gen;
    }

    public EnergyOptions getEnergyOptions() {
        return energyOptions;
    }

    public AnimalOptions animalOptions() {
        return animalOptions;
    }

    public int getEnergy() {
        return energy;
    }

    public void eat(){
        energy += energyOptions.energyFromPlant();
    }

    public boolean isFeed(){
        return energy >= energyOptions.energyToReproduce();
    }


    public void setPosition(Vector2d position) {
        this.position = position;
    }

    @Override
    public Vector2d position() {
        return position;
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    @Override
    public String toString() {
        return switch (orientation) {
            case NORTH -> "↑";
            case EAST -> "→";
            case SOUTH -> "↓";
            case WEST -> "←";
            case EAST_NORTH -> "↗";
            case EAST_SOUTH -> "↘";
            case WEST_SOUTH -> "↙";
            case WEST_NORTH -> "↖";
        };
    }

    public boolean isAt(Vector2d position){
        return this.position.equals(position);
    }

    public static boolean canReproduce(Animal firstPartner, Animal secondPartner){
        return firstPartner.isFeed() && secondPartner.isFeed();
    }

    public void rotate(){
        orientation = orientation.next(genIterator.next());
    }

    public void rotate180(){
        orientation = orientation.opposite();
    }

    public int getDayOfBirth() {
        return dayOfBirth;
    }

    public int getNumOfKids() {
        return numOfKids;
    }

    public void increaseNumOfKids(){
        numOfKids++;
    }

    public boolean isDead(){
        return energy<=0;
    }
}
