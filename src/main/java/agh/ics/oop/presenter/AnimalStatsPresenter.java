package agh.ics.oop.presenter;

import agh.ics.oop.model.SimulationListener;
import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.model.RealWorldMap;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.WorldMap;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AnimalStatsPresenter {
    @FXML
    private TextField animalGenomField;
    @FXML
    private TextField activeAnimalGenomField;
    @FXML
    private TextField animalEnergyField;
    @FXML
    private TextField plantConsumedCounterField;
    @FXML
    private TextField childCounterField;
    @FXML
    private TextField descendantCounterField;
    @FXML
    private TextField livedDayCounterField;
    @FXML
    private TextField deathDayField;

    private Animal animal;
    private Simulation simulation;
    private SimulationListener listener;

    public void showAnimalStats(Animal animal,RealWorldMap worldMap, Simulation simulation){
        this.animal = animal;
        this.simulation = simulation;
        if (animal.isAlive()) deathDayField.setText("-");
        updateTextFields(worldMap, 0, true);
        listener = this::updateTextFields;
        simulation.addMapChangeListener(listener);
    }


    public void updateTextFields(WorldMap worldMap, int day, boolean isLive){
        if(!isLive) return;

        animalGenomField.setText(animal.getGen().toString());

        if (animal.isAlive()) {
            int activeGenIndex = (animal.getNumOfLivedDays()) % animal.getGen().getLenOfGen();
            activeAnimalGenomField.setText(String.valueOf(animal.getGen().getGenList().get(activeGenIndex)));
        }
        else {activeAnimalGenomField.setText("-");}

        animalEnergyField.setText(String.valueOf(animal.getEnergy()));
        plantConsumedCounterField.setText(String.valueOf(animal.getPlantConsumedCounter()));
        childCounterField.setText(String.valueOf(animal.getNumOfKids()));
        descendantCounterField.setText(String.valueOf(animal.getNumOfDescendants()));
        livedDayCounterField.setText(String.valueOf(animal.getNumOfLivedDays()));
        if (!animal.isAlive()) deathDayField.setText(String.valueOf(animal.getDeathDay()));
    }

    public void closeStatsWindow(){
        simulation.removeMapChangeListener(listener);
    }

}
