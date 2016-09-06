package edu.dmitry.geoserver.datamodel;

import javafx.beans.property.SimpleStringProperty;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class DownloaderTableData {
    private SimpleStringProperty downloaderName;
    private SimpleStringProperty downloaderUuid;
    private SimpleStringProperty tasksCount;
    private SimpleStringProperty workTime;
    private DateTime startWorkTime;

    public DownloaderTableData(String downloaderName, String downloaderUuid, long tasksCount, DateTime startWorkTime) {
        this.startWorkTime = startWorkTime;
        this.downloaderName = new SimpleStringProperty(downloaderName);
        this.downloaderUuid = new SimpleStringProperty(downloaderUuid);
        this.tasksCount = new SimpleStringProperty(String.valueOf(tasksCount));
        this.workTime = new SimpleStringProperty(getWorkTime());
    }

    public String getDownloaderName() {
        return downloaderName.get();
    }

    public SimpleStringProperty downloaderNameProperty() {
        return downloaderName;
    }

    public void setDownloaderName(String downloaderName) {
        this.downloaderName.set(downloaderName);
    }

    public String getTasksCount() {
        return tasksCount.get();
    }

    public SimpleStringProperty tasksCountProperty() {
        return tasksCount;
    }

    public void setTasksCount(String tasksCount) {
        this.tasksCount.set(tasksCount);
    }

    public String getWorkTime() {
        Period period = new Period(startWorkTime, new DateTime());
        int days = period.getYears() * 365 + period.getMonths() * 30 + period.getWeeks() * 7 + period.getDays();
        return String.format("%02d:%02d:%02d:%02d", days, period.getHours(), period.getMinutes(), period.getSeconds());
    }

    public SimpleStringProperty workTimeProperty() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime.set(workTime);
    }

    public String getDownloaderUuid() {
        return downloaderUuid.get();
    }

    public SimpleStringProperty downloaderUuidProperty() {
        return downloaderUuid;
    }

    public void setDownloaderUuid(String downloaderUuid) {
        this.downloaderUuid.set(downloaderUuid);
    }
}
