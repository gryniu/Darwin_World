package agh.ics.oop.model;

import java.util.List;

// todo: opisac bledy
public class WrongFieldStateException extends Exception {
    private final List<String> missingFields;
    public WrongFieldStateException(List<String> missingFields) {
        super("Missing or invalid fields: " + missingFields);
        this.missingFields = missingFields;
    }

    public List<String> getErrors(){
        return missingFields;
    }
}