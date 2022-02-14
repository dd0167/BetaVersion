package com.example.betaversion;

import android.os.Parcel;
import android.os.Parcelable;

public class TasksDay implements Parcelable {

    private String TasksDayName; // שם היום
    private String TasksDayDate; // יום ביצוע המטלות

    public TasksDay() {}

    public TasksDay(String tasksDayName, String tasksDayDate) {
        TasksDayName = tasksDayName;
        TasksDayDate = tasksDayDate;
    }

    protected TasksDay(Parcel in) {
        TasksDayName = in.readString();
        TasksDayDate = in.readString();
    }

    public static final Creator<TasksDay> CREATOR = new Creator<TasksDay>() {
        @Override
        public TasksDay createFromParcel(Parcel in) {
            return new TasksDay(in);
        }

        @Override
        public TasksDay[] newArray(int size) {
            return new TasksDay[size];
        }
    };

    public String getTasksDayName() {
        return TasksDayName;
    }

    public void setTasksDayName(String tasksDayName) {
        TasksDayName = tasksDayName;
    }

    public String getTasksDayDate() {
        return TasksDayDate;
    }

    public void setTasksDayDate(String tasksDayDate) {
        TasksDayDate = tasksDayDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TasksDayName);
        dest.writeString(TasksDayDate);
    }
}
