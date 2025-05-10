package com.example.newequest.model.question;

public class EditQuestion extends AnswerableQuestion {

    private static final String NAO_SABE = "Não sabe";
    private static final String NAO_RESPONDEU = "Não respondeu";
    private static final String NAO_SE_APLICA = "Não se aplica";
    private static final String NAO_SABE_VALUE = "-999";
    private static final String NAO_RESPONDEU_VALUE = "-777";
    private static final String NAO_SE_APLICA_VALUE = "-888";

    private boolean allowOnlyNumbers;
    private Integer size;

    public EditQuestion() {
    }

    public boolean isAllowOnlyNumbers() {
        return allowOnlyNumbers;
    }

    public void setAllowOnlyNumbers(boolean allowOnlyNumbers) {
        this.allowOnlyNumbers = allowOnlyNumbers;
    }

    @Override
    public void setAnswerValue(){
        String answer = this.getAnswer();

        switch (answer) {
            case NAO_SABE: this.setAnswerValue(NAO_SABE_VALUE); break;
            case NAO_RESPONDEU: this.setAnswerValue(NAO_RESPONDEU_VALUE); break;
            case NAO_SE_APLICA: this.setAnswerValue(NAO_SE_APLICA_VALUE); break;
            default: this.setAnswerValue(answer);
        }
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getSize(){
        return this.size;
    }
}