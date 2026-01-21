package agh.ics.oop.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RealWorldMap extends AbstractWorldMap<Animal> {
    protected final UUID id;

    private final PlantsGenerator plantsGenerator;
    private final Iterator<Vector2d> plantsGeneratorIterator;

    private final AnimalOptions defaultAnimalOptions;
    private final MapOptions mapOptions;

    protected int plantNumEveryDay;
    protected double energyFromPlantMultiplier = 1;
    protected double energyDecreaseMultiplier = 1;
    protected double plantNumMultiplier = 1;

    private WorldMapListener<Plant> plantGrowListener;
    private WorldMapListener<Gen> genCountListener;
    private WorldMapListener<Animal> deadAnimalListener;

    public RealWorldMap(MapOptions mapOptions, AnimalOptions defaultAnimalOptions){
        super(mapOptions.mapWidth(), mapOptions.mapHeight());

        day = 0;
        id = UUID.randomUUID();
        plantNumEveryDay = mapOptions.plantNumEveryDay();
        this.defaultAnimalOptions = defaultAnimalOptions;
        this.mapOptions = mapOptions;
        plantsGenerator = new PlantsGenerator(width, height);
        plantsGeneratorIterator = plantsGenerator.iterator();

        createAnimalsOnRandomPositions();
        createPlants(mapOptions.startingNumOfPlants());
    }

    public void place(Animal animal) {
        animals.addAnimal(animal);
        if(genCountListener != null)
            genCountListener.change(animal.getGen(), 1);
    }

    @Override
    public Optional<List<Animal>> getAnimals(Vector2d position){
        return animals.getFrom(position);
    }

    public Optional<List<Animal>> getAnimalsOrdered(Vector2d position) {
        return getAnimals(position)
                .map(items -> items
                        .stream()
                        .sorted(Comparator
                                .comparingInt(Animal::getEnergy).reversed()
                                .thenComparingInt(Animal::getDayOfBirth)
                                 .thenComparingInt(Animal::getNumOfKids).reversed()
                                .thenComparingDouble(animal -> ThreadLocalRandom.current().nextDouble())
                        )
                        .toList());
    }

    @Override
    public List<Animal> getAllAnimals() {
        return animals.getAll();
    }

    public List<Animal> getAllAnimalsOrdered() {
        return getAllAnimals()
                .stream()
                .sorted(Comparator
                            .comparingInt(Animal::getEnergy).reversed()
                            .thenComparing(Animal::getDayOfBirth)
                            .thenComparing(Animal::getNumOfKids).reversed()
                            .thenComparingDouble(animal -> ThreadLocalRandom.current().nextDouble())
                    )
                    .toList();
    }

    public UUID getId(){
        return id;
    }

    public void move(Animal animal) {
        Vector2d oldPosition = animal.position();
        animals.removeAnimal(animal);
        Vector2d newPosition = oldPosition.add(animal.getOrientation().toUnitVector());

        if (newPosition.y() < 0 || newPosition.y() >= height){
            animal.rotate180();
            newPosition = oldPosition.add(animal.getOrientation().toUnitVector());
        }
        if (newPosition.x() < 0 || newPosition.x() >= width){
            newPosition = new Vector2d(Math.floorMod(newPosition.x(), width), newPosition.y());
        }

        animal.setPosition(newPosition);
        animals.addAnimal(animal);
    }

    public void createNewPlants(){
        plantsGenerator.reShuffle();
        createPlants((int)(plantNumEveryDay*plantNumMultiplier));
    }

    private void createPlants(int n){
        int created = 0;
        while (created < n && plantsGeneratorIterator.hasNext()) {
            Vector2d position = plantsGeneratorIterator.next();
            Plant plant = new Plant(position);
            plants.put(position, plant);
            if(plantGrowListener != null)
                plantGrowListener.change(plant, 1);
            created++;
        }
    }

    private void eatPlant(Vector2d position) {
        getAnimalsOrdered(position)
                .filter(items -> !items.isEmpty())
                .ifPresent(items -> {
                    items.getFirst().eat(energyFromPlantMultiplier);
                    plants.remove(position);
                    plantsGenerator.returnPlant(position);
                });
    }

    public void eatAllPossiblePlants(){
        for(var plant: getPlants()){
            eatPlant(plant.position());
        }
    }

    public void removeDeadAnimals(){
        for (var animal: animals.getAll()){
            if(animal.getEnergy()<=0){
                animal.die(day);
                animals.removeAnimal(animal);
                if(genCountListener != null)
                    genCountListener.change(animal.getGen(), -1);
                if(deadAnimalListener != null)
                    deadAnimalListener.change(animal, 1);
            }
        }
    }

    public void reproducePopulation(){
        List<Animal> newborns = new ArrayList<>();

        animals.getPositions().forEach(position ->
                getAnimalsOrdered(position)
                        .filter(items->items.size()>=2)
                        .ifPresent(items -> {
                            for (int i = 0; i<items.size()-1; i+=2){
                                Animal firstPartner = items.get(i);
                                Animal secondPartner = items.get(i+1);
                                firstPartner.sex(secondPartner, day).ifPresent(kidAnimalData -> {
                                    Animal child = new Animal(
                                            position,
                                            firstPartner.animalOptions(),
                                            kidAnimalData
                                    );
                                    newborns.add(child);
                                    firstPartner.addKid(child);
                                    secondPartner.addKid(child);
                                });
                            }
                        }));

        for (Animal child : newborns) {
            place(child);
        }
    }

    public void moveAllAnimals(){
        for (Animal animal: getAllAnimals()){
            move(animal);
            animal.rotate();
            animal.increaseNumOfLivedDays();
        }
    }

    public void decreaseEnergyAllAnimals(){
        List<Animal> currentAnimals = getAllAnimals();

        for (Animal animal: currentAnimals){
            animal.decreaseDailyEnergy(energyDecreaseMultiplier);
        }
    }

    private void createAnimalsOnRandomPositions(){
        for (int i = 0 ;i< mapOptions.startingNumOfAnimals(); i++)
            place(new Animal(getRandomPosition(), defaultAnimalOptions, mapOptions.energyStart(), 0));
    }

    public int getFreeFieldsCount(){
        Set<Vector2d> takenFields = new HashSet<>(animals.getPositions());
        takenFields.addAll(plants.keySet());
        return width*height - takenFields.size();
    }

    private Vector2d getRandomPosition() {
        int x = ThreadLocalRandom.current().nextInt(
                0,
                width
        );
        int y = ThreadLocalRandom.current().nextInt(
                0,
                height
        );
        return new Vector2d(x, y);
    }

    public void setPlantGrowListener(WorldMapListener<Plant> plantGrowListener) {
        this.plantGrowListener = plantGrowListener;
    }

    public void setGenCountListener(WorldMapListener<Gen> genCountListener) {
        this.genCountListener = genCountListener;
    }

    public void setDeadAnimalListener(WorldMapListener<Animal> deadAnimalListener) {
        this.deadAnimalListener = deadAnimalListener;
    }

    public void handleEndOfADay() {
        day++;
    }
}
