package com.example.newequest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newequest.R;
import com.example.newequest.activity.AnswerActivity;
import com.example.newequest.fragment.MultipleChoiceQuestionFragment;

import java.util.ArrayList;

public class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.MyViewHolder> {

    //TODO: Acrescentar suporte a OpOther
    private final int flagReceiver;
    private Boolean hasAnswer;
    private String others;
    private ArrayList<String> options;
    private Context context;
    private int selectedButton;
    private ArrayList<Boolean> selectedButtons;

    public interface onOptionClickWithinAdapterListener {
        void onOptionClickWithinAdapter(String option, boolean isChecked);
    }

    public onOptionClickWithinAdapterListener mCallback;

    public ChoicesAdapter(Context context, ArrayList<String> options, onOptionClickWithinAdapterListener callback, int choiceTypeFlag) {
        this.context = context;
        this.options = options;
        this.mCallback = callback;
        this.flagReceiver = choiceTypeFlag;

        this.selectedButtons = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            boolean check = false;
            selectedButtons.add(check);
        }

        selectedButton = -1;
    }

    public void setMultipleAnswers(ArrayList<String> answersArray){
        for(int i=0; i<answersArray.size(); i++ ){
            if(options.contains(answersArray.get(i))){
                int position = options.indexOf(answersArray.get(i));
                selectedButtons.set(position, true);
                notifyItemChanged(position);
            }
        }
    }

    public void setSingleAnswer(String singleAnswer){
        selectedButton = options.indexOf(singleAnswer);
        notifyItemChanged(options.indexOf(singleAnswer));
    }

    public Boolean isAnswered() {
        return hasAnswer;
    }

    public void setAnswered(Boolean answered) {
        hasAnswer = answered;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemChoice = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_choice, viewGroup, false);
        return new ChoicesAdapter.MyViewHolder(itemChoice);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.button.setText(options.get(i));

        if(flagReceiver == AnswerActivity.ONLY_ONE_CHOICE_FLAG){
            if(isAnswered() && selectedButton==i){
                myViewHolder.button.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_orange_whiteborder));
                myViewHolder.button.setTextColor(ContextCompat.getColor(context, R.color.white));
            }else{
                myViewHolder.button.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_white_orangeborder));
                myViewHolder.button.setTextColor(ContextCompat.getColor(context, R.color.tx_orange));
            }
        }

        if(flagReceiver == AnswerActivity.MULTIPLE_CHOICE_FLAG){
            if(isAnswered() && selectedButtons.get(i)){
                myViewHolder.button.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_square_orange_whiteborder));
                myViewHolder.button.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                myViewHolder.button.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_square_white_orangeborder));
                myViewHolder.button.setTextColor(ContextCompat.getColor(context, R.color.tx_orange));
            }
        }
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private Button button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.bt_choice);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(flagReceiver == AnswerActivity.ONLY_ONE_CHOICE_FLAG){
                        mCallback.onOptionClickWithinAdapter(options.get(getAdapterPosition()), true);
                        button.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_orange_whiteborder));
                        button.setTextColor(ContextCompat.getColor(context, R.color.white));
                        int position = selectedButton;
                        selectedButton = getAdapterPosition();
                        notifyItemChanged(position);
                    }

                    if(flagReceiver == AnswerActivity.MULTIPLE_CHOICE_FLAG){
                        //verifica se botão anteriormente marcado é NR NS NA e desmarca
                        for(int i=0; i<selectedButtons.size(); i++){
                            if( (options.get(i).equals(MultipleChoiceQuestionFragment.NAO_RESPONDEU) && selectedButtons.get(i)) ||
                                    (options.get(i).equals(MultipleChoiceQuestionFragment.NAO_SABE) && selectedButtons.get(i)) ||
                                    (options.get(i).equals(MultipleChoiceQuestionFragment.NAO_SE_APLICA) && selectedButtons.get(i)) ){
                                selectedButtons.set(i,false);
                                mCallback.onOptionClickWithinAdapter(options.get(i), false);
                                notifyItemChanged(i);
                            }
                        }

                        //Verifica se o botão marcado por ultimo é NR NS NA.
                        if(options.get(getAdapterPosition()).equals(MultipleChoiceQuestionFragment.NAO_RESPONDEU) ||
                                options.get(getAdapterPosition()).equals(MultipleChoiceQuestionFragment.NAO_SABE) ||
                                options.get(getAdapterPosition()).equals(MultipleChoiceQuestionFragment.NAO_SE_APLICA)){
                            //Desmarca todos os q estejam marcados
                            for(int i = 0; i < selectedButtons.size(); i++){
                                if (selectedButtons.get(i)) {
                                    selectedButtons.set(i,false);
                                    mCallback.onOptionClickWithinAdapter(options.get(i), false);
                                    notifyItemChanged(i);
                                }
                            }
                            //Marca o NR NS ou NA
                            mCallback.onOptionClickWithinAdapter(options.get(getAdapterPosition()), true);
                            selectedButtons.set(getAdapterPosition(), true);
                            button.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_square_orange_whiteborder));
                            button.setTextColor(ContextCompat.getColor(context, R.color.white));

                        }else {
                            //Se o botão ja estava marcado: desmarca
                            if (selectedButtons.get(getAdapterPosition())) {
                                mCallback.onOptionClickWithinAdapter(options.get(getAdapterPosition()), false);
                                selectedButtons.set(getAdapterPosition(), false);
                                notifyItemChanged(getAdapterPosition());
                            } else {
                                //Se o botão não estava marcado: marca
                                mCallback.onOptionClickWithinAdapter(options.get(getAdapterPosition()), true);
                                selectedButtons.set(getAdapterPosition(), true);
                                button.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_square_orange_whiteborder));
                                button.setTextColor(ContextCompat.getColor(context, R.color.white));
                            }
                        }
                    }
                }
            });
        }
    }
}
