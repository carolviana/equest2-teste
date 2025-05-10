package com.example.newequest.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.newequest.R;
import com.example.newequest.model.question.SpinnerChoiceQuestion;

public class SpinnerQuestionFragment extends QuestionFragment{

    private String respondent = "";
    private SpinnerChoiceQuestion question;
    private Spinner inputView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_spinner_question, container, false);

        Bundle bundle = getArguments();
        question = bundle.getParcelable("question");
        respondent = bundle.getString("respondent");
        //String questionOutput = question.getTitle().replace("[ ]", respondent);
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

        inputView = rootView.findViewById(R.id.input);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.item_spinner, question.getOptions());
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        inputView.setAdapter(adapter);

        //
        if(!question.getAnswer().equals("")){
            inputView.setSelection(question.getOptions().indexOf(question.getAnswer()));
        }
        //

        ImageView nextButton = rootView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputView.getSelectedItem().toString();
                question.setAnswer(input);
                mCallback.onNextButtonClick();
            }
        });

        ImageView previousButton = rootView.findViewById(R.id.previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputView.getSelectedItem().toString();
                question.setAnswer(input);
                mCallback.onPreviousButtonClick();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        String input = inputView.getSelectedItem().toString();
        question.setAnswer(input);
        super.onDestroy();
    }
}