package edu.dmitry.geoserver.task;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InstagramLocationPostTask.class, name = "InstagramLocationPostTask"),
        @JsonSubTypes.Type(value = InstagramLocationLinksTask.class, name = "InstagramLocationLinksTask")
})
abstract public class Task {
    @JsonProperty("taskId")
    private int taskId;

    @JsonCreator
    public Task(@JsonProperty("taskId") int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }
}
