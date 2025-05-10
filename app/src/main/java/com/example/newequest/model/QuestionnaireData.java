package com.example.newequest.model;

import java.util.ArrayList;

public class QuestionnaireData {
    private ArrayList<Answer> answers = new ArrayList<>();

    private int primaryKey;
    private boolean isComplete = false;
    private String dateTime;
    private String endTime;
    private double elapsedTimeInSeconds;
    private String interviewer;
    private String interviewerName;
    private String location;
    private String peopleString;
    private String jsonVersion;
    private boolean sentIncomplete = false;

    public QuestionnaireData() {
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getElapsedTimeInSeconds() {
        return elapsedTimeInSeconds;
    }

    public void setElapsedTimeInSeconds(double elapsedTimeInSeconds) {
        this.elapsedTimeInSeconds = elapsedTimeInSeconds;
    }

    public String getInterviewer() {
        return interviewer;
    }

    public void setInterviewer(String interviewer) {
        this.interviewer = interviewer;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPeopleString() {
        return peopleString;
    }

    public void setPeopleString(String peopleString) {
        this.peopleString = peopleString;
    }

    public String getJsonVersion() {
        return jsonVersion;
    }

    public void setJsonVersion(String jsonVersion) {
        this.jsonVersion = jsonVersion;
    }

    public boolean isSentIncomplete() {
        return sentIncomplete;
    }

    public void setSentIncomplete(boolean sentIncomplete) {
        this.sentIncomplete = sentIncomplete;
    }
}
