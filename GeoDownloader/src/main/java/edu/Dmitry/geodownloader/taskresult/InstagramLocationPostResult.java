package edu.Dmitry.geodownloader.taskresult;

import edu.Dmitry.geodownloader.datamodel.InstagramPost;
import edu.Dmitry.geodownloader.exeption.TaskResultError;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InstagramLocationPostResult extends TaskResult {
    @JsonProperty("instagramPost")
    private InstagramPost instagramPost;

    @JsonCreator
    public InstagramLocationPostResult(@JsonProperty("taskId") int taskId,
                                       @JsonProperty("instagramPost") InstagramPost instagramPost,
                                       @JsonProperty("taskResultError") TaskResultError taskResultError) {
        super(taskId, taskResultError);
        this.instagramPost = instagramPost;
    }

    public InstagramPost getInstagramPost() {
        return instagramPost;
    }
}
