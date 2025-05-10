package com.example.newequest.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newequest.R;
import com.example.newequest.activity.AnswerActivity;
import com.example.newequest.adapters.ChoicesAdapter;
import com.example.newequest.model.question.MultipleChoiceQuestion;

import java.util.ArrayList;

public class MultipleChoiceQuestionFragment extends QuestionFragment implements ChoicesAdapter.onOptionClickWithinAdapterListener {

    public static final String LOG_TAG = MultipleChoiceQuestionFragment.class.getName();
    public static final String NAO_SABE = "Não sabe";
    public static final String NAO_RESPONDEU = "Não respondeu";
    public static final String NAO_SE_APLICA = "Não se aplica";

    private MultipleChoiceQuestion question;

    private EditText otherInputView;
    private String respondent = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choice_question, container, false);

        Bundle bundle = getArguments();
        question = bundle.getParcelable("question");
        respondent = bundle.getString("respondent");
        bundle.clear();

        String questionOutput;

        TextView questionView = rootView.findViewById(R.id.question);

        if((question.isReplica()) && (!question.getTitle().contains("[ ]"))){
            questionOutput = question.getTitle().concat(" (" + respondent + ")");
        }else {
            questionOutput = question.getTitle().replace("[ ]", respondent);
        }

        try {
            Spannable wordToSpan = new SpannableString(questionOutput);
            wordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.tx_orange)),
                    questionOutput.indexOf(respondent),
                    questionOutput.indexOf(respondent) + respondent.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            questionView.setText(wordToSpan);
        }catch (Exception e){
            e.printStackTrace();
            questionView.setText(questionOutput);
        }

        //GridView grid = rootView.findViewById(R.id.choices_grid);
        RecyclerView grid = rootView.findViewById(R.id.choices_grid);

        ChoicesAdapter adapter;

        if((question.getOpOther() != null) && (!question.getOptions().get(question.getOptions().size() - 1).equals(question.getOpOther()))){
            question.getOptions().add(question.getOpOther());
        }

//        adapter = new ChoicesAdapter(getActivity(),
//                question.getOptions(),
//                R.layout.item_multiple_choice,
//                this,
//                AnswerActivity.MULTIPLE_CHOICE_FLAG);
        adapter = new ChoicesAdapter(getContext(), question.getOptions(), this, AnswerActivity.MULTIPLE_CHOICE_FLAG);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        layoutManager.canScrollVertically();
        grid.setLayoutManager(layoutManager);
        grid.setHasFixedSize(true);

        //verifica se a pergunta já foi respondida
        if(question.getAnswers().isEmpty()){
            question.setAnswers(new ArrayList<String>());
            adapter.setAnswered(false);
        }else{
            adapter.setMultipleAnswers(question.getAnswers());
            adapter.setAnswered(true);
        }
        //

        grid.setAdapter(adapter);

        otherInputView = rootView.findViewById(R.id.other_input);

        ImageView nextButton = rootView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String opOther = otherInputView.getText().toString();
                if (!opOther.equals("")){
                    //question.getAnswers().add(opOther);
                    question.addAnswer(opOther);
                    //TODO treat the opOther that is adding to the list improperly
                }
                mCallback.onNextButtonClick();
            }
        });

        ImageView previousButton = rootView.findViewById(R.id.previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opOther = otherInputView.getText().toString();
                if (!opOther.equals("")){
                    //question.getAnswers().add(opOther);
                    question.addAnswer(opOther);
                    //TODO treat the opOther that is adding to the list improperly
                }
                mCallback.onPreviousButtonClick();
            }
        });

        return rootView;
    }

    @Override
    public void onOptionClickWithinAdapter(String option, boolean isChecked) {

        //Log.i("carol", option + " " + isChecked);

        if (option.equals(MultipleChoiceQuestionFragment.NAO_RESPONDEU)
                || option.equals(MultipleChoiceQuestionFragment.NAO_SABE)
                || option.equals(MultipleChoiceQuestionFragment.NAO_SE_APLICA)){
            question.getAnswers().clear();
        }

        for (int i = 0; i < question.getOptions().size(); i++){
            if (question.getOptions().get(i).equals(option)){
                if (isChecked){
                    question.addAnswer(option);
                } else {
                    question.removeAnswer(option);
                }
            }
        }

        if (option.equals(question.getOpOther())){
            if (isChecked){
                otherInputView.setVisibility(View.VISIBLE);
            } else {
                otherInputView.setVisibility(View.GONE);
            }
        }
    }
}