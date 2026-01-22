package agh.ics.oop.model.animal;

import agh.ics.oop.model.Vector2d;
import agh.ics.oop.model.MapDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AnimalTest {

    private AnimalOptions animalOptions;
    private Animal animal;

    @BeforeEach
    void setUp() {
        // defaultowe ustawienia dla testów
        EnergyOptions energyOptions = new EnergyOptions(10,2,20,10);
        this.animalOptions = new AnimalOptions(energyOptions,1,3,10);
        animal = new Animal(new Vector2d(0,0), animalOptions, 100, 0);
    }

    @Test
    void testInitialEnergy() {
        assertEquals(100, animal.getEnergy(), "Zwierzę powinno mieć początkową energię");
    }

    @Test
    void testEatIncreasesEnergy() {
        int energyBefore = animal.getEnergy();
        animal.eat(1.0); // multiplier 1
        assertTrue(animal.getEnergy() > energyBefore, "Energia powinna wzrosnąć po jedzeniu");
        assertEquals(1, animal.getPlantConsumedCounter(), "PlantConsumedCounter powinien zwiększyć się o 1");
    }

    @Test
    void testDailyEnergyDecrease() {
        int energyBefore = animal.getEnergy();
        animal.decreaseDailyEnergy(1);
        assertTrue(animal.getEnergy() < energyBefore, "Energia powinna spaść po dziennej utracie");
    }

    @Test
    void testIsAlive() {
        assertTrue(animal.isAlive(), "Zwierzę powinno być żywe na start");
        animal.die(5);
        assertFalse(animal.isAlive(), "Zwierzę po śmierci nie powinno być żywe");
        assertEquals(5, animal.getDeathDay());
    }

    @Test
    void testNumOfLivedDays() {
        assertEquals(0, animal.getNumOfLivedDays());
        animal.increaseNumOfLivedDays();
        assertEquals(1, animal.getNumOfLivedDays());
    }

    @Test
    void testRotateAndOrientation() {
        MapDirection original = animal.getOrientation();
        animal.rotate180();
        assertEquals(original.opposite(), animal.getOrientation(), "rotate180 powinno ustawić przeciwną orientację");
    }

    @Test
    void testSetPosition() {
        Vector2d newPos = new Vector2d(5, 7);
        animal.setPosition(newPos);
        assertEquals(newPos, animal.getPosition());
    }

    @Test
    void testSexCreatesKid() {
        Animal partner = new Animal(new Vector2d(0,0), animalOptions, 100, 0);
        Optional<AnimalData> kidData = animal.sex(partner, 0);
        assertTrue(kidData.isPresent(), "Powinien powstać AnimalData dziecka");
        assertTrue(kidData.get().energyStart() <= animal.getEnergy() + partner.getEnergy(),
                "Dziecko nie może mieć więcej energii niż rodzice przekazali");
        assertEquals(animal.getDayOfBirth(),kidData.get().dayOfBirth());
    }

}
