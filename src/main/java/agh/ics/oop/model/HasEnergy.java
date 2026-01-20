package agh.ics.oop.model;

import javafx.scene.paint.Color;

public interface HasEnergy {
    int getEnergy();

    default double getEnergyRatio(int median, int percentile85) {
        if (percentile85 <= median) return 0;

        double k = Math.log(9) / (percentile85 - median);
        return 1.0 / (1.0 + Math.exp(-k * (getEnergy() - median)));
    }

    default Color getEnergyColor(int softCap) {
        int energy = getEnergy();

        if (energy < softCap * .15) return Color.RED;
        if (energy < softCap * .3) return Color.YELLOW;
        if (energy < softCap * .5) return Color.ORANGE;
        if (energy < softCap * .75) return Color.LIMEGREEN;
        return Color.DARKGREEN;
    }
}
