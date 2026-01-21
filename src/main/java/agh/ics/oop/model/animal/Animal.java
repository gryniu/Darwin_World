package agh.ics.oop.model.animal;

import agh.ics.oop.model.*;
import agh.ics.oop.model.animal.component.EnergyComponent;
import agh.ics.oop.model.animal.component.LifeComponent;
import agh.ics.oop.model.animal.component.ReproductionComponent;

import java.util.*;

public class Animal extends AbstractAnimal{
    private int plantConsumedCounter = 0;
    // niby mozna by bylo do osobnej klasy dać plant Counter zeby nie lamac srp, ale to przesada

    private final Iterator<Integer> genIterator;

    private final AnimalOptions animalOptions;
    private final EnergyComponent energyComponent;
    private final LifeComponent lifeComponent;
    private final ReproductionComponent reproductionComponent;

    public Animal(Vector2d position, AnimalOptions animalOptions, int energyStart, int dayOfBirth) {
        this(position,animalOptions,
                new AnimalData(new Gen(animalOptions.lenOfGen()),
                        energyStart, dayOfBirth));
    }
    public Animal(Vector2d position, AnimalOptions animalOptions, AnimalData animalData) {
        this.lifeComponent = new LifeComponent(animalData.dayOfBirth());
        this.reproductionComponent = new ReproductionComponent();
        this.energyComponent = new EnergyComponent(animalData.energyStart(), animalOptions.energyOptions());

        this.orientation = MapDirection.getRandomDirection();
        this.position = position;
        this.gen = animalData.gen();
        this.genIterator = gen.iterator();
        this.animalOptions = animalOptions;
    }

    // energy
    public void eat(double energyFromPlantMultiplier){
        plantConsumedCounter++;
        energyComponent.eat(energyFromPlantMultiplier);
    }

    public void decreaseDailyEnergy(double energyDecreaseMultiplier){
        energyComponent.dailyLoss(energyDecreaseMultiplier);
    }

    public EnergyComponent getEnergyComponent() {
        return energyComponent;
    }

    @Override
    public int getEnergy() {
        return energyComponent.getEnergy();
    }

    // life
    public boolean isAlive() { return lifeComponent.isAlive();}

    public int getNumOfLivedDays() { return lifeComponent.getNumOfLivedDays(); }

    public void increaseNumOfLivedDays() { lifeComponent.nextDay(); }

    public void die(int day){ lifeComponent.die(day);}

    public int getDeathDay() { return lifeComponent.getDeathDay(); }

    public int getDayOfBirth() { return lifeComponent.getDayOfBirth();}

    // reproduction
    public Optional<AnimalData> sex(Animal partner, int day){
        if (!energyComponent.canReproduce() && partner.getEnergyComponent().canReproduce())
            return Optional.empty();
        int kidStartingEnergy = energyComponent.giveEnergyToKid() + partner.getEnergyComponent().giveEnergyToKid();

        Gen kidGen = Gen.mixGens(this, partner);
        kidGen.randomize(animalOptions.minMutationNum(), animalOptions.maxMutationNum());

        reproductionComponent.increaseNumOfKids();
        partner.getReproductionComponent().increaseNumOfKids();
        return Optional.of(new AnimalData(kidGen, kidStartingEnergy, day));
    }

    public int getNumOfKids() {
        return reproductionComponent.getNumOfKids();
    }

    public void addKid(Animal child) { reproductionComponent.addChild(child);}

    public int getNumOfDescendants() {
        return reproductionComponent.getNumOfDescendants();
    }

    // position and rotation
    public void setPosition(Vector2d position) {
        this.position = position;
    }


    public void rotate(){
        orientation = orientation.next(genIterator.next());
    }

    public void rotate180(){
        orientation = orientation.opposite();
    }

    // plant counter
    public int getPlantConsumedCounter() { return plantConsumedCounter; }

    // datatostring
    @Override
    public String dataToString() {
        return position.dataToString() + "," + orientation.dataToString() + "," + energyComponent.getEnergy() + "," + gen;
    }

    // inne gettery
    public AnimalOptions animalOptions() {
        return animalOptions;
    }

    public ReproductionComponent getReproductionComponent() {
        return reproductionComponent;
    }


}
