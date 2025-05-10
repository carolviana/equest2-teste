package com.example.newequest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newequest.BuildConfig;
import com.example.newequest.R;
import com.example.newequest.adapters.ReportsAdapter;
import com.example.newequest.database.AppDatabase;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.model.QuestionnaireData;
import com.example.newequest.model.QuestionnaireEntry;
import com.example.newequest.util.DatabaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ReportsActivity extends AppCompatActivity implements ReportsAdapter.ReportsEntryAdapterOnClickHandler {
    List<QuestionnaireEntry> questionnaireEntries = null;

    Boolean txlogClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        RecyclerView recyclerView = findViewById(R.id.list);
        final ReportsAdapter reportsAdapter = new ReportsAdapter(this,this);
        recyclerView.setAdapter(reportsAdapter);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final AppDatabase db = AppDatabase.getInstance(this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                questionnaireEntries = db.questionnaireDao().loadAllQuestionnaireEntries();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reportsAdapter.setQuestionnaireEntries(questionnaireEntries);
                    }
                });
            }
        });
        thread.start();

        Button button = findViewById(R.id.button);
        EditText txlog = findViewById(R.id.txlog);
        txlog.setVisibility(View.GONE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txlogClicked = !txlogClicked;

                if(txlogClicked) {
                    try {
                        txlog.setVisibility(View.VISIBLE);
                        FileInputStream fis = getApplicationContext().openFileInput(AnswerActivity.FILENAME);
                        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = reader.readLine();
                        while (line != null) {
                            stringBuilder.append(line).append('\n');
                            line = reader.readLine();
                        }
                        String contents = stringBuilder.toString();
                        txlog.setText(contents);
                        Log.i("carol", contents);
                    } catch (IOException e) {
                        // Error occurred when opening raw file for reading.
                    }
                }else{
                    txlog.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public void onClickComplete(int primaryKey) {
        //Log.i("carol", "pk: " + primaryKey);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("config").child("resend").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Erro ao recuperar dados do banco de dados remoto.", Toast.LENGTH_SHORT).show();
                    Log.i("carol", "Error getting data", task.getException());
                }
                else {
                    String password = String.valueOf(task.getResult().getValue());

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportsActivity.this);
                    builder.setTitle("ATENÇÃO!").setMessage("Digite a senha de administrador para continuar:");

                    final EditText passwordInput = new EditText(ReportsActivity.this);
                    passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordInput.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_round_white_orangeborder));
                    passwordInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tx_orange));
                    passwordInput.setPadding(16,16,16,16);

                    builder.setView(passwordInput);
                    builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String text = passwordInput.getText().toString();
                            if(text.equals(password)){
                                final AppDatabase db = AppDatabase.getInstance(ReportsActivity.this);
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Questionnaire questionnaire = db.questionnaireDao().loadQuestionnaireByPrimaryKey(primaryKey);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                QuestionnaireData questionnaireData = questionnaire.getQuestionnaireData();
                                                String formatDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                firebaseDatabase.getReference("database").child("questionnaires")
                                                        .child(formatDate + "-reenvio").push().setValue(questionnaireData);
                                            }
                                        });
                                    }
                                });
                                thread.start();
                                Toast.makeText(getApplicationContext(), "Questionário enviado com sucesso", Toast.LENGTH_SHORT).show();
                            }else{
                                Log.i("carol", "erro de login");
                                Toast.makeText(getApplicationContext(), "Erro ao fazer login.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });
    }

    @Override
    public void onClickIncomplete(int primaryKey) {

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("config").child("questionnairebkup").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Erro ao recuperar dados do banco de dados remoto.", Toast.LENGTH_SHORT).show();
                    Log.i("carol", "Error getting data", task.getException());
                } else {

                    String password = String.valueOf(task.getResult().getValue());

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportsActivity.this);
                    builder.setTitle("ATENÇÃO!").setMessage("Digite a senha de administrador para continuar:");

                    final EditText passwordInput = new EditText(ReportsActivity.this);
                    passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordInput.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_round_white_orangeborder));
                    passwordInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tx_orange));
                    passwordInput.setPadding(16, 16, 16, 16);

                    builder.setView(passwordInput);
                    builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String text = passwordInput.getText().toString();
                            if (text.equals(password)) {


                                final AppDatabase db = AppDatabase.getInstance(ReportsActivity.this);
                                final String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                                final String fileName = "exportQuestionnaire" + date + ".json";
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        File file = new File(getExternalFilesDir(null), fileName);
                                        try {
                                            Log.i("carol", "iniciando json export do questionario" + primaryKey + "...");
                                            Questionnaire questionnaire = db.questionnaireDao().loadQuestionnaireByPrimaryKey(primaryKey);
                                            FileWriter writer = new FileWriter(file, true);
                                            writer.write(gson.toJson(questionnaire));
                                            writer.close();
                                            Log.i("carol", "arquivo criado");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.i("carol", "Erro ao criar arquivo");
                                        }

                                        try {
                                            Log.i("carol", "Enviando arquivo...");
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("*/*");
                                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"carolina.sov@gmail.com"});
                                            intent.putExtra(Intent.EXTRA_SUBJECT, "Email backup BD Json");
                                            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", file));
                                            if (intent.resolveActivity(getPackageManager()) != null) {
                                                startActivity(intent);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.i("carol", "Erro ao enviar arquivo");
                                        }
                                    }
                                });
                                thread.start();

                            } else {
                                Log.i("carol", "erro de login");
                                Toast.makeText(getApplicationContext(), "Erro ao fazer login.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.show();
                }

            }
        });
    }
}