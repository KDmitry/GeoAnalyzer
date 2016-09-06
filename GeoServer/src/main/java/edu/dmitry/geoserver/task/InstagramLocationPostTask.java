package edu.dmitry.geoserver.task;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InstagramLocationPostTask extends Task {
    @JsonProperty("postLink")
    private String postLink;

    @JsonCreator
    public InstagramLocationPostTask(@JsonProperty("taskId") int taskId, @JsonProperty("postLink") String postLink) {
        super(taskId);
        this.postLink = postLink;
    }

    public String getPostLink() {
        return postLink;
    }
}
