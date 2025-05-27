package com.example.newequest.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.newequest.R;
import com.example.newequest.database.AppDatabase;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.model.QuestionnaireEntry;
import com.example.newequest.model.Session;
import com.example.newequest.model.User;
import com.example.newequest.provider.RemoteUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
public class MainActivity extends AppCompatActivity {
    
    public static final String FILE = "quest_ComunidadeAgricola-CM.json";

    private TextView completeCountView;
    private TextView incompleteTextView;
    private TextView totalTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {






        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        // Dentro do método onCreate() de uma Activity ou outro ponto de teste
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("testConnection");

        // Escreve um valor simples no banco
        dbRef.setValue("Conexão bem-sucedida com Firebase!")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FIREBASE_TEST", "Escrita no Firebase bem-sucedida.");
                    } else {
                        Log.e("FIREBASE_TEST", "Erro ao escrever no Firebase: " + task.getException());
                    }
                });

        // Lê o valor de volta
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                Log.d("FIREBASE_TEST", "Valor lido do Firebase: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FIREBASE_TEST", "Erro ao ler do Firebase: " + error.getMessage());
            }
        });


        ImageView exitView = findViewById(R.id.exit);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.logout(MainActivity.this);
            }
        });

        FrameLayout panelFrame = findViewById(R.id.panel_frame);

        Typeface openSansType = null;
        Typeface firaSansType = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            openSansType = getResources().getFont(R.font.opensansregular);
            firaSansType = getResources().getFont(R.font.firasansregular);
        }

        completeCountView = findViewById(R.id.complete_quests_count);

        try {
            overrideFonts(this, panelFrame, firaSansType);
            completeCountView.setTypeface(openSansType);
        }catch (Exception e){
            e.printStackTrace();
        }

        incompleteTextView = findViewById(R.id.incomplete_count);
        totalTextView = findViewById(R.id.total_count);

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        ConstraintLayout newQuestButton = findViewById(R.id.new_questionnaire);
        newQuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    newQuestButton.setEnabled(false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Deseja iniciar um novo questionário?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Questionnaire questionnaire = new Questionnaire(true);
                                    long id = db.questionnaireDao().insertQuestionnaire(questionnaire);
                                    Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
                                    intent.putExtra("primaryKey", (int) id);
                                    startActivity(intent);
                                }
                            });
                            thread.start();
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();

                    newQuestButton.setEnabled(true);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("ATENÇÃO!").setMessage("Ative o GPS para continuar");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });

        ConstraintLayout continueButton = findViewById(R.id.continue_answering);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Intent intent = new Intent(MainActivity.this, SelectQuestionnaireActivity.class);
                    startActivity(intent);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("ATENÇÃO!").setMessage("Ative o GPS para continuar");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });

        ConstraintLayout newUserButton = findViewById(R.id.new_user);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserConfigActivity.class);
                startActivity(intent);
            }
        });

        ConstraintLayout reportsButton = findViewById(R.id.reports);
        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReportsActivity.class);
                startActivity(intent);
            }
        });

        ConstraintLayout completeQuestsLayout = findViewById(R.id.complete_quests_cl);

        ConstraintLayout exportButton = findViewById(R.id.export);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ExportActivity.class);
                startActivity(intent);
            }
        });

        ImageView backupButton = findViewById(R.id.backup);
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BackUpActivity.class);
                startActivity(intent);
            }
        });

        int userType = 0;

        TextView txInterviewer = findViewById(R.id.txInterviewer);
        try {
            txInterviewer.setText(Session.getUser().getName());
            userType = Session.getUser().getType();
            FirebaseCrashlytics.getInstance().setUserId(Session.getUser().getName());
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().setUserId("Não identificado");
        }

        switch (userType) {
            case 0: //Interviewer
                newUserButton.setVisibility(View.GONE);
                exportButton.setVisibility(View.GONE);
                backupButton.setVisibility(View.GONE);
                break;
            case 1: //Local admin
                exportButton.setVisibility(View.GONE);
                backupButton.setVisibility(View.GONE);
                break;
            case 2: //Statistician
                newQuestButton.setVisibility(View.GONE);
                continueButton.setVisibility(View.GONE);
//                newUserButton.setVisibility(View.GONE);
                reportsButton.setVisibility(View.GONE);
                completeQuestsLayout.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        updateQuestionnaireAmount();
//        RemoteUser.createUser(new User("Carolina Viana", "carolina.sov@gmail.com", 0, "123456", "Campos dos Goytacazes", true), getApplicationContext());
    }

    private void overrideFonts(final Context context, final View v, Typeface font) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child, font);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateQuestionnaireAmount(){
        final AppDatabase db = AppDatabase.getInstance(this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final List<QuestionnaireEntry> questionnaireEntries = db.questionnaireDao().loadAllQuestionnaireEntries();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int incomplete = 0;
                        int complete = 0;
                        for (QuestionnaireEntry questionnaireEntry : questionnaireEntries) {
                            if (questionnaireEntry.isComplete()) {
                                complete++;
                            } else {
                                incomplete++;
                            }
                        }
                        int total = complete + incomplete;
                        completeCountView.setText(String.valueOf(complete));
                        incompleteTextView.setText(String.valueOf(incomplete));
                        totalTextView.setText(String.valueOf(total));
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateQuestionnaireAmount();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Deseja sair do aplicativo?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Session.logout(MainActivity.this);
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();

    }

    class QuestionnaireUpdate {
        private String date;
        private String id;
        private String value;

        public QuestionnaireUpdate(String date, String id) {
            this.date = date;
            this.id = id;
        }

        public QuestionnaireUpdate(String date, String id, String value) {
            this.date = date;
            this.id = id;
            this.value = value;
        }

        public String getDate() {
            return date;
        }

        public String getId() {
            return id;
        }

        public String getValue (){
            return value;
        }
    }
}