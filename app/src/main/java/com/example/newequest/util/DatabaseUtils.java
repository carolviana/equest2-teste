package com.example.newequest.util;

import android.content.Context;
import android.widget.Toast;

import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.newequest.database.AppDatabase;
import com.example.newequest.model.Questionnaire;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DatabaseUtils {
    public static void testRoomDatabase(final Questionnaire questionnaire, final Context context) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("room_test").child("questionnaires_from_app").child("test").setValue(questionnaire);
        AppDatabase db = AppDatabase.getSynchronousInstance(context);
        db.questionnaireDao().insertQuestionnaire(questionnaire);
        List<Questionnaire> queriedQuestionnaires = db.questionnaireDao().loadAllQuestionnaires();
        for (Questionnaire element : queriedQuestionnaires) {
            firebaseDatabase.getReference().child("room_test").child("questionnaires_from_room").child("test").setValue(element);

        }
        Toast.makeText(context, "Questionário enviado do Room", Toast.LENGTH_LONG).show();
    }

    public static void insertQuestionnaire(Context context, final Questionnaire questionnaire) {
        final AppDatabase db = AppDatabase.getInstance(context);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.questionnaireDao().insertQuestionnaire(questionnaire);
            }
        });
        thread.start();
    }

    public static void updateQuestionnaire(Context context, final Questionnaire questionnaire) {
        final AppDatabase db = AppDatabase.getInstance(context);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.questionnaireDao().updateQuestionnaire(questionnaire);
            }
        });
        thread.start();
    }

    public static void deleteQuestionnaire (Context context, final Questionnaire questionnaire) {
        final AppDatabase db = AppDatabase.getInstance(context);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.questionnaireDao().deleteQuestionnaire(questionnaire);
            }
        });
        thread.start();
    }

    public static void deleteQuestionnaireByPrimaryKey (Context context, final int primaryKey) {
        final AppDatabase db = AppDatabase.getInstance(context);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.questionnaireDao().deleteByPrimaryKey(primaryKey);
            }
        });
        thread.start();
    }

}
