package com.example.newequest.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newequest.R;
import com.example.newequest.activity.AnswerActivity;
import com.example.newequest.model.Person;
import com.example.newequest.model.QuestionnaireEntry;

import java.util.List;

public class ReportsAdapter  extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {

    private List<QuestionnaireEntry> questionnaireEntries;
    private Context context;
    private ReportsEntryAdapterOnClickHandler onClickHandler;

    public interface ReportsEntryAdapterOnClickHandler {
        void onClickComplete(int primaryKey);
        void onClickIncomplete(int primaryKey);
    }

    public ReportsAdapter(Context context, ReportsEntryAdapterOnClickHandler onClickHandler) {
        this.context = context;
        this.onClickHandler = onClickHandler;
    }

    public List<QuestionnaireEntry> getQuestionnaireEntries() {
        return questionnaireEntries;
    }

    public void setQuestionnaireEntries(List<QuestionnaireEntry> questionnaireEntries) {
        this.questionnaireEntries = questionnaireEntries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_report, parent, false);
        return new ReportsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Person mainRespondent = questionnaireEntries.get(position).getMainRespondent();
        String name;
        if (mainRespondent != null && mainRespondent.getName() != null) {
            name = mainRespondent.getName();
        } else {
            name = "Questionário sem nome definido";
        }
        holder.nameView.setText(name);
        if (questionnaireEntries.get(position).isComplete()) {
            holder.statusView.setText("Completo");
        } else {
            holder.statusView.setText("Incompleto");
        }
        if ((position % 2) == 0){
            holder.rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        } else {
            holder.rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.lighter_blue));
        }
    }

    @Override
    public int getItemCount() {
        if (questionnaireEntries == null) return 0;
        return questionnaireEntries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout rowView;
        private final TextView nameView;
        private final TextView statusView;
        ViewHolder(View itemView) {
            super(itemView);
            rowView = itemView.findViewById(R.id.row);
            nameView = itemView.findViewById(R.id.name);
            statusView = itemView.findViewById(R.id.status);
            statusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    int pk = questionnaireEntries.get(adapterPosition).getPrimaryKey();
                    //Log.i("carol", "pk: " + pk);
                    if(statusView.getText().equals("Completo")) {
                        onClickHandler.onClickComplete(pk);
                    }else{
                        onClickHandler.onClickIncomplete(pk);
                    }
                }
            });

        }
    }
}
