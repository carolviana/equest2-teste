package com.example.newequest.database;

import android.util.Log;

import androidx.room.TypeConverter;

import com.example.newequest.model.Block;
import com.example.newequest.model.Person;
import com.example.newequest.model.question.Question;
import com.example.newequest.util.QuestionDeserializer;
import com.example.newequest.util.QuestionSerializer;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<Block> fromStringToArrayListOfBlock(String serializedArrayList) {
        Type listType = new TypeToken<ArrayList<Block>>() {
        }.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Question.class, new QuestionDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(serializedArrayList, listType);
    }

    @TypeConverter
    public static String fromArrayListOfBlockToString(ArrayList<Block> arrayList) {
        ArrayList<Block> arrayClone = (ArrayList<Block>) arrayList.clone();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Question.class, new QuestionSerializer());
        Gson gson = gsonBuilder.create();
        String clone = "";
        int i = 0;
        while(clone.equals("")) {
            Log.i("carol", "size: " + arrayList.size());
            try {
                clone = gson.toJson(arrayClone);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log("Error: fromArrayListOfBlockToString");
                Log.i("carol","Error: fromArrayListOfBlockToString");
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                FirebaseCrashlytics.getInstance().log("OutOfMemoryError");
                Log.i("carol","OutOfMemoryError");
            }
//            Log.i("carol", "clone: " + clone);
//            Log.i("carol", "no loop");
        }
        return clone;
        //return "";
    }

    @TypeConverter
    public static ArrayList<Person> fromStringToArrayListOfPerson(String serializedArrayList) {
        Type listType = new TypeToken<ArrayList<Person>>() {
        }.getType();
        return new Gson().fromJson(serializedArrayList, listType);
    }

    @TypeConverter
    public static String fromArrayListOfPersonToString(ArrayList<Person> arrayList) {
        return new Gson().toJson(arrayList);
    }

    @TypeConverter
    public static Person fromStringToPerson(String serializedPerson) {
        return new Gson().fromJson(serializedPerson, Person.class);
    }

    @TypeConverter
    public static String fromPersonToString(Person arrayList) {
        return new Gson().toJson(arrayList);
    }

    @TypeConverter
    public static Question fromStringToQuestion(String serializedQuestion) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Question.class, new QuestionDeserializer());
        Gson gson = gsonBuilder.create();
        return gson.fromJson(serializedQuestion, Question.class);
    }

    @TypeConverter
    public static String fromQuestionToString(Question question) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Question.class, new QuestionSerializer());
        Gson gson = gsonBuilder.create();
        return gson.toJson(question);
    }

}
