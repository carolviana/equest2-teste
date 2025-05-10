package com.example.newequest.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newequest.R;
import com.example.newequest.activity.AnswerActivity;
import com.example.newequest.model.question.EditQuestion;

public class EditQuestionFragment extends QuestionFragment {

    public static final String LOG_TAG = EditQuestionFragment.class.getName();
    private String respondent = "";
    private EditQuestion question;
    private boolean isAnswered = false;

    private EditText inputView;
    private RadioGroup enesView;
    private RadioButton enes1View;
    private RadioButton enes2View;
    private RadioButton enes3View;

    public static final String NAO_SABE = "Não sabe";
    public static final String NAO_RESPONDEU = "Não respondeu";
    public static final String NAO_SE_APLICA = "Não se aplica";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_edit_question, container, false);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }

        Bundle bundle = getArguments();
        question = bundle.getParcelable("question");
        final int flagReceiver = bundle.getInt("question_type_flag");

        String questionOutput;

        ImageView previousButton = rootView.findViewById(R.id.previous);
        ImageView nextButton = rootView.findViewById(R.id.next);
        final View inputMaskView = rootView.findViewById(R.id.input_mask);

        inputView = rootView.findViewById(R.id.input);

        if (question.isAllowOnlyNumbers()){
            inputView.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        if(question.getSize() != null) {
            inputView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(question.getSize())});
        }

        if (flagReceiver != AnswerActivity.PERSON_CREATOR_FLAG){
            respondent = bundle.getString("respondent");

            if((question.isReplica()) && (!question.getTitle().contains("[ ]"))){
                questionOutput = question.getTitle().concat(" (" + respondent + ")");
            }else {
                questionOutput = question.getTitle().replace("[ ]", respondent);
            }
        } else {
            if(question.getRespondent()>0) {
                questionOutput = question.getTitle() + " (pessoa " + (question.getRespondent() + 1) + ")";
            }else{
                questionOutput = question.getTitle() + " (respondente principal)";
            }

            if(question.getAnswer().equals("")){
                previousButton.setVisibility(View.GONE);
            }else{
                previousButton.setVisibility(View.VISIBLE);
            }
        }

        bundle.clear();

        if((flagReceiver == AnswerActivity.REPLICATION_FLAG) && (!question.getAnswer().equals(""))){
            inputView.setFocusable(false);
            inputView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.shape_round_gray_darkgrayborder));
            inputView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }

        TextView questionView = rootView.findViewById(R.id.question);

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

        enesView = rootView.findViewById(R.id.enes);
        enes1View = rootView.findViewById(R.id.enes1);
        enes2View = rootView.findViewById(R.id.enes2);
        enes3View = rootView.findViewById(R.id.enes3);


        if(question.hasReference()){
            switch (question.getAnswer()) {
                case NAO_SABE: enes1View.setChecked(true); break;
                case NAO_RESPONDEU: enes2View.setChecked(true); break;
                case NAO_SE_APLICA: enes3View.setChecked(true); break;
                default: {
                    if (inputView.length() == 0) {
                        inputView.append(question.getReference());
                    }
                }
            }
        }

        if(!question.getAnswer().equals("")){
            isAnswered = true;
            switch (question.getAnswer()) {
                case NAO_SABE: enes1View.setChecked(true); break;
                case NAO_RESPONDEU: enes2View.setChecked(true); break;
                case NAO_SE_APLICA: enes3View.setChecked(true); break;
                default: {
                    if (inputView.length() == 0) {
                        inputView.append(question.getAnswer());
                    }
                }
            }
        }

        inputView.requestFocus();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(flagReceiver == AnswerActivity.REPLICATION_FLAG)) {

                    String input = inputView.getText().toString();
                    if (!input.equals("") || enes1View.isChecked() || enes2View.isChecked() || enes3View.isChecked()) {
                        if (enes1View.isChecked()) {
                            question.setAnswer(NAO_SABE);
                        } else if (enes2View.isChecked()) {
                            question.setAnswer(NAO_RESPONDEU);
                        } else if (enes3View.isChecked()) {
                            question.setAnswer(NAO_SE_APLICA);
                        } else {
                            if (input.endsWith(" ")) {
                                input = input.substring(0, input.length() - 1);
                            }
                            question.setAnswer(input);
                        }

                        imm.hideSoftInputFromWindow(inputView.getWindowToken(), 0);

                        if (flagReceiver == AnswerActivity.EDIT_FLAG) {
                            mCallback.onNextButtonClick();
                        } else if (flagReceiver == AnswerActivity.PERSON_CREATOR_FLAG) {
                            if (isAnswered) {
                                mCallback.onPersonCreatorNextButtonClick(input, question.getRespondent(), "update", "next");
                            } else {
                                mCallback.onPersonCreatorNextButtonClick(input, question.getRespondent(), "create", "next");
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Preencha o campo para continuar", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mCallback.onNextButtonClick();
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(flagReceiver == AnswerActivity.REPLICATION_FLAG)) {
                    String input = inputView.getText().toString();
                    if (!input.equals("") || enes1View.isChecked() || enes2View.isChecked() || enes3View.isChecked()) {
                        if (enes1View.isChecked()) {
                            question.setAnswer(NAO_SABE);
                        } else if (enes2View.isChecked()) {
                            question.setAnswer(NAO_RESPONDEU);
                        } else if (enes3View.isChecked()) {
                            question.setAnswer(NAO_SE_APLICA);
                        } else {
                            if (input.endsWith(" ")) {
                                input = input.substring(0, input.length() - 1);
                            }
                            question.setAnswer(input);
                        }
                    }

                    imm.hideSoftInputFromWindow(inputView.getWindowToken(), 0);

                    if (flagReceiver == AnswerActivity.EDIT_FLAG) {
                        mCallback.onPreviousButtonClick();
                    } else if (flagReceiver == AnswerActivity.PERSON_CREATOR_FLAG) {
                        if (isAnswered) {
                            mCallback.onPersonCreatorNextButtonClick(input, question.getRespondent(), "update", "previous");
                        } else {
                            mCallback.onPersonCreatorNextButtonClick(input, question.getRespondent(), "create", "previous");
                        }
                    }
                }else{
                    mCallback.onPreviousButtonClick();
                }
            }
        });

        inputMaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputView.setEnabled(true);
                if (imm != null) {
                    imm.restartInput(inputView);
                    inputView.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                }
                enesView.clearCheck();
            }
        });

        enes1View.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (inputView.length() > 0) {
                        inputView.getText().clear();
                    }
                    inputView.setEnabled(false);
                }
            }
        });

        enes2View.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (inputView.length() > 0) {
                        inputView.getText().clear();
                    }
                    inputView.setEnabled(false);
                }
            }
        });

        enes3View.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (inputView.length() > 0) {
                        inputView.getText().clear();
                    }
                    inputView.setEnabled(false);
                }
            }
        });

        if(!question.isShowDontKnow()){
            enes1View.setVisibility(View.GONE);
        }

        if(!question.isShowDontAnswer()){
            enes2View.setVisibility(View.GONE);
        }

        if(!question.isShowDontAply()){
            enes3View.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onResume() {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.restartInput(inputView);
        inputView.requestFocus();

        super.onResume();
    }

    @Override
    public void onPause() {
        View view = getActivity().findViewById(R.id.background_cl);
        view.setOnTouchListener(null);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        String input = inputView.getText().toString();
        if (!input.equals("") || enes1View.isChecked() || enes2View.isChecked() || enes3View.isChecked()){
            if(enes1View.isChecked()){
                question.setAnswer(NAO_SABE);
            }else if(enes2View.isChecked()){
                question.setAnswer(NAO_RESPONDEU);
            }else if(enes3View.isChecked()){
                question.setAnswer(NAO_SE_APLICA);
            }else{
                question.setAnswer(input);
            }
        }else {
            Toast.makeText(getActivity(), "Preencha o campo para continuar", Toast.LENGTH_SHORT).show();
        }

        super.onDestroy();
    }
}