package agh.ics.oop.model;

import javafx.scene.paint.Color;

import java.util.List;

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

        for (int i = 0; i<currentAnimals.size(); i++){
            boolean isHeated = false;
            for (int j = i+1; j<currentAnimals.size(); j++){
                if (Vector2d.getDistance(currentAnimals.get(i).position(), currentAnimals.get(j).position()) <= seasonsOptions.distanceRequiredToHeat()){
                    isHeated = true;
                    break;
                }
            }
            if(isHeated){
                currentAnimals.get(i).decreaseDailyEnergy(1);
            } else{
                currentAnimals.get(i).decreaseDailyEnergy(energyDecreaseMultiplier);
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
}
