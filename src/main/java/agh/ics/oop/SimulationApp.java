package agh.ics.oop;
// todo: kod wygenerowany, pozmieniac, tak samo jak presenter i simulation
import agh.ics.oop.model.HistoryFileHandler;
import agh.ics.oop.presenter.MainWindowPresenter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimulationApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("Main.fxml"));

        VBox viewRoot = loader.load();
        MainWindowPresenter presenter = loader.getController();
        // Konfiguracja
        configureStage(primaryStage, viewRoot);
        primaryStage.setOnCloseRequest(windowEvent -> {
            HistoryFileHandler.clearHistory();
        });

        // Wyświetlanie
        primaryStage.show();
    }

    private void configureStage(Stage primaryStage, VBox viewRoot) {
        // stworzenie sceny (panelu do wyświetlania wraz zawartoscia z FXML)
        var scene = new Scene(viewRoot);

        // ustawienie sceny w oknie
        primaryStage.setScene(scene);

        // konfiguracja okna
        primaryStage.setTitle("Darwin World");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
    }
}