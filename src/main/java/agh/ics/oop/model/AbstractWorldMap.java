package agh.ics.oop.model;

import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public abstract class AbstractWorldMap implements LivingWorldMap {
    protected final UUID id;
    protected final AnimalsMap<Animal> animals = new AnimalsMap<>();
    private final ArrayList<Listener> subscribers = new ArrayList<>();
    protected final MapVisualizer mapVisualizer = new MapVisualizer(this);
    // liczniki częstotliwości
    protected final Map<String, Integer> genotypeCounter = new HashMap<>();
    protected final Map<Vector2d, Long> plantsFrequencyCounter = new HashMap<>();

    private final Map<Vector2d, Plant> plants = new HashMap<>();
    private final PlantsGenerator plantsGenerator;
    private Iterator<Vector2d> plantsGeneratorIterator;
    protected Long maxNumOfPlantsOnPosition = 7L;
    // ustawiam na poczatek na 7 zeby nie bylo sytuacji,
    // ze na poczatku wszystko jest na ciemno-zielono

    private long deadAnimalsCounter = 0L;
    private long totalLifespanYears = 0L;

    private final int width;
    private final int height;
    private final AnimalOptions defaultAnimalOptions;
    private final MapOptions mapOptions;

    protected int plantNumEveryDay;
    protected double energyFromPlantMultiplier = 1;
    protected double energyDecreaseMultiplier = 1;
    protected double plantNumMultiplier = 1;

    public AbstractWorldMap(MapOptions mapOptions, AnimalOptions defaultAnimalOptions){
        id = UUID.randomUUID();
        width = mapOptions.mapWidth();
        height = mapOptions.mapHeight();
        plantNumEveryDay = mapOptions.plantNumEveryDay();
        this.defaultAnimalOptions = defaultAnimalOptions;
        this.mapOptions = mapOptions;

        plantsGenerator = new PlantsGenerator(width, height);
        plantsGeneratorIterator = plantsGenerator.iterator();

        createAnimalsOnRandomPositions();

        for(int i = 0; i < mapOptions.startingNumOfPlants(); i++){
            createPlant();
        }
    }

    public void place(Animal animal) {
        Vector2d position = animal.position();
        if (!inBounds(animal.position())){
            throw new IncorrectPositionException(position,getCurrentBounds());
        }
        animals.addAnimal(animal);
        increaseGenotypeCounter(animal);
        mapChanged("animal placed on %s".formatted(position));
    }

    @Override
    public String toString(){
        Boundary bounds = getCurrentBounds();
        return mapVisualizer.draw(bounds.lowerLeft(), bounds.upperRight());
    }

    @Override
    public Optional<List<Animal>> getAnimals(Vector2d position){
        return animals.getFrom(position);
    }

    @Override
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

    @Override
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

    @Override
    public void addSubscriber(Listener subscriber){
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(Listener subscriber){
        subscribers.remove(subscriber);
    }

    @Override
    public void mapChanged(String message){
        for(Listener subscriber: subscribers){
            subscriber.change(this, message);
        }
    }

    @Override
    public UUID getId(){
        return id;
    }

    public boolean inBounds(Vector2d position){
        Boundary boundary = getCurrentBounds();
        return position.follows(boundary.lowerLeft()) && position.precedes(boundary.upperRight());
    }

    public void move(Animal animal) {
        Vector2d oldPosition = animal.position();
        animals.removeAnimal(animal);
        Vector2d newPosition = oldPosition.add(animal.getOrientation().toUnitVector());

        if (newPosition.getY() < 0 || newPosition.getY() >= height){
            animal.rotate180();
            newPosition = oldPosition.add(animal.getOrientation().toUnitVector());
        }
        if (newPosition.getX() < 0 || newPosition.getX() >= width){
            newPosition = new Vector2d(Math.floorMod(newPosition.getX(), width), newPosition.getY());
        }

        animal.setPosition(newPosition);
        animals.addAnimal(animal);

        mapChanged("Animal moved from %s to %s".formatted(oldPosition, newPosition));
    }

    @Override
    public Boundary getCurrentBounds() { // todo: Bounds sie nie zmieniaja bo to nie GrassField do wyjebania
        Vector2d lowerLeft = new Vector2d(0,  0);
        Vector2d upperRight = new Vector2d(width-1, height-1);
        return new Boundary(lowerLeft, upperRight);
    }

    private void createPlant(){
        if(!plantsGeneratorIterator.hasNext()) return;
        Vector2d position = plantsGeneratorIterator.next();
        plantsFrequencyCounter.put(position, 1 + plantsFrequencyCounter.getOrDefault(position,0L));
        maxNumOfPlantsOnPosition = Math.max(maxNumOfPlantsOnPosition, plantsFrequencyCounter.get(position));
        plants.put(position, new Plant(position));
    }

    @Override
    public void createNewPlants(){
        plantsGeneratorIterator = plantsGenerator.reShuffle();

        int created = 0;
        while (created < (int)(plantNumEveryDay*plantNumMultiplier) && plantsGeneratorIterator.hasNext()) {
            createPlant();
            created++;
        }
    }

    @Override
    public List<Plant> getPlants(){
        return new ArrayList<>(plants.values());
    }

    private void eatPlant(Vector2d position) {
        getAnimalsOrdered(position)
                .filter(items -> !items.isEmpty())
                .ifPresent(items -> {
                    items.getFirst().eat(energyFromPlantMultiplier);
                    plants.remove(position);
                    plantsGenerator.returnPlant(position);
                    mapChanged("Animal ate plant at " + position);
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
                mapChanged("animal died on %s".formatted(animal.position()));
                decreaseGenotypeCounter(animal);
                deadAnimalsCounter++;
                totalLifespanYears += day - animal.getDayOfBirth();
            }
        }
    }

    @Override
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
                                    firstPartner.increaseDescendantsCounter();
                                    secondPartner.increaseDescendantsCounter();
                                    newborns.add(child);
                                });
                            }
                        }));

        for (Animal child : newborns) {
            place(child);
            mapChanged("New animal born at " + child.position());
        }
    }

    @Override
    public void moveAllAnimals(){
        for (Animal animal: getAllAnimals()){
            move(animal);
            animal.rotate();
            animal.increaseNumOfLivedDays();
        }
    }

    @Override
    public void decreaseEnergyAllAnimals(){
        List<Animal> currentAnimals = getAllAnimals();

        for (Animal animal: currentAnimals){
            animal.decreaseDailyEnergy(energyDecreaseMultiplier);
        }
    }

    private void createAnimalsOnRandomPositions(){
        Boundary boundary = getCurrentBounds();
        for (int i = 0 ;i< mapOptions.startingNumOfAnimals(); i++)
            place(new Animal(boundary.getRandomPosition(), defaultAnimalOptions, mapOptions.energyStart(), 0));
    }

    public Optional<WorldElement> objectAt(Vector2d position) {
        var items = getAnimalsOrdered(position);
        if(items.isEmpty()){
            return Optional.ofNullable(plants.get(position));
        }else{
            return items.map(List::getFirst);
        }
    }

    public int getAnimalsCount(){
        return animals.getAnimalsCount();
    }

    @Override
    public int getPlantsCount(){
        return plants.size();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
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
        if (!genotypeCounter.containsKey(genotyp)) return;
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

    @Override
    public List<WorldElement> getAllMapElements() {
        List<WorldElement> elements = new ArrayList<>();
        elements.addAll(getAllAnimals());
        elements.addAll(getPlants());
        return elements;
    }

    @Override
    public MapStats getMapStats(){
        return new MapStats(getAnimalsCount(), getPlantsCount(), getFreeFieldsCount(), getAverageEnergy(), getAverageLifespan(), getAverageChildren(), getMostPopularGenotype());

    }


    public Color getColorOfField(Vector2d fieldPosition){
        Long plantsFrequency = plantsFrequencyCounter.getOrDefault(fieldPosition,0L);
        if (plantsFrequency < maxNumOfPlantsOnPosition*.33) return Color.LIGHTGREEN;
        if (plantsFrequency < maxNumOfPlantsOnPosition*.75) return Color.GREEN;
        return Color.DARKGREEN;
    }

    // Zwraca wartość energii, poniżej której znajduje się percentile zwierzaków,
    @Override
    public int getEnergyPercentile(int percentile){
        if (percentile < 0 || percentile > 100) throw new IllegalArgumentException("Percentile must be in [0,100]");
        if (animals.getAll().isEmpty()) return 0;

        List<Integer> energies = animals.getAll().stream()
                .map(Animal::getEnergy)
                .sorted()
                .toList();

        int index = (int) Math.ceil(percentile / 100.0 * energies.size()) - 1;
        index = Math.max(0, Math.min(index, energies.size() - 1));

        return energies.get(index);
    }

}
