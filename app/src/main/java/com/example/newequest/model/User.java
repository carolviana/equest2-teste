package com.example.newequest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class User implements Parcelable {

    private String userId;
    private String name;
    private String email;
    private int type; //type = 0 -> interviewer; type = 1 -> local admin; type = 2 -> statistician;
    private String password;
    private String city;
    private boolean active;


    public User() {
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, int type, String password, String city, boolean active) {
        this.name = name;
        this.email = email;
        this.type = type;
        this.password = password;
        this.city = city;
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected User(Parcel in) {
        userId = in.readString();
        name = in.readString();
        email = in.readString();
        type = in.readInt();
        password = in.readString();
        city = in.readString();
        active = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeInt(type);
        dest.writeString(password);
        dest.writeString(city);
        dest.writeByte((byte) (active ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
