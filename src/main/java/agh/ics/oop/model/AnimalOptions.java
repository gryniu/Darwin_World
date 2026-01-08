package agh.ics.oop.model;

public record AnimalOptions(EnergyOptions energyOptions, int mutationNum, int lenOfGen) {
    public AnimalOptions {
        if (lenOfGen <= 0) {
            throw new IllegalArgumentException("lenOfGen must be > 0");
        }
        if (mutationNum < 0) {
            throw new IllegalArgumentException("mutationNum must be >= 0");
        }
        if (mutationNum >= lenOfGen) {
            throw new IllegalArgumentException(
                    "mutationNum must be smaller than lenOfGen"
            );
        }
    }

    public AnimalOptions withEnergyOptions(EnergyOptions newEnergyOptions) {
        return new AnimalOptions(newEnergyOptions, mutationNum, lenOfGen);
    }
}
