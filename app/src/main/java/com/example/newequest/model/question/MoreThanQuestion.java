package com.example.newequest.model.question;

public class MoreThanQuestion extends AnswerableQuestion {
    private int value;
    private final String TRUE = "true";
    private final String FALSE = "false";

    private static final String NAO_SABE = "999";
    private static final String NAO_RESPONDEU = "777";
    private static final String NAO_SE_APLICA = "888";

    public MoreThanQuestion() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setAnswer(){
        if(getReference().equals(NAO_RESPONDEU) || getReference().equals(NAO_SE_APLICA) || getReference().equals(NAO_SABE)){
            return;
        }else {
            try {
                if (Integer.parseInt(this.getReference()) > value) {
                    this.answer = TRUE;
                    setAnswerValue();
                } else {
                    this.answer = FALSE;
                    setAnswerValue();
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.answer = this.getReference();
                setAnswerValue();
                //TODO melhorar...
            }
        }
    }

    @Override
    public void setAnswerValue(){
        if (answer.equals(TRUE)) {
            this.setAnswerValue("Maior de " + value);
        }else if (answer.equals(FALSE)){
            this.setAnswerValue("Menor de " + value);
        }else{
            this.setAnswerValue(this.getReference());
        }
    }
}