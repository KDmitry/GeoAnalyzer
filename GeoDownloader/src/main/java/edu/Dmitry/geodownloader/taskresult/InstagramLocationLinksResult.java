package edu.Dmitry.geodownloader.taskresult;

import edu.Dmitry.geodownloader.exeption.TaskResultError;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class InstagramLocationLinksResult extends TaskResult{
    @JsonProperty("postMaxId")
    private long postMaxId;
    @JsonProperty("prevPostMaxId")
    private long prevPostMaxId;
    @JsonProperty("nextPage")
    private String nextPage;
    @JsonProperty("postsLinks")
    private List<String> postsLinks;

    @JsonCreator
    public InstagramLocationLinksResult(@JsonProperty("taskId") int taskId,
                                        @JsonProperty("postMaxId")long postMaxId,
                                        @JsonProperty("prevPostMaxId") long prevPostMaxId,
                                        @JsonProperty("nextPage") String nextPage,
                                        @JsonProperty("postsLinks") List<String> postsLinks,
                                        @JsonProperty("taskResultError") TaskResultError taskResultError){
        super(taskId, taskResultError);
        this.postMaxId = postMaxId;
        this.prevPostMaxId = prevPostMaxId;
        this.nextPage = nextPage;
        this.postsLinks = postsLinks;
    }

    public long getPostMaxId() {
        return postMaxId;
    }

    public List<String> getPostsLinks() {
        return postsLinks;
    }

    public String getNextPage() {
        return nextPage;
    }

    public long getPrevPostMaxId() {
        return prevPostMaxId;
    }
}