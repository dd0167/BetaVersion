package com.example.betaversion;

import android.os.Parcel;
import android.os.Parcelable;

public class List implements Parcelable {

    private String ListName; // שם הרשימה
    private String ListCreationDate; // תאריך יצירת הרשימה

    public List() {}

    public List(String listName, String listCreationDate) {
        ListName = listName;
        ListCreationDate = listCreationDate;
    }

    protected List(Parcel in) {
        ListName = in.readString();
        ListCreationDate = in.readString();
    }

    public static final Creator<List> CREATOR = new Creator<List>() {
        @Override
        public List createFromParcel(Parcel in) {
            return new List(in);
        }

        @Override
        public List[] newArray(int size) {
            return new List[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ListName);
        dest.writeString(ListCreationDate);
    }
}
