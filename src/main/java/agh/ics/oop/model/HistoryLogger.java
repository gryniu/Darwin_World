package agh.ics.oop.model;

public class HistoryLogger implements SimulationListener {

    @Override
    public void change(WorldMap worldMap, int day, boolean isLive){
        if (isLive && worldMap instanceof RealWorldMap worldMap1) {
            //zapisywanie animali
            HistoryFileHandler.writeToFile("/%s-%d-animals.txt".formatted(worldMap1.getId(), day), worldMap.getAllAnimals());

            //zapisywanie roslin
            HistoryFileHandler.writeToFile("/%s-%d-plants.txt".formatted(worldMap1.getId(), day), worldMap.getPlants());

            //zapisywanie statystyk
            HistoryFileHandler.writeToFile("/%s-%d-stats.txt".formatted(worldMap1.getId(), day), worldMap.getMapStats().toDataString());
        }
    }
}
