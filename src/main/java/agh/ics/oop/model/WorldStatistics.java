package agh.ics.oop.model;

import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorldStatistics {
    protected final Map<String, Long> genCounter = new HashMap<>();
    protected final Map<Vector2d, Long> plantsFrequencyCounter = new HashMap<>();

    protected Long maxNumOfPlantsOnPosition = 6L;
    private final RealWorldMap worldMap;

    private long deadAnimalsCounter = 0L;
    private long totalLifespanYears = 0L;

    public WorldStatistics(RealWorldMap worldMap) {
        this.worldMap = worldMap;
        worldMap.setPlantGrowListener(this::updatePlantsCounter);
        worldMap.setGenCountListener(this::updateGenCounter);
        worldMap.setDeadAnimalListener(this::updateDeadAnimalsCounter);
    }

    public int getEnergyPercentile(int percentile) {
        if (percentile < 0 || percentile > 100) throw new IllegalArgumentException("Percentile must be in [0,100]");
        List<Animal> animals = worldMap.getAllAnimals();
        if (animals.isEmpty()) return 0;

        List<Integer> energies = animals.stream()
                .map(Animal::getEnergy)
                .sorted()
                .toList();

        int index = (int) Math.ceil(percentile / 100.0 * energies.size()) - 1;
        index = Math.max(0, Math.min(index, energies.size() - 1));

        return energies.get(index);
    }


    private String getMostPopularGenotype() {
        if (genCounter.isEmpty()) {
            return "-";
        }

        long maxCounter = genCounter.values()
                .stream()
                .max(Comparator.comparingLong(Long::longValue))
                .orElse(0L);

        if (maxCounter < 2) return "-";

        return genCounter
                .keySet()
                .stream()
                .filter(key -> genCounter.get(key) == maxCounter)
                .findFirst()
                .orElse("-");
    }

    private Double getAverageEnergy() {
        return worldMap.getAllAnimals()
                .stream()
                .collect(Collectors.averagingInt(Animal::getEnergy));
    }

    private void increaseGenotypeCounter(Gen gen, int count) {
        genCounter.put(gen.toString(), genCounter.getOrDefault(gen.toString(), 0L) + count);
    }

    private void decreaseGenotypeCounter(Gen gen, int count) {
        String gen1 = gen.toString();
        if (genCounter.getOrDefault(gen1, 0L) == count) {
            genCounter.remove(gen1);
            return;
        }
        genCounter.put(gen1, genCounter.getOrDefault(gen1, 0L) - count);
    }

    public MapStats getMapStats(){
        return new MapStats(worldMap.getAnimalsCount(), worldMap.getPlantsCount(), worldMap.getFreeFieldsCount(), getAverageEnergy(), getAverageLifespan(), getAverageChildren(), getMostPopularGenotype());
    }

    private void updatePlantsCounter(Vector2d position, int count){
        plantsFrequencyCounter.put(position, count + plantsFrequencyCounter.getOrDefault(position,0L));
        maxNumOfPlantsOnPosition = Math.max(maxNumOfPlantsOnPosition, plantsFrequencyCounter.get(position));
    }

    private void updateGenCounter(Gen gen, int count){
        if(count > 0) increaseGenotypeCounter(gen ,count);
        else decreaseGenotypeCounter(gen, count);
    }

    private double getAverageLifespan(){
        if (deadAnimalsCounter == 0) return 0.0;
        return (double) totalLifespanYears / deadAnimalsCounter;
    }

    private double getAverageChildren(){
        return worldMap.getAllAnimals()
                .stream()
                .collect(Collectors.averagingInt(Animal::getNumOfKids));
    }


//    public FieldCategory getFieldCategory(Vector2d fieldPosition){
//        Long plantsFrequency = plantsFrequencyCounter.getOrDefault(fieldPosition,0L);
//        if (plantsFrequency < maxNumOfPlantsOnPosition*.33) return Color.valueOf("#78D23D");
//        if (plantsFrequency < maxNumOfPlantsOnPosition*.75) return Color.valueOf("#58BB43");
//        return Color.valueOf("#3AA346");
//    }
    public FieldCategory getFieldCategory(Vector2d fieldPosition){
        Long plantsFrequency = plantsFrequencyCounter.getOrDefault(fieldPosition,0L);
        if (plantsFrequency < maxNumOfPlantsOnPosition*.33) return FieldCategory.NORMAL;
        if (plantsFrequency < maxNumOfPlantsOnPosition*.75) return FieldCategory.POPULAR;
        return FieldCategory.VERY_POPULAR;
    }

    public void updateDeadAnimalsCounter(Animal animal, int count){
        deadAnimalsCounter += count;
        totalLifespanYears += worldMap.getDay() - animal.getDayOfBirth();
    }
}
