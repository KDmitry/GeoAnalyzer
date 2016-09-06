package edu.Dmitry.geodownloader.command;

import edu.Dmitry.geodownloader.api.InstagramApi;
import edu.Dmitry.geodownloader.datamodel.InstagramPost;
import edu.Dmitry.geodownloader.exeption.InstagramApiExeption;
import edu.Dmitry.geodownloader.exeption.TaskResultError;
import edu.Dmitry.geodownloader.task.InstagramLocationPostTask;
import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.InstagramLocationPostResult;
import edu.Dmitry.geodownloader.taskresult.TaskResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class InstagramLocationPostCommand implements Command {
    private InstagramApi instagramApi = new InstagramApi();
    private static Logger logger = LogManager.getRootLogger();

    @Override
    public TaskResult execute(Task task) {
        InstagramLocationPostTask instagramLocationPostTask = (InstagramLocationPostTask) task;
        InstagramPost instagramPost;
        try {
            instagramPost = instagramApi.getInstagramPost(instagramLocationPostTask.getPostLink());
            return new InstagramLocationPostResult(instagramLocationPostTask.getTaskId(), instagramPost, null);
        } catch (InstagramApiExeption instagramApiExeption) {
            logger.error(instagramApiExeption.getMessage());
            return new InstagramLocationPostResult(instagramLocationPostTask.getTaskId(), null,
                    new TaskResultError(instagramApiExeption.getInternalException() != null
                    ? instagramApiExeption.getInternalException().getMessage()
                    : instagramApiExeption.getMessage()));
        }
    }
}
