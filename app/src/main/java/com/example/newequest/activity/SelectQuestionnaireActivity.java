package com.example.newequest.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newequest.R;
import com.example.newequest.adapters.QuestionnaireEntryAdapter;
import com.example.newequest.database.AppDatabase;
import com.example.newequest.fragment.QuestionnaireDownloadFragment;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.model.QuestionnaireData;
import com.example.newequest.model.QuestionnaireEntry;
import com.example.newequest.util.DatabaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SelectQuestionnaireActivity extends AppCompatActivity implements
        QuestionnaireEntryAdapter.QuestionnaireEntryAdapterOnClickHandler {

    List<QuestionnaireEntry> questionnaireEntries = null;
    private QuestionnaireEntryAdapter questionnaireEntryAdapter;
    private final int LOAD = 0;
    private final int UPDATE = 1;

    //    ActivityResultLauncher<Intent> activityResultLauncher =
//            registerForActivityResult (new ActivityResultContracts.StartActivityForResult());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("carol", "onCreate()");
        setContentView(R.layout.activity_select_questionnaire);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        ImageView btDownload = findViewById(R.id.bt_download);
        btDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Validar sinal de internet
                Log.i("carol", "bt");

                DialogFragment dialog = new QuestionnaireDownloadFragment();
                dialog.show(getSupportFragmentManager().beginTransaction(), "QuestionnaireDownloadFragment");

            }
        });

        RecyclerView recyclerView = findViewById(R.id.select_questionnaire_rv);
        questionnaireEntryAdapter = new QuestionnaireEntryAdapter(this);
        recyclerView.setAdapter(questionnaireEntryAdapter);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadQuestionnaireEntries(LOAD);

       // loadingLinearLayout.setVisibility(View.GONE);

//        final AppDatabase db = AppDatabase.getInstance(this);
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                questionnaireEntries = db.questionnaireDao().loadAllIncompleteQuestionnaireEntries();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        questionnaireEntryAdapter.setQuestionnaireEntries(questionnaireEntries);
//                    }
//                });
//            }
//        });
//        thread.start();
    }

    private void loadQuestionnaireEntries(int mode){
        final AppDatabase db = AppDatabase.getInstance(this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                questionnaireEntries = db.questionnaireDao().loadAllIncompleteQuestionnaireEntries();

                //questionnaireEntries = db.questionnaireDao().loadAllQuestionnaireEntries();

                for(QuestionnaireEntry questionnaireEntry: questionnaireEntries){
                    Log.i("carol", "QPk: " + questionnaireEntry.getPrimaryKey());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (mode){
                            case LOAD:
                                questionnaireEntryAdapter.setQuestionnaireEntries(questionnaireEntries);
                                break;
                            case UPDATE:
                                questionnaireEntryAdapter.updateQuestionnaireEntries(questionnaireEntries);
                                LinearLayout loadingLinearLayout = findViewById(R.id.loading_quest);
                                loadingLinearLayout.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    public void onClickEdit(int primaryKey, int position) {
        Intent intent = new Intent(SelectQuestionnaireActivity.this, AnswerActivity.class);
        intent.putExtra("primaryKey", primaryKey);
        startActivity(intent);
        Log.i("carol","onClickEdit");
        //questionnaireEntryAdapter.updateQuestionnaireEntries();
    }

    @Override
    public void onClickDelete(int primaryKey, int position) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("config").child("erase").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Erro ao recuperar dados", Toast.LENGTH_SHORT).show();
                    Log.i("carol", "Error getting data", task.getException());
                }
                else {
                    String password = String.valueOf(task.getResult().getValue());

                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectQuestionnaireActivity.this);
                    builder.setTitle("ATENÇÃO!").setMessage("Digite a senha de administrador para continuar:");

                    final EditText passwordInput = new EditText(SelectQuestionnaireActivity.this);
                    passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordInput.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_round_white_orangeborder));
                    passwordInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tx_orange));
                    passwordInput.setPadding(16,16,16,16);
                    builder.setView(passwordInput);

                    builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //EditText editTextPassword = findViewById(R.id.password);
                                    String text = passwordInput.getText().toString();
                                    if(text.equals(password)){
                                        Log.i("carol", "logou");
                                        DatabaseUtils.deleteQuestionnaireByPrimaryKey(SelectQuestionnaireActivity.this, primaryKey);
                                        questionnaireEntryAdapter.removeQuestionnaireEntry(position);
//                                    notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), "Questionário excluido com sucesso", Toast.LENGTH_SHORT).show();
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
    public void onClickUpload(final int primaryKey, int position) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("config").child("send").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Erro ao recuperar dados do banco de dados remoto.", Toast.LENGTH_SHORT).show();
                    Log.i("carol", "Error getting data", task.getException());
                }
                else {
                    String password = String.valueOf(task.getResult().getValue());

                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectQuestionnaireActivity.this);
                    builder.setTitle("ATENÇÃO!").setMessage("Digite a senha de administrador para continuar:");

                    final EditText passwordInput = new EditText(SelectQuestionnaireActivity.this);
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
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(SelectQuestionnaireActivity.this);
                                builder1.setTitle("ATENÇÃO!").setMessage("Deseja enviar questionário incompleto ou finalizar questionário incompleto?");
                                builder1.setPositiveButton("Enviar questionário incompleto", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        final AppDatabase db = AppDatabase.getInstance(SelectQuestionnaireActivity.this);
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                final Questionnaire questionnaire = db.questionnaireDao().loadQuestionnaireByPrimaryKey(primaryKey);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                        firebaseDatabase.getReference("database").child("incomplete").push().setValue(questionnaire);
                                                        questionnaire.setComplete(true);
                                                        DatabaseUtils.updateQuestionnaire(SelectQuestionnaireActivity.this, questionnaire);
                                                        Toast.makeText(SelectQuestionnaireActivity.this, "Questionário enviado", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                        thread.start();
                                    }
                                })
                                        .setNegativeButton("Finalizar e enviar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                final AppDatabase db = AppDatabase.getInstance(SelectQuestionnaireActivity.this);
                                                Thread thread = new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Questionnaire questionnaire = db.questionnaireDao().loadQuestionnaireByPrimaryKey(primaryKey);
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                QuestionnaireData questionnaireData = questionnaire.getQuestionnaireData();
                                                                questionnaireData.setSentIncomplete(true);
                                                                String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                                                                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                                firebaseDatabase.getReference("database").child("questionnaires")
                                                                        .child(date)
                                                                        .push().setValue(questionnaireData);
                                                                questionnaire.setComplete(true);
                                                                DatabaseUtils.updateQuestionnaire(SelectQuestionnaireActivity.this, questionnaire);
                                                                Toast.makeText(SelectQuestionnaireActivity.this, "Questionário enviado", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                });
                                                thread.start();
                                            }
                                        });

                                builder1.show();
                                questionnaireEntryAdapter.removeQuestionnaireEntry(position);
                                //Toast.makeText(getApplicationContext(), "Questionário enviado com sucesso", Toast.LENGTH_SHORT).show();
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
    protected void onRestart() {
        super.onRestart();
        Log.i("carol", "onRestart() - SelectQuestionnaireActivity");

        LinearLayout loadingLinearLayout = findViewById(R.id.loading_quest);
        loadingLinearLayout.setVisibility(View.VISIBLE);

        loadQuestionnaireEntries(UPDATE);

//        loadingLinearLayout.setVisibility(View.GONE);
    }

}
