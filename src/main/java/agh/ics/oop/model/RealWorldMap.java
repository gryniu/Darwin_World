package agh.ics.oop.model;

import java.util.*;
import java.util.stream.Collectors;

public class RealWorldMap extends AbstractWorldMap{
    private final Map<Vector2d, Plant> plants = new HashMap<>();
    private final PlantsGenerator plantsGenerator;
    private final Iterator<Vector2d> plantsGeneratorIterator;

    public final int width;
    public final int height;
    public final int plantNumEveryDay;

    public RealWorldMap(MapOptions mapOptions, List<Animal> animals){
        width = mapOptions.mapWidth();
        height = mapOptions.mapHeight();
        plantNumEveryDay = mapOptions.plantNumEveryDay();

        plantsGenerator = new PlantsGenerator(width, height);
        plantsGeneratorIterator = plantsGenerator.iterator();

        for(int i = 0; i < mapOptions.startingNumOfPlants(); i++){
            createPlant();
        }

        for(var animal: animals){
            this.animals.addAnimal(animal);
        }
    }

    @Override
    public void move(Animal animal) {
        Vector2d oldPosition = animal.position();
        animals.removeAnimal(animal);
        Vector2d newPosition = oldPosition.add(animal.getOrientation().toUnitVector());

        if (newPosition.getY() < 0 || newPosition.getX() >= height){
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
    public Boundary getCurrentBounds() {
        Vector2d lowerLeft = new Vector2d(0,  0);
        Vector2d upperRight = new Vector2d(width-1, height-1);
        return new Boundary(lowerLeft, upperRight);
    }

    public void removeDeadAnimals(){
        for (var animal: getAllAnimals()){
            if(animal.getEnergy() == 0){
                animals.removeAnimal(animal);
            }
        }
    }

    private void createPlant(){
        if(!plantsGeneratorIterator.hasNext()) return;
        Vector2d position = plantsGeneratorIterator.next();
        plants.put(position, new Plant(position));
    }

    public void createNewPlants(){
        plantsGenerator.reShuffle();
        for(int i = 0; i < plantNumEveryDay; i++){
            createPlant();
        }
    }

    public List<Plant> getPlants(){
        return  new ArrayList<>(plants.values());
    }

    public void eatPlant(Vector2d position){
        getAnimalsOrdered(position).ifPresent(items -> {
            items.getFirst().eat();
            plantsGenerator.returnPlant(plants.remove(position));
        });
    }

    public void eatAllPlants(){
        for(var plant: getPlants()){
            eatPlant(plant.position());
        }
    }
}
