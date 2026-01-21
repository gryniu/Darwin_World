package agh.ics.oop.model.map;

import agh.ics.oop.model.filesystem.HistoryFileHandler;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.animal.FakeAnimal;

import java.util.*;

public class FakeWorldMap extends AbstractWorldMap<FakeAnimal> {

    public FakeWorldMap(UUID id, int day, int width, int height) {
        super(width, height);
        super.day = day;

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
