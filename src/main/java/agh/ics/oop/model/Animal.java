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

    public Animal(Vector2d position, AnimalOptions animalOptions) {
        this(position,new Gen(animalOptions.lenOfGen()),animalOptions);
    }
    public Animal(Vector2d position, Gen gen, AnimalOptions animalOptions) {
        this.orientation = MapDirection.getRandomDirection();
        this.position = position;
        this.gen = gen;
        this.genIterator = gen.iterator();
        this.mutationNum = animalOptions.mutationNum();
        this.lenOfGen = animalOptions.lenOfGen();
        this.animalOptions = animalOptions;
        this.energyOptions = animalOptions.energyOptions();
        this.energy = this.energyOptions.energyStart();
    }


    public double giveEnergyToKid(){
        energy -= energyOptions.energyToKid();
        return energyOptions.energyToKid();
    }

    public Optional<Animal> sex(Animal partner){
        if (!(this.isFeed() && partner.isFeed())){
            return Optional.empty();
        }
        boolean isSideLeft = ThreadLocalRandom.current().nextBoolean();
        Animal strongestAnimal = getEnergy()>partner.getEnergy() ? this : partner;
        Animal weekerAnimal = getEnergy()>partner.getEnergy() ? partner : this;
        double kidStartingEnergy = giveEnergyToKid() + partner.giveEnergyToKid();
        List<Integer> kidGenList = new ArrayList<>();

        if (isSideLeft){
            int participationIdx = (int)(strongestAnimal.getEnergy()/(strongestAnimal.getEnergy()+weekerAnimal.getEnergy())) + 1;
            kidGenList.addAll(strongestAnimal.getGen().getGenList().
                    subList(0,participationIdx));
            kidGenList.addAll(weekerAnimal.getGen().getGenList()
                    .subList(participationIdx,lenOfGen));
        }
        else {
            int participationIdx = (int)(weekerAnimal.getEnergy()/(strongestAnimal.getEnergy()+weekerAnimal.getEnergy())) + 1;
            kidGenList.addAll(weekerAnimal.getGen().getGenList()
                    .subList(0,participationIdx));
            kidGenList.addAll(strongestAnimal.getGen().getGenList().
                    subList(participationIdx,lenOfGen));
        }
        Gen kidGen = new Gen(kidGenList);
        for (int i = 0; i<mutationNum; i++){
            kidGen.setRandomElementInGenList();
        }

        EnergyOptions kidEnergyOptions = energyOptions.withEnergyStart(kidStartingEnergy);
        AnimalOptions kidAnimalOptions = animalOptions.withEnergyOptions(kidEnergyOptions);

        return Optional.of(new Animal(position,kidGen,kidAnimalOptions));
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
