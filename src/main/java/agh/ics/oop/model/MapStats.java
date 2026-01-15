package agh.ics.oop.model;

public record MapStats(
        int animalsCount,
        int plantsCount,
        int freeFieldsCount,
        double averageEnergy,
        double averageLifespan,
        double averageChildren,
        String mostPopularGenotype
) {
    public String animalsCountStr() {
        return String.valueOf(animalsCount);
    }

    public String plantsCountStr() {
        return String.valueOf(plantsCount);
    }

    public String freeFieldsCountStr() {
        return String.valueOf(freeFieldsCount);
    }

    public String averageEnergyStr() {
        return String.format("%.2f", averageEnergy);
    }

    public String averageLifespanStr() {
        return String.format("%.2f", averageLifespan);
    }

    public String averageChildrenStr() {
        return String.format("%.2f", averageChildren);
    }

    @Override
    public String toString() {
        return "animalsCount %d\nplantsCount %d\nfreeFieldsCount %d\naverageEnergy %.2f\naverageLifespan %.2f\naverageChildren %.2f\nmostPopularGenotype %s\n"
            .formatted(
                animalsCount,
                plantsCount,
                freeFieldsCount,
                averageEnergy,
                averageLifespan,
                averageChildren,
                mostPopularGenotype
        );
    }

}
