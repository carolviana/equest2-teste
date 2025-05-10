package com.example.newequest.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.example.newequest.R;
import com.example.newequest.activity.MainActivity;
import com.example.newequest.adapters.QuestionnaireDownloadAdapter;
import com.example.newequest.database.AppDatabase;
import com.example.newequest.model.Block;
import com.example.newequest.model.Person;
import com.example.newequest.model.Questionnaire;
import com.example.newequest.model.Session;
import com.example.newequest.model.question.AnswerableQuestion;
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
import com.example.newequest.util.JsonUtils;
import com.example.newequest.util.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.ArrayList;

public class QuestionnaireDownloadFragment extends DialogFragment {

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

    Questionnaire questionnaire;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_questionnaire_download, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        EditText etCode = rootView.findViewById(R.id.et_cod);
        TextView tvQuestionnaire = rootView.findViewById((R.id.tv_quest_info));
        Button btSave = rootView.findViewById(R.id.bt_save);
        Button btOk = rootView.findViewById(R.id.bt_ok);
        ProgressBar progressBar = rootView.findViewById(R.id.loading);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("carol", "btOk");
                progressBar.setVisibility(View.VISIBLE);

                String code = etCode.getText().toString();
//                Log.i("carol", code);
                if(!code.equals("")) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    try {
                        DatabaseReference itemReference = firebaseDatabase.getReference("database").child("incomplete").child(code);
                        ValueEventListener mReferenceListener = itemReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.getValue()!=null){
                                    Log.i("carol", "not null " + snapshot.getChildrenCount());

                                    questionnaire = snapshot.getValue(Questionnaire.class);
                                    questionnaire.setPrimaryKey(0);

//                                    blocks
                                    ArrayList<Block> blocks = new ArrayList<>();
                                    for(DataSnapshot blocksSnapshot : snapshot.child("blocks").getChildren()){
                                        Block block = new Block();
//                                        Log.i("carol", blocksSnapshot.getKey());
                                        ArrayList<Question> questions = new ArrayList<>();
                                        for(DataSnapshot questionSnapshot : blocksSnapshot.child("questions").getChildren()){
//                                            Log.i("carol", "q: " + questionSnapshot.child("type").getValue().toString());
                                            questions.add(questionFactory(questionSnapshot));
                                        }
                                        Log.i("carol", "Bloco: " + blocksSnapshot.child("title").getValue().toString());
                                        Log.i("carol", "total perguntas: " + questions.size());

                                        block.setQuestions(questions);
                                        block.setTitle(blocksSnapshot.child("title").getValue().toString());

                                        blocks.add(block);
                                    }
                                    questionnaire.setBlocks(blocks);

//                                    lastQuestion
                                    questionnaire.setLastQuestion(questionFactory(snapshot.child("lastQuestion")));

                                    tvQuestionnaire.setText("Questionario:\n\n   " + questionnaire.getMainRespondent().getName());
                                    tvQuestionnaire.append("\n   " + questionnaire.getCity());
                                    tvQuestionnaire.append(questionnaire.getInterviewerName() != null ? "\n   " + questionnaire.getInterviewerName() : "\n   Entrevistador não identificado");
                                    btSave.setVisibility(View.VISIBLE);

                                }else{
                                    Log.i("carol", "null");
                                    tvQuestionnaire.setText("Questionário não encontrado!\nTente novamente");
                                    btSave.setVisibility(View.GONE);

                                }
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } catch (OutOfMemoryError error){
                        error.printStackTrace();
                        Toast.makeText(getContext(), "Sem memória para realizar essa operação", Toast.LENGTH_SHORT).show();
                        Log.i("carol", "sem memoria");
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception exception){
                        exception.printStackTrace();
                        Toast.makeText(getContext(), "Erro: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.i("carol", "deu erro!");
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("carol", "btSave");

                final AppDatabase db = AppDatabase.getInstance(getContext());
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            long id = db.questionnaireDao().insertQuestionnaire(questionnaire);
                            Log.i("carol","salvando... " + id);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

                tvQuestionnaire.setText("Download de questionário completo!");
                btSave.setVisibility(View.GONE);

//                for(Block block : questionnaire.getBlocks()){
//                    for (Question question : block.getQuestions()){
//                        Log.i("carol", question.getId());
//                    }
//                }
            }
        });

        return rootView;
    }

    private Question questionFactory(DataSnapshot questionSnapshot) {
        switch (questionSnapshot.child("type").getValue().toString()) {
            case NOT_ANSWERABLE_TYPE:
                return((NotAnswerableQuestion) questionSnapshot.getValue(NotAnswerableQuestion.class));
            case EDIT_TYPE:
                return((EditQuestion) questionSnapshot.getValue(EditQuestion.class));
            case MULTIPLE_CHOICE_TYPE:
                return((MultipleChoiceQuestion) questionSnapshot.getValue(MultipleChoiceQuestion.class));
            case ONLY_ONE_CHOICE_TYPE:
                return((OnlyOneChoiceQuestion) questionSnapshot.getValue(OnlyOneChoiceQuestion.class));
            case PERSON_CREATOR_TYPE:
                return((PersonCreatorQuestion) questionSnapshot.getValue(PersonCreatorQuestion.class));
            case SPINNER_CHOICE_TYPE:
                return((SpinnerChoiceQuestion) questionSnapshot.getValue(SpinnerChoiceQuestion.class));
            case GEO_LOCATION_TYPE:
                return((GeoLocationQuestion) questionSnapshot.getValue(GeoLocationQuestion.class));
            case CITY_CHECK_TYPE:
                return((CityCheckQuestion) questionSnapshot.getValue(CityCheckQuestion.class));
            default:
                Log.i("carol", "não identificou tipo!! - " + questionSnapshot.child("type").getValue().toString());
                return((AnswerableQuestion) questionSnapshot.getValue(AnswerableQuestion.class));
        }
    }

    private int verifyExistence(String questionID, Block block) {
        for(Question question : block.getQuestions()){
            if(question.getId().equals(questionID)){
                return block.getQuestions().indexOf(question);
            }
        }
        return(-1);
    }

}
