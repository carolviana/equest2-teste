package com.example.newequest.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class QuestionnaireDownloadEntry implements Parcelable {
    private String id;
    private String name;
    private String city;
    private String date;
    private String interviewer;

    public QuestionnaireDownloadEntry(String id, String name, String city, String date, String interviewer) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.date = date;
        this.interviewer = interviewer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
    }

    @Override
    public String toString() {
        return "QuestionnaireDownloadEntry{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", date='" + date + '\'' +
                ", interviewer='" + interviewer + '\'' +
                '}';
    }

    protected QuestionnaireDownloadEntry(Parcel in){
        this.id = in.readString();
        this.name = in.readString();
        this.city = in.readString();
        this.date = in.readString();
        this.date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(city);
        parcel.writeString(date);
        parcel.writeString(interviewer);
    }

    public static final Parcelable.Creator<QuestionnaireDownloadEntry> CREATOR = new Creator<QuestionnaireDownloadEntry>() {
        @Override
        public QuestionnaireDownloadEntry createFromParcel(Parcel parcel) {
            return new QuestionnaireDownloadEntry(parcel);
        }

        @Override
        public QuestionnaireDownloadEntry[] newArray(int i) {
            return new QuestionnaireDownloadEntry[i];
        }
    };


}
