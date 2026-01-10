package agh.ics.oop.model;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

public class Animal implements WorldElement{
    private Vector2d position;
    private MapDirection orientation;
    private double energy;
    private final Gen gen;
    private final int mutationNum;

    private final int lenOfGen;
    private final Iterator<Integer> genIterator;
    private final EnergyOptions energyOptions;
    private final AnimalOptions animalOptions;

    public Animal(Vector2d position, AnimalOptions animalOptions, double energyStart) {
        this(position,animalOptions, energyStart, new Gen(animalOptions.lenOfGen()));
    }
    public Animal(Vector2d position, AnimalOptions animalOptions, double energyStart, Gen gen) {
        this.orientation = MapDirection.getRandomDirection();
        this.position = position;
        this.gen = gen;
        this.genIterator = gen.iterator();
        this.mutationNum = animalOptions.mutationNum();
        this.lenOfGen = animalOptions.lenOfGen();
        this.animalOptions = animalOptions;
        this.energyOptions = animalOptions.energyOptions();
        this.energy = energyStart;
    }


    public double giveEnergyToKid(){
        energy -= energyOptions.energyToKid();
        return energyOptions.energyToKid();
    }

    public Optional<AnimalData> sex(Animal partner){
        if (!(this.isFeed() && partner.isFeed())){
            return Optional.empty();
        }
        double kidStartingEnergy = giveEnergyToKid() + partner.giveEnergyToKid();
        Gen kidGen = mixGens(partner);
        for (int i = 0; i<mutationNum; i++){
            kidGen.setRandomElementInGenList();
        }
        return Optional.of(new AnimalData(kidGen, kidStartingEnergy));
    }

    private Gen mixGens(Animal partner){ // Animal must have same lenght of Gen
        boolean isSideLeft = ThreadLocalRandom.current().nextBoolean();
        Animal strongestAnimal = getEnergy()>partner.getEnergy() ? this : partner;
        Animal weekerAnimal = getEnergy()>partner.getEnergy() ? partner : this;
        List<Integer> kidGenList = new ArrayList<>();
        int kidGenLen = this.getGen().getLenOfGen();

        if (isSideLeft){
            int participationIdx = (int)(strongestAnimal.getEnergy()/(strongestAnimal.getEnergy()+weekerAnimal.getEnergy())) + 1;
            kidGenList.addAll(strongestAnimal.getGen().getGenList().
                    subList(0,participationIdx));
            kidGenList.addAll(weekerAnimal.getGen().getGenList()
                    .subList(participationIdx, kidGenLen));
        }
        else {
            int participationIdx = (int)(weekerAnimal.getEnergy()/(strongestAnimal.getEnergy()+weekerAnimal.getEnergy())) + 1;
            kidGenList.addAll(weekerAnimal.getGen().getGenList()
                    .subList(0,participationIdx));
            kidGenList.addAll(strongestAnimal.getGen().getGenList().
                    subList(participationIdx, kidGenLen));
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

    public double getEnergy() {
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

    public void rotate(){
        orientation = orientation.next(genIterator.next());
    }

    public void rotate180(){
        orientation = orientation.opposite();
    }

}
