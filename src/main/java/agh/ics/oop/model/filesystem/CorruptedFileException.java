package agh.ics.oop.model.filesystem;

public class CorruptedFileException extends RuntimeException {
    public CorruptedFileException(String message) {
        super("File " + message + " is corrupted!");
    }
}
