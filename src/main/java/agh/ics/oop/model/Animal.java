package agh.ics.oop.model;

import java.util.*;

public class Animal implements WorldElement{
    private Vector2d position;
    private MapDirection orientation;
    private int energy;
    private final Gen gen;
    private final int dayOfBirth;
    private int numOfKids = 0;

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
        this.animalOptions = animalOptions;
        this.energyOptions = animalOptions.energyOptions();
        this.energy = animalData.energyStart();
    }

    Animal(Gen gen, Vector2d position, MapDirection orientation, int energy, int dayOfBirth, int numOfKids, AnimalOptions animalOptions){
        this.gen = gen;
        this.genIterator = gen.iterator();
        this.dayOfBirth = dayOfBirth;
        this.orientation = orientation;
        this.position = position;
        this.energy = energy;
        this.numOfKids = numOfKids;
        this.animalOptions = animalOptions;
        this.energyOptions = animalOptions.energyOptions();
    }


    public int giveEnergyToKid(){
        int energyGiven = Math.min(energy, energyOptions.energyToKid());
        energy -= energyGiven;
        return energyGiven;
    }

    public Optional<AnimalData> sex(Animal partner, int day){
        if (!canReproduce(this,partner))
            return Optional.empty();
        int kidStartingEnergy = giveEnergyToKid() + partner.giveEnergyToKid();

        Gen kidGen = Gen.mixGens(this, partner);
        kidGen.randomize(animalOptions.mutationNum());

        increaseNumOfKids();
        partner.increaseNumOfKids();
        return Optional.of(new AnimalData(kidGen, kidStartingEnergy, day));
    }

    public void decreaseDailyEnergy(double energyDecreaseMultiplier){
        energy = Math.max(0,energy - (int)(energyOptions.dailyEnergyLoss()*energyDecreaseMultiplier));
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

    public void eat(double energyFromPlantMultiplier){
        energy += (int)(energyOptions.energyFromPlant()*energyFromPlantMultiplier);
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

    public String animalDataToString(){
        return gen + "," + position + "," + orientation + "," + energy + "," + dayOfBirth + "," + numOfKids;
    }
}
