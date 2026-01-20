package agh.ics.oop.model;

public class EnergyComponent {
    private int energy;
    private final EnergyOptions options;

    public EnergyComponent(int energy, EnergyOptions energyOptions) {
        this.energy = energy;
        this.options = energyOptions;
    }
    public boolean canReproduce() {
        return energy >= options.energyToReproduce();
    }

    public int giveEnergyToKid() {
        int given = Math.min(energy, options.energyToKid());
        energy -= given;
        return given;
    }

    public void eat(double multiplier) {
        energy += (int)(options.energyFromPlant() * multiplier);
    }

    public void dailyLoss(double multiplier) {
        energy = Math.max(0, energy - (int)(options.dailyEnergyLoss() * multiplier));
    }

    public int getEnergy() {
        return energy;
    }

}
