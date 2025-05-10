package com.example.newequest.model.question;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class AnswerableQuestion extends Question {
    @NonNull
    protected String answer = "";
    protected String answerValue;

    protected ArrayList<String> answers;
    protected ArrayList<String> answersValue;


    public AnswerableQuestion() {
    }

    @NonNull
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(@NonNull String answer) {
        this.answer = answer;
        setAnswerValue();
    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    public void setAnswerValue(){
        this.answerValue = answer;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public ArrayList<String> getAnswersValue() {
        return answersValue;
    }

    public void setAnswersValue(ArrayList<String> answersValue) {
        this.answersValue = answersValue;
    }
}
