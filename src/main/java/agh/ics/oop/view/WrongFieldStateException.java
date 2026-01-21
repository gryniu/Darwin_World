package agh.ics.oop.view;

import java.util.List;

public class WrongFieldStateException extends Exception {
    public WrongFieldStateException(List<String> missingFields) {
        super(String.join("\n", missingFields));
    }
}