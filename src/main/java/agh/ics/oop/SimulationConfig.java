package agh.ics.oop;

import agh.ics.oop.model.WrongFieldStateException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class SimulationConfig {

    public final int mapWidth;
    public final int mapHeight;
    public final int startPlantCount;
    public final int energyFromPlant;
    public final int plantsPerDay;
    public final int startAnimalCount;
    public final int startAnimalEnergy;
    public final int energyLossPerDay;
    public final int energyToReproduce;
    public final int energyToKid;
    public final int minMutations;
    public final int maxMutations;
    public final int genomeLength;
    public final int seasonLength;
    public final int minTemperature;
    public final int distanceRequiredToHeat;

    private SimulationConfig(Builder b) {
        this.mapWidth = b.mapWidth;
        this.mapHeight = b.mapHeight;
        this.startPlantCount = b.startPlantCount;
        this.energyFromPlant = b.energyFromPlant;
        this.plantsPerDay = b.plantsPerDay;
        this.startAnimalCount = b.startAnimalCount;
        this.startAnimalEnergy = b.startAnimalEnergy;
        this.energyLossPerDay = b.energyLossPerDay;
        this.energyToReproduce = b.energyToReproduce;
        this.energyToKid = b.energyToKid;
        this.minMutations = b.minMutations;
        this.maxMutations = b.maxMutations;
        this.genomeLength = b.genomeLength;
        this.seasonLength = b.seasonLength;
        this.minTemperature = b.minTemperature;
        this.distanceRequiredToHeat = b.distanceRequiredToHeat;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int mapWidth;
        private int mapHeight;
        private int startPlantCount;
        private int energyFromPlant;
        private int plantsPerDay;
        private int startAnimalCount;
        private int startAnimalEnergy;
        private int energyLossPerDay;
        private int energyToReproduce;
        private int energyToKid;
        private int minMutations;
        private int maxMutations;
        private int genomeLength;
        private int seasonLength;
        private int minTemperature;
        private int distanceRequiredToHeat;

        public Builder mapWidth(int v) { mapWidth = v; return this; }
        public Builder mapHeight(int v) { mapHeight = v; return this; }
        public Builder startPlantCount(int v) { startPlantCount = v; return this; }
        public Builder energyFromPlant(int v) { energyFromPlant = v; return this; }
        public Builder plantsPerDay(int v) { plantsPerDay = v; return this; }
        public Builder startAnimalCount(int v) { startAnimalCount = v; return this; }
        public Builder startAnimalEnergy(int v) { startAnimalEnergy = v; return this; }
        public Builder energyLossPerDay(int v) { energyLossPerDay = v; return this; }
        public Builder energyToReproduce(int v) { energyToReproduce = v; return this; }
        public Builder energyToKid(int v) { energyToKid = v; return this; }
        public Builder minMutations(int v) { minMutations = v; return this; }
        public Builder maxMutations(int v) { maxMutations = v; return this; }
        public Builder genomeLength(int v) { genomeLength = v; return this; }
        public Builder seasonLength(int v){ seasonLength = v; return this; }
        public Builder minTemperature(int v){ minTemperature = v; return this; }
        public Builder distanceRequiredToHeat(int v){ distanceRequiredToHeat = v; return this; }

        public SimulationConfig build() throws WrongFieldStateException {
            validate();
            return new SimulationConfig(this);
        }

        private void validate() throws WrongFieldStateException {
            List<String> missingFields = new ArrayList<>();

            // todo: porobic ograniczenia na pola
            if (mapWidth <= 0 || mapWidth>100) missingFields.add("mapWidth");
            if (mapHeight <= 0 || mapHeight>100) missingFields.add("mapHeight");
            if (startPlantCount < 0 || startPlantCount>(mapHeight*mapWidth - startAnimalCount)) missingFields.add("startPlantCount");
            if (energyFromPlant <= 0) missingFields.add("energyFromPlant");
            if (plantsPerDay < 0) missingFields.add("plantsPerDay");
            if (startAnimalCount <= 0) missingFields.add("startAnimalCount");
            if (startAnimalEnergy <= 0) missingFields.add("startAnimalEnergy");
            if (energyLossPerDay < 0) missingFields.add("energyLossPerDay");
            if (energyToReproduce <= 0) missingFields.add("energyToFeed");
            if (energyToKid <= 0 || energyToKid>energyToReproduce) missingFields.add("energyToKid");
            if (minMutations < 0) missingFields.add("minMutations");
            if (maxMutations < minMutations || maxMutations > genomeLength) missingFields.add("maxMutations");
            if (genomeLength <= 0) missingFields.add("genomeLength");
            if (seasonLength < 0) missingFields.add("seasonLength");
            if (minTemperature > 30) missingFields.add("minTemperature"); // todo: maxTemperatura to 30, zrobic lepsze errory
            if (distanceRequiredToHeat < 0 || distanceRequiredToHeat > Math.max(mapWidth,mapHeight)) missingFields.add("distanceRequiredToHeat");

            if (!missingFields.isEmpty()) {
                throw new WrongFieldStateException(missingFields);
            }
        }
    }
}