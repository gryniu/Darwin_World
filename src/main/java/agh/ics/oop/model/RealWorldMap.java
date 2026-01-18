package agh.ics.oop.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RealWorldMap extends AbstractWorldMap<Animal> {
    protected final UUID id;

    private final PlantsGenerator plantsGenerator;
    private final Iterator<Vector2d> plantsGeneratorIterator;

    private long deadAnimalsCounter = 0L;
    private long totalLifespanYears = 0L;

    private final AnimalOptions defaultAnimalOptions;
    private final MapOptions mapOptions;

    protected int plantNumEveryDay;
    protected double energyFromPlantMultiplier = 1;
    protected double energyDecreaseMultiplier = 1;
    protected double plantNumMultiplier = 1;

    public RealWorldMap(MapOptions mapOptions, AnimalOptions defaultAnimalOptions){
        super(mapOptions.mapWidth(), mapOptions.mapHeight());

        id = UUID.randomUUID();
        plantNumEveryDay = mapOptions.plantNumEveryDay();
        this.defaultAnimalOptions = defaultAnimalOptions;
        this.mapOptions = mapOptions;
        this.maxNumOfPlantsOnPosition = (long) (mapOptions.startingNumOfPlants()/4);
        plantsGenerator = new PlantsGenerator(width, height);
        plantsGeneratorIterator = plantsGenerator.iterator();

        createAnimalsOnRandomPositions();

        for(int i = 0; i < mapOptions.startingNumOfPlants(); i++){
            Vector2d position = plantsGeneratorIterator.next();
            plants.put(position, new Plant(position));
        }
    }

    public void place(Animal animal) {
        animals.addAnimal(animal);
        increaseGenotypeCounter(animal);
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

        int created = 0;
        while (created < (int)(plantNumEveryDay*plantNumMultiplier) && plantsGeneratorIterator.hasNext()) {
            Vector2d position = plantsGeneratorIterator.next();
            plantsFrequencyCounter.put(position, 1 + plantsFrequencyCounter.getOrDefault(position,0L));
            maxNumOfPlantsOnPosition = Math.max(maxNumOfPlantsOnPosition, plantsFrequencyCounter.get(position));
            plants.put(position, new Plant(position));
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

    public void removeDeadAnimals(int day){
        for (var animal: animals.getAll()){
            if(animal.getEnergy()<=0){
                animal.setAlive(false);
                animal.setDeathDay(day);
                animals.removeAnimal(animal);
                decreaseGenotypeCounter(animal);
                deadAnimalsCounter++;
                totalLifespanYears += day - animal.getDayOfBirth();
            }
        }
    }

    public void reproducePopulation(int day){
        List<Animal> newborns = new ArrayList<>();

        animals.getPositions().forEach(position ->
                getAnimalsOrdered(position)
                        .filter(items->items.size()>=2)
                        .ifPresent(items -> {
                            for (int i = 0; i<items.size()-1; i+=2){
                                Animal firstPartner = items.get(i);
                                Animal secondPartner = items.get(i+1);
                                if (!Animal.canReproduce(firstPartner, secondPartner))
                                    continue;
                                firstPartner.sex(secondPartner, day).ifPresent(kidAnimalData -> {
                                    Animal child = new Animal(
                                            position,
                                            firstPartner.animalOptions(),
                                            kidAnimalData
                                    );
                                    increaseGenotypeCounter(child);
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

    private int getFreeFieldsCount(){
        Set<Vector2d> takenFields = new HashSet<>(animals.getPositions());
        takenFields.addAll(plants.keySet());
        return width*height - takenFields.size();
    }


    private String getMostPopularGenotype(){
        if (genotypeCounter.isEmpty()) {
            return animals.getAll().isEmpty() ?
                    "-" :
                    animals.getAll().getFirst().getGen().toString();
        }

        return genotypeCounter.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("-");
    }

    private Double getAverageEnergy(){
        return animals.getAll()
                .stream()
                .collect(Collectors.averagingInt(Animal::getEnergy));
    }

    private void increaseGenotypeCounter(Animal animal){
        String gen = animal.getGen().toString();
        genotypeCounter.put(gen, genotypeCounter.getOrDefault(gen, 0) + 1);
    }

    private void decreaseGenotypeCounter(Animal animal){
        String genotyp = animal.getGen().toString();
        if (genotypeCounter.get(genotyp) == 1) {
            genotypeCounter.remove(genotyp);
            return;
        }
        genotypeCounter.put(genotyp, genotypeCounter.get(genotyp) - 1);
    }

    private double getAverageLifespan(){
        if (deadAnimalsCounter == 0) return 0.0;
        return (double) totalLifespanYears / deadAnimalsCounter;
    }

    private double getAverageChildren(){
        return animals.getAll()
                .stream()
                .collect(Collectors.averagingInt(Animal::getNumOfKids));
    }


    public MapStats getMapStats(){
        return new MapStats(getAnimalsCount(), getPlantsCount(), getFreeFieldsCount(), getAverageEnergy(), getAverageLifespan(), getAverageChildren(), getMostPopularGenotype(),getEnergyPercentile(85),getEnergyPercentile(50));
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
}
