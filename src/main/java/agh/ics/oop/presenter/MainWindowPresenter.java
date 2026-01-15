package agh.ics.oop.presenter;

import agh.ics.oop.SimulationConfig;
import agh.ics.oop.model.WrongFieldStateException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowPresenter implements Initializable {
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
    private TextField genLenghtField;
    @FXML
    private TextField seasonLengthField;
    @FXML
    private TextField minTemperatureField;
    @FXML
    private TextField distanceRequiredToHeatField;

    @FXML
    private Button startSimulationButton;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startSimulationButton.setOnAction(e -> startSimulation());
    }

    private void startSimulation(){
        try{
            SimulationConfig simulationConfig = readConfig();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Simulation.fxml"));
            BorderPane viewRoot = loader.load();
            SimulationPresenter presenter = loader.getController();

            Stage stage = new Stage();
            configureStage(stage, viewRoot);
            stage.setOnCloseRequest(windowEvent -> {
                presenter.endSimulation();
            });
            stage.show();

            presenter.startSimulation(simulationConfig);
        }
        catch (NumberFormatException e){
            showNumberFormatExceptionAlert(e);
        }
        catch (WrongFieldStateException e){
            showValidationErrorAlert(e);
        }
        catch (IOException e) {
            e.printStackTrace();
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
                .genomeLength(parse(genLenghtField))
                .seasonLength(parse(seasonLengthField))
                .minTemperature(parse(minTemperatureField))
                .distanceRequiredToHeat(parse(distanceRequiredToHeatField))
                .build();
    }

    private int parse(TextField field) throws NumberFormatException{
        return Integer.parseInt(field.getText().trim());
    }

    private void resetFieldStyles() {
        TextField[] allFields = {
                mapWidthField, mapHeightField, startPlantCountField,
                energyFromPlantField, plantEveryDayField, startAnimalCountField,
                startAnimalEnergyField, energyLossEverydayField, energyToReproduce,
                energyToKidField, minMutationNumField, maxMutationNumField, genLenghtField
        };

        for (TextField field : allFields) {
            field.setStyle("");
        }
    }

    private void showValidationErrorAlert(WrongFieldStateException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd walidacji");
        alert.setHeaderText("Nieprawidłowe parametry symulacji");
        resetFieldStyles();
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

        resetFieldStyles();

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
        //todo: ustawic szerokosc i wysokosc okienka
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
    }

}