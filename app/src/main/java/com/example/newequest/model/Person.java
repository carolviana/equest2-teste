package com.example.newequest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {

    private int id;
    private String name;
    private Boolean mainRespondent;

    public Person() {
    }

    public Person(int id, String name, Boolean mainRespondent) {
        this.id = id;
        this.name = name;
        this.mainRespondent = mainRespondent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMainRespondent() {
        return mainRespondent;
    }

    public void setMainRespondent(Boolean mainRespondent) {
        this.mainRespondent = mainRespondent;
    }

    //TODO acrescentar mainRespondent

    protected Person(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}