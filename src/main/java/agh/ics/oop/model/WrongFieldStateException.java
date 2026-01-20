package agh.ics.oop.model;

import java.util.List;
import java.util.stream.Collectors;

// todo: opisac bledy
public class WrongFieldStateException extends Exception {
    public WrongFieldStateException(List<String> missingFields) {
        super(String.join("\n", missingFields));
    }
}