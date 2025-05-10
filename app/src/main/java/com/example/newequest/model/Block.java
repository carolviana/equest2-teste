package com.example.newequest.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.newequest.model.question.Question;

import java.util.ArrayList;

public class Block implements Parcelable {
    private String title;
    private ArrayList<Question> questions;

    public Block() {
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLastQuestionIndex(){
        return (questions.size()-1);
    }

    protected Block(Parcel in) {
        title = in.readString();
        if (in.readByte() == 0x01) {
            questions = new ArrayList<Question>();
            in.readList(questions, Question.class.getClassLoader());
        } else {
            questions = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (questions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(questions);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Block> CREATOR = new Parcelable.Creator<Block>() {
        @Override
        public Block createFromParcel(Parcel in) {
            return new Block(in);
        }

        @Override
        public Block[] newArray(int size) {
            return new Block[size];
        }
    };
}
