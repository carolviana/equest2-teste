package com.example.newequest.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.newequest.R;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.util.JsonUtils;
import com.example.newequest.util.Utils;
import com.github.javafaker.Faker;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.util.Locale;

public class UploadActivity extends AppCompatActivity {
    Questionnaire questionnaire = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                questionnaire = bundle.getParcelable("questionnaire");
            }
        }

        Button button = findViewById(R.id.upload_bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (questionnaire != null) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference("database").child("questionnaires")
                            .push().setValue(questionnaire);
                    Toast.makeText(UploadActivity.this, "Questionnaire sent", Toast.LENGTH_SHORT).show();
                }
                runFirebaseDatabaseTestWithRealtimeDatabase();
            }
        });
    }

    public void runFirebaseDatabaseTestWithRealtimeDatabase() {
//        String jsonStr = Utils.getFileFromAssetsAsString(this, MainActivity.FILE);
//        try {
//            Questionnaire questionnaire = JsonUtils.getQuestionnaireFromJson(jsonStr);
//            questionnaire.startQuestionnaire(new Faker(new Locale("pt-BR")).name().firstName());
//            questionnaire.fillWithDummyData();
//            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//            firebaseDatabase.getReference("database").child("test_questionnaires").push().setValue(questionnaire);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
}