package com.example.betaversion;

public class List {

    private String ListName; // שם הרשימה
    private String ListCreationDate; // תאריך יצירת הרשימה

    public List() {}

    public List(String listName, String listCreationDate) {
        ListName = listName;
        ListCreationDate = listCreationDate;
    }

    public String getListName() {
        return ListName;
    }

    public void setListName(String listName) {
        ListName = listName;
    }

    public String getListCreationDate() {
        return ListCreationDate;
    }

    public void setListCreationDate(String listCreationDate) {
        ListCreationDate = listCreationDate;
    }
}
