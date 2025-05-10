package com.example.newequest.model.question;

import java.util.ArrayList;

public class GeoLocationQuestion extends AnswerableQuestion {

    public GeoLocationQuestion() {
        answers = new ArrayList<>();
        answersValue = new ArrayList<>();

        answersValue.add("0");
        answersValue.add("0");
        //[0]->LATITUDE
        //[1]->LONGITUDE
    }

    @Override
    public void setAnswers(ArrayList<String> answers) {
        this.answers.clear();
        this.answers = answers;
        setAnswersValue(answers);
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    @Override
    public void setAnswersValue(ArrayList<String> answersValue) {
        this.answersValue.clear();
        this.answersValue = answersValue;
        setAnswerValue();
    }

    @Override
    public ArrayList<String> getAnswersValue() {
        return super.getAnswersValue();
    }

    @Override
    public void setAnswerValue() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getAnswersValue().get(0));
        sb.append("|");
        sb.append(this.getAnswersValue().get(1));
        this.answerValue = sb.toString();
    }
}
