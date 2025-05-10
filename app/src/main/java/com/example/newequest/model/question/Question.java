package com.example.newequest.model.question;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Ignore;

import com.example.newequest.model.Dependency;

import java.util.ArrayList;

public class Question implements Cloneable, Parcelable {
    protected String id;
    protected String title;
    protected String type;
    protected String behavior;
    protected boolean showEnes;
    protected boolean showDontKnow;
    protected boolean showDontAnswer;
    protected boolean showDontAply;
    protected ArrayList<Dependency> dependencies;
    protected int respondent;
    protected String replication;
    protected String reference;
    protected Boolean replicationStatus;

    public Question() {
        replicationStatus = false;
    }

    public Question(String id, String title, String type, String behavior, boolean showEnes, ArrayList<Dependency> dependencies, String replication, int respondent) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.behavior = behavior;
        this.showEnes = showEnes;
        this.dependencies = dependencies;
        this.replication = replication;
        this.respondent = respondent;
        replicationStatus = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBehavior() {
        return behavior;
    }

    public Boolean isReplica(){
        return replicationStatus;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public boolean isShowEnes() {
        return showEnes;
    }

    public void setShowEnes(boolean showEnes) {
        this.showEnes = showEnes;
    }

    public boolean isShowDontKnow() {
        return showDontKnow;
    }

    public void setShowDontKnow(boolean showDontKnow) {
        this.showDontKnow = showDontKnow;
    }

    public boolean isShowDontAnswer() {
        return showDontAnswer;
    }

    public void setShowDontAnswer(boolean showDontAnswer) {
        this.showDontAnswer = showDontAnswer;
    }

    public boolean isShowDontAply() {
        return showDontAply;
    }

    public void setShowDontAply(boolean showDontAply) {
        this.showDontAply = showDontAply;
    }

    public ArrayList<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(ArrayList<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public int getRespondent() {
        return respondent;
    }

    public void setRespondent(int respondent) {
        this.respondent = respondent;
    }

    public void setReplicationStatus(Boolean status) {
        replicationStatus = status;
    }

    public String getReplication() {
        return replication;
    }

    public void setReplication(String replication) {
        this.replication = replication;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean hasReference(){
        return reference != null;
    }

    public Boolean hasReplication() {
        return replication != null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected Question(Parcel in) {
        id = in.readString();
        title = in.readString();
        type = in.readString();
        behavior = in.readString();
        showEnes = in.readByte() != 0x00;
        showDontKnow = in.readByte() != 0x00;
        showDontAnswer = in.readByte() != 0x00;
        showDontAply = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            dependencies = new ArrayList<Dependency>();
            in.readList(dependencies, Dependency.class.getClassLoader());
        } else {
            dependencies = null;
        }
        //respondent = (Person) in.readValue(Person.class.getClassLoader());
        respondent = in.readInt();
        replication = in.readString();//TODO verificar se está certo!
        reference = in.readString();
        replicationStatus = in.readByte() != 0x00;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(behavior);
        dest.writeByte((byte) (showEnes ? 0x01 : 0x00));
        dest.writeByte((byte) (showDontKnow ? 0x01 : 0x00));
        dest.writeByte((byte) (showDontAnswer ? 0x01 : 0x00));
        dest.writeByte((byte) (showDontAply ? 0x01 : 0x00));
        if (dependencies == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(dependencies);
        }
        dest.writeInt(respondent);
        dest.writeString(replication);//TODO verificar se está certo! (replication e respondent)
        dest.writeString(reference);
        dest.writeByte((byte) (replicationStatus ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}