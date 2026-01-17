package agh.ics.oop.model;

import javafx.scene.paint.Color;

import java.util.*;

public class Animal extends AbstractAnimal{
    private final int dayOfBirth;
    private int numOfKids = 0;
    private int numOfDescendants = 0;
    private int plantConsumedCounter = 0;
    private boolean isAlive;
    private final Animal firstParent;
    private final Animal secondParent;
    private int numOfLivedDays;

    private final Iterator<Integer> genIterator;
    private final EnergyOptions energyOptions;
    private final AnimalOptions animalOptions;

    public Animal(Vector2d position, AnimalOptions animalOptions, int energyStart, int dayOfBirth) {
        this(position,animalOptions,
                new AnimalData(new Gen(animalOptions.lenOfGen()),
                        energyStart, dayOfBirth, null,
                        null));
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
        this.firstParent = animalData.firstParent();
        this.secondParent = animalData.secondParent();
        this.isAlive = true;
        this.numOfLivedDays = 0;
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
        return Optional.of(new AnimalData(kidGen, kidStartingEnergy, day, this, partner));
    }

    public void decreaseDailyEnergy(double energyDecreaseMultiplier){
        energy = Math.max(0,energy - (int)(energyOptions.dailyEnergyLoss()*energyDecreaseMultiplier));
    }

    public EnergyOptions getEnergyOptions() {
        return energyOptions;
    }

    public AnimalOptions animalOptions() {
        return animalOptions;
    }


    public void eat(double energyFromPlantMultiplier){
        plantConsumedCounter++;
        energy += (int)(energyOptions.energyFromPlant()*energyFromPlantMultiplier);
    }

    public boolean isFeed(){
        return energy >= energyOptions.energyToReproduce();
    }


    public void setPosition(Vector2d position) {
        this.position = position;
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

    public int getPlantConsumedCounter() { return plantConsumedCounter; }

    public void setAlive(boolean alive) { isAlive = alive; }

    public boolean isAlive() { return isAlive ;}

    public Animal getFirstParent() { return firstParent; }

    public Animal getSecondParent() { return secondParent; }

    public int getNumOfDescendants() { return numOfDescendants; }

    public int getNumOfLivedDays() { return numOfLivedDays; }

    public void increaseNumOfLivedDays() { numOfLivedDays++; }

    public void increaseDescendantsCounter(){
        numOfDescendants++;
        if (firstParent != null) firstParent.increaseDescendantsCounter();
        if (secondParent != null) secondParent.increaseDescendantsCounter();
    }

}
