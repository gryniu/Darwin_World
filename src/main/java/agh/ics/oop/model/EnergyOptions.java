package agh.ics.oop.model;

public record EnergyOptions(double energyAfterPlant, double energyStart, double energyLoss, double energyFeed, double energyToKid) {
    public EnergyOptions {
        if (energyToKid>energyFeed){
            throw new IllegalArgumentException("Energy that is obligatory to reproducing must be >= than energy that is given to kid");
        }
    }
    public EnergyOptions withEnergyStart(double newEnergyStart) {
        return new EnergyOptions(energyAfterPlant, newEnergyStart, energyLoss, energyFeed, energyToKid);
    }
}
