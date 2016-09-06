package edu.Dmitry.geodownloader.command;

import edu.Dmitry.geodownloader.task.InstagramLocationLinksTask;
import edu.Dmitry.geodownloader.task.InstagramLocationPostTask;
import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.InstagramLocationLinksResult;
import edu.Dmitry.geodownloader.taskresult.TaskResult;

public class CommandExecutor
{
    private CommandExecutor() {
    }

    public static TaskResult execute(Task task) {
        if (task instanceof InstagramLocationPostTask) {
            return new InstagramLocationPostCommand().execute(task);
        } else if (task instanceof InstagramLocationLinksTask) {
            return new InstagramLocationLinksCommand().execute(task);
        }
        return null;
    }

}
