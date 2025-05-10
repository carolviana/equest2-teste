package com.example.newequest.model.question;

import java.util.ArrayList;

public class OnlyOneChoiceQuestion extends AnswerableQuestion {
    private ArrayList<String> options;
    private String opOther;

    private static final String NAO_SABE = "Não sabe";
    private static final String NAO_RESPONDEU = "Não respondeu";
    private static final String NAO_SE_APLICA = "Não se aplica";
    private static final String NAO_SABE_VALUE = "-999";
    private static final String NAO_RESPONDEU_VALUE = "-777";
    private static final String NAO_SE_APLICA_VALUE = "-888";

    public OnlyOneChoiceQuestion() {
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void addOption(String option){
        this.options.add(option);
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String getOpOther() {
        return opOther;
    }

    public void setOpOther(String opOther) {
        this.opOther = opOther;
    }

    @Override
    public void setAnswerValue(){
        if(options!=null) {
            String answer = this.getAnswer();

            if (opOther != null) {
                if (answer.startsWith(opOther)) {
                    this.setAnswerValue(answer);
                    return;
                }
            }

            switch (answer) {
                case NAO_SABE:
                    this.setAnswerValue(NAO_SABE_VALUE);
                    break;
                case NAO_RESPONDEU:
                    this.setAnswerValue(NAO_RESPONDEU_VALUE);
                    break;
                case NAO_SE_APLICA:
                    this.setAnswerValue(NAO_SE_APLICA_VALUE);
                    break;
                default:
                    int position = options.indexOf(answer) + 1;
                    if (position != 0) {
                        this.setAnswerValue(Integer.toString(position));
                    }
            }
        }else{
            super.setAnswerValue();
        }
    }
}