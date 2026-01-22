package agh.ics.oop.model.animal;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenTest {

    @Test
    void testConstructorWithLength() {
        Gen gen = new Gen(5);
        assertEquals(5, gen.getLenOfGen());
        assertEquals(5, gen.getGenList().size());
        for (int i : gen.getGenList()) {
            assertTrue(i >= 0 && i < 8, "Wartości genów muszą być w zakresie 0-7");
        }
    }

    @Test
    void testConstructorWithList() {
        List<Integer> list = Arrays.asList(0, 1, 2, 3);
        Gen gen = new Gen(list);
        assertEquals(4, gen.getLenOfGen());
        assertEquals(list, gen.getGenList());
    }

    @Test
    void testConstructorWithString() {
        String s = "0123";
        Gen gen = new Gen(s);
        assertEquals(4, gen.getLenOfGen());
        assertEquals(Arrays.asList(0,1,2,3), gen.getGenList());
    }

    @Test
    void testIteratorNextCyclesThrough() {
        List<Integer> list = Arrays.asList(0,1,2,3);
        Gen gen = new Gen(list);
        Iterator<Integer> it = gen.iterator();

        for (int i = 0; i < 10; i++) {
            int val = it.next();
            assertTrue(val >= 0 && val < 4, "Iterator zwraca wartość z listy");
        }
    }

    @Test
    void testRandomizeChangesAtLeastOneElement() {
        List<Integer> list = Arrays.asList(0,0,0,0,0);
        Gen gen = new Gen(list);

        List<Integer> beforeRandomize = List.copyOf(gen.getGenList());

        gen.randomize(1,3);

        boolean changed = false;
        for (int i = 0; i < beforeRandomize.size(); i++) {
            if (!beforeRandomize.get(i).equals(gen.getGenList().get(i))) {
                changed = true;
                break;
            }
        }
        assertTrue(changed, "Po randomize co najmniej jeden element genu powinien się zmienić");
    }


    @Test
    void testMixGensProducesCorrectLength() {
        List<Integer> gen1List = Arrays.asList(0,0,0,0);
        List<Integer> gen2List = Arrays.asList(1,1,1,1);
        EnergyOptions energyOptions = new EnergyOptions(10,2,20,10);
        AnimalOptions animalOptions = new AnimalOptions(energyOptions,0,0,10);

        Animal a1 = new Animal(null,animalOptions,new AnimalData(new Gen(gen1List),100,0));
        Animal a2 = new Animal(null, animalOptions, new AnimalData(new Gen(gen2List), 100, 0));

        Gen genKid = Gen.mixGens(a1, a2);
        assertEquals(a1.getGen().getLenOfGen(), genKid.getLenOfGen(), "Długość genu dziecka powinna być równa długości rodziców");
        assertEquals(a2.getGen().getLenOfGen(), genKid.getLenOfGen(), "Długość genu dziecka powinna być równa długości rodziców");
        for (int val : genKid.getGenList()) {
            assertTrue(val == 0 || val == 1, "Geny dziecka powinny pochodzić od rodziców");
        }
    }

    @Test
    void testToString() {
        List<Integer> list = Arrays.asList(0,1,2);
        Gen gen = new Gen(list);
        assertEquals("012", gen.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        List<Integer> list = Arrays.asList(1,2,3);
        Gen g1 = new Gen(list);
        Gen g2 = new Gen(list);
        Gen g3 = new Gen(Arrays.asList(3,2,1));

        assertEquals(g1, g2, "Te same geny powinny być równe");
        assertNotEquals(g1, g3, "Różne geny nie powinny być równe");
        assertEquals(g1.hashCode(), g2.hashCode(), "Równe geny powinny mieć ten sam hashCode");
    }

    @Test
    void testConstructorWithInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> new Gen(0));
    }

    @Test
    void testConstructorWithEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> new Gen(Arrays.asList()));
    }

    @Test
    void testConstructorWithEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> new Gen(""));
    }
}
