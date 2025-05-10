package com.example.newequest.util;

import android.util.Log;

import com.example.newequest.model.Block;
import com.example.newequest.model.Dependency;
import com.example.newequest.model.Questionnaire;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class JsonUtils {
    private static final String OWM_TYPE = "type";
    private static final String OWM_DEPENDENCIES = "dependencies";
    private static final String OWM_QUESTIONS = "questions";
    private static final String OWM_TABLE = "table";
    private static final String OWM_REPLICATION = "replication";
    private static final String OWM_REFERENCE = "reference";
    private static final String OWM_ENES = "showENES";
    private static final String OWM_DONTKNOW = "showDontKnow";
    private static final String OWM_DONTANSWER = "showDontAnswer";
    private static final String OWM_DONTAPLY = "showDontAply";
    private static final String OWM_VALUE = "value";
    private static final String OWM_OTHER = "OpOther";
    private static final String OWM_SIZE = "size";
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

    private static final String NAO_SABE = "Não sabe";
    private static final String NAO_RESPONDEU = "Não respondeu";
    private static final String NAO_SE_APLICA = "Não se aplica";

    // Essa função faz o parsing do json em um modelo Java Questionnaire
    //public static Questionnaire getQuestionnaireFromJson(String jsonStr) throws JSONException {
    public static ArrayList<Block> getBlocksFromJson(String jsonStr) throws JSONException {
        JSONArray blocksJsonArray = new JSONArray(jsonStr);
        //Questionnaire questionnaire = new Questionnaire();
        ArrayList<Block> blocksArrayList = new ArrayList<>();
        for (int i = 0; i < blocksJsonArray.length(); i++) {
            JSONObject blockJsonObject = blocksJsonArray.getJSONObject(i);
            Iterator<String> it = blockJsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                JSONArray questionsJsonArray = blockJsonObject.getJSONArray(key);
                ArrayList<Question> questionArrayList = new ArrayList<>();
                for (int j = 0; j < questionsJsonArray.length(); j++) {
                    JSONObject questionJsonObject = questionsJsonArray.getJSONObject(j);
                    Question question = getQuestion(questionJsonObject);

                    if (questionJsonObject.has(OWM_DEPENDENCIES)) {
                        JSONArray dependenciesJsonArray = questionJsonObject.getJSONArray(OWM_DEPENDENCIES);
                        ArrayList<Dependency> dependencyArrayList = new ArrayList<>();
                        for (int k = 0; k < dependenciesJsonArray.length(); k++) {
                            JSONObject dependencyJsonObject = dependenciesJsonArray.getJSONObject(k);
                            Gson gson = new Gson();
                            Dependency dependency = gson.fromJson(dependencyJsonObject.toString(), Dependency.class);
                            dependencyArrayList.add(dependency);
                        }
                        question.setDependencies(dependencyArrayList);
                    }

                    if (questionJsonObject.has(OWM_REPLICATION)) {
                        question.setReplication(questionJsonObject.getString(OWM_REPLICATION));
                    }

                    if (questionJsonObject.has(OWM_REFERENCE)) {
                        question.setReference(questionJsonObject.getString(OWM_REFERENCE));
                    }

//                    //enes
//                    if (questionJsonObject.has(OWM_ENES)) {
//                        if (questionJsonObject.getBoolean(OWM_ENES)) {
//                            question.setShowEnes(true);
//                            setEnes(question);
//                        } else {
//                            question.setShowEnes(false);
//                        }
//                    } else {
//                        question.setShowEnes(false);
//                    }

                    //não sabe
                    if (questionJsonObject.has(OWM_DONTKNOW)) {
                        if (questionJsonObject.getBoolean(OWM_DONTKNOW)) {
                            question.setShowDontKnow(true);
                            setEnesIndividual(question,NAO_SABE);
                        } else {
                            question.setShowDontKnow(false);
                        }
                    } else {
                        question.setShowDontKnow(false);
                    }

                    //não respondeu
                    if (questionJsonObject.has(OWM_DONTANSWER)) {
                        if (questionJsonObject.getBoolean(OWM_DONTANSWER)) {
                            question.setShowDontAnswer(true);
                            setEnesIndividual(question,NAO_RESPONDEU);
                        } else {
                            question.setShowDontAnswer(false);
                        }
                    } else {
                        question.setShowDontAnswer(false);
                    }

                    //não se aplica
                    if (questionJsonObject.has(OWM_DONTAPLY)) {
                        if (questionJsonObject.getBoolean(OWM_DONTAPLY)) {
                            question.setShowDontAply(true);
                            setEnesIndividual(question,NAO_SE_APLICA);
                        } else {
                            question.setShowDontAply(false);
                        }
                    } else {
                        question.setShowDontAply(false);
                    }

                    if(questionJsonObject.has(OWM_OTHER)){
                        switch (question.getType()){
                            case ONLY_ONE_CHOICE_TYPE:
                                ((OnlyOneChoiceQuestion)question).setOpOther(questionJsonObject.getString(OWM_OTHER));
                                break;
                            case MULTIPLE_CHOICE_TYPE:
                                ((MultipleChoiceQuestion)question).setOpOther(questionJsonObject.getString(OWM_OTHER));
                                break;
                            case SPINNER_CHOICE_TYPE:
                                ((SpinnerChoiceQuestion)question).setOpOther(questionJsonObject.getString(OWM_OTHER));
                                break;
                        }
                    }

                    if(questionJsonObject.has(OWM_SIZE)){
                        ((EditQuestion)question).setSize(questionJsonObject.getInt(OWM_SIZE));
                    }

                    question.setRespondent(0);

                    questionArrayList.add(question);
                }
                Block block = new Block();
                block.setTitle(key);
                block.setQuestions(questionArrayList);
                blocksArrayList.add(block);
            }
        }
        //questionnaire.setBlocks(blocksArrayList);
        //return questionnaire;
        return blocksArrayList;
    }

    public static Question getQuestion(JSONObject questionJsonObject) throws JSONException {
        Gson gson = new Gson();
        String type = questionJsonObject.getString(OWM_TYPE);
        Question question;
        switch (type) {
            case NOT_ANSWERABLE_TYPE:
                question = gson.fromJson(questionJsonObject.toString(),
                        NotAnswerableQuestion.class);
                break;
            case ONLY_ONE_CHOICE_TYPE:
                OnlyOneChoiceQuestion onlyOneChoiceQuestion = gson.fromJson(questionJsonObject.toString(),
                        OnlyOneChoiceQuestion.class);
                onlyOneChoiceQuestion.setOptions(opToArrayList(questionJsonObject));
                question = onlyOneChoiceQuestion;
                break;
            case MULTIPLE_CHOICE_TYPE:
                MultipleChoiceQuestion multipleChoiceQuestion = gson.fromJson(questionJsonObject.toString(),
                        MultipleChoiceQuestion.class);
                multipleChoiceQuestion.setOptions(opToArrayList(questionJsonObject));
                question = multipleChoiceQuestion;
                break;
            case EDIT_TYPE:
                question = gson.fromJson(questionJsonObject.toString(),
                        EditQuestion.class);
                break;
            case SPINNER_CHOICE_TYPE:
                SpinnerChoiceQuestion spinnerChoiceQuestion = gson.fromJson(questionJsonObject.toString(),
                        SpinnerChoiceQuestion.class);
                spinnerChoiceQuestion.setOptions(opToArrayList(questionJsonObject));
                question = spinnerChoiceQuestion;
                break;
            case TABLE_TYPE:
                Table table = gson.fromJson(questionJsonObject.toString(),
                        Table.class);
                table.setQuestions(getQuestions(questionJsonObject.getJSONArray(OWM_TABLE)));
                question = table;
                break;
            case LIST_TYPE:
                question = gson.fromJson(questionJsonObject.toString(),
                        ListQuestion.class);
                break;
            case ROW_TYPE:
                RowQuestion rowQuestion = gson.fromJson(questionJsonObject.toString(),
                        RowQuestion.class);
                rowQuestion.setQuestions(getQuestions(questionJsonObject.getJSONArray(OWM_QUESTIONS)));
                question = rowQuestion;
                break;
            case PERSON_CREATOR_TYPE:
                question = gson.fromJson(questionJsonObject.toString(),
                        PersonCreatorQuestion.class);
                break;
            case GEO_LOCATION_TYPE:
                question = gson.fromJson(questionJsonObject.toString(),
                        GeoLocationQuestion.class);
                break;
            case MORE_THAN_TYPE:
                MoreThanQuestion moreThanQuestion = gson.fromJson(questionJsonObject.toString(),
                        MoreThanQuestion.class);
                moreThanQuestion.setValue(questionJsonObject.getInt(OWM_VALUE));
                question = moreThanQuestion;
                break;
            case CITY_CHECK_TYPE:
                CityCheckQuestion cityCheckQuestion = gson.fromJson(questionJsonObject.toString(),
                        CityCheckQuestion.class);
                cityCheckQuestion.setOptions(opToArrayList(questionJsonObject));
                question = cityCheckQuestion;
                break;
            default:
                question = gson.fromJson(questionJsonObject.toString(), Question.class);
        }
        return question;
    }

    public static void setEnes(Question question) {
        String type = question.getType();
        switch (type) {
            case ONLY_ONE_CHOICE_TYPE:
                ((OnlyOneChoiceQuestion) question).addOption(NAO_SABE);
                ((OnlyOneChoiceQuestion) question).addOption(NAO_RESPONDEU);
                ((OnlyOneChoiceQuestion) question).addOption(NAO_SE_APLICA);
                break;
            case MULTIPLE_CHOICE_TYPE:
                ((MultipleChoiceQuestion) question).addOption(NAO_SABE);
                ((MultipleChoiceQuestion) question).addOption(NAO_RESPONDEU);
                ((MultipleChoiceQuestion) question).addOption(NAO_SE_APLICA);
                break;
            case SPINNER_CHOICE_TYPE:
                ((SpinnerChoiceQuestion) question).addOption(NAO_SABE);
                ((SpinnerChoiceQuestion) question).addOption(NAO_RESPONDEU);
                ((SpinnerChoiceQuestion) question).addOption(NAO_SE_APLICA);
                break;
        }
    }

    public static void setEnesIndividual(Question question, String option) {
        String type = question.getType();
        switch (type) {
            case ONLY_ONE_CHOICE_TYPE:
                ((OnlyOneChoiceQuestion) question).addOption(option);
                break;
            case MULTIPLE_CHOICE_TYPE:
                ((MultipleChoiceQuestion) question).addOption(option);
                break;
            case SPINNER_CHOICE_TYPE:
                ((SpinnerChoiceQuestion) question).addOption(option);
                break;
        }
    }


//    public static ArrayList<String> opToArrayList(JSONObject jsonObject) {
//        ArrayList<String> options = new ArrayList<>();
//        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
//            String key = "Op" + alphabet;
//            try {
//                String op = jsonObject.getString(key);
//                options.add(op);
//            } catch (JSONException ignore) {
//            }
//        }
//        return options;
//    }

    public static ArrayList<String> opToArrayList(JSONObject jsonObject) {
        ArrayList<String> options = new ArrayList<>();
        for (int i = 1; i <= 300; i++) {
            String key = "Op" + i;
            try {
                String op = jsonObject.getString(key);
                options.add(op);
            } catch (JSONException ignore) {
            }
        }
        return options;
    }

    public static ArrayList<Question> getQuestions(JSONArray questionsJsonArray) throws JSONException {
        Gson gson = new Gson();
        ArrayList<Question> questionArrayList = new ArrayList<>();
        for (int j = 0; j < questionsJsonArray.length(); j++) {
            JSONObject questionJsonObject = questionsJsonArray.getJSONObject(j);
            String type = questionJsonObject.getString(OWM_TYPE);
            Question question;
            switch (type) {
                case "NotAnswerableQuestion":
                    question = gson.fromJson(questionJsonObject.toString(),
                            NotAnswerableQuestion.class);
                    break;
                case "OnlyOneChoiceQuestion":
                    OnlyOneChoiceQuestion onlyOneChoiceQuestion = gson.fromJson(questionJsonObject.toString(),
                            OnlyOneChoiceQuestion.class);
                    onlyOneChoiceQuestion.setOptions(opToArrayList(questionJsonObject));
                    question = onlyOneChoiceQuestion;
                    break;
                case "MultipleChoiceQuestion":
                    MultipleChoiceQuestion multipleChoiceQuestion = gson.fromJson(questionJsonObject.toString(),
                            MultipleChoiceQuestion.class);
                    multipleChoiceQuestion.setOptions(opToArrayList(questionJsonObject));
                    question = multipleChoiceQuestion;
                    break;
                case "EditQuestion":
                    question = gson.fromJson(questionJsonObject.toString(),
                            EditQuestion.class);
                    break;
                case "SpinnerChoiceQuestion":
                    SpinnerChoiceQuestion spinnerChoiceQuestion = gson.fromJson(questionJsonObject.toString(),
                            SpinnerChoiceQuestion.class);
                    spinnerChoiceQuestion.setOptions(opToArrayList(questionJsonObject));
                    question = spinnerChoiceQuestion;
                    break;
                case "ListQuestion":
                    question = gson.fromJson(questionJsonObject.toString(),
                            ListQuestion.class);
                    break;
                case "PersonCreatorQuestion":
                    question = gson.fromJson(questionJsonObject.toString(),
                            PersonCreatorQuestion.class);
                    break;
                case GEO_LOCATION_TYPE:
                    question = gson.fromJson(questionJsonObject.toString(),
                            GeoLocationQuestion.class);
                    break;
                case MORE_THAN_TYPE:
                    MoreThanQuestion moreThanQuestion = gson.fromJson(questionJsonObject.toString(),
                            MoreThanQuestion.class);
                    moreThanQuestion.setValue(questionJsonObject.getInt(OWM_VALUE));
                    question = moreThanQuestion;
                    break;
                default:
                    question = gson.fromJson(questionJsonObject.toString(), Question.class);
            }
            if (questionJsonObject.has(OWM_DEPENDENCIES)) {
                JSONArray dependenciesJsonArray = questionJsonObject.getJSONArray(OWM_DEPENDENCIES);
                ArrayList<Dependency> dependencyArrayList = new ArrayList<>();
                for (int k = 0; k < dependenciesJsonArray.length(); k++) {
                    JSONObject dependencyJsonObject = dependenciesJsonArray.getJSONObject(k);
                    Dependency dependency = gson.fromJson(dependencyJsonObject.toString(), Dependency.class);
                    dependencyArrayList.add(dependency);
                }
                question.setDependencies(dependencyArrayList);
            }

            if (questionJsonObject.has(OWM_REPLICATION)) {
                question.setReplication(questionJsonObject.getString(OWM_REPLICATION));
            }

            if (questionJsonObject.has(OWM_REFERENCE)) {
                question.setReference(questionJsonObject.getString(OWM_REFERENCE));
            }

//            //enes
//            if (questionJsonObject.has(OWM_ENES)) {
//                if (questionJsonObject.getBoolean(OWM_ENES)) {
//                    question.setShowEnes(true);
//                    setEnes(question);
//                } else {
//                    question.setShowEnes(false);
//                }
//            } else {
//                question.setShowEnes(false);
//            }

            //não sabe
            if (questionJsonObject.has(OWM_DONTKNOW)) {
                if (questionJsonObject.getBoolean(OWM_DONTKNOW)) {
                    question.setShowDontKnow(true);
                    setEnesIndividual(question,NAO_SABE);
                } else {
                    question.setShowDontKnow(false);
                }
            } else {
                question.setShowDontKnow(false);
            }

            //não respondeu
            if (questionJsonObject.has(OWM_DONTANSWER)) {
                if (questionJsonObject.getBoolean(OWM_DONTANSWER)) {
                    question.setShowDontAnswer(true);
                    setEnesIndividual(question,NAO_RESPONDEU);
                } else {
                    question.setShowDontAnswer(false);
                }
            } else {
                question.setShowDontAnswer(false);
            }

            //não se aplica
            if (questionJsonObject.has(OWM_DONTAPLY)) {
                if (questionJsonObject.getBoolean(OWM_DONTAPLY)) {
                    question.setShowDontAply(true);
                    setEnesIndividual(question,NAO_SE_APLICA);
                } else {
                    question.setShowDontAply(false);
                }
            } else {
                question.setShowDontAply(false);
            }

            if(questionJsonObject.has(OWM_OTHER)){
                switch (question.getType()){
                    case ONLY_ONE_CHOICE_TYPE:
                        ((OnlyOneChoiceQuestion)question).setOpOther(questionJsonObject.getString(OWM_OTHER));
                        break;
                    case MULTIPLE_CHOICE_TYPE:
                        ((MultipleChoiceQuestion)question).setOpOther(questionJsonObject.getString(OWM_OTHER));
                        break;
                    case SPINNER_CHOICE_TYPE:
                        ((SpinnerChoiceQuestion)question).setOpOther(questionJsonObject.getString(OWM_OTHER));
                        break;
                }
            }

            question.setRespondent(0);

            questionArrayList.add(question);
        }
        return questionArrayList;
    }

    public static ArrayList<String> getAllIds(String jsonStr) throws JSONException {
        JSONArray blocksJsonArray = new JSONArray(jsonStr);
        ArrayList<Block> blocksArrayList = new ArrayList<>();
        for (int i = 0; i < blocksJsonArray.length(); i++) {
            JSONObject blockJsonObject = blocksJsonArray.getJSONObject(i);
            Iterator<String> it = blockJsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                JSONArray questionsJsonArray = blockJsonObject.getJSONArray(key);
                ArrayList<Question> questionArrayList = new ArrayList<>();
                for (int j = 0; j < questionsJsonArray.length(); j++) {
                    JSONObject questionJsonObject = questionsJsonArray.getJSONObject(j);
                    Question question = getQuestion(questionJsonObject);
                    questionArrayList.add(question);
                }
                Block block = new Block();
                block.setTitle(key);
                block.setQuestions(questionArrayList);
                blocksArrayList.add(block);
            }
        }

        ArrayList<String> idsArrayList = new ArrayList<>();
        //idsArrayList.add("ID");
        //idsArrayList.add("Respondente");
        for (Block block : blocksArrayList) {
            for (Question question : block.getQuestions()) {
                if (!question.getType().equals("NotAnswerableQuestion")) {
                    if (question.getType().equals("Table")) {
                        for (Question internalQuestion : ((Table) question).getQuestions()) {
                            if (!internalQuestion.getType().equals("NotAnswerableQuestion")) {
                                if (internalQuestion.getType().equals("MultipleChoiceQuestion")) {
                                    MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) internalQuestion;
                                    String id = multipleChoiceQuestion.getId();
                                    for (int i = 0; i < multipleChoiceQuestion.getOptions().size(); i++) {
                                        idsArrayList.add(id + "_" + (i+1));
                                    }
                                } else if(internalQuestion.getType().equals("GeoLocationQuestion")){
                                    idsArrayList.add(internalQuestion.getId() + "-" + "Latitude");
                                    idsArrayList.add(internalQuestion.getId() + "-" + "Longitude");
                                } else {
                                    idsArrayList.add(internalQuestion.getId());
                                }
                            }
                        }
                    } else if (question.getType().equals("RowQuestion")) {
                        for (Question internalQuestion : ((RowQuestion) question).getQuestions()) {
                            if (!internalQuestion.getType().equals("NotAnswerableQuestion")) {
                                if (internalQuestion.getType().equals("MultipleChoiceQuestion")) {
                                    MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) internalQuestion;
                                    String id = multipleChoiceQuestion.getId();
                                    for (int i = 0; i < multipleChoiceQuestion.getOptions().size(); i++) {
                                        idsArrayList.add(id + "_" + (i+1));
                                    }
                                } else {
                                    idsArrayList.add(internalQuestion.getId());
                                }
                            }
                        }
                    } else {
                        if (question.getType().equals("MultipleChoiceQuestion")) {
                            MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) question;
                            String id = multipleChoiceQuestion.getId();
                            for (int i = 0; i < multipleChoiceQuestion.getOptions().size(); i++) {
                                idsArrayList.add(id + "_" + (i+1));
                            }
                            if (multipleChoiceQuestion.isShowDontAnswer()
                                    || multipleChoiceQuestion.isShowDontAply()
                                    || multipleChoiceQuestion.isShowDontKnow()){
                                idsArrayList.add(id + "_N");
                            }
                        } else if(question.getType().equals("GeoLocationQuestion")) {
                            idsArrayList.add(question.getId() + "-" + "Latitude");
                            idsArrayList.add(question.getId() + "-" + "Longitude");
                        } else {
                            idsArrayList.add(question.getId());
                        }
                    }
                }
            }
        }
        return idsArrayList;
    }

    public static ArrayList<Question> getAllQuestionsType(String jsonStr) throws JSONException {
        JSONArray blocksJsonArray = new JSONArray(jsonStr);
        ArrayList<Block> blocksArrayList = new ArrayList<>();
        for (int i = 0; i < blocksJsonArray.length(); i++) {
            JSONObject blockJsonObject = blocksJsonArray.getJSONObject(i);
            Iterator<String> it = blockJsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                JSONArray questionsJsonArray = blockJsonObject.getJSONArray(key);
                ArrayList<Question> questionArrayList = new ArrayList<>();
                for (int j = 0; j < questionsJsonArray.length(); j++) {
                    JSONObject questionJsonObject = questionsJsonArray.getJSONObject(j);
                    Question question = getQuestion(questionJsonObject);
                    questionArrayList.add(question);
                }
                Block block = new Block();
                block.setTitle(key);
                block.setQuestions(questionArrayList);
                blocksArrayList.add(block);
            }
        }

        ArrayList<Question> questionList = new ArrayList<>();

        for (Block block : blocksArrayList) {
            for (Question question : block.getQuestions()) {
                if (!question.getType().equals("NotAnswerableQuestion")) {
                    questionList.add(question);
                }
            }
        }
        return questionList;
    }


    public static String getLastQuestionID(String jsonStr) throws JSONException {
        JSONArray blocksJsonArray = new JSONArray(jsonStr);
        ArrayList<Block> blocksArrayList = new ArrayList<>();
        for (int i = 0; i < blocksJsonArray.length(); i++) {
            JSONObject blockJsonObject = blocksJsonArray.getJSONObject(i);
            Iterator<String> it = blockJsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                JSONArray questionsJsonArray = blockJsonObject.getJSONArray(key);
                ArrayList<Question> questionArrayList = new ArrayList<>();
                for (int j = 0; j < questionsJsonArray.length(); j++) {
                    JSONObject questionJsonObject = questionsJsonArray.getJSONObject(j);
                    Question question = getQuestion(questionJsonObject);
                    questionArrayList.add(question);
                }
                Block block = new Block();
                block.setTitle(key);
                block.setQuestions(questionArrayList);
                blocksArrayList.add(block);
            }
        }

        ArrayList<Question> questionList = blocksArrayList.get(blocksArrayList.size() - 1).getQuestions();
        return questionList.get(questionList.size() - 1).getId();
    }
}
