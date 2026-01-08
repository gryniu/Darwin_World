package agh.ics.oop.model;

import java.util.*;

public class RealWorldMap extends AbstractWorldMap{
    private final Map<Vector2d, Plant> plants = new HashMap<>();
    public final int width;
    public final int height;
    public final int plantNumEveryDay;

    public RealWorldMap(AnimalOptions animalOptions, MapOptions mapOptions){
        width = mapOptions.mapWidth();
        height = mapOptions.mapHeight();
        plantNumEveryDay = mapOptions.plantNumEveryDay();

        Random random = new Random();

        for(int i = 0; i < mapOptions.startingNumOfPlants(); i++){
            createPlant();
        }

        for(int i = 0; i < mapOptions.startingNumOfAnimals(); i++){
            Vector2d position = new Vector2d(random.nextInt(width), random.nextInt(height));
            Animal animal = new Animal(position, animalOptions);
            animals.addAnimal(animal);
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
            newPosition = new Vector2d(newPosition.getX() % width, newPosition.getY());
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

    private void createPlant(){
        Random random = new Random();
        Vector2d position;
        do {
            int x = random.nextInt(width);
            int y;
            if(random.nextInt(10) < 8){
                y = random.nextInt(height/5)+(2*height/5);
            }
            else{
                y = random.nextInt(2*height/5) + random.nextInt(2)*(3*height/5);
            }
            position = new Vector2d(x, y);
        } while (plants.containsKey(position));
        plants.put(position, new Plant(position));
    }

    public void removeDeadAnimals(){
        for (var animal: getAllAnimals()){
            if(animal.getEnergy() == 0){
                animals.removeAnimal(animal);
            }
        }
    }

    public void createNewPlants(){
        for(int i = 0; i < plantNumEveryDay; i++){
            createPlant();
        }
    }

    public List<Plant> getPlants(){
        return  new ArrayList<>(plants.values());
    }
}
