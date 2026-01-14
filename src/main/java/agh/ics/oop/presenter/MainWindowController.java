package agh.ics.oop.presenter;

import agh.ics.oop.SimulationConfig;
import agh.ics.oop.model.WrongFieldStateException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindowController {
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
    private TextField energyFeedField;
    @FXML
    private TextField energyToKidField;
    @FXML
    private TextField minMutationNumField;
    @FXML
    private TextField maxMutationNumField;
    @FXML
    private TextField genLenghtField;

    @FXML
    private Button startSimulationButton;

    @FXML
    public void initialize() {
        startSimulationButton.setOnAction(e -> startSimulation());
    }

    private void startSimulation(){
        try{
            SimulationConfig simulationConfig = readConfig();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/agh/ics/oop/view/SimulationWindow.fxml"));
            Parent root = loader.load();

            SimulationController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Simulation");
            stage.setScene(new Scene(root));
            stage.show();

            controller.startSimulation(simulationConfig);

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
                .energyToFeed(parse(energyFeedField))
                .energyToKid(parse(energyToKidField))
                .minMutations(parse(minMutationNumField))
                .maxMutations(parse(maxMutationNumField))
                .genomeLength(parse(genLenghtField))
                .build();
    }

    private int parse(TextField field) throws NumberFormatException{
        return Integer.parseInt(field.getText().trim());
    }

    private void resetFieldStyles() {
        TextField[] allFields = {
                mapWidthField, mapHeightField, startPlantCountField,
                energyFromPlantField, plantEveryDayField, startAnimalCountField,
                startAnimalEnergyField, energyLossEverydayField, energyFeedField,
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

}
