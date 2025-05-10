package com.example.newequest.model;

public class AdminReportItem {

    private String name;
    private int completeQuestionnaireCount;
    private int incompleteQuestionnaireCount;

    public AdminReportItem(String name, int completeQuestionnaireCount, int incompleteQuestionnaireCount) {
        this.name = name;
        this.completeQuestionnaireCount = completeQuestionnaireCount;
        this.incompleteQuestionnaireCount = incompleteQuestionnaireCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCompleteQuestionnaireCount() {
        return completeQuestionnaireCount;
    }

    public void setCompleteQuestionnaireCount(int completeQuestionnaireCount) {
        this.completeQuestionnaireCount = completeQuestionnaireCount;
    }

    public int getIncompleteQuestionnaireCount() {
        return incompleteQuestionnaireCount;
    }

    public void setIncompleteQuestionnaireCount(int incompleteQuestionnaireCount) {
        this.incompleteQuestionnaireCount = incompleteQuestionnaireCount;
    }
}
