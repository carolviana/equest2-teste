package com.example.newequest.model;

public class NewQuestionnaireEntry {
    private int primaryKey;
    private boolean isNew;

    public NewQuestionnaireEntry(int primaryKey, boolean isNew) {
        this.primaryKey = primaryKey;
        this.isNew = isNew;
    }

    public NewQuestionnaireEntry() {
    }

    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
