package com.example.betaversion;

public class TasksDay {

    private String TasksDayName; // שם היום
    private String TasksDayDate; // יום ביצוע המטלות

    public TasksDay() {}

    public TasksDay(String tasksDayName, String tasksDayDate) {
        TasksDayName = tasksDayName;
        TasksDayDate = tasksDayDate;
    }

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
}
