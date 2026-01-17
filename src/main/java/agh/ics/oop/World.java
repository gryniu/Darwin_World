package agh.ics.oop;

import agh.ics.oop.model.*;

public class World {
    public static void main(String[] args){
        MapOptions mapOptions = new MapOptions(20, 20, 10, 5, 25, 100);
        EnergyOptions energyOptions = new EnergyOptions(20, 5, 25, 10);
        AnimalOptions animalOptions = new AnimalOptions(energyOptions, 1,3, 5);
        SeasonsOptions seasonsOptions = new SeasonsOptions(3, -10, 3);

        Simulation simulation = new Simulation(new SeasonalWorldMap(mapOptions,animalOptions,seasonsOptions),200);
        simulation.run();
    }
}
