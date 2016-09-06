package edu.dmitry.geoserver.exeption;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class TaskResultError implements Serializable{
    @JsonProperty("message")
    private String message;

    @JsonCreator
    public TaskResultError(@JsonProperty("message") String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
