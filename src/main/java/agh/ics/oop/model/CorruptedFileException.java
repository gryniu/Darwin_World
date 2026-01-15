package agh.ics.oop.model;

public class CorruptedFileException extends RuntimeException {
    public CorruptedFileException(String message) {
        super("File " + message + " is corrupted!");
    }
}
