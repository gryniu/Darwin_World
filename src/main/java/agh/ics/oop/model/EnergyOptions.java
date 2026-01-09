package agh.ics.oop.model;

public record EnergyOptions(double energyFromPlant, double energyStart, double dailyEnergyLoss, double energyToReproduce, double energyToKid) {
    public EnergyOptions {
        if (energyToKid> energyToReproduce){
            throw new IllegalArgumentException("Energy that is obligatory to reproducing must be >= than energy that is given to kid");
        }
    }
    public EnergyOptions withEnergyStart(double newEnergyStart) {
        return new EnergyOptions(energyFromPlant, newEnergyStart, dailyEnergyLoss, energyToReproduce, energyToKid);
    }
}
