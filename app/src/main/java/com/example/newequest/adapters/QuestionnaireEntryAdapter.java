package com.example.newequest.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newequest.R;
import com.example.newequest.activity.ReportsActivity;
import com.example.newequest.model.Person;
import com.example.newequest.model.QuestionnaireEntry;

import java.util.List;

public class QuestionnaireEntryAdapter extends RecyclerView.Adapter<QuestionnaireEntryAdapter.ViewHolder> {

    private List<QuestionnaireEntry> questionnaireEntries;
    private QuestionnaireEntryAdapterOnClickHandler onClickHandler;
    private Context context;

    public interface QuestionnaireEntryAdapterOnClickHandler {
        void onClickEdit(int primaryKey, int position);
        void onClickDelete(int primaryKey, int position);
        void onClickUpload(int primaryKey, int position);
    }

    public QuestionnaireEntryAdapter(QuestionnaireEntryAdapterOnClickHandler onClickHandler) {
        this.onClickHandler = onClickHandler;
        this.context = (Context) onClickHandler;
    }

    public List<QuestionnaireEntry> getQuestionnaireEntries() {
        return questionnaireEntries;
    }

    public void setQuestionnaireEntries(List<QuestionnaireEntry> questionnaireEntries) {
        this.questionnaireEntries = questionnaireEntries;
        notifyDataSetChanged();
    }

    public void removeQuestionnaireEntry(int position){
        questionnaireEntries.remove(position);
        notifyItemChanged(position);
    }

    public void updateQuestionnaireEntries(List<QuestionnaireEntry> questionnaireEntries){
        this.questionnaireEntries = questionnaireEntries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_questionnaire_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionnaireEntry questionnaireEntry = questionnaireEntries.get(position);
        Person mainRespondent = questionnaireEntry.getMainRespondent();
        String name;
        if (mainRespondent != null && mainRespondent.getName() != null) {
            name = mainRespondent.getName();
        } else {
            name = "Questionário sem nome definido";
        }
        holder.mainRespondentTextView.setText(name);
        if ((position % 2) == 0){
            holder.rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        } else {
            holder.rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.lighter_blue));
        }

        //holder.dateTextView.setText(questionnaireEntry.getDateTime().split(" ")[0]);
        String date = questionnaireEntry.getDateTime().split(" ")[0] + " de " + questionnaireEntry.getDateTime().split(" ")[2] + " de " + questionnaireEntry.getDateTime().split(" ")[4];
        holder.dateTextView.setText(date);
    }

    @Override
    public int getItemCount() {
        if (questionnaireEntries == null) return 0;
        return questionnaireEntries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mainRespondentTextView;
        private final ImageButton editImageButton;
        private final LinearLayout rowView;
        private final ImageButton deleteImageButton;
        private final ImageButton uploadImageButton;
        private final TextView dateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            mainRespondentTextView = itemView.findViewById(R.id.main_respondent_tv);
            editImageButton = itemView.findViewById(R.id.edit_bt);
            editImageButton.setOnClickListener(this);
            deleteImageButton = itemView.findViewById(R.id.delete_bt);
            rowView = itemView.findViewById(R.id.row);
            deleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Atenção!");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setMessage("Deseja apagar questionário?");
                    builder.setPositiveButton("Sim",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int adapterPosition = getAdapterPosition();
                                    onClickHandler.onClickDelete(questionnaireEntries.get(adapterPosition).getPrimaryKey(), adapterPosition);
                                    dialog.dismiss();
                                }
                            });
                    builder.setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            });
            uploadImageButton = itemView.findViewById(R.id.upload_bt);
            uploadImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Atenção!");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setMessage("Deseja enviar questionário? Tenha em vista que não será mais possível realizar alterações neste após o envio.");
                    builder.setPositiveButton( "Sim",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    int adapterPosition = getAdapterPosition();
                                    onClickHandler.onClickUpload(questionnaireEntries.get(adapterPosition).getPrimaryKey(), adapterPosition);
                                    dialog.dismiss();
                                }
                            });
                    builder.setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            });
            dateTextView = itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            onClickHandler.onClickEdit(questionnaireEntries.get(adapterPosition).getPrimaryKey(), adapterPosition);
        }
    }
}
