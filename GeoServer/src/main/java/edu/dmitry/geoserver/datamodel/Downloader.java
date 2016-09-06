package edu.dmitry.geoserver.datamodel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class Downloader {
    private String name;
    private String uuid;
    private List<Integer> tasks = new ArrayList<>();
    private long doneTasksCount;
    private DateTime startTasksDateTime;
    private DateTime startWorkTime;

    public Downloader(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
        startWorkTime = new DateTime();
    }

    public String getName() {
        return name;
    }

    public List<Integer> getTasks() {
        return tasks;
    }

    public void setTasks(List<Integer> tasks) {
        this.tasks = tasks;
    }

    public DateTime getStartTasksDateTime() {
        return startTasksDateTime;
    }

    public void setStartTasksDateTime(DateTime dateTime) {
        startTasksDateTime = dateTime;
    }

    public long getDoneTasksCount() {
        return doneTasksCount;
    }

    public void setDoneTasksCount(long doneTasksCount) {
        this.doneTasksCount += doneTasksCount;
    }

    public DateTime getStartWorkTime() {
        return startWorkTime;
    }

    public void setStartWorkTime(DateTime startWorkTime) {
        this.startWorkTime = startWorkTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
