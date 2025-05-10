package com.example.newequest.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newequest.R;
import com.example.newequest.adapters.PeopleAdapter;
import com.example.newequest.database.AppDatabase;
import com.example.newequest.fragment.EditQuestionFragment;
import com.example.newequest.fragment.GeoLocationQuestionFragment;
import com.example.newequest.fragment.MultipleChoiceQuestionFragment;
import com.example.newequest.fragment.NotAnswerableQuestionFragment;
import com.example.newequest.fragment.OnlyOneChoiceQuestionFragment;
import com.example.newequest.fragment.QuestionFragment;
import com.example.newequest.fragment.SpinnerQuestionFragment;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.model.QuestionnaireData;
import com.example.newequest.model.QuestionnaireEntry;
import com.example.newequest.model.Session;
import com.example.newequest.model.question.AnswerableQuestion;
import com.example.newequest.model.question.CityCheckQuestion;
import com.example.newequest.model.question.GeoLocationQuestion;
import com.example.newequest.model.question.MultipleChoiceQuestion;
import com.example.newequest.model.question.NotAnswerableQuestion;
import com.example.newequest.model.question.OnlyOneChoiceQuestion;
import com.example.newequest.model.question.Question;
import com.example.newequest.model.question.SpinnerChoiceQuestion;
import com.example.newequest.util.Connection;
import com.example.newequest.util.DatabaseUtils;
import com.example.newequest.util.JsonUtils;
import com.example.newequest.util.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class AnswerActivity extends AppCompatActivity implements QuestionFragment.OnNextButtonClickListener, PeopleAdapter.PeopleAdapterOnClickHandler {

    public static final String LOG_TAG = AnswerActivity.class.getName();

    public static final int MULTIPLE_CHOICE_FLAG = 0;
    public static final int ONLY_ONE_CHOICE_FLAG = 1;
    public static final int PERSON_CREATOR_FLAG = 2;
    public static final int EDIT_FLAG = 3;
    public static final int CITY_CHECK_FLAG = 4;
    public static final int REPLICATION_FLAG = 5;

    private static final String NOT_ANSWERABLE_TYPE = "NotAnswerableQuestion";
    private static final String EDIT_TYPE = "EditQuestion";
    private static final String MULTIPLE_CHOICE_TYPE = "MultipleChoiceQuestion";
    private static final String ONLY_ONE_CHOICE_TYPE = "OnlyOneChoiceQuestion";
    private static final String PERSON_CREATOR_TYPE = "PersonCreatorQuestion";
    private static final String SPINNER_CHOICE_TYPE = "SpinnerChoiceQuestion";
    private static final String ROW_TYPE = "RowQuestion";
    private static final String TABLE_TYPE = "Table";
    private static final String MORE_THAN_TYPE = "MoreThanQuestion";
    private static final String CITY_CHECK_TYPE = "CityCheckQuestion";
    private static final String GEO_LOCATION_TYPE = "GeoLocationQuestion";

    private static final String NAO_SABE_VALUE = "-999";
    private static final String NAO_RESPONDEU_VALUE = "-777";
    private static final String NAO_SE_APLICA_VALUE = "-888";
    private static final String NAO_SABE = "Não sabe";
    private static final String NAO_RESPONDEU = "Não respondeu";
    private static final String NAO_SE_APLICA = "Não se aplica";

    private static final int GEO_LOCATION_SET = 0;
    private static final int GEO_LOCATION_ADD = 1;

    public static final int FILLING_QUEST_FLAG = 0;
    public static final int ENDING_QUEST_FLAG = 1;
    public static final int SEND_QUEST_FLAG = 2;

    private TextView questionIdView;
    private TextView questionBehaviorView;
    private TextView blockCounter;

    private PeopleAdapter adapter;
    private RecyclerView recyclerPeople;
    private Boolean listPeopleClicked = false;
    private Integer answeringPerson = null;

    private Question currentQuestion;
    private Question currentInternalQuestion = null;
    private Questionnaire questionnaire;
    private int progressMonitor = 0;

    private int saveMonitor = 0;
    private static final int saveSize = 5;
    private boolean questionnaireSended = false;

    private List<QuestionnaireEntry> questionnaireEntries = null;

    private File file;
    public static final String FILENAME = "eQuest";
    private FileOutputStream fos;


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
        builder.setTitle("Deseja sair do questionário?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        saveQuestionnaireAndExit();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        questionIdView = findViewById(R.id.question_id);
        questionBehaviorView = findViewById(R.id.behavior);
        blockCounter = findViewById(R.id.block_counter);
        recyclerPeople = findViewById(R.id.people_recycler);

        //configurar adapter
        adapter = new PeopleAdapter(this);

        //configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerPeople.setLayoutManager(layoutManager);
        recyclerPeople.setHasFixedSize(true);
        recyclerPeople.setAdapter(adapter);
        recyclerPeople.setVisibility(View.GONE);

        ImageView peopleButton = findViewById(R.id.people_fab);
        peopleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listPeopleClicked = !listPeopleClicked;
                if(listPeopleClicked){
                    recyclerPeople.setVisibility(View.VISIBLE);
                }else{
                    recyclerPeople.setVisibility(View.GONE);
                }
            }
        });

        //carrega dados salvos do questionário
        Intent intent = getIntent();
        //if (intent != null && intent.getIntExtra("primaryKey", -1) != -1) {
        if (intent != null ) {
            int primaryKey = intent.getIntExtra("primaryKey", -1);
            Log.i("carol", "primaryKey: " + primaryKey);
            if(primaryKey == -1){
                //erro ao carregar
                AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
                builder.setTitle("Erro ao carregar questionário").setMessage("Tente novamente");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                builder.show();
            }else {
                FetchQuestionnaireTask fetchQuestionnaireTask = new FetchQuestionnaireTask(primaryKey);
                fetchQuestionnaireTask.execute();
            }
        } else {
            //erro ao carregar
            AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
            builder.setTitle("Erro ao carregar questionário").setMessage("Tente novamente");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.show();
        }

        ImageView saveButton = findViewById(R.id.save_fab);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateQuestionnaire();
            }
        });

        ImageView exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveQuestionnaireAndExit();
            }
        });

        updateBlockCounter();

//        // Add a crash button.
//        Button crashButton = new Button(this);
//        crashButton.setText("Crash!");
//        crashButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                throw new RuntimeException("Test Crash"); // Force a crash
//            }
//        });
//        addContentView(crashButton, new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));

    }


    @Override
    public void onPersonCreatorNextButtonClick(String personName, int personID, String option, String direction) {
        if((direction.equals("previous") && !personName.equals("")) || (direction.equals("next"))) {
            questionnaire.createPerson(personName, personID, option);
            adapter.setPeopleDataset(questionnaire.getPeopleArray());
        }
        if (direction.equals("next")){
            onNextButtonClick();
        } else {
            onPreviousButtonClick();
        }
    }

    @Override
    public void onCityCheckNextButtonClick(String cityName, String direction) {
        questionnaire.setCity(cityName);
        if (direction.equals("next")){
            onNextButtonClick();
        } else {
            onPreviousButtonClick();
        }
    }

    @Override
    public void onAddNewPersonButtonClick() {
        Question question = questionnaire.addNewPerson();
        if (question == null) {
            Toast.makeText(this, "Erro ao adicionar nova Pessoa", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pessoa adicionada com sucesso", Toast.LENGTH_SHORT).show();
            currentQuestion = question;
            setQuestionPanel(currentQuestion);
        }
    }

    @Override
    public void onNextButtonClick() {
        try {
            switch (progressMonitor) {
                case FILLING_QUEST_FLAG:

                    //Get next question and assign it to currentQuestion and then set the core panel accordingly
                    Question lastQuestion = currentQuestion;

                    if (!currentQuestion.getType().equals(NOT_ANSWERABLE_TYPE)) {

                        if (currentQuestion.getType().equals(MULTIPLE_CHOICE_TYPE)){
                            if (((MultipleChoiceQuestion) currentQuestion).getAnswers().size() == 0) {
                                Toast.makeText(this, "Escolha pelo menos uma opção", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else if (currentQuestion.getType().equals(GEO_LOCATION_TYPE)){
                            if (((GeoLocationQuestion) currentQuestion).getAnswers().size() == 0) {
                                Toast.makeText(this, "Marque o local para continuar", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }else{
                            if(((AnswerableQuestion) currentQuestion).getAnswer().equals("")) {
                                Toast.makeText(this, "Responda a pergunta para continuar", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        //log answer
                        String answer = questionnaire.getPrimaryKey() + " - " + currentQuestion.getId() + "+" + currentQuestion.getRespondent() + ": " + ((AnswerableQuestion) currentQuestion).getAnswerValue() + "\n";
                        byte[] b = answer.getBytes();
                        try {
                            file = new File(getApplicationContext().getFilesDir(), FILENAME);
                            fos = getApplicationContext().openFileOutput(FILENAME, getApplicationContext().MODE_PRIVATE|getApplicationContext().MODE_APPEND);
                            fos.write(b); //byte[]
                            fos.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        FirebaseCrashlytics.getInstance().log(currentQuestion.getId() + ": " + ((AnswerableQuestion) currentQuestion).getAnswerValue());
                    }

                    currentQuestion = questionnaire.getNextQuestionWithLastQuestion(lastQuestion, answeringPerson);

                    saveMonitor++;
                    if(saveMonitor == saveSize){
                        updateQuestionnaire();
                        saveMonitor = 0;
                        FirebaseCrashlytics.getInstance().log("Salvou");
                        Log.i("carol", "salvou");
                    }

                    //validar se todas as perguntas foram respondidas
                    if (currentQuestion == null) {
                        Question question = updateCurrentQuestion(null);
                        if(question == null){
                            progressMonitor++;
                        } else {
                            answeringPerson = null;
                            adapter.setNewPositionClicked(-1);

                            currentQuestion = question;
                            questionIdView.setText(currentQuestion.getId());
                            if(currentQuestion.getBehavior() == null){
                                questionBehaviorView.setVisibility(View.GONE);
                            }else {
                                questionBehaviorView.setVisibility(View.VISIBLE);
                                questionBehaviorView.setText(currentQuestion.getBehavior());
                            }
                            setQuestionPanel(currentQuestion);
                        }
                    } else {
                        setQuestionPanel(currentQuestion);
                    }
                    break;

                case ENDING_QUEST_FLAG:
                    NotAnswerableQuestion endQuestion = new NotAnswerableQuestion();
                    if(Connection.isConected(AnswerActivity.this)){
                        Log.i("carol", "conectado");
                        endQuestion.setTitle("Questionário preenchido. Clique em \"próximo\" para enviar.");
                        endQuestion.setType(NOT_ANSWERABLE_TYPE);
                        endQuestion.setClosure(true);
                        endQuestion.setSendStatus(true);
                        questionIdView.setText("Pronto!");
                        questionBehaviorView.setText("");
                        setQuestionPanel(endQuestion);
                        progressMonitor++;
                    }else{
                        endQuestion.setTitle("Sem acesso a internet, tente novamente mais tarde!");
                        endQuestion.setType(NOT_ANSWERABLE_TYPE);
                        questionIdView.setText("Atenção!");
                        questionBehaviorView.setText("");
                        endQuestion.setClosure(true);
                        endQuestion.setSendStatus(false);
                        setQuestionPanel(endQuestion);
                        Log.i("carol", "não - conectado");
                    }
                    break;

                case SEND_QUEST_FLAG:
                    if(Connection.isConected(AnswerActivity.this)){

                        try {
                            questionnaire.finishTimer();
                        } catch (Exception e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().log("Erro ao finalizar timer");
                        }
                        questionnaire.setQuestionnaireEndTime();
                        QuestionnaireData questionnaireData = questionnaire.getQuestionnaireData();
                        saveQuestionnaire();
                        String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        firebaseDatabase.getReference("database").child("questionnaires")
                                .child(date)
                                .push().setValue(questionnaireData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        questionnaire.setComplete(true);
                                        Log.i("carol", "enviou");
                                        Toast.makeText(AnswerActivity.this, "Questionário enviado", Toast.LENGTH_SHORT).show();
                                        questionnaireSended = true;
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("carol", "falha");
                                        progressMonitor--;

                                        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
                                        builder.setTitle("ATENÇÃO!").setMessage("Erro ao enviar questionário, tente novamente! \nSe persistir o erro entre em contato com a equipe de TI");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                finish();
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                    }else{
                        progressMonitor--;

                        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
                        builder.setTitle("ATENÇÃO!").setMessage("Erro ao enviar questionário, sem acesso à internet.\nTente novamente mais tarde.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        builder.show();
                    }
                    break;
            }
            updateBlockCounter();

        } catch (Exception e) {
            e.printStackTrace();
            saveQuestionnaireAndExit();
            Log.e("maroto", e.toString());
        }
    }

    private void recoverQuestionnaire(int primaryKey){

        Log.i("carol", "iniciando recuperação de dados do questionário!");

        //carrega perguntas
        String jsonStr = Utils.getFileFromAssetsAsString(getApplicationContext(), MainActivity.FILE);
        try {
            questionnaire.setBlocks(JsonUtils.getBlocksFromJson(jsonStr));
        }catch (JSONException j){
            j.printStackTrace();

            AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
            builder.setTitle("Erro ao carregar questionário").setMessage("Tente novamente");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.show();
        }

        //set respostas
        try {
            FileInputStream fis = getApplicationContext().openFileInput(AnswerActivity.FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {

                int pk = Integer.parseInt(line.split(" - ")[0]);
                if(primaryKey == pk){
                    Log.i("carol", "------------------------------------");

                    //String pattern = "\\w.* - [\\w.*][+]\\w.*: \\w.*";
                    String patternNovo = "[a-zA-Z0-9]* - \\w.*[+]\\w.*: \\w.*";
                    String patternAntigo = "[a-zA-Z0-9]* - \\w.*: \\w.*";

                    if(line.matches(patternNovo)){
                        Log.i("carol", "do novo");
                    }else if(line.matches(patternAntigo)){
                        Log.i("carol", "do antigo");
                        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
                        builder.setTitle("ATENÇÃO!").setMessage("Erro ao tentar recuperar o questionário! \nEntre em contato com a equipe de TI\nPK: " + primaryKey);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        builder.show();
                        break;
                    }else {
                        Log.i("carol", "não identificado");
                    }

                    if(line.contains(" - (PERGUNTA DE CONFERÊNCIA")){
                        line = line.replace(" - (PERGUNTA DE CONFERÊNCIA", "");
                        Log.i("carol", "-> correção -> " + line);
                    }


                    String question = line.split(" - ")[1].split(": ")[0];
                    String questionID = question.split("[+]")[0];

                    int respondent = Integer.parseInt(question.split("[+]")[1]);
                    String answer = line.split(" - ")[1].split(": ")[1];
                    if(!answer.equals("null")) {
                        Log.i("carol", "|" + pk + "|" + questionID + "|" + respondent + "|" + answer + "|");

                        try {
                            currentQuestion = questionnaire.getQuestionByIdAndRespondent(questionID, 0);
                        }catch (Exception e){
                            e.printStackTrace();
                            try{
                                currentQuestion = questionnaire.getQuestionByIdAndRespondent(questionID+" - (PERGUNTA DE CONFERÊNCIA", 0);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }

                        if(currentQuestion != null) {

                            if (currentQuestion.hasReplication()) {
                                Log.i("carol", "tem replicação");
                                questionnaire.verifyReplication(currentQuestion);
                            }

                            if (respondent != 0) {
                                currentQuestion = questionnaire.getQuestionByIdAndRespondent(questionID, respondent);
                            }

                            if (answer.equals(NAO_RESPONDEU_VALUE) || answer.equals(NAO_SE_APLICA_VALUE) || answer.equals(NAO_SABE_VALUE)) {
                                if (currentQuestion.getType().equals(MULTIPLE_CHOICE_TYPE)) {
                                    switch (answer) {
                                        case NAO_RESPONDEU_VALUE:
                                            ((AnswerableQuestion) currentQuestion).setAnswers(new ArrayList<String>(Collections.singleton(NAO_RESPONDEU)));
                                            break;
                                        case NAO_SE_APLICA_VALUE:
                                            ((AnswerableQuestion) currentQuestion).setAnswers(new ArrayList<String>(Collections.singleton(NAO_SE_APLICA)));
                                            break;
                                        case NAO_SABE_VALUE:
                                            ((AnswerableQuestion) currentQuestion).setAnswers(new ArrayList<String>(Collections.singleton(NAO_SABE)));
                                            break;
                                    }
                                } else {
                                    switch (answer) {
                                        case NAO_RESPONDEU_VALUE:
                                            ((AnswerableQuestion) currentQuestion).setAnswer(NAO_RESPONDEU);
                                            break;
                                        case NAO_SE_APLICA_VALUE:
                                            ((AnswerableQuestion) currentQuestion).setAnswer(NAO_SE_APLICA);
                                            break;
                                        case NAO_SABE_VALUE:
                                            ((AnswerableQuestion) currentQuestion).setAnswer(NAO_SABE);
                                            break;
                                    }
                                }

                            } else {

                                switch (currentQuestion.getType()) {

                                    case CITY_CHECK_TYPE: {
                                        ((AnswerableQuestion) currentQuestion).setAnswer(((CityCheckQuestion) currentQuestion).getOptions().get(Integer.parseInt(answer) - 1));
                                        break;
                                    }

                                    case SPINNER_CHOICE_TYPE: {
                                        ((AnswerableQuestion) currentQuestion).setAnswer(((SpinnerChoiceQuestion) currentQuestion).getOptions().get(Integer.parseInt(answer) - 1));
                                        break;
                                    }

                                    case ONLY_ONE_CHOICE_TYPE: {
                                        if( Integer.parseInt(answer) > 0 ){
                                            ((AnswerableQuestion) currentQuestion).setAnswer(((OnlyOneChoiceQuestion) currentQuestion).getOptions().get(Integer.parseInt(answer) - 1));
                                        }else{
                                            Log.i("carol", "Opção de resposta não encontrada");
                                        }
                                        break;
                                    }

                                    case PERSON_CREATOR_TYPE:
                                    case EDIT_TYPE: {
                                        ((AnswerableQuestion) currentQuestion).setAnswer(answer);
                                        break;
                                    }

                                    case GEO_LOCATION_TYPE: {
                                        ArrayList<String> answers = new ArrayList<>();
                                        answers.add(answer.split("[|]")[0]);
                                        answers.add(answer.split("[|]")[1]);
                                        ((AnswerableQuestion) currentQuestion).setAnswers(answers);
                                        break;
                                    }

                                    case MULTIPLE_CHOICE_TYPE: {
                                        int cont = 0;
                                        int sizeAnswer = answer.length();
                                        ArrayList<String> options = new ArrayList<>();
                                        while (cont != sizeAnswer) {
                                            StringBuilder position = new StringBuilder();
                                            if (answer.charAt(cont) != '0') {
                                                position.append(answer.charAt(cont));
                                                if (cont >= 9) {
                                                    cont++;
                                                    position.append(answer.charAt(cont));
                                                }
                                                options.add(((MultipleChoiceQuestion) currentQuestion).getOptions().get(Integer.parseInt(position.toString()) - 1));
                                            }
                                            cont++;
                                        }
                                        ((MultipleChoiceQuestion) currentQuestion).setAnswers(options);
                                        break;
                                    }
                                }
                            }

                            if (currentQuestion.getType().equals(GEO_LOCATION_TYPE) || currentQuestion.getType().equals(MULTIPLE_CHOICE_TYPE)) {
                                Log.i("carol", "resultado: q: " + currentQuestion.getId() + " r: " + currentQuestion.getRespondent() + " a: " + ((AnswerableQuestion) currentQuestion).getAnswers() + " av: " + ((AnswerableQuestion) currentQuestion).getAnswerValue());
                            } else {
                                Log.i("carol", "resultado: q: " + currentQuestion.getId() + " r: " + currentQuestion.getRespondent() + " a: " + ((AnswerableQuestion) currentQuestion).getAnswer() + " av: " + ((AnswerableQuestion) currentQuestion).getAnswerValue());
                            }
                        }else{
                            Log.i("carol", "Questão não encontrada -> pk:" + pk + "|QID: " + questionID + "|R: " + respondent + "|A:" + answer + "|");
                        }
                    }
                }

                line = reader.readLine();
            }

            currentQuestion = updateCurrentQuestion(null);
        } catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
            builder.setTitle("ATENÇÃO!").setMessage("Erro ao tentar recuperar o questionário! \nEntre em contato com a equipe de TI");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.show();
        }
    }

    public void onPreviousButtonClick(){
        Question question = questionnaire.getLastQuestionWithCurrentQuestion(currentQuestion, answeringPerson);

        if (question == null) {
            Toast.makeText(this, "Você chegou a primeira pergunta", Toast.LENGTH_SHORT).show();
            return;
        }

        currentQuestion = question;
        setQuestionPanel(currentQuestion);
        updateBlockCounter();
    }

    private void setQuestionPanel(Question question) {
        //Analyze the currentQuestion's type and then switch the flow in order to
        //set the layout properly

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();

        String questionType;
        String respondent = "";

        if( progressMonitor == FILLING_QUEST_FLAG) {
            questionIdView.setText(currentQuestion.getId());
            if (currentQuestion.getBehavior() == null) {
                questionBehaviorView.setVisibility(View.GONE);
            } else {
                questionBehaviorView.setVisibility(View.VISIBLE);
                questionBehaviorView.setText(currentQuestion.getBehavior());
            }

            if (question.hasReplication() || question.isReplica()) {
                if (questionnaire.getPeople().size() > 0) {
                    respondent = questionnaire.getPersonNameById(question.getRespondent());
                }
            }

        }

        questionType = question.getType();

        switch (questionType) {

            case NOT_ANSWERABLE_TYPE:
                bundle.putParcelable("question", question);
                bundle.putString("respondent", respondent);
                NotAnswerableQuestionFragment notAnswerableQuestionFragment = new NotAnswerableQuestionFragment();
                notAnswerableQuestionFragment.setArguments(bundle);
                transaction.replace(R.id.question_container, notAnswerableQuestionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

            case EDIT_TYPE:
                bundle.putParcelable("question", question);
                try {
                    if (questionnaire.getReplicationQuestionID().equals(question.getId())) {
                        bundle.putInt("question_type_flag", REPLICATION_FLAG);
                    } else {
                        bundle.putInt("question_type_flag", EDIT_FLAG);
                    }
                } catch (Exception e) {
                    bundle.putInt("question_type_flag", EDIT_FLAG);
                }
                bundle.putString("respondent", respondent);
                EditQuestionFragment editQuestionFragment = new EditQuestionFragment();
                editQuestionFragment.setArguments(bundle);
                transaction.replace(R.id.question_container, editQuestionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

            case MULTIPLE_CHOICE_TYPE:
                bundle.putParcelable("question", question);
                bundle.putString("respondent", respondent);
                MultipleChoiceQuestionFragment multipleChoiceQuestionFragment = new MultipleChoiceQuestionFragment();
                multipleChoiceQuestionFragment.setArguments(bundle);
                transaction.replace(R.id.question_container, multipleChoiceQuestionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

            case ONLY_ONE_CHOICE_TYPE:
                bundle.putParcelable("question", question);
                bundle.putString("respondent", respondent);
                OnlyOneChoiceQuestionFragment onlyOneChoiceQuestionFragment = new OnlyOneChoiceQuestionFragment();
                onlyOneChoiceQuestionFragment.setArguments(bundle);
                transaction.replace(R.id.question_container, onlyOneChoiceQuestionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

            case PERSON_CREATOR_TYPE:
                bundle.putParcelable("question", question);
                bundle.putInt("question_type_flag", PERSON_CREATOR_FLAG);
                EditQuestionFragment personCreatorFragment = new EditQuestionFragment();
                personCreatorFragment.setArguments(bundle);
                transaction.replace(R.id.question_container, personCreatorFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

            case CITY_CHECK_TYPE:
                bundle.putParcelable("question", question);
                bundle.putString("respondent", respondent);
                bundle.putInt("question_type_flag", CITY_CHECK_FLAG);
                OnlyOneChoiceQuestionFragment cityCheckFragment = new OnlyOneChoiceQuestionFragment();
                cityCheckFragment.setArguments(bundle);
                transaction.replace(R.id.question_container, cityCheckFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

            case SPINNER_CHOICE_TYPE:
                bundle.putParcelable("question", question);
                bundle.putString("respondent", respondent);
                SpinnerQuestionFragment spinnerQuestionFragment = new SpinnerQuestionFragment();
                spinnerQuestionFragment.setArguments(bundle);
                transaction.replace(R.id.question_container, spinnerQuestionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

            case GEO_LOCATION_TYPE:
                bundle.putParcelable("question", question);
                bundle.putString("respondent", respondent);
                GeoLocationQuestionFragment geoLocationQuestionFragment = new GeoLocationQuestionFragment();
                geoLocationQuestionFragment.setArguments(bundle);
                transaction.replace(R.id.question_container, geoLocationQuestionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
    }

    private void updateBlockCounter() {
        Pair<Integer, Integer> blockAndQuestionIndexes = new Pair<>(null, null);
        if  (currentQuestion != null) {
            blockAndQuestionIndexes = questionnaire.getBlockAndQuestionIndexes(currentQuestion);
        }
        if (blockAndQuestionIndexes.first != null) {
            String currentBlock = String.valueOf(blockAndQuestionIndexes.first + 1);
            String output = currentBlock + "/" + questionnaire.getBlocks().size();
            blockCounter.setText(output);
        }
    }

    private void saveQuestionnaireAndExit() {
        questionnaire.finishTimer();
        DatabaseUtils.insertQuestionnaire(AnswerActivity.this, questionnaire);
        Toast.makeText(AnswerActivity.this, "Questionário salvo", Toast.LENGTH_SHORT).show();
        questionnaireSended = true;
        finish();
    }

    private void saveQuestionnaire() {
        DatabaseUtils.insertQuestionnaire(AnswerActivity.this, questionnaire);
        Toast.makeText(AnswerActivity.this, "Questionário salvo", Toast.LENGTH_SHORT).show();
    }

    private void updateQuestionnaire() {
        DatabaseUtils.updateQuestionnaire(AnswerActivity.this, questionnaire);
        Toast.makeText(AnswerActivity.this, "Questionário salvo", Toast.LENGTH_SHORT).show();
    }

    private Question updateCurrentQuestion(Integer personID){
        Question question = questionnaire.getFirstQuestion();
        question = questionnaire.getFirstNotAnsweredQuestion(question, personID);
        return question;
    }

    private void printLines(int primaryKey){
        try {
            FileInputStream fis = getApplicationContext().openFileInput(AnswerActivity.FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null){
                int pk = Integer.parseInt(line.split(" - ")[0]);
                if(primaryKey == pk) {
                    Log.i("carol", line);
                }
                line = reader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClickPeopleAdd() {
        if(questionnaire.verifyAddPeopleDependency()) {
            Question question = questionnaire.addNewPerson();
            if (question == null) {
                Toast.makeText(this, "Erro ao adicionar nova Pessoa à família.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pessoa adicionada à família com sucesso", Toast.LENGTH_SHORT).show();
                currentQuestion = question;
                answeringPerson = currentQuestion.getRespondent();
                adapter.setPeopleDataset(questionnaire.getPeopleArray());
                adapter.setNewPositionClicked(answeringPerson);
                setQuestionPanel(currentQuestion);
            }
        }else{
            Toast.makeText(this, "Operação não permitida no momento. Continue respondendo o questionário.", Toast.LENGTH_SHORT).show();
        }
        recyclerPeople.setVisibility(View.GONE);
    }

    @Override
    public void onClickPeopleDelete(int personID) {
        if(answeringPerson!=null) {
            if (answeringPerson == personID) {
                answeringPerson = null;
                adapter.setNewPositionClicked(answeringPerson);
                currentQuestion = updateCurrentQuestion(null);
                if (currentQuestion != null) {
                    setQuestionPanel(currentQuestion);
                } else {
                    onNextButtonClick();
                }
            }
        }
        questionnaire.deletePerson(personID);
        adapter.setPeopleDataset(questionnaire.getPeopleArray());
    }

    @Override
    public void onClickPeople(int personID) {
        Question question;
        if(personID == -1){
            answeringPerson = null;
            question = updateCurrentQuestion(answeringPerson);
        } else {
            question = updateCurrentQuestion(personID);
        }

        if(question != null) {
            if(personID != -1) {
                answeringPerson = personID;
            }
            currentQuestion = question;
            setQuestionPanel(currentQuestion);
            recyclerPeople.setVisibility(View.GONE);
        }else{
            Toast.makeText(getApplicationContext(), "Escolha outra pessoa!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation(int flag){
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //check permission
        if(ActivityCompat.checkSelfPermission(AnswerActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //when permission granted
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //initialize location
                    Location location= task.getResult();
                    if(location != null){
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.i("carol", "lat: " + latitude + " long: " + longitude);
                        switch (flag){
                            case GEO_LOCATION_SET:
                                questionnaire.setLocation(latitude + ";" + longitude); break;
                            case GEO_LOCATION_ADD:
                                questionnaire.addLocation(latitude + ";" + longitude); break;
                        }
                    }
                }
            });
        }else{
            //when permission denied
            ActivityCompat.requestPermissions(AnswerActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        updateQuestionnaire();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(!questionnaireSended) {
            saveQuestionnaire();
        }
        super.onDestroy();
    }

    private class FetchQuestionnaireTask extends AsyncTask<Void, Void, Void> {
        private final int primaryKey;

        FetchQuestionnaireTask(int primaryKey) {
            this.primaryKey = primaryKey;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = AppDatabase.getInstance(AnswerActivity.this);
            questionnaire = db.questionnaireDao().loadQuestionnaireByPrimaryKey(primaryKey);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(questionnaire.isNew()){
                //carrega json
                String jsonStr = Utils.getFileFromAssetsAsString(getApplicationContext(), MainActivity.FILE);
                try {
                    questionnaire.setBlocks(JsonUtils.getBlocksFromJson(jsonStr));
                }catch (JSONException e){
                    e.printStackTrace();

                    AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
                    builder.setTitle("Erro ao carregar questionário").setMessage("Tente novamente");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    });
                    builder.show();
                }

                questionnaire.setNew(false);

                try{
                    questionnaire.setInterviewer(Session.getUser().getUserId());
                    questionnaire.setInterviewerName(Session.getUser().getName());
                    Log.i("carol","set interviewer");
                }catch (Exception e){

                    //TODO criar dialogfragment
                    Log.i("carol", "sem entrevistador");

                    AlertDialog.Builder builder = new AlertDialog.Builder(AnswerActivity.this);
                    builder.setTitle("ATENÇÃO!").setMessage("Nome do Entrevistador:");

                    final EditText interviewerInput = new EditText(AnswerActivity.this);
                    interviewerInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    interviewerInput.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.shape_round_white_orangeborder));
                    interviewerInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.tx_orange));
                    interviewerInput.setPadding(16,16,16,16);
                    builder.setView(interviewerInput);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!interviewerInput.getText().toString().equals("")) {
                                questionnaire.setInterviewerName(interviewerInput.getText().toString());
                                dialogInterface.dismiss();
                            }

                        }
                    });//.show();

                    final AlertDialog dialog = builder.create();

                    builder.show();

                }

                questionnaire.startTimer();
                getLocation(GEO_LOCATION_SET);
                currentQuestion = updateCurrentQuestion(null);
                setQuestionPanel(currentQuestion);
            }else{
                questionnaire.startTimer();
                getLocation(GEO_LOCATION_ADD);
                adapter.setPeopleDataset(questionnaire.getPeopleArray());

                try {
                    currentQuestion = updateCurrentQuestion(null);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("carol", "entrou em recuperação de dados");
                    recoverQuestionnaire(primaryKey);
                }

                if(currentQuestion == null){
                    //TODO verifica se todo questionario esta respondido
                    progressMonitor++;
                    onNextButtonClick();
                }else {
                    updateBlockCounter();
                    setQuestionPanel(currentQuestion);
                }
            }
        }
    }
}