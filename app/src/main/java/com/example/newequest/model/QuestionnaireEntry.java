package com.example.newequest.model;

import com.example.newequest.model.question.Question;

public class QuestionnaireEntry {
    private int primaryKey;
    private Person mainRespondent;
    private boolean isComplete;
    private String dateTime;
    private Question lastQuestion;

//    public QuestionnaireEntry(int primaryKey, Person mainRespondent, boolean isComplete, String dateTime) {
//        this.primaryKey = primaryKey;
//        this.mainRespondent = mainRespondent;
//        this.isComplete = isComplete;
//        this.dateTime = dateTime;
//    }

    public QuestionnaireEntry(int primaryKey, Person mainRespondent, boolean isComplete, String dateTime, Question lastQuestion) {
        this.primaryKey = primaryKey;
        this.mainRespondent = mainRespondent;
        this.isComplete = isComplete;
        this.dateTime = dateTime;
        this.lastQuestion = lastQuestion;
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Person getMainRespondent() {
        return mainRespondent;
    }

    public void setMainRespondent(Person mainRespondent) {
        this.mainRespondent = mainRespondent;
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

    public Question getLastQuestion() {
        return lastQuestion;
    }

    public void setLastQuestion(Question lastQuestion) {
        this.lastQuestion = lastQuestion;
    }
}
