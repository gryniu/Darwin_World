package agh.ics.oop.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PresetsManager {
    public static List<Path> getPresetsList() throws IOException, URISyntaxException {
        URL url = PresetsManager.class.getClassLoader().getResource("config");
        if (url != null) {
            Path path = Paths.get(url.toURI());
            return Files.list(path).filter(p -> p.toString().endsWith("preset.properties")).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public static void loadPreset(Path path){
        Properties userParams = new Properties();

        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            userParams.load(fis);

            String startDate = userParams.getProperty("startDate");
            String endDate = userParams.getProperty("endDate");
            String filterRegion = userParams.getProperty("filterRegion");
            boolean showTop10 = Boolean.parseBoolean(userParams.getProperty("showTop10"));

            System.out.println("Start: " + startDate);
            System.out.println("End: " + endDate);
            System.out.println("Region: " + filterRegion);
            System.out.println("Top 10: " + showTop10);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
