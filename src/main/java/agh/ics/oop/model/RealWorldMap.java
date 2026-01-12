package agh.ics.oop.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class RealWorldMap extends AbstractWorldMap{
    private final Map<Vector2d, Plant> plants = new HashMap<>();
    private final PlantsGenerator plantsGenerator;
    private Iterator<Vector2d> plantsGeneratorIterator;
    private final MapVisualizer mapVisualizer = new MapVisualizer(this);

    private final int width;
    private final int height;
    private final int plantNumEveryDay;
    private final AnimalOptions defaultAnimalOptions;
    private final MapOptions mapOptions;

    public RealWorldMap(MapOptions mapOptions, AnimalOptions defaultAnimalOptions){
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
        plantsGenerator.reShuffle();

        plantsGeneratorIterator = plantsGenerator.iterator(); // reset iteratora, wczesniej wskazywal na starego planta

        int created = 0;
        while (created < plantNumEveryDay && plantsGeneratorIterator.hasNext()) {
            createPlant();
            created++;
        }
    }

    public List<Plant> getPlants(){
        return  new ArrayList<>(plants.values());
    }

    private void eatPlant(Vector2d position) {
        getAnimalsOrdered(position)
                .filter(items -> !items.isEmpty())
                .map(items -> items.getFirst())
                .ifPresent(strongestAnimal -> {
                    strongestAnimal.eat();
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
        animals.getAnimalsHashMap().values().forEach(animalList -> {
            Iterator<Animal> iterator = animalList.iterator();
            while (iterator.hasNext()) {
                Animal animal = iterator.next();
                if (animal.isDead()) {
                    Vector2d position = animal.position();
                    iterator.remove();
                    mapChanged("Animal dead at " + position);
                }
            }
        });

        animals.getAnimalsHashMap().entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public void reproducePopulation(int day){
        List<Animal> newborns = new ArrayList<>();

        animals.getPositions().forEach(position ->
                getAnimalsOrdered(position).filter(items->items.size()>=2).ifPresent(items -> {
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

    public void createAnimalsOnRandomPositions(int numOfAnimals, int dayOfBirth){
        Boundary boundary = getCurrentBounds();
        for (int i = 0 ;i<numOfAnimals; i++)
            animals.addAnimal(new Animal(boundary.getRandomPosition(), defaultAnimalOptions, mapOptions.energyStart(), dayOfBirth));
    }

    public MapOptions getMapOptions() {
        return mapOptions;
    }

    @Override
    public Optional<WorldElement> objectAt(Vector2d position) {

        Optional<Animal> animal = getAnimalsOrdered(position)
                .flatMap(list -> list.stream().findFirst());

        if (animal.isPresent()) {
            return animal.map(a -> (Animal) a);
        }

        return Optional.ofNullable(plants.get(position));
    }

}
