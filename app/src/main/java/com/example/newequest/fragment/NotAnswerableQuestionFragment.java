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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newequest.R;
import com.example.newequest.model.question.NotAnswerableQuestion;
import com.example.newequest.model.question.Question;

public class NotAnswerableQuestionFragment extends QuestionFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_not_answerable_question, container, false);

        Bundle bundle = getArguments();
        //String title = bundle.getString("title");
        Question question = bundle.getParcelable("question");
        String respondent = bundle.getString("respondent");
        boolean isClosure = ((NotAnswerableQuestion) question).isClosure();
        boolean sendStatus = ((NotAnswerableQuestion) question).isSendStatus();
        bundle.clear();

        ImageView previousButton = rootView.findViewById(R.id.previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onPreviousButtonClick();
            }
        });

        final int[] clicked = {0};
        ImageView nextButton = rootView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked[0]++;
                if(clicked[0] == 1) {
                    mCallback.onNextButtonClick();
                }
            }
        });

        if (isClosure){
            previousButton.setVisibility(View.INVISIBLE);
            ImageView closureView = rootView.findViewById(R.id.closure);
            ImageView noClosureView = rootView.findViewById(R.id.noclosure);
            if(sendStatus){
                closureView.setVisibility(View.VISIBLE);
            }else{
                noClosureView.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.GONE);
            }
        }

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

//        TextView questionView = rootView.findViewById(R.id.question);
//        questionView.setText(title);





        return rootView;
    }
}