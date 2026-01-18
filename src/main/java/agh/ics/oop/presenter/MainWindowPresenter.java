package agh.ics.oop.presenter;

import agh.ics.oop.simulation.SimulationConfig;
import agh.ics.oop.model.WrongFieldStateException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainWindowPresenter implements Initializable {
    private final String CONFIG_PATH = "config";
    private final String PRESET_FILE_ENDING = "_preset.properties";
    BooleanProperty isSeasonal = new SimpleBooleanProperty(true);

    @FXML
    private GridPane seasonalGridPane;
    @FXML
    private CheckBox isSeasonalCheckBox;
    @FXML
    private CheckBox exportCsvCheckBox;
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
    }

    private void startSimulation(){
        try{
            SimulationConfig simulationConfig = readConfig();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Simulation.fxml"));
            BorderPane viewRoot = loader.load();
            SimulationPresenter presenter = loader.getController();

            Stage stage = new Stage();
            stage.setOnCloseRequest(event -> {
                presenter.closeSimulation();
            });
            configureStage(stage, viewRoot);
            stage.show();

            presenter.startSimulation(simulationConfig, exportCsvCheckBox.isSelected());
        }
        catch (NumberFormatException e){
            showNumberFormatExceptionAlert(e);
        }
        catch (WrongFieldStateException e){
            showValidationErrorAlert(e);
        }
        catch (IOException e) {
            showIOExceptionAlert(e);
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
                .build();
    }

    private int parse(TextField field) throws NumberFormatException{
        return Integer.parseInt(field.getText().trim());
    }



    private void showValidationErrorAlert(WrongFieldStateException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd walidacji");
        alert.setHeaderText("Nieprawidłowe parametry symulacji");
        String errorMessage = String.join("\n• ", e.getErrors());

        alert.setContentText("Błędy wystąpiły na polach:\n• " + errorMessage);

        alert.showAndWait();
    }

    private void showIOExceptionAlert(IOException e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText("Nie udało się uruchomić okna symulacji");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void showNumberFormatExceptionAlert(NumberFormatException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd walidacji");
        alert.setHeaderText("Nieprawidłowe parametry symulacji");

        String errorMessage = String.join("\n• ", "Wpisane Parametry nie są liczbami");

        alert.setContentText(errorMessage);

        alert.showAndWait();
    }

    private void configureStage(Stage primaryStage, BorderPane viewRoot) {
        // stworzenie sceny (panelu do wyświetlania wraz zawartoscia z FXML)
        var scene = new Scene(viewRoot);

        // ustawienie sceny w oknie
        primaryStage.setScene(scene);

        // konfiguracja okna
        primaryStage.setTitle("Simulation app");
    }

    private void saveSimulationPreset() {
        try {
            String fileName = savePresetsTextField.getText().trim();
            if (fileName.isEmpty()) throw new Exception("File name cannot be empty!");
            if (loadPresetsComboBox.getItems().contains(fileName)) throw new Exception("File with name %s already exists!".formatted(fileName));

            Properties props = new Properties();
            readConfig();

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


            File dir = new File(CONFIG_PATH);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, fileName + PRESET_FILE_ENDING);

            try (var fos = new java.io.FileOutputStream(file)) {
                props.store(fos, "Simulation preset saved by user");
                System.out.println("Preset saved to: " + file.getName());
            }

            if (!loadPresetsComboBox.getItems().contains(fileName)) {
                loadPresetsComboBox.getItems().add(fileName);
            }

        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd zapisu");
            alert.setHeaderText("Nie udało się zapisać presetu symulacji");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void deleteSimulationPreset() {
        try {
            String selectedPreset = (String)loadPresetsComboBox.getValue();
            if (selectedPreset == null)  throw new Exception("Preset not selected");;

            File dir = new File(CONFIG_PATH);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, selectedPreset + PRESET_FILE_ENDING);
            file.delete();

            loadPresetsComboBox.getItems().remove(selectedPreset);
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Nie udało się usunąć");
            alert.setHeaderText("Nie udało się odczytać presetu symulacji");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void loadSimulationPreset() {
        try {
            String selectedPreset = (String)loadPresetsComboBox.getValue();
            if (selectedPreset == null)  throw new Exception("Preset not selected");;

            File dir = new File(CONFIG_PATH);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, selectedPreset + PRESET_FILE_ENDING);

            Properties props = new Properties();
            try (var fis = new java.io.FileInputStream(file)) {
                props.load(fis);
            }

            // wczytujemy wartości do pól
            isSeasonal.set(Boolean.parseBoolean(props.getProperty("isSeasonal", "true")));
            System.out.println(isSeasonal);
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
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd odczytu");
            alert.setHeaderText("Nie udało się odczytać presetu symulacji");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void loadPresetsList() {
        try {
            File dir = new File(CONFIG_PATH);
            if (!dir.exists()) dir.mkdirs();

            loadPresetsComboBox.getItems().clear();

            Files.list(dir.toPath())
                    .filter(p -> p.getFileName().toString().endsWith(PRESET_FILE_ENDING))
                    .forEach(preset -> {
                        String fileName = preset.getFileName().toString();
                        fileName = fileName.substring(0, fileName.length() - PRESET_FILE_ENDING.length());
                        loadPresetsComboBox.getItems().add(fileName);
                    });
        } catch (Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Nie udało się załadować presetów");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}