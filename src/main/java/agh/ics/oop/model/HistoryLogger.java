package agh.ics.oop.model;

public class HistoryLogger implements SimulationListener {

    @Override
    public void change(WorldMap worldMap, MapStats mapStats, int day, boolean isLive){
        if (isLive && worldMap instanceof RealWorldMap realWorldMap) {
            //zapisywanie animali
            HistoryFileHandler.writeToFile("/%s-%d-animals.txt".formatted(realWorldMap.getId(), day), worldMap.getAllAnimals());

            //zapisywanie roslin
            HistoryFileHandler.writeToFile("/%s-%d-plants.txt".formatted(realWorldMap.getId(), day), worldMap.getPlants());
        }
    }
}
