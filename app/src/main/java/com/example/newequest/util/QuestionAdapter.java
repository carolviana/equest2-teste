package com.example.newequest.util;

import com.example.newequest.model.question.AnswerableQuestion;
import com.example.newequest.model.question.EditQuestion;
import com.example.newequest.model.question.MultipleChoiceQuestion;
import com.example.newequest.model.question.NotAnswerableQuestion;
import com.example.newequest.model.question.OnlyOneChoiceQuestion;
import com.example.newequest.model.question.PersonCreatorQuestion;
import com.example.newequest.model.question.Question;
import com.example.newequest.model.question.RowQuestion;
import com.example.newequest.model.question.SpinnerChoiceQuestion;
import com.example.newequest.model.question.Table;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class QuestionAdapter extends TypeAdapter<Question> {

    private static final String NOT_ANSWERABLE_TYPE = "NotAnswerableQuestion";
    private static final String EDIT_TYPE = "EditQuestion";
    private static final String MULTIPLE_CHOICE_TYPE = "MultipleChoiceQuestion";
    private static final String ONLY_ONE_CHOICE_TYPE = "OnlyOneChoiceQuestion";
    private static final String PERSON_CREATOR_TYPE = "PersonCreatorQuestion";
    private static final String SPINNER_CHOICE_TYPE = "SpinnerChoiceQuestion";
    private static final String ROW_TYPE = "RowQuestion";
    private static final String TABLE_TYPE = "Table";

    @Override
    public void write(JsonWriter out, Question value) throws IOException {

    }

    @Override
    public Question read(JsonReader in) throws IOException {
        return null;
    }

    private String convertFromQuestionToString(Question question) {
        Gson gson = new Gson();
        switch (question.getType()) {
            case NOT_ANSWERABLE_TYPE:
                NotAnswerableQuestion notAnswerableQuestion = (NotAnswerableQuestion) question;
                return gson.toJson(notAnswerableQuestion, NotAnswerableQuestion.class);
            case EDIT_TYPE:
                EditQuestion editQuestion = (EditQuestion) question;
                return gson.toJson(editQuestion, EditQuestion.class);
            case MULTIPLE_CHOICE_TYPE:
                MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
                return gson.toJson(multipleChoiceQuestion, MultipleChoiceQuestion.class);
            case ONLY_ONE_CHOICE_TYPE:
                OnlyOneChoiceQuestion onlyOneChoiceQuestion = (OnlyOneChoiceQuestion) question;
                return gson.toJson(onlyOneChoiceQuestion, OnlyOneChoiceQuestion.class);
            case PERSON_CREATOR_TYPE:
                PersonCreatorQuestion personCreatorQuestion = (PersonCreatorQuestion) question;
                return gson.toJson(personCreatorQuestion, PersonCreatorQuestion.class);
            case SPINNER_CHOICE_TYPE:
                SpinnerChoiceQuestion spinnerChoiceQuestion = (SpinnerChoiceQuestion) question;
                return gson.toJson(spinnerChoiceQuestion, SpinnerChoiceQuestion.class);
            case ROW_TYPE:
                RowQuestion rowQuestion = (RowQuestion) question;
                return gson.toJson(rowQuestion, RowQuestion.class);
            case TABLE_TYPE:
                Table table = (Table) question;
                return gson.toJson(table, Table.class);
            default:
                return gson.toJson(question, AnswerableQuestion.class);
        }
    }
}