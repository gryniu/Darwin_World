package agh.ics.oop.model;

import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class SeasonalWorldMap extends RealWorldMap {
    private static final double SUMMER_TEMPERATURE = 30;
    private static final double SUMMER_PLANT_NUM_MULTIPLIER = 1.5;
    private static final double SUMMER_ENERGY_FROM_PLANT_MULTIPLIER = 1.5;

    private boolean isWinter;
    private int dayOfCurrentSeason;
    private double temperature;

    private final SeasonsOptions seasonsOptions;

    public SeasonalWorldMap(MapOptions mapOptions, AnimalOptions defaultAnimalOptions, SeasonsOptions seasonsOptions) {
        super(mapOptions, defaultAnimalOptions);
        this.seasonsOptions = seasonsOptions;
        temperature = SUMMER_TEMPERATURE;
        isWinter = false;
        dayOfCurrentSeason = 0;
        plantNumMultiplier = SUMMER_PLANT_NUM_MULTIPLIER;
        energyFromPlantMultiplier = SUMMER_ENERGY_FROM_PLANT_MULTIPLIER;
    }

    private void handleEndOfADay(){
        dayOfCurrentSeason++;
        if (dayOfCurrentSeason >= seasonsOptions.seasonLength()){
            isWinter = !isWinter;
            dayOfCurrentSeason = 0;
            if (isWinter) {
                plantNumMultiplier = 1;
                energyFromPlantMultiplier = 1;
            }else{
                plantNumMultiplier = SUMMER_PLANT_NUM_MULTIPLIER;
                energyFromPlantMultiplier = SUMMER_ENERGY_FROM_PLANT_MULTIPLIER;
                temperature = SUMMER_TEMPERATURE;
                energyDecreaseMultiplier = 1;
            }
        }

        if(isWinter){
            int middleDay = seasonsOptions.seasonLength()/2+1;
            if(dayOfCurrentSeason < middleDay){
                int dayLeftToMiddleDay = middleDay - dayOfCurrentSeason;
                double dailyTemperatureLoss = (temperature - seasonsOptions.minTemperature())/dayLeftToMiddleDay;
                temperature -= dailyTemperatureLoss;
            }else {
                int dayLeftToSummer = seasonsOptions.seasonLength() - dayOfCurrentSeason + 1;
                double dailyTemperatureIncrement = (SUMMER_TEMPERATURE - temperature)/dayLeftToSummer;
                temperature += dailyTemperatureIncrement;
            }
            double temperatureAmplitude = SUMMER_TEMPERATURE-seasonsOptions.minTemperature();
            double MAX_ENERGY_DECREASE_MULTIPLIER = 2;
            energyDecreaseMultiplier = 1 + (MAX_ENERGY_DECREASE_MULTIPLIER -1) * ((SUMMER_TEMPERATURE - temperature) / temperatureAmplitude);
        }
    }

    @Override
    public void decreaseEnergyAllAnimals(){
        List<Animal> currentAnimals = getAllAnimals();
        boolean[] isHeated = new boolean[currentAnimals.size()];
        checkIfHeated(currentAnimals, isHeated);

    }

    private void checkIfHeated(List<Animal> animals, boolean[] isHeated){
        List<Integer> sortedX = IntStream.range(0, animals.size())
                .boxed()
                .sorted(Comparator.comparingInt(id -> animals.get(id).position.getX()))
                .toList();

        checkNeighbours(animals, isHeated, sortedX);

        List<Integer> sortedY = IntStream.range(0, animals.size())
                .boxed()
                .sorted(Comparator.comparingInt(id -> animals.get(id).position.getY()))
                .toList();

        checkNeighbours(animals, isHeated, sortedY);
    }

    private void checkNeighbours(List<Animal> animals, boolean[] isHeated, List<Integer> sorted) {
        for(int i = 0; i < sorted.size()-1; i++){
            if(Vector2d.getDistance(animals.get(sorted.get(i)).position, animals.get(sorted.get(i+1)).position) <= seasonsOptions.distanceRequiredToHeat()){
                isHeated[sorted.get(i)] = true;
                isHeated[sorted.get(i+1)] = true;
            }
        }
    }

    @Override
    public void createNewPlants(){
        super.createNewPlants();
        handleEndOfADay();
    }

    public double getTemperature() {
        return temperature;
    }

    public boolean getIsWinter(){
        return isWinter;
    }

    @Override
    public Color getColorOfField(Vector2d fieldPosition){
        if (!isWinter) return super.getColorOfField(fieldPosition);
        Long plantsFrequency = plantsFrequencyCounter.getOrDefault(fieldPosition,0L);
        if (plantsFrequency < maxNumOfPlantsOnPosition*.33) return Color.LIGHTBLUE;
        if (plantsFrequency < maxNumOfPlantsOnPosition*.75) return Color.BLUE;
        return Color.DARKBLUE;
    }
}
