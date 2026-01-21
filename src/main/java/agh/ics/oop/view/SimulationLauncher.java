package agh.ics.oop.view;


import agh.ics.oop.model.simulation.SimulationConfig;
import agh.ics.oop.presenter.SimulationPresenter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationLauncher {
    public void launchSimulation(SimulationConfig simulationConfig, boolean exportCsv) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Simulation.fxml"));
        BorderPane viewRoot = loader.load();
        SimulationPresenter presenter = loader.getController();

        Stage stage = new Stage();
        stage.setOnCloseRequest(event -> presenter.closeSimulation());
        configureStage(stage, viewRoot);
        stage.show();

        presenter.startSimulation(simulationConfig, exportCsv);
    }

    private void configureStage(Stage stage, BorderPane viewRoot) {
        Scene scene = new Scene(viewRoot);
        stage.setScene(scene);
        stage.setTitle("Simulation app");
    }
}
