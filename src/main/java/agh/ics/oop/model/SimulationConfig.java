package agh.ics.oop.model;

import java.util.ArrayList;
import java.util.List;

public class SimulationConfig {
    public final boolean isSeasonal;
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
        this.isSeasonal = b.isSeasonal;
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
        private boolean isSeasonal;
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

        public Builder isSeasonal(boolean v) {isSeasonal = v; return this;}
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
            List<String> errors = new ArrayList<>();

            // todo: porobic ograniczenia na pola
            if (mapWidth <= 1 || mapWidth > 100)
                errors.add("mapWidth: szerokość mapy musi być > 1 i ≤ 100");

            if (mapHeight <= 1 || mapHeight > 100)
                errors.add("mapHeight: wysokość mapy musi być > 1 i ≤ 100");

            if (startPlantCount < 0)
                errors.add("startPlantCount: startowa liczba roślin musi być >= 0");

            if (energyFromPlant <= 0)
                errors.add("energyFromPlant: energia z rośliny musi być > 0");

            if (plantsPerDay < 0)
                errors.add("plantsPerDay: liczba roślin na dzień nie może być ujemna");

            if (startAnimalCount <= 0)
                errors.add("startAnimalCount: liczba startowych zwierząt musi być > 0");

            if (startAnimalEnergy <= 0)
                errors.add("startAnimalEnergy: energia startowa zwierząt musi być > 0");

            if (energyLossPerDay < 0)
                errors.add("energyLossPerDay: dzienna utrata energii nie może być ujemna");

            if (energyToReproduce <= 0)
                errors.add("energyToFeed: energia potrzebna do rozmnażania musi być > 0");

            if (energyToKid <= 0 || energyToKid > energyToReproduce)
                errors.add("energyToKid: energia dla potomka musi być > 0 i ≤ energii do rozmnażania");

            if (minMutations < 0)
                errors.add("minMutations: minimalna liczba mutacji nie może być ujemna");

            if (maxMutations < minMutations || maxMutations > genomeLength)
                errors.add("maxMutations: musi być ≥ minMutations i ≤ genomeLength");

            if (genomeLength <= 0)
                errors.add("genomeLength: długość genomu musi być > 0");

            if (isSeasonal) {
                if (seasonLength < 0)
                    errors.add("seasonLength: długość sezonu nie może być ujemna");

                if (minTemperature > 30)
                    errors.add("minTemperature: minimalna temperatura nie może być większa niż 30");

                if (distanceRequiredToHeat < 0 || distanceRequiredToHeat > Math.max(mapWidth, mapHeight))
                    errors.add("distanceRequiredToHeat: odległość poza zakresem mapy");
            } else {
                seasonLength = 0;
                minTemperature = 0;
                distanceRequiredToHeat = 0;
            }

            if (!errors.isEmpty()) {
                throw new WrongFieldStateException(errors);
            }
        }

    }
}