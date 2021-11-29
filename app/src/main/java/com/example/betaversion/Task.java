package com.example.betaversion;

public class Task {

    private String TaskName; // שם המטלה
    private String TaskAddress; // כתובת ביצוע המטלה
    private String TaskDay; // יום ביצוע המטלה
    private String TaskHour; // שעת ביצוע המטלה
    private String TaskCreationDate; // תאריך יצירת המטלה
    private String TaskNotes; // הערות
    private String TaskColor; // צבע המטלה
    private String TaskPictureUid; // תמונת המטלה

    public Task() {}

    public Task(String taskName, String taskAddress, String taskDay, String taskHour, String taskCreationDate, String taskNotes, String taskColor, String taskPictureUid) {
        TaskName = taskName;
        TaskAddress = taskAddress;
        TaskDay = taskDay;
        TaskHour = taskHour;
        TaskCreationDate = taskCreationDate;
        TaskNotes = taskNotes;
        TaskColor = taskColor;
        TaskPictureUid = taskPictureUid;
    }

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getTaskAddress() {
        return TaskAddress;
    }

    public void setTaskAddress(String taskAddress) {
        TaskAddress = taskAddress;
    }

    public String getTaskDay() {
        return TaskDay;
    }

    public void setTaskDay(String taskDay) {
        TaskDay = taskDay;
    }

    public String getTaskHour() {
        return TaskHour;
    }

    public void setTaskHour(String taskHour) {
        TaskHour = taskHour;
    }

    public String getTaskCreationDate() {
        return TaskCreationDate;
    }

    public void setTaskCreationDate(String taskCreationDate) {
        TaskCreationDate = taskCreationDate;
    }

    public String getTaskNotes() {
        return TaskNotes;
    }

    public void setTaskNotes(String taskNotes) {
        TaskNotes = taskNotes;
    }

    public String getTaskColor() {
        return TaskColor;
    }

    public void setTaskColor(String taskColor) {
        TaskColor = taskColor;
    }

    public String getTaskPictureUid() {
        return TaskPictureUid;
    }

    public void setTaskPictureUid(String taskPictureUid) {
        TaskPictureUid = taskPictureUid;
    }
}
