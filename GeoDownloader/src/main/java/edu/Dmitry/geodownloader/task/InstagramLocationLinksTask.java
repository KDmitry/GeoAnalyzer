package edu.Dmitry.geodownloader.task;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InstagramLocationLinksTask extends Task {
    @JsonProperty("locationId")
    private long locationId;
    @JsonProperty("postMaxId")
    private long postMaxId;
    @JsonProperty("prevPostMaxId")
    private long prevPostMaxId;
    @JsonProperty("nextPage")
    private String nextPage;

    @JsonCreator
    public InstagramLocationLinksTask(@JsonProperty("taskId") int taskId,
                                      @JsonProperty("locationId") long locationId,
                                      @JsonProperty("postMaxId") long postMaxId,
                                      @JsonProperty("prevPostMaxId") long prevPostMaxId,
                                      @JsonProperty("nextPage") String nextPage) {
        super(taskId);
        this.locationId = locationId;
        this.postMaxId = postMaxId;
        this.prevPostMaxId = prevPostMaxId;
        this.nextPage = nextPage;
    }

    public long getLocationId() {
        return locationId;
    }

    public long getPostMaxId() {
        return postMaxId;
    }

    public long getPrevPostMaxId() {
        return prevPostMaxId;
    }

    public String getNextPage() {
        return nextPage;
    }
}