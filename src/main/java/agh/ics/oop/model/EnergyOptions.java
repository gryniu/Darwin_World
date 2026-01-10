package agh.ics.oop.model;

public record EnergyOptions(int energyFromPlant, int dailyEnergyLoss, int energyToReproduce, int energyToKid) {
    public EnergyOptions {
        if (energyToKid> energyToReproduce){
            throw new IllegalArgumentException("Energy that is obligatory to reproducing must be >= than energy that is given to kid");
        }
    }
}
