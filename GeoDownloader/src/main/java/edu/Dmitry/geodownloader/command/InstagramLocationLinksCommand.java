package edu.Dmitry.geodownloader.command;

import edu.Dmitry.geodownloader.api.InstagramApi;
import edu.Dmitry.geodownloader.exeption.InstagramApiExeption;
import edu.Dmitry.geodownloader.exeption.TaskResultError;
import edu.Dmitry.geodownloader.task.InstagramLocationLinksTask;
import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.InstagramLocationLinksResult;
import edu.Dmitry.geodownloader.taskresult.TaskResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstagramLocationLinksCommand implements Command {
    private InstagramApi instagramApi = new InstagramApi();
    private static Logger logger = LogManager.getRootLogger();

    @Override
    public TaskResult execute(Task task) {
        InstagramLocationLinksTask instagramLocationLinksTask = (InstagramLocationLinksTask) task;
        Map<Long, String> postsLinks;
        long maxPostId = instagramLocationLinksTask.getPostMaxId();
        StringBuilder nextPage = new StringBuilder(instagramLocationLinksTask.getNextPage() != null ?
                instagramLocationLinksTask.getNextPage() : "");
        try {
            boolean needSavePostMaxId = instagramLocationLinksTask.getNextPage() == null;

            postsLinks = instagramApi.getLastPostsLinks(instagramLocationLinksTask.getLocationId(), maxPostId,
                    instagramLocationLinksTask.getPrevPostMaxId(), nextPage);

            List<String> list;

            if (postsLinks.size() > 0) {
                list = new ArrayList<>(postsLinks.values());
                if (needSavePostMaxId) {
                    maxPostId = postsLinks.entrySet().iterator().next().getKey();
                }
            } else {
                list = new ArrayList<>();
            }

            return new InstagramLocationLinksResult(instagramLocationLinksTask.getTaskId(), maxPostId,
                    instagramLocationLinksTask.getPrevPostMaxId(),
                    !nextPage.toString().equals("") ? nextPage.toString() : null,
                    list, null);

        } catch (InstagramApiExeption instagramApiExeption) {
            logger.error(instagramApiExeption.getMessage());
            return new InstagramLocationLinksResult(instagramLocationLinksTask.getTaskId(), -1, -1, null, null,
                    new TaskResultError(instagramApiExeption.getInternalException() != null
                            ? instagramApiExeption.getInternalException().toString()
                            : instagramApiExeption.getMessage()));
        }
    }
}
