package agh.ics.oop.model.map;

import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.animal.Animal;
import agh.ics.oop.model.animal.AnimalOptions;
import agh.ics.oop.model.animal.EnergyOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeasonalWorldMapTest {
    private SeasonalWorldMap map;
    private AnimalOptions animalOptions;
    private Vector2d position;

    @BeforeEach
    void setUp() {
        EnergyOptions energyOptions = new EnergyOptions(10, 1, 50, 25);
        animalOptions = new AnimalOptions(energyOptions, 0, 2, 32);
        MapOptions mapOptions = new MapOptions(10, 10, 5, 10, 0, 100);
        SeasonsOptions seasonsOptions = new SeasonsOptions(10, -10, 3);
        map = new SeasonalWorldMap(mapOptions, animalOptions, seasonsOptions);
        position = new Vector2d(5, 5);
    }

    @Test
    void testAnimalsLoseEnergyInWinter() {
        Animal animal = new Animal(position, animalOptions, 100, 0);
        map.place(animal);
        
        int energyBefore = animal.getEnergy();
        map.decreaseEnergyAllAnimals();
        int energyAfter = animal.getEnergy();
        
        assertTrue(energyAfter < energyBefore, "Zwierzeta powinny tracic energie");
        assertEquals(99, energyAfter, "-1 energii dziennie");
    }

    @Test
    void testMultipleAnimalsLoseEnergy() {
        Animal animal1 = new Animal(position, animalOptions, 100, 0);
        Animal animal2 = new Animal(new Vector2d(6, 6), animalOptions, 50, 0);
        map.place(animal1);
        map.place(animal2);
        
        int energy1Before = animal1.getEnergy();
        int energy2Before = animal2.getEnergy();
        
        map.decreaseEnergyAllAnimals();
        
        assertTrue(animal1.getEnergy() < energy1Before);
        assertTrue(animal2.getEnergy() < energy2Before);
    }

    @Test
    void testAnimalDiesFromEnergyLoss() {
        Animal animal = new Animal(position, animalOptions, 1, 0);
        map.place(animal);
        
        assertTrue(animal.isAlive(), "zwierze powinno zyc");
        map.decreaseEnergyAllAnimals();

        map.removeDeadAnimals();
        assertFalse(animal.isAlive(), "zwierze powinno umrzec majac 0 energii");
    }

    @Test
    void testAnimalsLoseMoreEnergyInWinterThanSummer() {
        // sprawdzamy w lato
        Animal animalSummer = new Animal(position, animalOptions, 100, 0);
        map.place(animalSummer);
        
        int energySummerBefore = animalSummer.getEnergy();
        map.decreaseEnergyAllAnimals();
        int energySummerAfter = animalSummer.getEnergy();
        int summerEnergyLoss = energySummerBefore - energySummerAfter;
        
        // przechodzimy do zimy
        for (int i = 0; i < 15; i++) {
            map.handleEndOfADay();
        }

        // sprawdzamy w zime (oddalone)
        Animal animalWinterIsolated = new Animal(new Vector2d(0, 0), animalOptions, 100, 0);
        map.place(animalWinterIsolated);
        
        double energyWinterBefore = animalWinterIsolated.getEnergy();
        map.decreaseEnergyAllAnimals();
        double energyWinterAfter = animalWinterIsolated.getEnergy();
        double winterEnergyLoss = energyWinterBefore - energyWinterAfter;
        
        assertTrue(winterEnergyLoss > summerEnergyLoss, 
            "Oddalone zwierzete powinny tracic wiecej energii w zime. " +
            "Winter loss: " + winterEnergyLoss + ", Summer loss: " + summerEnergyLoss);
    }

    @Test
    void testHeatedAnimalsLoseEnergyInWinter() {
        // sprawdzamy w lato
        Animal animalSummer = new Animal(position, animalOptions, 100, 0);
        map.place(animalSummer);

        int energySummerBefore = animalSummer.getEnergy();
        map.decreaseEnergyAllAnimals();
        int energySummerAfter = animalSummer.getEnergy();
        int summerEnergyLoss = energySummerBefore - energySummerAfter;

        // przechodzimy do zimy
        for (int i = 0; i < 15; i++) {
            map.handleEndOfADay();
        }

        // sprawdzamy w zime (blisko siebie)
        Animal animalWinterIsolated = new Animal(position.add(new Vector2d(3, 3)), animalOptions, 100, 0);
        map.place(animalWinterIsolated);

        double energyWinterBefore = animalWinterIsolated.getEnergy();
        map.decreaseEnergyAllAnimals();
        double energyWinterAfter = animalWinterIsolated.getEnergy();
        double winterEnergyLoss = energyWinterBefore - energyWinterAfter;

        assertEquals(winterEnergyLoss, summerEnergyLoss,
                "Zwierzeta w zime ktore sa ogrzane powinno tracic tyle energii co w lato. " +
                        "Winter loss: " + winterEnergyLoss + ", Summer loss: " + summerEnergyLoss);
    }
}