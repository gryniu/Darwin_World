package agh.ics.oop.model;

import java.util.*;

public class FakeWorldMap extends AbstractWorldMap<FakeAnimal> {

    public FakeWorldMap(UUID id, int day, int width, int height) {
        super(width, height);

        //importowanie animali
        for (var animal : HistoryFileHandler.importAnimals(id, day)) {
            animals.addAnimal(animal);
        }

        //importowanie roslin
        for (var position : HistoryFileHandler.importPlants(id, day)) {
            plants.put(position, new Plant(position));
        }
    }
}
