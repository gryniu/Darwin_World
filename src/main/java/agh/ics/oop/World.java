package agh.ics.oop;

import agh.ics.oop.model.*;

public class World {
    public static void main(String[] args){
        MapOptions mapOptions = new MapOptions(20, 20, 1, 1, 25, 100);
        EnergyOptions energyOptions = new EnergyOptions(20, 5, 25, 10);
        AnimalOptions animalOptions = new AnimalOptions(energyOptions, 2, 5);

        Simulation simulation = new Simulation(mapOptions, animalOptions);
        simulation.run();
    }
}
