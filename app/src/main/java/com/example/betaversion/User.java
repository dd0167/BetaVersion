package com.example.betaversion;

public class User {

    private String UserUid; // מזהה המשתמש
    private String UserFirstName; // שם פרטי
    private String UserLastName; // שם משפחה
    private String UserAge; // גיל
    private String UserHomeAddress; // כתובת בית
    private String UserEmail; // אימייל
    private String UserPhoneNumber; // מספר טלפון
    private String UserPictureUrl; // תמונת המשתמש

    public User() {}

    public User(String userUid, String userFirstName, String userLastName, String userAge, String userHomeAddress, String userEmail, String userPhoneNumber, String userPictureUrl) {
        UserUid = userUid;
        UserFirstName = userFirstName;
        UserLastName = userLastName;
        UserAge = userAge;
        UserHomeAddress = userHomeAddress;
        UserEmail = userEmail;
        UserPhoneNumber = userPhoneNumber;
        UserPictureUrl = userPictureUrl;
    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }

    public String getUserFirstName() {
        return UserFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        UserFirstName = userFirstName;
    }

    public String getUserLastName() {
        return UserLastName;
    }

    public void setUserLastName(String userLastName) {
        UserLastName = userLastName;
    }

    public String getUserAge() {
        return UserAge;
    }

    public void setUserAge(String userAge) {
        UserAge = userAge;
    }

    public String getUserHomeAddress() {
        return UserHomeAddress;
    }

    public void setUserHomeAddress(String userHomeAddress) {
        UserHomeAddress = userHomeAddress;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return UserPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        UserPhoneNumber = userPhoneNumber;
    }

    public String getUserPictureUid() {
        return UserPictureUrl;
    }

    public void setUserPictureUid(String userPictureUrl) {
        UserPictureUrl = userPictureUrl;
    }
}
