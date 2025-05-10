package com.example.newequest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Dependency implements Parcelable {
    private String dependencyID;
    private String dependencyValue;
    //private Boolean replication;

    public Dependency() {
    }

    public String getDependencyID() {
        return dependencyID;
    }

    public void setDependencyID(String dependencyID) {
        this.dependencyID = dependencyID;
    }

    public String getDependencyValue() {
        return dependencyValue;
    }

    public void setDependencyValue(String dependencyValue) {
        this.dependencyValue = dependencyValue;
    }

    /*public Boolean getReplication() {
        return replication;
    }*/

    /*public void setReplication(Boolean replication) {
        this.replication = replication;
    }*/

    public Boolean hasValue() {
        return dependencyValue != null;
    }

    /*public Boolean hasReplication() {
        return replication != null;
    }*/

    protected Dependency(Parcel in) {
        dependencyID = in.readString();
        dependencyValue = in.readString();
        //byte replicationVal = in.readByte();
        //replication = replicationVal == 0x02 ? null : replicationVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dependencyID);
        dest.writeString(dependencyValue);
        /*if (replication == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (replication ? 0x01 : 0x00));
        }*/
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Dependency> CREATOR = new Parcelable.Creator<Dependency>() {
        @Override
        public Dependency createFromParcel(Parcel in) {
            return new Dependency(in);
        }

        @Override
        public Dependency[] newArray(int size) {
            return new Dependency[size];
        }
    };
}
