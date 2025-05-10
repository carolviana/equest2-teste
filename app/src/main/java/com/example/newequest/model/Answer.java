package com.example.newequest.model;

import java.util.ArrayList;

public class Answer {
    protected String id;
    protected String type;
    protected int respondent;
    protected String answer;
    protected ArrayList<String> answers;
    protected String answerValue;


    public Answer() {
    }

    public Answer(String id, String type, int respondent, String answer, ArrayList<String> answers, String answerValue) {
        this.id = id;
        this.type = type;
        this.respondent = respondent;
        this.answer = answer;
        this.answers = answers;
        this.answerValue = answerValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRespondent() {
        return respondent;
    }

    public void setRespondent(int respondent) {
        this.respondent = respondent;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }
}
