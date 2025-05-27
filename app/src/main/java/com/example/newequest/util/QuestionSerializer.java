package com.example.newequest.util;

import com.example.newequest.model.question.AnswerableQuestion;
import com.example.newequest.model.question.CityCheckQuestion;
import com.example.newequest.model.question.EditQuestion;
import com.example.newequest.model.question.GeoLocationQuestion;
import com.example.newequest.model.question.ListQuestion;
import com.example.newequest.model.question.MoreThanQuestion;
import com.example.newequest.model.question.MultipleChoiceQuestion;
import com.example.newequest.model.question.NotAnswerableQuestion;
import com.example.newequest.model.question.OnlyOneChoiceQuestion;
import com.example.newequest.model.question.PersonCreatorQuestion;
import com.example.newequest.model.question.Question;
import com.example.newequest.model.question.RowQuestion;
import com.example.newequest.model.question.SpinnerChoiceQuestion;
import com.example.newequest.model.question.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class QuestionSerializer implements JsonSerializer<Question> {
    private static final String NOT_ANSWERABLE_TYPE = "NotAnswerableQuestion";
    private static final String EDIT_TYPE = "EditQuestion";
    private static final String MULTIPLE_CHOICE_TYPE = "MultipleChoiceQuestion";
    private static final String ONLY_ONE_CHOICE_TYPE = "OnlyOneChoiceQuestion";
    private static final String PERSON_CREATOR_TYPE = "PersonCreatorQuestion";
    private static final String SPINNER_CHOICE_TYPE = "SpinnerChoiceQuestion";
    private static final String ROW_TYPE = "RowQuestion";
    private static final String TABLE_TYPE = "Table";
    private static final String GEO_LOCATION_TYPE = "GeoLocationQuestion";
    private static final String LIST_TYPE = "ListQuestion";
    private static final String MORE_THAN_TYPE = "MoreThanQuestion";
    private static final String CITY_CHECK_TYPE = "CityCheckQuestion";

    @Override
    public JsonElement serialize(Question question, Type typeOfSrc, JsonSerializationContext context) {
        String jsonString = convertFromQuestionToString(question);
        JsonParser parser = new JsonParser();
        return parser.parse(jsonString).getAsJsonObject();
    }

//    private String convertFromQuestionToString(Question question) {
//        Gson gson = new Gson();
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(Question.class, new QuestionSerializer());
//        Gson alternateGson = gsonBuilder.create();
//        switch (question.getType()) {
//            case NOT_ANSWERABLE_TYPE:
//                NotAnswerableQuestion notAnswerableQuestion = ((NotAnswerableQuestion) question);
//                return gson.toJson(notAnswerableQuestion, NotAnswerableQuestion.class);
//            case EDIT_TYPE:
//                EditQuestion editQuestion = (EditQuestion) question;
//                return gson.toJson(editQuestion, EditQuestion.class);
//            case MULTIPLE_CHOICE_TYPE:
//                MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
//                return gson.toJson(multipleChoiceQuestion, MultipleChoiceQuestion.class);
//            case ONLY_ONE_CHOICE_TYPE:
//                OnlyOneChoiceQuestion onlyOneChoiceQuestion = (OnlyOneChoiceQuestion) question;
//                return gson.toJson(onlyOneChoiceQuestion, OnlyOneChoiceQuestion.class);
//            case PERSON_CREATOR_TYPE:
//                PersonCreatorQuestion personCreatorQuestion = (PersonCreatorQuestion) question;
//                return gson.toJson(personCreatorQuestion, PersonCreatorQuestion.class);
//            case SPINNER_CHOICE_TYPE:
//                SpinnerChoiceQuestion spinnerChoiceQuestion = (SpinnerChoiceQuestion) question;
//                return gson.toJson(spinnerChoiceQuestion, SpinnerChoiceQuestion.class);
//            case ROW_TYPE:
//                RowQuestion rowQuestion = (RowQuestion) question;
//                return alternateGson.toJson(rowQuestion, RowQuestion.class);
//            case TABLE_TYPE:
//                Table table = (Table) question;
//                return alternateGson.toJson(table, Table.class);
//            case GEO_LOCATION_TYPE:
//                GeoLocationQuestion geoLocationQuestion = (GeoLocationQuestion) question;
//                return alternateGson.toJson(geoLocationQuestion, GeoLocationQuestion.class);
//            case LIST_TYPE:
//                ListQuestion listQuestion = (ListQuestion) question;
//                return alternateGson.toJson(listQuestion, ListQuestion.class);
//            case MORE_THAN_TYPE:
//                MoreThanQuestion moreThanQuestion = (MoreThanQuestion) question;
//                return alternateGson.toJson(moreThanQuestion, MoreThanQuestion.class);
//            case CITY_CHECK_TYPE:
//                CityCheckQuestion cityCheckQuestion = (CityCheckQuestion) question;
//                return alternateGson.toJson(cityCheckQuestion, CityCheckQuestion.class);
//            default:
//                return gson.toJson(question, AnswerableQuestion.class);
//        }
//    }
    private String convertFromQuestionToString(Question question) {
        Gson gson = new Gson();
        switch (question.getType()) {
            case NOT_ANSWERABLE_TYPE:
                return gson.toJson((NotAnswerableQuestion) question);
            case EDIT_TYPE:
                return gson.toJson((EditQuestion) question);
            case MULTIPLE_CHOICE_TYPE:
                return gson.toJson((MultipleChoiceQuestion) question);
            case ONLY_ONE_CHOICE_TYPE:
                return gson.toJson((OnlyOneChoiceQuestion) question);
            case PERSON_CREATOR_TYPE:
                return gson.toJson((PersonCreatorQuestion) question);
            case SPINNER_CHOICE_TYPE:
                return gson.toJson((SpinnerChoiceQuestion) question);
            case ROW_TYPE:
                return gson.toJson((RowQuestion) question);
            case TABLE_TYPE:
                return gson.toJson((Table) question);
            case GEO_LOCATION_TYPE:
                return gson.toJson((GeoLocationQuestion) question);
            case LIST_TYPE:
                return gson.toJson((ListQuestion) question);
            case MORE_THAN_TYPE:
                return gson.toJson((MoreThanQuestion) question);
            case CITY_CHECK_TYPE:
                return gson.toJson((CityCheckQuestion) question);
            default:
                return gson.toJson(question, Question.class);
        }
    }

}