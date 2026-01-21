package agh.ics.oop.presenter;

import agh.ics.oop.model.filesystem.PresetManager;
import agh.ics.oop.model.simulation.SimulationConfig;
import agh.ics.oop.view.SimulationLauncher;
import agh.ics.oop.view.WrongFieldStateException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainWindowPresenter implements Initializable {
    private final String CONFIG_PATH = "config";
    private final String PRESET_FILE_ENDING = "_preset.properties";
    BooleanProperty isSeasonal = new SimpleBooleanProperty(true);
    BooleanProperty isAnimalAdd = new SimpleBooleanProperty(false);

    @FXML
    private GridPane seasonalGridPane;
    @FXML
    private CheckBox isSeasonalCheckBox;
    @FXML
    private CheckBox exportCsvCheckBox;
    @FXML
    private CheckBox isAnimalAddCheckBox;
    @FXML
    private Button deletePresetsButton;
    @FXML
    private TextField savePresetsTextField;
    @FXML
    private Button savePresetsButton;
    @FXML
    private ComboBox loadPresetsComboBox;
    @FXML
    private Button loadPresetsButton;
    @FXML
    private TextField mapWidthField;
    @FXML
    private TextField mapHeightField;
    @FXML
    private TextField startPlantCountField;
    @FXML
    private TextField energyFromPlantField;
    @FXML
    private TextField plantEveryDayField;
    @FXML
    private TextField startAnimalCountField;
    @FXML
    private TextField startAnimalEnergyField;
    @FXML
    private TextField energyLossEverydayField;
    @FXML
    private TextField energyToReproduce;
    @FXML
    private TextField energyToKidField;
    @FXML
    private TextField minMutationNumField;
    @FXML
    private TextField maxMutationNumField;
    @FXML
    private TextField genLengthField;
    @FXML
    private TextField seasonLengthField;
    @FXML
    private TextField minTemperatureField;
    @FXML
    private TextField distanceRequiredToHeatField;
    @FXML
    private Button startSimulationButton;
    private final PresetManager presetManager = new PresetManager(CONFIG_PATH, PRESET_FILE_ENDING);

    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPresetsList();
        loadPresetsButton.setOnAction(e -> loadSimulationPreset());
        savePresetsButton.setOnAction(e -> saveSimulationPreset());
        deletePresetsButton.setOnAction(e -> deleteSimulationPreset());

        startSimulationButton.setOnAction(e -> startSimulation());

        if(loadPresetsComboBox.getItems().contains("default"))
        {
            loadPresetsComboBox.setValue("default");
            loadSimulationPreset();
        }

        isSeasonalCheckBox.selectedProperty().bindBidirectional(isSeasonal);
        seasonalGridPane.visibleProperty().bind(isSeasonal);
        isAnimalAddCheckBox.selectedProperty().bindBidirectional(isAnimalAdd);
    }

    private void startSimulation(){
        try{
            SimulationConfig simulationConfig = readConfig();

            new SimulationLauncher().launchSimulation(simulationConfig,exportCsvCheckBox.isSelected());
        }
        catch (NumberFormatException e){
            showAlert("Błąd walidacji", "Nieprawidłowe parametry symulacji", String.join("\n• ", "Wpisane Parametry nie są liczbami"));
        }
        catch (WrongFieldStateException e){
            showAlert("Błąd walidacji", "Nieprawidłowe parametry symulacji", e.getMessage());
        }
        catch (IOException e) {
            showAlert("Błąd","", "Nie udało się uruchomić okna symulacji");
        }
    }

    private SimulationConfig readConfig() throws WrongFieldStateException {
        return SimulationConfig.builder()
                .mapWidth(parse(mapWidthField))
                .mapHeight(parse(mapHeightField))
                .startPlantCount(parse(startPlantCountField))
                .energyFromPlant(parse(energyFromPlantField))
                .plantsPerDay(parse(plantEveryDayField))
                .startAnimalCount(parse(startAnimalCountField))
                .startAnimalEnergy(parse(startAnimalEnergyField))
                .energyLossPerDay(parse(energyLossEverydayField))
                .energyToReproduce(parse(energyToReproduce))
                .energyToKid(parse(energyToKidField))
                .minMutations(parse(minMutationNumField))
                .maxMutations(parse(maxMutationNumField))
                .genomeLength(parse(genLengthField))
                .seasonLength(parse(seasonLengthField))
                .minTemperature(parse(minTemperatureField))
                .distanceRequiredToHeat(parse(distanceRequiredToHeatField))
                .isSeasonal(isSeasonal.get())
                .isAnimalAdd(isAnimalAdd.get())
                .build();
    }

    private int parse(TextField field) throws NumberFormatException{
        return Integer.parseInt(field.getText().trim());
    }

    private void saveSimulationPreset() {
        try {
            String fileName = savePresetsTextField.getText().trim();
            if (fileName.isEmpty()) throw new Exception("Nazwa pliku nie może być pusta!");
            if (loadPresetsComboBox.getItems().contains(fileName)) throw new Exception("Preset o tej nazwie już istnieje!");

            Properties props = new Properties();
            props.setProperty("isSeasonal", Boolean.toString(isSeasonal.get()));
            props.setProperty("mapWidth", mapWidthField.getText());
            props.setProperty("mapHeight", mapHeightField.getText());
            props.setProperty("startPlantCount", startPlantCountField.getText());
            props.setProperty("energyFromPlant", energyFromPlantField.getText());
            props.setProperty("plantsPerDay", plantEveryDayField.getText());
            props.setProperty("startAnimalCount", startAnimalCountField.getText());
            props.setProperty("startAnimalEnergy", startAnimalEnergyField.getText());
            props.setProperty("energyLossPerDay", energyLossEverydayField.getText());
            props.setProperty("energyToReproduce", energyToReproduce.getText());
            props.setProperty("energyToKid", energyToKidField.getText());
            props.setProperty("minMutations", minMutationNumField.getText());
            props.setProperty("maxMutations", maxMutationNumField.getText());
            props.setProperty("genomeLength", genLengthField.getText());
            props.setProperty("seasonLength", seasonLengthField.getText());
            props.setProperty("minTemperature", minTemperatureField.getText());
            props.setProperty("distanceRequiredToHeat", distanceRequiredToHeatField.getText());

            presetManager.savePreset(fileName, props);

            if (!loadPresetsComboBox.getItems().contains(fileName)) {
                loadPresetsComboBox.getItems().add(fileName);
            }
            showAlert("Sukces", "Preset zapisany", "Zapisano preset: " + fileName);
        } catch (Exception e) {
            showAlert("Błąd zapisu", "Nie udało się zapisać presetu", e.getMessage());
        }
    }


    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void deleteSimulationPreset() {
        try {
            String selectedPreset = (String) loadPresetsComboBox.getValue();
            if (selectedPreset == null) throw new Exception("Preset nie został wybrany");

            presetManager.deletePreset(selectedPreset);
            loadPresetsComboBox.getItems().remove(selectedPreset);
            showAlert("Sukces", "Preset usunięty", "Usunięto preset: " + selectedPreset);
        } catch (Exception e) {
            showAlert("Błąd", "Nie udało się usunąć presetu", e.getMessage());
        }
    }

    private void loadSimulationPreset() {
        try {
            String selectedPreset = (String) loadPresetsComboBox.getValue();
            if (selectedPreset == null) return;

            Properties props = presetManager.loadPreset(selectedPreset);

            isSeasonal.set(Boolean.parseBoolean(props.getProperty("isSeasonal", "true")));
            mapWidthField.setText(props.getProperty("mapWidth", ""));
            mapHeightField.setText(props.getProperty("mapHeight", ""));
            startPlantCountField.setText(props.getProperty("startPlantCount", ""));
            energyFromPlantField.setText(props.getProperty("energyFromPlant", ""));
            plantEveryDayField.setText(props.getProperty("plantsPerDay", ""));
            startAnimalCountField.setText(props.getProperty("startAnimalCount", ""));
            startAnimalEnergyField.setText(props.getProperty("startAnimalEnergy", ""));
            energyLossEverydayField.setText(props.getProperty("energyLossPerDay", ""));
            energyToReproduce.setText(props.getProperty("energyToReproduce", ""));
            energyToKidField.setText(props.getProperty("energyToKid", ""));
            minMutationNumField.setText(props.getProperty("minMutations", ""));
            maxMutationNumField.setText(props.getProperty("maxMutations", ""));
            genLengthField.setText(props.getProperty("genomeLength", ""));
            seasonLengthField.setText(props.getProperty("seasonLength", ""));
            minTemperatureField.setText(props.getProperty("minTemperature", ""));
            distanceRequiredToHeatField.setText(props.getProperty("distanceRequiredToHeat", ""));

            System.out.println("Preset loaded: " + selectedPreset);
        } catch (Exception e) {
            showAlert("Błąd odczytu", "Nie udało się odczytać presetu", e.getMessage());
        }
    }


    public void loadPresetsList() {
        try {
            loadPresetsComboBox.getItems().clear();
            List<String> presets = presetManager.listPresets();
            loadPresetsComboBox.getItems().addAll(presets);
        } catch (Exception e) {
            showAlert("Błąd", "Nie udało się załadować presetów", e.getMessage());
        }
    }
}