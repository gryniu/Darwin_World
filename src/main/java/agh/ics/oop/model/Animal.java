package agh.ics.oop.model;

import java.util.*;

public class Animal extends AbstractAnimal{
    private final int dayOfBirth;
    private int numOfKids = 0;
    private int plantConsumedCounter = 0;
    private boolean isAlive;
    private int deathDay;
    private final List<Animal> listOfKids = new ArrayList<>();
    private int numOfLivedDays;

    private final Iterator<Integer> genIterator;
    private final EnergyOptions energyOptions;
    private final AnimalOptions animalOptions;

    public Animal(Vector2d position, AnimalOptions animalOptions, int energyStart, int dayOfBirth) {
        this(position,animalOptions,
                new AnimalData(new Gen(animalOptions.lenOfGen()),
                        energyStart, dayOfBirth));
    }
    public Animal(Vector2d position, AnimalOptions animalOptions, AnimalData animalData) {
        this.dayOfBirth = animalData.dayOfBirth();
        this.orientation = MapDirection.getRandomDirection();
        this.position = position;
        this.gen = animalData.gen();
        this.genIterator = gen.iterator();
        this.animalOptions = animalOptions;
        this.energyOptions = animalOptions.energyOptions();
        this.energy = animalData.energyStart();
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
        kidGen.randomize(animalOptions.minMutationNum(), animalOptions.maxMutationNum());

        increaseNumOfKids();
        partner.increaseNumOfKids();
        return Optional.of(new AnimalData(kidGen, kidStartingEnergy, day));
    }

    public void decreaseDailyEnergy(double energyDecreaseMultiplier){
        energy = Math.max(0,energy - (int)(energyOptions.dailyEnergyLoss()*energyDecreaseMultiplier));
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


    public int getNumOfLivedDays() { return numOfLivedDays; }

    public void increaseNumOfLivedDays() { numOfLivedDays++; }

    public void setDeathDay(int deathDay) {this.deathDay = deathDay;}

    public int getDeathDay() {return deathDay;}

    public void addKid(Animal kid) { listOfKids.add(kid); }

    public int getNumOfDescendants() {
        int res = numOfKids;
        for (Animal kid: listOfKids)
            res += kid.getNumOfDescendants();
        return res;
    }
}
