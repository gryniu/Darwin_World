package agh.ics.oop.model;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class SeasonalWorldMap extends RealWorldMap {
    private static final double SUMMER_TEMPERATURE = 30;
    private static final double SUMMER_PLANT_NUM_MULTIPLIER = 1.5;
    private static final double SUMMER_ENERGY_FROM_PLANT_MULTIPLIER = 1.5;
    private static final double MAX_ENERGY_DECREASE_MULTIPLIER = 2;

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

    @Override
    public void decreaseEnergyAllAnimals(){
        List<Animal> currentAnimals = getAllAnimals();
        boolean[] isHeated = new boolean[currentAnimals.size()];
        checkIfHeated(currentAnimals, isHeated);
        for(int i = 0; i < currentAnimals.size(); i++){
            currentAnimals.get(i).decreaseDailyEnergy(isHeated[i] ? 1 : energyDecreaseMultiplier);
        }
    }

    private void checkIfHeated(List<Animal> animals, boolean[] isHeated) {
        List<Integer> indexes = IntStream.range(0, animals.size())
                .boxed()
                .sorted(Comparator.comparingInt(i -> animals.get(i).position.x()))
                .toList();

        HashSet<Integer> activeSet = new HashSet<>();

        for (int idx : indexes) {
            Animal current = animals.get(idx);

            activeSet.removeIf(i -> current.position.x() - animals.get(i).position.x() > seasonsOptions.distanceRequiredToHeat());

            for (int i : activeSet) {
                Animal other = animals.get(i);
                if (Vector2d.getDistance(current.position, other.position) <= seasonsOptions.distanceRequiredToHeat()) {
                    isHeated[idx] = true;
                    isHeated[i] = true;
                }
            }
            activeSet.add(idx);
        }
    }

    public void handleEndOfADay(){
        super.handleEndOfADay();
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

            energyDecreaseMultiplier = 1 + (MAX_ENERGY_DECREASE_MULTIPLIER -1) * ((SUMMER_TEMPERATURE - temperature) / temperatureAmplitude);
        }
    }
}
