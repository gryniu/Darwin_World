package agh.ics.oop.model.simulation;

import agh.ics.oop.view.WrongFieldStateException;

import java.util.ArrayList;
import java.util.List;

public class SimulationConfigValidator {

    public static void validate(SimulationConfig config) throws WrongFieldStateException {
        List<String> errors = new ArrayList<>();

        if (config.mapWidth <= 1 || config.mapWidth > 100)
            errors.add("mapWidth: szerokość mapy musi być > 1 i ≤ 100");

        if (config.mapHeight <= 1 || config.mapHeight > 100)
            errors.add("mapHeight: wysokość mapy musi być > 1 i ≤ 100");

        if (config.startPlantCount < 0)
            errors.add("startPlantCount: startowa liczba roślin musi być >= 0");

        if (config.energyFromPlant <= 0)
            errors.add("energyFromPlant: energia z rośliny musi być > 0");

        if (config.plantsPerDay < 0)
            errors.add("plantsPerDay: liczba roślin na dzień nie może być ujemna");

        if (config.startAnimalCount <= 0)
            errors.add("startAnimalCount: liczba startowych zwierząt musi być > 0");

        if (config.startAnimalEnergy <= 0)
            errors.add("startAnimalEnergy: energia startowa zwierząt musi być > 0");

        if (config.energyLossPerDay < 0)
            errors.add("energyLossPerDay: dzienna utrata energii nie może być ujemna");

        if (config.energyToReproduce <= 0)
            errors.add("energyToReproduce: energia potrzebna do rozmnażania musi być > 0");

        if (config.energyToKid <= 0 || config.energyToKid > config.energyToReproduce)
            errors.add("energyToKid: energia dla potomka musi być > 0 i ≤ energii do rozmnażania");

        if (config.minMutations < 0)
            errors.add("minMutations: minimalna liczba mutacji nie może być ujemna");

        if (config.maxMutations < config.minMutations || config.maxMutations > config.genomeLength)
            errors.add("maxMutations: musi być ≥ minMutations i ≤ genomeLength");

        if (config.genomeLength <= 0)
            errors.add("genomeLength: długość genomu musi być > 0");

        if (config.isSeasonal) {
            if (config.seasonLength < 0)
                errors.add("seasonLength: długość sezonu nie może być ujemna");

            if (config.minTemperature > 30)
                errors.add("minTemperature: minimalna temperatura nie może być większa niż 30");

            if (config.distanceRequiredToHeat < 0 ||
                    config.distanceRequiredToHeat > Math.max(config.mapWidth, config.mapHeight))
                errors.add("distanceRequiredToHeat: odległość poza zakresem mapy");
        }

        if (!errors.isEmpty()) {
            throw new WrongFieldStateException(errors);
        }
    }
}
