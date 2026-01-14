package agh.ics.oop.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractWorldMap implements WorldMap {
    protected final UUID id = UUID.randomUUID();
    protected final AnimalsMap animals = new AnimalsMap();
    private final ArrayList<Listener> subscribers = new ArrayList<>();
    protected final MapVisualizer mapVisualizer = new MapVisualizer(this);


    private final Map<Vector2d, Plant> plants = new HashMap<>();
    private final PlantsGenerator plantsGenerator;
    private Iterator<Vector2d> plantsGeneratorIterator;;

    private final int width;
    private final int height;
    private final AnimalOptions defaultAnimalOptions;
    private final MapOptions mapOptions;

    protected int plantNumEveryDay;
    protected double energyFromPlantMultiplier = 1;
    protected double energyDecreaseMultiplier = 1;
    protected double plantNumMultiplier = 1;

    public AbstractWorldMap(MapOptions mapOptions, AnimalOptions defaultAnimalOptions){
        width = mapOptions.mapWidth();
        height = mapOptions.mapHeight();
        plantNumEveryDay = mapOptions.plantNumEveryDay();
        this.defaultAnimalOptions = defaultAnimalOptions;
        this.mapOptions = mapOptions;

        plantsGenerator = new PlantsGenerator(width, height);
        plantsGeneratorIterator = plantsGenerator.iterator();

        for(int i = 0; i < mapOptions.startingNumOfPlants(); i++){
            createPlant();
        }
    }

    @Override
    public void place(Animal animal) {
        Vector2d position = animal.position();
        if (!inBounds(animal.position())){
            throw new IncorrectPositionException(position,getCurrentBounds());
        }
        animals.addAnimal(animal);
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

    @Override
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
        plants.put(position, new Plant(position));
    }

    public void createNewPlants(){
        plantsGeneratorIterator = plantsGenerator.reShuffle();

        int created = 0;
        while (created < (int)(plantNumEveryDay*plantNumMultiplier) && plantsGeneratorIterator.hasNext()) {
            createPlant();
            created++;
        }
    }

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

    public void removeDeadAnimals(){
        for (var animal: animals.getAll()){
            if(animal.isDead()){
                animals.removeAnimal(animal);
                mapChanged("animal died on %s".formatted(animal.position()));
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
                                    newborns.add(child);
                                });
                            }
                        }));

        for (Animal child : newborns) {
            place(child);
            mapChanged("New animal born at " + child.position());
        }
    }

    public void moveAllAnimals(){
        for (Animal animal: getAllAnimals()){
            animal.rotate();
            move(animal);
        }
    }

    public void decreaseEnergyAllAnimals(){
        List<Animal> currentAnimals = getAllAnimals();

        for (Animal animal: currentAnimals){
            animal.decreaseDailyEnergy(energyDecreaseMultiplier);
        }
    }

    public void createAnimalsOnRandomPositions(int dayOfBirth){
        Boundary boundary = getCurrentBounds();
        for (int i = 0 ;i< mapOptions.startingNumOfAnimals(); i++)
            animals.addAnimal(new Animal(boundary.getRandomPosition(), defaultAnimalOptions, mapOptions.energyStart(), dayOfBirth));
    }

    public MapOptions getMapOptions() {
        return mapOptions;
    }

    @Override
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
}
