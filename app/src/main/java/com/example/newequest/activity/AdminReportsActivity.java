package com.example.newequest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.newequest.R;
import com.example.newequest.adapters.AdminReportsAdapter;
import com.example.newequest.model.AdminReportItem;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.model.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminReportsActivity extends AppCompatActivity {
    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        final Intent intent = getIntent();
        if (intent != null) {
            userType = intent.getIntExtra("userType", 0);
        }

        Spinner filterView = findViewById(R.id.filter);
        ArrayList<String> filterItems = new ArrayList<>();
        filterItems.add("dos últimos 30 dias");
        filterItems.add("dos últimos 60 dias");
        filterItems.add("dos últimos 90 dias");
        filterItems.add("dos últimos 6 meses");
        filterItems.add("completo");
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, R.layout.item_spinner, filterItems);
        filterAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        filterView.setAdapter(filterAdapter);

        RecyclerView reportsRecyclerView = findViewById(R.id.reports_admin_rv);
        reportsRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        reportsRecyclerView.setLayoutManager(linearLayoutManager);

        final AdminReportsAdapter adminReportsAdapter = new AdminReportsAdapter(this);
        reportsRecyclerView.setAdapter(adminReportsAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("database").child("questionnaires");

        ValueEventListener localAdminValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, AdminReportItem> hashMap = new HashMap<>();
                for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                    String city = citySnapshot.getKey();
                    if (city.equals(Session.getUser().getCity())) {
                        for (DataSnapshot questionnaireSnapshot : citySnapshot.getChildren()) {
                            Questionnaire questionnaire = questionnaireSnapshot.getValue(Questionnaire.class);
                            String interviewer = questionnaire.getInterviewerName();
                            if (interviewer == null) {
                                interviewer = "Sem entrevistador definido";
                            }
                            if (!hashMap.containsKey(interviewer)) {
                                hashMap.put(interviewer, new AdminReportItem(interviewer, 0, 0));
                            }
                            AdminReportItem adminReportItem;
                            if (questionnaire.isComplete()) {
                                adminReportItem = hashMap.get(interviewer);
                                adminReportItem.setCompleteQuestionnaireCount(adminReportItem.getCompleteQuestionnaireCount() + 1);
                            } else {
                                adminReportItem = hashMap.get(interviewer);
                                adminReportItem.setIncompleteQuestionnaireCount(adminReportItem.getIncompleteQuestionnaireCount() + 1);
                            }
                            hashMap.put(interviewer, adminReportItem);
                        }
                    }
                }
                adminReportsAdapter.setDataset(new ArrayList<>(hashMap.values()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ValueEventListener statisticianValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
                    String city = citySnapshot.getKey();
                    int completeCount = 0;
                    int incompleteCount = 0;
                    for (DataSnapshot questionnaireSnapshot : citySnapshot.getChildren()) {
                        Questionnaire questionnaire = questionnaireSnapshot.getValue(Questionnaire.class);
                        if (questionnaire.isComplete()) {
                            completeCount++;
                        } else {
                            incompleteCount++;
                        }
                    }
                    AdminReportItem item = new AdminReportItem(city, completeCount, incompleteCount);
                    adminReportsAdapter.addToDataset(item);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        TextView firstColumnTextView = findViewById(R.id.first_column_tv);
        if (userType == 1) {
            firstColumnTextView.setText("Entrevistador");
            databaseReference.addValueEventListener(localAdminValueEventListener);
        } else if (userType == 2) {
            firstColumnTextView.setText("Cidade");
            databaseReference.addValueEventListener(statisticianValueEventListener);
        }
    }
}