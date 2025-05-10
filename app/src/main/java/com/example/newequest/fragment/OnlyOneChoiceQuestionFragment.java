package com.example.newequest.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newequest.R;
import com.example.newequest.activity.AnswerActivity;
import com.example.newequest.adapters.ChoicesAdapter;
import com.example.newequest.model.question.OnlyOneChoiceQuestion;

public class OnlyOneChoiceQuestionFragment extends QuestionFragment implements ChoicesAdapter.onOptionClickWithinAdapterListener {

    private OnlyOneChoiceQuestion question;

    private EditText otherInputView;
    private String respondent = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choice_question, container, false);

        Bundle bundle = getArguments();
        question = bundle.getParcelable("question");
        respondent = bundle.getString("respondent");

        final int flagReceiver = bundle.getInt("question_type_flag");
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

        ChoicesAdapter adapter = new ChoicesAdapter(getContext().getApplicationContext(),
                question.getOptions(),
                this,
                AnswerActivity.ONLY_ONE_CHOICE_FLAG);

        //GridView grid = rootView.findViewById(R.id.choices_grid);
        RecyclerView grid = rootView.findViewById(R.id.choices_grid);
        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        layoutManager.canScrollVertically();
        grid.setLayoutManager(layoutManager);
        grid.setHasFixedSize(true);

        grid.setAdapter(adapter);

        otherInputView = rootView.findViewById(R.id.other_input);
        otherInputView.setHint("Digite aqui");

       /* if (question.getOptions().get(1).equals("Não") && question.getOptions().size() == 2){
            ArrayList<String> yesOrNoOptions = new ArrayList<>();
            yesOrNoOptions.add("Sim");
            yesOrNoOptions.add("Não");

            adapter = new ChoicesAdapter(getActivity(),
                                         yesOrNoOptions,
                                         R.layout.item_only_one_choice,
                                         this,
                                         AnswerActivity.ONLY_ONE_CHOICE_FLAG);



       if (question.getOpOther() != null){
           otherInputView.setHint(question.getOpOther());
       }
    } else {*/

        if((question.getOpOther() != null) && (!question.getOptions().get(question.getOptions().size() - 1).equals(question.getOpOther()) )){
            question.getOptions().add(question.getOpOther());
            otherInputView.setHint("Digite aqui");
        }

//        adapter = new ChoicesAdapter(getActivity(),
//                question.getOptions(),
//                R.layout.item_only_one_choice,
//                this,
//                AnswerActivity.ONLY_ONE_CHOICE_FLAG);


        // }



        if (question.getOpOther() != null){
            adapter.setOthers(question.getOpOther());
        }

        //verifica se a pergunta já foi respondida
        if(!question.getAnswer().equals("")) {
            adapter.setAnswered(true);
            adapter.setSingleAnswer(question.getAnswer());
            if(question.getOpOther() != null){
                if (question.getAnswer().startsWith(question.getOpOther())) {
                    otherInputView.setVisibility(View.VISIBLE);
                    String text = question.getAnswer().substring(question.getOpOther().length() + 2);
                    otherInputView.setText(text);
                }
            }
        }else{
            adapter.setAnswered(false);
        }
        //

        ImageView nextButton = rootView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String opOther = otherInputView.getText().toString();
                if (!opOther.equals("")){
                    //Utils.hideSoftKeyboard(getActivity());
                    //String answer = question.getAnswer();
                    question.setAnswer(question.getOpOther() + ", " + opOther);
                }
                if (flagReceiver == AnswerActivity.CITY_CHECK_FLAG){
                    mCallback.onCityCheckNextButtonClick(question.getAnswer(),"next");
                } else {
                    mCallback.onNextButtonClick();
                }
            }
        });

        ImageView previousButton = rootView.findViewById(R.id.previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opOther = otherInputView.getText().toString();
                if (!opOther.equals("")){
                    //Utils.hideSoftKeyboard(getActivity());
                    //String answer = question.getAnswer();
                    question.setAnswer(question.getOpOther() + ", " + opOther);
                }
                if (flagReceiver == AnswerActivity.CITY_CHECK_FLAG){
                    mCallback.onCityCheckNextButtonClick(question.getAnswer(),"previous");
                } else {
                    mCallback.onPreviousButtonClick();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onOptionClickWithinAdapter(String option, boolean isChecked) {
        if (!isChecked){
            question.setAnswer("");
            otherInputView.setVisibility(View.GONE);
            otherInputView.setText("");
        } else {
            question.setAnswer(option);
            if (option.equals(question.getOpOther())){
                if (question.getOpOther() != null){
                    otherInputView.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}