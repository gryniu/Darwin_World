package agh.ics.oop.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FakeWorldMap extends AbstractWorldMap<FakeAnimal>{
//    private final MapStats mapStats;

    public FakeWorldMap(UUID id, int day, int width, int height) {
        super(width, height);

        //importowanie animali
        for(var animal: HistoryFileHandler.importAnimals(id, day)){
            animals.addAnimal(animal);
        }

        //importowanie roslin
        for(var position: HistoryFileHandler.importPlants(id, day)){
            plants.put(position, new Plant(position));
        }

//        mapStats = HistoryFileHandler.importStats(id, day);
    }
//
//    @Override
//    public MapStats getMapStats() {
//        return mapStats;
//    }
}
