package com.example.betaversion;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {

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

    protected Task(Parcel in) {
        TaskName = in.readString();
        TaskAddress = in.readString();
        TaskDay = in.readString();
        TaskHour = in.readString();
        TaskCreationDate = in.readString();
        TaskNotes = in.readString();
        TaskColor = in.readString();
        TaskPictureUid = in.readString();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TaskName);
        dest.writeString(TaskAddress);
        dest.writeString(TaskDay);
        dest.writeString(TaskHour);
        dest.writeString(TaskCreationDate);
        dest.writeString(TaskNotes);
        dest.writeString(TaskColor);
        dest.writeString(TaskPictureUid);
    }
}
