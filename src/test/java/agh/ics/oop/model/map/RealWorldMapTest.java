package agh.ics.oop.model.map;

import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.animal.Animal;
import agh.ics.oop.model.animal.AnimalOptions;
import agh.ics.oop.model.animal.EnergyOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RealWorldMapTest {
        private RealWorldMap map;
        private MapOptions mapOptions;
        private AnimalOptions animalOptions;

        @BeforeEach
        void setUp() {
            EnergyOptions energyOptions = new EnergyOptions(10, 10, 50, 25);
            animalOptions = new AnimalOptions(energyOptions, 0, 2, 32);
            mapOptions = new MapOptions(20, 20, 5, 10, 10, 100);
            map = new RealWorldMap(mapOptions, animalOptions);
        }

        @Test
        void testMapInitialization() {
            assertNotNull(map.getId());
            assertEquals(20, map.getWidth());
            assertEquals(20, map.getHeight());
            assertEquals(0, map.getDay());
        }

        @Test
        void testPlaceAnimal() {
            Animal animal = new Animal(new Vector2d(5, 5), animalOptions, 50, 0);
            map.place(animal);
            assertTrue(map.getAnimals(new Vector2d(5, 5)).isPresent());
        }

        @Test
        void testGetAnimalsOrdered() {
            Animal animal1 = new Animal(new Vector2d(5, 5), animalOptions, 30, 0);
            Animal animal2 = new Animal(new Vector2d(5, 5), animalOptions, 50, 0);
            map.place(animal1);
            map.place(animal2);
            
            Optional<List<Animal>> animals = map.getAnimalsOrdered(new Vector2d(5, 5));
            assertTrue(animals.isPresent());
            assertEquals(2, animals.get().size());
            assertEquals(50, animals.get().get(0).getEnergy());  // Highest energy (50) comes first
            assertEquals(30, animals.get().get(1).getEnergy());  // Lower energy (30) comes second
        }

        @Test
        void testMoveAnimal() {
            Animal animal = new Animal(new Vector2d(5, 5), animalOptions, 50, 0);
            map.place(animal);
            map.move(animal);
            assertFalse(map.getAnimals(new Vector2d(5, 5)).orElse(List.of()).contains(animal));
        }

        @Test
        void testGetAllAnimals() {
            Animal animal1 = new Animal(new Vector2d(5, 5), animalOptions, 50, 0);
            Animal animal2 = new Animal(new Vector2d(10, 10), animalOptions, 50, 0);
            map.place(animal1);
            map.place(animal2);
            
            List<Animal> all = map.getAllAnimals();
            assertFalse(all.isEmpty());
        }

        @Test
        void testRemoveDeadAnimals() {
            Animal animal = new Animal(new Vector2d(5, 5), animalOptions, mapOptions.energyStart(), 0);
            map.place(animal);
            for (int i = 0; i<10; i++){
                map.decreaseEnergyAllAnimals();
                map.removeDeadAnimals();
            }
            assertTrue(map.getAllAnimals().isEmpty());
        }

        @Test
        void testDecreaseEnergyAllAnimals() {
            Animal animal = new Animal(new Vector2d(5, 5), animalOptions, 50, 0);
            map.place(animal);
            int initialEnergy = animal.getEnergy();
            map.decreaseEnergyAllAnimals();
            assertTrue(animal.getEnergy() < initialEnergy);
        }

        @Test
        void testMoveAllAnimals() {
            Animal animal = new Animal(new Vector2d(5, 5), animalOptions, 50, 0);
            map.place(animal);
            Vector2d initialPos = animal.position();
            map.moveAllAnimals();
            assertNotEquals(initialPos, animal.position());
        }

        @Test
        void testRotateAllAnimals() {
            Animal animal = new Animal(new Vector2d(5, 5), animalOptions, 50, 0);
            map.place(animal);
            var initialOrientation = animal.getOrientation();
            map.rotateAllAnimals();
            assertNotEquals(initialOrientation, animal.getOrientation());
        }

        @Test
        void testHandleEndOfADay() {
            Animal animal = new Animal(new Vector2d(5, 5), animalOptions, 50, 0);
            map.place(animal);
            int initialDay = map.getDay();
            map.handleEndOfADay();
            assertEquals(initialDay + 1, map.getDay());
        }

        @Test
        void testGetFreeFieldsCount() {
            int freeFields = map.getFreeFieldsCount();
            assertTrue(freeFields > 0);
            assertTrue(freeFields <= 400);
        }

        @Test
        void testWrappingAroundMapBoundaries() {
            Animal animal = new Animal(new Vector2d(0, 10), animalOptions, 50, 0);
            map.place(animal);
            map.move(animal);
            assertTrue(animal.position().x() >= 0 && animal.position().x() < 20);
        }
}
