package com.example.newequest.model.question;

public class NotAnswerableQuestion extends Question {

    private boolean isClosure;
    private boolean sendStatus;

    public NotAnswerableQuestion() {
        isClosure = false;
        sendStatus = false;
    }

    public boolean isClosure() {
        return isClosure;
    }

    public void setClosure(boolean closure) {
        isClosure = closure;
    }

    public boolean isSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(boolean sendStatus) {
        this.sendStatus = sendStatus;
    }
}