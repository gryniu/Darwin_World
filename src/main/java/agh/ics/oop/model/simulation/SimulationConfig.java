package agh.ics.oop.model.simulation;

import agh.ics.oop.view.WrongFieldStateException;

public class SimulationConfig {
    public final boolean isSeasonal;
    public final boolean isAnimalAdd;
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

    private SimulationConfig(Builder b) throws WrongFieldStateException {
        this.isSeasonal = b.isSeasonal;
        this.isAnimalAdd = b.isAnimalAdd;
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
        SimulationConfigValidator.validate(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean isSeasonal;
        private boolean isAnimalAdd;
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

        public Builder isSeasonal(boolean v) { isSeasonal = v; return this; }
        public Builder isAnimalAdd(boolean v) { isAnimalAdd = v; return this; };
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
            return new SimulationConfig(this);
        }


    }
}