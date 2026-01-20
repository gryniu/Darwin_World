package agh.ics.oop.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

public class PresetManager {
    private final String configPath;
    private final String presetEnding;

    public PresetManager(String configPath, String presetEnding) {
        this.configPath = configPath;
        this.presetEnding = presetEnding;
        ensureConfigDirectory();
    }

    private void ensureConfigDirectory() {
        File dir = new File(configPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public List<String> listPresets() throws IOException {
        File dir = new File(configPath);
        if (!dir.exists()) return List.of();

        return Files.list(dir.toPath())
                .filter(p -> p.getFileName().toString().endsWith(presetEnding))
                .map(p -> p.getFileName().toString().replace(presetEnding, ""))
                .toList();
    }

    public Properties loadPreset(String name) throws IOException {
        File file = new File(configPath, name + presetEnding);
        if (!file.exists()) throw new IOException("Preset not found: " + name);
        Properties props = new Properties();
        try (var fis = new FileInputStream(file)) {
            props.load(fis);
        }
        return props;
    }

    public void savePreset(String name, Properties props) throws IOException {
        File file = new File(configPath, name + presetEnding);
        try (var fos = new FileOutputStream(file)) {
            props.store(fos, "Simulation preset saved by user");
        }
    }

    public void deletePreset(String name) throws IOException {
        File file = new File(configPath, name + presetEnding);
        if (!file.exists()) throw new IOException("Preset not found: " + name);
        if (!file.delete()) throw new IOException("Cannot delete preset: " + name);
    }
}
