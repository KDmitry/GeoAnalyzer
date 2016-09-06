package edu.Dmitry.geodownloader.command;

import edu.Dmitry.geodownloader.task.Task;
import edu.Dmitry.geodownloader.taskresult.TaskResult;

interface Command
{
    TaskResult execute(Task task);
}
