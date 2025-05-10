package com.example.newequest.util;

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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class QuestionDeserializer implements JsonDeserializer<Question> {

    private static final String OWM_TYPE = "type";
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
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return convertFromJsonElementToQuestion(json);

    }

    private Question convertFromJsonElementToQuestion(JsonElement jsonElement) {
        return getQuestion(jsonElement.getAsJsonObject());
    }

    private static Question getQuestion(JsonObject questionJsonObject) {
        Gson gson = new Gson();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Question.class, new QuestionDeserializer());
        Gson alternateGson = gsonBuilder.create();
        String type = questionJsonObject.get(OWM_TYPE).getAsString();
        Question question;
        switch (type) {
            case NOT_ANSWERABLE_TYPE:
                question = gson.fromJson(questionJsonObject,
                        NotAnswerableQuestion.class);
                break;
            case ONLY_ONE_CHOICE_TYPE:
                question = gson.fromJson(questionJsonObject,
                        OnlyOneChoiceQuestion.class);
                break;
            case MULTIPLE_CHOICE_TYPE:
                question = gson.fromJson(questionJsonObject,
                        MultipleChoiceQuestion.class);
                break;
            case EDIT_TYPE:
                question = gson.fromJson(questionJsonObject,
                        EditQuestion.class);
                break;
            case SPINNER_CHOICE_TYPE:
                question = gson.fromJson(questionJsonObject,
                        SpinnerChoiceQuestion.class);
                break;
            case TABLE_TYPE:
                question = alternateGson.fromJson(questionJsonObject,
                        Table.class);
                break;
            case LIST_TYPE:
                question = gson.fromJson(questionJsonObject,
                        ListQuestion.class);
                break;
            case ROW_TYPE:
                question = alternateGson.fromJson(questionJsonObject,
                        RowQuestion.class);
                break;
            case PERSON_CREATOR_TYPE:
                question = gson.fromJson(questionJsonObject,
                        PersonCreatorQuestion.class);
                break;
            case GEO_LOCATION_TYPE:
                question = gson.fromJson(questionJsonObject,
                        GeoLocationQuestion.class);
                break;
            case MORE_THAN_TYPE:
                question = gson.fromJson(questionJsonObject,
                        MoreThanQuestion.class);
                break;
            case CITY_CHECK_TYPE:
                question = gson.fromJson(questionJsonObject,
                        CityCheckQuestion.class);
                break;
            default:
                question = gson.fromJson(questionJsonObject, Question.class);
        }
        return question;
    }
}
