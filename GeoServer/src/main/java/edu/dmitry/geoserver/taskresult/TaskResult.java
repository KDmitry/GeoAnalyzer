package edu.dmitry.geoserver.taskresult;

import edu.dmitry.geoserver.exeption.TaskResultError;
import org.codehaus.jackson.annotate.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InstagramLocationPostResult.class, name = "InstagramLocationPostResult"),
        @JsonSubTypes.Type(value = InstagramLocationLinksResult.class, name = "InstagramLocationLinksResult")
})
@JsonIgnoreProperties(ignoreUnknown=true)
public class TaskResult {
    @JsonProperty("taskId")
    private int taskId;
    @JsonProperty("taskResultError")
    private TaskResultError taskResultError;

    @JsonCreator
    public TaskResult(@JsonProperty("taskId") int taskId, @JsonProperty("taskResultError") TaskResultError taskResultError) {
        this.taskId = taskId;
        this.taskResultError = taskResultError;
    }

    public int getTaskId() {
        return taskId;
    }

    public TaskResultError getTaskResultError() {
        return taskResultError;
    }

    public boolean isSuccess() {
       return taskResultError == null;
    }
}
