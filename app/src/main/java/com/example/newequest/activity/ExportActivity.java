package com.example.newequest.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.newequest.database.AppDatabase;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.model.QuestionnaireEntry;
import com.example.newequest.model.question.CityCheckQuestion;
import com.example.newequest.model.question.OnlyOneChoiceQuestion;
import com.example.newequest.model.question.SpinnerChoiceQuestion;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVWriter;
import com.example.newequest.BuildConfig;
import com.example.newequest.R;
import com.example.newequest.fragment.DatePickerFragment;
import com.example.newequest.model.question.AnswerableQuestion;
import com.example.newequest.model.question.MultipleChoiceQuestion;
import com.example.newequest.model.question.Question;
import com.example.newequest.util.JsonUtils;
import com.example.newequest.util.Utils;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ExportActivity extends AppCompatActivity {
    private ArrayList<String> topLine = new ArrayList<>();
// private TextView fromView;
// private TextView toView;

    private EditText dateInput;
    private EditText textView;
    private ImageView nextButton;
    private Button send;
    private Button backup;

    //private File file;

    private ValueEventListener mReferenceListener;
    private DatabaseReference itemReference;

    private static final String NAO_SABE = "Não sabe";
    private static final String NAO_RESPONDEU = "Não respondeu";
    private static final String NAO_SE_APLICA = "Não se aplica";

    private static final String NAO_SABE_VALUE = "-999";
    private static final String NAO_RESPONDEU_VALUE = "-777";
    private static final String NAO_SE_APLICA_VALUE = "-888";

    private static final String FORA_DE_FLUXO = "-888";

    private ArrayList<Question> allIdsAndTypes;
    private int questionnaireType = 2; //1 = modelo antigo; 2 = modelo novo sem respondente; 3 = modelo novo com respondente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }



// fromView = findViewById(R.id.from_tv);
// toView = findViewById(R.id.to_tv);

// final Calendar c = Calendar.getInstance();
// int year = c.get(Calendar.YEAR);
// int month = c.get(Calendar.MONTH) + 1;
// int day = c.get(Calendar.DAY_OF_MONTH);
// String fromDefault = String.format("%02d", day) + "/" + String.format("%02d", (month - 1)) + "/" + year;
// String toDefault = String.format("%02d", day) + "/" + String.format("%02d", month) + "/" + year;
//
// fromView.setText(fromDefault);
// toView.setText(toDefault);


// final DatePickerFragment datePickerFragment = new DatePickerFragment();

// fromView.setOnClickListener(new View.OnClickListener() {
// @Override
// public void onClick(View view) {
// String indicator = "from";
// Bundle bundle = new Bundle();
// bundle.putString("indicator", indicator);
// bundle.putString("currentlyChosenDate", fromView.getText().toString());
//
// datePickerFragment.setArguments(bundle);
// datePickerFragment.show(getSupportFragmentManager().beginTransaction(), "DatePicker");
// }
// });
//
// toView.setOnClickListener(new View.OnClickListener() {
// @Override
// public void onClick(View view) {
// String indicator = "to";
// Bundle bundle = new Bundle();
// bundle.putString("indicator", indicator);
// bundle.putString("currentlyChosenDate", toView.getText().toString());
//
// datePickerFragment.setArguments(bundle);
// datePickerFragment.show(getSupportFragmentManager().beginTransaction(), "DatePicker");
// }
// });

        textView = findViewById(R.id.tv_log);

        dateInput = findViewById(R.id.date);

        nextButton = findViewById(R.id.next);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dateInput.getText().toString().equals("")) {
                    LinearLayout loadingLinearLayout = findViewById(R.id.loading_ll);
                    loadingLinearLayout.setVisibility(View.VISIBLE);
                    ConstraintLayout container = findViewById(R.id.export_container);
                    container.setVisibility(View.GONE);
                    generateQuestionnaire();
                    //nextButton.setEnabled(false);
                }else{
                    Toast.makeText(ExportActivity.this, "Digite uma data valida", Toast.LENGTH_SHORT).show();
                }
            }
        });

        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"carolina.sov@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Email Teste");
                //Uri fileUri = FileProvider.getUriForFile(this,getPackageName()+".provider",file); "export.csv"
                File file = new File(getExternalFilesDir(null), "export.csv");
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", file));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        backup = findViewById(R.id.backup);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!dateInput.getText().toString().equals("")) {

                    loadLog();
                    final AppDatabase db = AppDatabase.getInstance(ExportActivity.this);
                    final String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                    final String fileName = dateInput.getText() + "-exportQuestionnaire-" + date + ".json";

                    //Thread thread = new Thread(new Runnable() {
                    ExportActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            File file = new File(getExternalFilesDir(null), fileName);
                            List<QuestionnaireEntry> questionnaireEntryList = db.questionnaireDao().loadAllQuestionnaireEntries();
                            Log.i("carol", "qtd questionarios: " + questionnaireEntryList.size());
                            int lastQuestionnairePK = questionnaireEntryList.get(questionnaireEntryList.size() - 1).getPrimaryKey();
                            Log.i("carol", "ultima PK: " + lastQuestionnairePK);
//                        textView.setText("qtd questionarios: " + questionnaireEntryList.size());
                            try {
                                Log.i("carol", "iniciando json export do bd...");
//                            textView.append("\niniciando json export do bd...");
                                FileWriter writer = new FileWriter(file, true);
                                writer.write("[\n");
                                for (QuestionnaireEntry entry : questionnaireEntryList) {
                                    Questionnaire questionnaire = db.questionnaireDao().loadQuestionnaireByPrimaryKey(entry.getPrimaryKey());
                                    Log.i("carol", "PK: " + questionnaire.getPrimaryKey());
//                                textView.append("\nPK: " + questionnaire.getPrimaryKey());
//                                List<Questionnaire> questionnairesList = db.questionnaireDao().loadAllQuestionnaires();

                                    writer.write(gson.toJson(questionnaire));
                                    if (entry.getPrimaryKey() != lastQuestionnairePK) {
                                        writer.write(",");
                                    }
                                    writer.write("\n");
                                }
                                writer.write("]");
                                writer.close();
                                Log.i("carol", "arquivo criado");
//                            textView.append("\narquivo criado");
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i("carol", "Erro ao criar arquivo");
//                            textView.append("\nErro ao criar arquivo");
                            }

                            try {
//                            textView.append("\nEnviando arquivo...");
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
//                            textView.append("\nErro ao enviar arquivo");
                            }
                        }
                    });
                    //thread.start();
                } else {
                    Log.i("carol", "digite o número de patrimônio");
                }
            }
        });
    }



    private void loadLog() {
        Log.i("carol", "loading logs...");
        try {
            Log.i("carol", String.valueOf(dateInput.getText()));
            FileInputStream fis = getApplicationContext().openFileInput(AnswerActivity.FILENAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            final String date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            String fileName = dateInput.getText() + "-log-" + date + ".txt";

            File file = new File(getExternalFilesDir(null), fileName);
            FileWriter writer = new FileWriter(file, true);

            String line = reader.readLine();
            while (line != null) {
                Log.i("carol", line);
                writer.write(line + "\n");
                line = reader.readLine();
            }
            writer.close();

        } catch (IOException e) {
           e.printStackTrace();
        }
        Log.i("carol", "Fim loading logs!");
    }

    private void generateQuestionnaire(){
//        ArrayList<String> arrayList = new ArrayList<>();

// arrayList.add("test");
// arrayList.add("teste2");


        topLine.clear();
        topLine.add("ID");
        topLine.add("primaryKey");
        topLine.add("Respondente");
        topLine.add("complete");//
        topLine.add("sentIncomplete");//ok
        topLine.add("lastQuestion");//ok
        topLine.add("path");//ok
        topLine.add("family");//
        topLine.add("type");
        topLine.add("dateTime");
        topLine.add("endTime");
        topLine.add("elapsedTimeInSeconds");
        topLine.add("interviewerName");
        topLine.add("location");
        setTopLine();
        writeTopLine();

        Log.i("carol", "iniciando...");
        getQuestionIdsAndTypes();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

// DatabaseReference itemReference= firebaseDatabase.getReference("/").child("20220705").child("-N6EoN8kGgyp9hC1Yc84");
// DatabaseReference itemReference = firebaseDatabase.getReference("/").child(item);

        try {
            itemReference = firebaseDatabase.getReference("database").child("questionnaires").child(dateInput.getText().toString());

            mReferenceListener = itemReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.i("carol", dateInput.getText().toString());
                    Log.i("carol", "quest: " + snapshot.getChildrenCount());

                    textView.setText("data: " + dateInput.getText().toString() + "\n");
                    textView.append("total questionários: " + snapshot.getChildrenCount() + "\n");

                    for (DataSnapshot questionnaireSnapshot : snapshot.getChildren()) {
                        Log.i("carol", "----- " + questionnaireSnapshot.getKey());
                        textView.append("-----> " + questionnaireSnapshot.getKey() + "\n");
                        questionnaireType = 2;

                        ArrayList<ArrayList<ArrayList<String>>> writableContent = new ArrayList<>();

                        ArrayList<AnswerableQuestion> questions = iterateQuestionnaireSnapshot(questionnaireSnapshot);
                        ArrayList<ArrayList<String>> lines = prepareLines(questionnaireSnapshot.getKey(), questions, questionnaireSnapshot);
                        writableContent.add(lines);

                        writeLines(writableContent);
                        LinearLayout loadingLinearLayout = findViewById(R.id.loading_ll);
                        loadingLinearLayout.setVisibility(View.GONE);
                        ConstraintLayout container = findViewById(R.id.export_container);
                        container.setVisibility(View.VISIBLE);
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "Conteúdo exportado com sucesso!", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        Log.i("carol", "tipo: " + questionnaireType);
                        Log.i("carol", "--------");
                        textView.append("----------------");

                        questionnaireType = 2;
                    }
                    removeListener();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("carol", "onCancelled " + error);
                }
            });

        }catch (OutOfMemoryError error){
            error.printStackTrace();
            Toast.makeText(this, "Sem memória para realizar essa operação", Toast.LENGTH_SHORT).show();
            Log.i("carol", "sem memoria");
            LinearLayout loadingLinearLayout = findViewById(R.id.loading_ll);
            loadingLinearLayout.setVisibility(View.GONE);
        }
        catch (Exception exception){
            exception.printStackTrace();
            Toast.makeText(this, "Erro: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            Log.i("carol", "deu erro!");
            LinearLayout loadingLinearLayout = findViewById(R.id.loading_ll);
            loadingLinearLayout.setVisibility(View.GONE);
        }

        Log.i("carol", "FIM!!");
        nextButton.setEnabled(true);
    }

    void removeListener(){
        itemReference.removeEventListener(mReferenceListener);
    }

    ArrayList<ArrayList<String>> prepareLines(String key, ArrayList<AnswerableQuestion> answeredQuestions, DataSnapshot questionnaireSnapshot) {
        ArrayList<ArrayList<String>> lines = new ArrayList<>();
        ArrayList<Integer> respondents = new ArrayList<>();
        ArrayList<String> repeatQuestions = new ArrayList<>();
        repeatQuestions.add("RD.0");repeatQuestions.add("RD.1");repeatQuestions.add("RD.4");repeatQuestions.add("RD.5");
        repeatQuestions.add("RD.6");repeatQuestions.add("RD.7");repeatQuestions.add("RD.9");repeatQuestions.add("RD.9.1");
        repeatQuestions.add("RD.9.2");repeatQuestions.add("RD.10.1");repeatQuestions.add("RD.10.2");repeatQuestions.add("RD.10.3");
        repeatQuestions.add("RD.10.4");repeatQuestions.add("RD.10.5");repeatQuestions.add("RD.10.6");repeatQuestions.add("RD.10.7");
        repeatQuestions.add("RD.10.8");repeatQuestions.add("RD.10.9");repeatQuestions.add("RD.10.10");

// getQuestionIdsAndTypes();

        /*for (DataSnapshot keySnapshot : questionnaireSnapshot.getChildren()) {
            if(keySnapshot.getKey().equals("peopleString")){
                String string = keySnapshot.getValue().toString();
                int cont = 0;
                String regex = "\n";

                if(string.contains(regex)) {
                    Log.i("carol", "respondentes: " + string.split(regex).length);
                    textView.append("respondentes: " + string.split(regex).length + "\n");
                    String rp = string.substring(0, string.indexOf(regex));
                    textView.append("RP: " + rp + "\n");
                }

                if(string.contains(";")){
                    regex = ";";
                    Log.i("carol", "respondentes: " + string.split(regex).length);
                    textView.append("respondentes: " + string.split(regex).length + "\n");
                    String rp = string.substring(0, string.indexOf(regex));
                    textView.append("RP: " + rp + "\n");
                }

                for (String pessoa : string.split(regex)) {
                    respondents.add(cont);
                    cont++;
                }
            }
        }*/

        if(questionnaireType == 2) {
            //set respondents

            //identificar respondentes
            int posicao = 0;
            String lastQuestionID = "";
            String repetida = "";
            boolean achou = false;

            /*while((!achou) || (posicao < answeredQuestions.size())){
                if(lastQuestionID.equals(answeredQuestions.get(posicao).getId())){
                    repetida = lastQuestionID;
                    achou = true;
                    Log.i("carol", "achou!");
                }else{
                    Log.i("carol", "lastQuestionID = " + lastQuestionID + " pos = " + posicao);
                    lastQuestionID = answeredQuestions.get(posicao).getId();
                    posicao++;
                }
            }*/

            for(AnswerableQuestion answerableQuestion : answeredQuestions){
                if( (lastQuestionID.equals(answerableQuestion.getId())) && !achou ){
                    achou = true;
                    repetida = lastQuestionID;
                }else{
                    lastQuestionID = answerableQuestion.getId();
                }
            }

            if(achou){
                int cont = 0;
                for(AnswerableQuestion answerableQuestion : answeredQuestions) {
                    if(repetida.equals(answerableQuestion.getId())){
                        respondents.add(cont);
                        cont++;
                    }
                }
            }else{
                respondents.add(0);
            }


            int contRespondent = 1;
            String oldQuestionID = "";
            for (AnswerableQuestion question : answeredQuestions) {
                if (question.getId().equals(oldQuestionID)) {
                    question.setRespondent(contRespondent);
                    contRespondent++;
                    if (contRespondent == respondents.size()) {
                        contRespondent = 1;
                    }
                }
                oldQuestionID = question.getId();
            }
        }else{
            for(AnswerableQuestion answerableQuestion : answeredQuestions) {
                //identificar respondentes
                if (!respondents.contains(answerableQuestion.getRespondent())) {
                    respondents.add(answerableQuestion.getRespondent());
                }
            }
        }

        //identificar ultima pergunta respondida
        String lastAnsweredQuestion = answeredQuestions.get(0).getId();
        for(AnswerableQuestion answerableQuestion : answeredQuestions){
            if(!answerableQuestion.getAnswer().equals("")){
                lastAnsweredQuestion = answeredQuestions.get(answeredQuestions.indexOf(answerableQuestion)).getId();
            }
        }

        Log.i("carol", "respondentes: " + respondents);
        String respondentsSize = String.valueOf(respondents.size());
        Log.i("carol", respondentsSize);
        Log.i("carol", "last question: " + lastAnsweredQuestion);

        for (int respondent : respondents) {
            ArrayList<String> line = new ArrayList<>();
            //Log.i("carol", "topLine.size(): " + topLine.size());
            for (int i = 0; i < topLine.size(); i++) {
                line.add(i, FORA_DE_FLUXO);
            }
            line.set(0, key);
            String resp = String.valueOf(respondent);
            line.set(topLine.indexOf("Respondente"), resp);
            if(respondents.get(0).equals(respondent)){
                for (DataSnapshot keySnapshot : questionnaireSnapshot.getChildren()) {
                    try {
                        if(topLine.contains(keySnapshot.getKey())){
                            line.set(topLine.indexOf(keySnapshot.getKey()), keySnapshot.getValue().toString());
                        }

                        if(keySnapshot.getKey().equals("interviewerName")){
                            textView.append("Entrevistador: " + keySnapshot.getValue().toString() + "\n");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                line.set(topLine.indexOf("path"), dateInput.getText().toString());
                line.set(topLine.indexOf("lastQuestion"), lastAnsweredQuestion);
                line.set(topLine.indexOf("family"), respondentsSize);
                line.set(topLine.indexOf("type"), String.valueOf(questionnaireType));

            }

            for (AnswerableQuestion question : answeredQuestions) {


// if(repeatQuestions.contains(question.getId())){
// if ((topLine.contains(question.getId())) && (question.getAnswerValue() != null)){
// if (question.getAnswer().equals("")){
// Log.i("carol", "getAnswer == null | id: " + question.getId() + " | answerValue: " + question.getAnswerValue());
// }else {
// line.set(topLine.indexOf(question.getId()), question.getAnswerValue());
// }
// }
// }

                if((question.getRespondent() == respondent) || (repeatQuestions.contains(question.getId()))) {
                    String id = question.getId();
                    int index;

                    Question questionJson = getQuestionByID(id);
                    boolean changeQuestion = false;
                    if(questionJson != null) {
                        if (!questionJson.getType().equals(question.getType())) {
                            Log.i("carol", "--> id: " + question.getId() + " Type: " + question.getType() + " TypeJson: " + questionJson.getType());
                            if((questionJson.getType().equals("MultipleChoiceQuestion")) && (question.getType().equals("OnlyOneChoiceQuestion"))) {// && (!question.getAnswer().equals(""))){
                                if (!question.getAnswer().equals("")) {
                                    if(((MultipleChoiceQuestion) questionJson).getOptions().contains(question.getAnswer())){
                                        Log.i("carol", "opção existe");
                                        ((MultipleChoiceQuestion) questionJson).setAnswers(new ArrayList<String>());
                                        ((MultipleChoiceQuestion) questionJson).addAnswer(question.getAnswer());
                                        changeQuestion = true;
                                        Log.i("carol", "nova resposta: " + ((MultipleChoiceQuestion)questionJson).getAnswerValue());
                                        question = (AnswerableQuestion) questionJson;
                                    } else {
                                        Log.i("carol", "opção NÃO existe");
                                    }
                                }else{
                                    Log.i("carol", "Pergunta sem resposta");
                                }
                            }
                        }
                    }else{
                        //Log.i("carol", "respondente: " + respondent);
                        //Log.i("carol", "--> questionJsonType null | id: " + question.getId() + " ans_val: " + question.getAnswerValue());
                        switch (question.getId()){
                            case "CF.28.1":
                                question.setId("CF.28.1 - (PERGUNTA DE CONFERÊNCIA");
                                id = "CF.28.1 - (PERGUNTA DE CONFERÊNCIA";
                                break;
                            case "CF.31.1":
                                question.setId("CF.31.1 - (PERGUNTA DE CONFERÊNCIA");
                                id = "CF.31.1 - (PERGUNTA DE CONFERÊNCIA";
                                break;
                            case "CF.33.1":
                                question.setId("CF.33.1 - (PERGUNTA DE CONFERÊNCIA");
                                id = "CF.33.1 - (PERGUNTA DE CONFERÊNCIA";
                                break;
                        }
                        //Log.i("carol", "id corrigida: " + question.getId() + " | " + id);
                    }

                    if (changeQuestion && (questionJson.getType().equals("MultipleChoiceQuestion"))){

                        if(((MultipleChoiceQuestion)questionJson).getAnswerValue().equals(NAO_RESPONDEU_VALUE) ||
                                ((MultipleChoiceQuestion)questionJson).getAnswerValue().equals(NAO_SABE_VALUE) ||
                                ((MultipleChoiceQuestion)questionJson).getAnswerValue().equals(NAO_SE_APLICA_VALUE)) {
                            index = topLine.indexOf(id + "_N");
                            line.set(index, ((MultipleChoiceQuestion)questionJson).getAnswerValue());

                        } else {
                            for (int i = 0; i < ((MultipleChoiceQuestion)questionJson).getAnswerValue().length(); i++) {
                                index = topLine.indexOf(id + "_" + (i + 1));
                                char c = ((MultipleChoiceQuestion)questionJson).getAnswerValue().charAt(i);
                                if (index != -1) {
                                    Log.i("carol","multipla: " + String.valueOf(c));
                                    if(String.valueOf(c).equals("0")) {
                                        line.set(index, FORA_DE_FLUXO);
                                    }else{
// line.set(index, String.valueOf(c));
// line.set(index, String.valueOf(i + 1) );
                                        line.set(index, String.valueOf(1));
                                    }
                                }
                            }
                        }

                        changeQuestion = false;

                    } else if (question.getType().equals("MultipleChoiceQuestion")) {
                        if (question.getAnswerValue() != null) {
                            if(question.getAnswerValue().equals(NAO_RESPONDEU_VALUE) ||
                                    question.getAnswerValue().equals(NAO_SABE_VALUE) ||
                                    question.getAnswerValue().equals(NAO_SE_APLICA_VALUE)) {
                                index = topLine.indexOf(id + "_N");
                                line.set(index, question.getAnswerValue());

                            }else {
                                for (int i = 0; i < question.getAnswerValue().length(); i++) {
                                    index = topLine.indexOf(id + "_" + (i + 1));
                                    char c = question.getAnswerValue().charAt(i);
                                    if (index != -1) {
                                        if(String.valueOf(c).equals("0")) {
                                            line.set(index, FORA_DE_FLUXO);
                                        }else{
                                            line.set(index, String.valueOf(i + 1));
                                        }
                                    }
                                }
                            }
                        }
                    } else if(question.getType().equals("GeoLocationQuestion")){
                        if (question.getAnswerValue() != null) {
                            String string = question.getAnswerValue();
                            //Latitude
                            index = topLine.indexOf(id + "-" + "Latitude");
                            if (index != -1) {
                                line.set(index, string.substring(0,string.indexOf("|")));
                            }
                            //Longitude
                            index = topLine.indexOf(id + "-" + "Longitude");
                            if (index != -1) {
                                line.set(index, string.substring(string.indexOf("|")+1));
                            }
                        }
                    } else {
                        index = topLine.indexOf(id);
                        if ((index != -1) && (question.getAnswerValue() != null)){
                            if (question.getAnswer().equals("")){
                                Log.i("carol", "getAnswer == null | id: " + question.getId() + " | answerValue: " + question.getAnswerValue());
                            }else {
                                line.set(index, question.getAnswerValue());
                            }
                        }
                    }
                }
            }
            lines.add(line);
        }

        return lines;
    }

    void writeTopLine(){
        try {
            File file = new File(getExternalFilesDir(null), "export.csv");
            FileWriter fileWriter = new FileWriter(file, true);
            CSVWriter writer = new CSVWriter(fileWriter, ',');
            String[] topLineEntries = topLine.toArray(new String[0]);
            writer.writeNext(topLineEntries);
            writer.close();
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    void writeLines(ArrayList<ArrayList<ArrayList<String>>> writableContent) {
        try {
            File file = new File(getExternalFilesDir(null), "export.csv");
            FileWriter fileWriter = new FileWriter(file, true);
            CSVWriter writer = new CSVWriter(fileWriter, ',');
            //String[] topLineEntries = topLine.toArray(new String[0]);
            //writer.writeNext(topLineEntries);
            for (ArrayList<ArrayList<String>> lines : writableContent) {
                for (ArrayList<String> line : lines) {
                    //Log.i("carol", "line size: " + line.size());
                    String[] entries = line.toArray(new String[0]);
                    writer.writeNext(entries);
                }
            }
            writer.close();
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
            e.printStackTrace();
        }

    }

    void setTopLine() {
        String jsonStr = Utils.getFileFromAssetsAsString(this, MainActivity.FILE);
        try {
            ArrayList<String> allids = JsonUtils.getAllIds(jsonStr);
            for(String id : allids){
                topLine.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void getQuestionIdsAndTypes() {
        String jsonStr = Utils.getFileFromAssetsAsString(this, MainActivity.FILE);
        try {
            allIdsAndTypes = JsonUtils.getAllQuestionsType(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Question getQuestionByID(String id){
        for (Question question : allIdsAndTypes) {
            if (question.getId().equals(id)) {
                return question;
            }
        }
        return null;
    }

    ArrayList<AnswerableQuestion> iterateQuestionnaireSnapshot(DataSnapshot questionnaireSnapshot) {
        ArrayList<AnswerableQuestion> questions = new ArrayList<>();
        for (DataSnapshot questionnaireAttributeSnapshot : questionnaireSnapshot.getChildren()) {
            String questionnaireAttributeKey = questionnaireAttributeSnapshot.getKey();
            if (questionnaireAttributeKey != null && questionnaireAttributeKey.equals("answers")) {
                for (DataSnapshot blockSnapshot : questionnaireAttributeSnapshot.getChildren()) {
                    questions.add(getQuestion(blockSnapshot));
                }
            }

            if (questionnaireAttributeKey != null && questionnaireAttributeKey.equals("blocks")) {
                questionnaireType = 1;
                for (DataSnapshot blockSnapshot : questionnaireAttributeSnapshot.getChildren()) {
                    for (DataSnapshot blockAttributeSnapshot : blockSnapshot.getChildren()) {
                        questions.addAll(getQuestions(blockAttributeSnapshot));
                    }
                }
            }
        }
        return questions;
    }

    AnswerableQuestion getQuestion(DataSnapshot questionSnapshot) { //TODO: comparar tipos e acertar answer value -> para tirar o problema do 0 em onlyonechoice
        AnswerableQuestion answerableQuestion = new AnswerableQuestion();

        for (DataSnapshot questionAttributeSnapshot : questionSnapshot.getChildren()) {
            String questionAttributeKey = questionAttributeSnapshot.getKey();
            String questionAttributeValue = String.valueOf(questionAttributeSnapshot.getValue());

            if (questionAttributeKey != null && questionAttributeValue != null && !questionAttributeValue.equals("")) {
                if (questionAttributeKey.equals("id")) {
                    answerableQuestion.setId(questionAttributeValue);
                }
                if (questionAttributeKey.equals("answer")) {
                    answerableQuestion.setAnswer(questionAttributeValue);
                }
                if (questionAttributeKey.equals("answers")) {
                    answerableQuestion.setAnswers((ArrayList<String>) questionAttributeSnapshot.getValue());
                }
                if (questionAttributeKey.equals("respondent")) {
                    questionnaireType = 3;
                    answerableQuestion.setRespondent(Integer.parseInt(questionAttributeValue));
                }
                if (questionAttributeKey.equals("type")) {
                    if (questionAttributeValue.equals("NotAnswerableQuestion")){
                        return null;
                    }
                    answerableQuestion.setType(questionAttributeValue);
                }
                if (questionAttributeKey.equals("answerValue")) {
                    answerableQuestion.setAnswerValue(questionAttributeValue);
                }
            }
        }

        if((answerableQuestion.getType().equals("MultipleChoiceQuestion")) && (answerableQuestion.getAnswers() != null)) {
            answerableQuestion.setAnswerValue(updateAnswerValue(answerableQuestion));
            //Log.i("carol", "new answer value: " + answerableQuestion.getAnswerValue());
        }
        return answerableQuestion;
    }

    public String updateAnswerValue(AnswerableQuestion answerableQuestion){
        String answerValue = "";
        ArrayList<String> answersValue = new ArrayList<>();
        Question questionJson = getQuestionByID(answerableQuestion.getId());
        MultipleChoiceQuestion multipleChoiceQuestion = (MultipleChoiceQuestion) questionJson;

        for(int i=0; i <= ((MultipleChoiceQuestion)questionJson).getOptions().size() - 1; i++){
            answersValue.add("0");
        }

        for(String answer: answerableQuestion.getAnswers()) {
            switch (answer) {
                case NAO_SABE:
                    return (NAO_SABE_VALUE);

                case NAO_RESPONDEU:
                    return(NAO_RESPONDEU_VALUE);

                case NAO_SE_APLICA:
                    return(NAO_SE_APLICA_VALUE);

                default:

                    if(((MultipleChoiceQuestion) questionJson).getOptions().contains(answer)) {
                        answersValue.set(((MultipleChoiceQuestion) questionJson).getOptions().indexOf(answer), "1");
                        StringBuilder sb = new StringBuilder();
                        for (String s : answersValue) {
                            sb.append(s);
                        }
                        answerValue = sb.toString();

                    }else{
                        Log.i("carol", "ID aq: " + answerableQuestion.getId() + " answerableQuestion: " + answerableQuestion.getAnswers() + "AV: " + answerableQuestion.getAnswerValue());
                        Log.i("carol", "ID qj: " + questionJson.getId() + " questionJson" + ((MultipleChoiceQuestion) questionJson).getOptions());
                        if(answerableQuestion.getAnswerValue().length() != ((MultipleChoiceQuestion) questionJson).getOptions().size()) {
                            if ((answerableQuestion.getId().equals("CAP.1.1")) || (answerableQuestion.getId().equals("CAP.194"))) {

                                switch (answer) {
                                    case "Como pescador(a) de água doce (lagoa e rios)": // CAP.1.1 op2
                                        answersValue.set(((MultipleChoiceQuestion) questionJson).getOptions().indexOf("Como pescador(a) continental de rios, lagos e lagoas"), "1");
                                        break;
                                    case "Como pescador(a) de laguna": // CAP.1.1 op3
                                        answersValue.set(((MultipleChoiceQuestion) questionJson).getOptions().indexOf("Como pescador(a) continental de laguna"), "1");
                                        break;
                                    case "Casquinha de siri (un)":
                                        answersValue.set(((MultipleChoiceQuestion) questionJson).getOptions().indexOf("Casquinha de siri (unidade)"), "1");
                                        break;
                                    case "Hambúrguer de peixe (un)":
                                        answersValue.set(((MultipleChoiceQuestion) questionJson).getOptions().indexOf("Hambúrguer de peixe (unidade)"), "1");
                                        break;
                                    case "Quibe de peixe (un)":
                                        answersValue.set(((MultipleChoiceQuestion) questionJson).getOptions().indexOf("Quibe de peixe (unidade)"), "1");
                                        break;
                                }

                                StringBuilder sb = new StringBuilder();
                                for (String s : answersValue) {
                                    sb.append(s);
                                }
                                answerValue = sb.toString();

                                Log.i("carol", "ERRO!! -> novo answerValue: " + answerValue);
                            } else {
                                textView.append("ERRO ao recuperar dados!\nQuestão: " + answerableQuestion.getId() + "\n");
                                Log.i("carol", "ERRO ao recuperar dados! Questão: " + answerableQuestion.getId());
                                Log.i("carol", "aq_length:" + answerableQuestion.getAnswerValue().length() + " qj_size: " + ((MultipleChoiceQuestion) questionJson).getOptions().size());
                                answerValue = answerableQuestion.getAnswerValue();
                            }
                        }
                    }
            }
        }
        return answerValue;
    }



    ArrayList<AnswerableQuestion> getQuestions(DataSnapshot iterable) {
        ArrayList<AnswerableQuestion> questions = new ArrayList<>();
        String blockAttributeKey = iterable.getKey();
        if (blockAttributeKey != null && blockAttributeKey.equals("questions")) {
            for (DataSnapshot questionSnapshot : iterable.getChildren()) {
                AnswerableQuestion answerableQuestion = getQuestionData(questionSnapshot);
                if (answerableQuestion != null) {
                    questions.add(answerableQuestion);
                    if (answerableQuestion.getType() != null && (answerableQuestion.getType().equals("Table")
                            || answerableQuestion.getType().equals("RowQuestion"))) {
                        for (DataSnapshot questionAttributeSnapshot : questionSnapshot.getChildren()) {
                            questions.addAll(getQuestions(questionAttributeSnapshot));
                        }
                    }
                }
            }
        }
        return questions;
    }

    AnswerableQuestion getQuestionData(DataSnapshot questionSnapshot) {
        AnswerableQuestion question = new AnswerableQuestion();
        for (DataSnapshot questionAttributeSnapshot : questionSnapshot.getChildren()) {
            String questionAttributeKey = questionAttributeSnapshot.getKey();
            String questionAttributeValue = String.valueOf(questionAttributeSnapshot.getValue());
            if (questionAttributeKey != null && questionAttributeValue != null && !questionAttributeValue.equals("")) {
                if (questionAttributeKey.equals("id")) {
                    question.setId(questionAttributeValue);
                }
                if (questionAttributeKey.equals("answer")) {
                    question.setAnswer(questionAttributeValue);
                }
                if (questionAttributeKey.equals("respondent")) {
                    question.setRespondent(Integer.parseInt(questionAttributeValue));
                }
                if (questionAttributeKey.equals("type")) {
                    question.setType(questionAttributeValue);
                }
                if (questionAttributeKey.equals("answerValue")) {
                    question.setAnswerValue(questionAttributeValue);
                }
            }
        }

        if (question.getType().equals("NotAnswerableQuestion")) {
            return null;
        }

        return question;
    }

    public void refreshDate(String date, String indicator) {
// if (indicator.equals("from")) {
// fromView.setText(date);
// }
// if (indicator.equals("to")) {
// toView.setText(date);
// }
    }
}