package com.example.newequest.model.question;

import java.util.ArrayList;

public class SpinnerChoiceQuestion extends AnswerableQuestion {
    private ArrayList<String> options;
    private String OpOther;

    private static final String NAO_SABE = "Não sabe";
    private static final String NAO_RESPONDEU = "Não respondeu";
    private static final String NAO_SE_APLICA = "Não se aplica";
    private static final String NAO_SABE_VALUE = "-999";
    private static final String NAO_RESPONDEU_VALUE = "-777";
    private static final String NAO_SE_APLICA_VALUE = "-888";

    public SpinnerChoiceQuestion() {
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

    @Override
    public void setAnswerValue(){
        String answer = this.getAnswer();
        switch (answer) {
            case NAO_SABE: this.setAnswerValue(NAO_SABE_VALUE); break;
            case NAO_RESPONDEU: this.setAnswerValue(NAO_RESPONDEU_VALUE); break;
            case NAO_SE_APLICA: this.setAnswerValue(NAO_SE_APLICA_VALUE); break;
            default:
                int position = options.indexOf(answer) + 1;
                this.setAnswerValue(Integer.toString(position));
        }
    }
}