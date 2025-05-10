package com.example.newequest.model.question;

import java.util.ArrayList;

public class MultipleChoiceQuestion extends AnswerableQuestion {
    private ArrayList<String> options;
    private String OpOther;

    private static final String NAO_SABE = "Não sabe";
    private static final String NAO_RESPONDEU = "Não respondeu";
    private static final String NAO_SE_APLICA = "Não se aplica";
    private static final String NAO_SABE_VALUE = "-999";
    private static final String NAO_RESPONDEU_VALUE = "-777";
    private static final String NAO_SE_APLICA_VALUE = "-888";

    public MultipleChoiceQuestion() {
        answers = new ArrayList<>();
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public void addOption(String option){
        this.options.add(option);
    }

    public String getOpOther() {
        return OpOther;
    }

    public void setOpOther(String opOther) {
        this.OpOther = opOther;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    @Override
    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;

        if(options!=null) {
            this.answersValue = new ArrayList<>();
            for (String zero : options) {
                if ((!zero.equals(NAO_SE_APLICA))
                        && (!zero.equals(NAO_RESPONDEU))
                        && (!zero.equals(NAO_SABE))) {
                    answersValue.add("0");
                }
            }
        }
    }

    public ArrayList<String> getAnswersValue() {
        return answersValue;
    }

    @Override
    public void setAnswersValue(ArrayList<String> answersValue) {
        this.answersValue = answersValue;
    }

    public void addAnswer(String answer){
        switch (answer) {
            case NAO_SABE:
                this.answers.clear();
                this.answers.add(NAO_SABE);
                this.answersValue.clear();
                this.answersValue.add(NAO_SABE_VALUE);
                this.setAnswerValue(NAO_SABE_VALUE);
                break;

            case NAO_RESPONDEU:
                this.answers.clear();
                this.answers.add(NAO_RESPONDEU);
                this.answersValue.clear();
                this.answersValue.add(NAO_RESPONDEU_VALUE);
                this.setAnswerValue(NAO_RESPONDEU_VALUE);
                break;

            case NAO_SE_APLICA:
                this.answers.clear();
                this.answers.add(NAO_SE_APLICA);
                this.answersValue.clear();
                this.answersValue.add(NAO_SE_APLICA_VALUE);
                this.setAnswerValue(NAO_SE_APLICA_VALUE);
                break;

            default:
                this.answers.add(answer);
                answersValue.set(options.indexOf(answer), String.valueOf(options.indexOf(answer) + 1));
                StringBuilder sb = new StringBuilder();
                for (String s : answersValue){
                    sb.append(s);
                }
                this.setAnswerValue(sb.toString());
        }
    }

    public void removeAnswer(String answer){
        if((answer.equals(NAO_SABE)) || (answer.equals(NAO_RESPONDEU)) || (answer.equals(NAO_SE_APLICA))){
            this.answers.clear();
            this.answersValue.clear();
            for(String zero : options){
                if((!zero.equals(NAO_SE_APLICA))
                        && (!zero.equals(NAO_RESPONDEU))
                        && (!zero.equals(NAO_SABE))) {
                    answersValue.add("0");
                }
            }
            this.answerValue = "";
        }else {
            this.answers.remove(answer);
            answersValue.set(options.indexOf(answer), "0");
            StringBuilder sb = new StringBuilder();
            for (String s : answersValue) {
                sb.append(s);
            }
            this.setAnswerValue(sb.toString());
        }
    }
}