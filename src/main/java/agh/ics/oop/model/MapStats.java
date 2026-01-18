package agh.ics.oop.model;

public record MapStats(
        int animalsCount,
        int plantsCount,
        int freeFieldsCount,
        double averageEnergy,
        double averageLifespan,
        double averageChildren,
        String mostPopularGenotype,
        int p85,
        int p50
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

    public String mostPopularGenotype() { return mostPopularGenotype ;}

    public String getLabel(){
        return "animalsCount,plantsCount,freeFieldsCount,averageEnergy,averageLifespan,averageChildren,mostPopularGenotype\n";
    }

    public String getRow(){
        return animalsCountStr() + "," + plantsCountStr() + "," + freeFieldsCountStr() + "," + averageEnergyStr()
                + "," + averageLifespanStr() + "," + averageChildrenStr() + "," + mostPopularGenotype() + "\n";
    }

}