package agh.ics.oop;

import agh.ics.oop.model.*;

public class World {
    public static void main(String[] args){
        MapOptions mapOptions = new MapOptions(20, 20, 85, 10, 25, 100);
        EnergyOptions energyOptions = new EnergyOptions(20, 5, 25, 10);
        AnimalOptions animalOptions = new AnimalOptions(energyOptions, 2, 5);
        RealWorldMap map = new RealWorldMap(mapOptions, animalOptions);
        MapVisualizer mapVisualizer = new MapVisualizer(map);

        Simulation simulation = new Simulation(map, animalOptions);
        simulation.run();
    }
}
