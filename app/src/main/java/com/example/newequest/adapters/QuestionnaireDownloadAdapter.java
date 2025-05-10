package com.example.newequest.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newequest.R;
import com.example.newequest.model.QuestionnaireDownloadEntry;

import java.util.ArrayList;

public class QuestionnaireDownloadAdapter extends RecyclerView.Adapter<QuestionnaireDownloadAdapter.ViewHolder> {

    private ArrayList<QuestionnaireDownloadEntry> questionnaireDownloadEntryList;
    private QuestionnaireDownloadAdapterOnClickHandler mCallback;
    private Context context;

    public interface QuestionnaireDownloadAdapterOnClickHandler{
        void onClickDownload(String id, int position);
    }

    public QuestionnaireDownloadAdapter(Context context, ArrayList<QuestionnaireDownloadEntry> questionnaireDownloadEntryList, QuestionnaireDownloadAdapterOnClickHandler callback) {
        this.context = context;
        this.questionnaireDownloadEntryList = questionnaireDownloadEntryList;
        this.mCallback = callback;
        Log.i("carol", "criou adapter: " + this.questionnaireDownloadEntryList);
    }

    public void updateEntries(ArrayList<QuestionnaireDownloadEntry> questionnaireDownloadEntryList) {
        this.questionnaireDownloadEntryList = questionnaireDownloadEntryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionnaireDownloadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_questionnaire_download_entry, parent, false);
        return new QuestionnaireDownloadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionnaireDownloadAdapter.ViewHolder holder, int position) {
        QuestionnaireDownloadEntry qde = questionnaireDownloadEntryList.get(position);
        Log.i("carol", "onBindViewHolder" + qde.toString());
        holder.tvName.setText(qde.getName());
        holder.tvCity.setText(qde.getCity());
        //String date = qde.getDate().split(" ")[0] + " de " + qde.getDate().split(" ")[2] + " de " + qde.getDate().split(" ")[4];
        holder.tvDate.setText(qde.getDate());
        if(qde.getInterviewer() != null) {
            holder.tvInterviewer.setText(qde.getInterviewer());
        }
    }

    @Override
    public int getItemCount() {
        if (questionnaireDownloadEntryList == null) return 0;
        return questionnaireDownloadEntryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvName;
        private final TextView tvCity;
        private final TextView tvInterviewer;
        private final TextView tvDate;
        private final ImageView btDownload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCity = itemView.findViewById(R.id.tv_city);
            tvInterviewer = itemView.findViewById(R.id.tv_interviewer);
            tvDate = itemView.findViewById(R.id.tv_date);
            btDownload = itemView.findViewById((R.id.bt_download));
            btDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    mCallback.onClickDownload(questionnaireDownloadEntryList.get(adapterPosition).getId(), adapterPosition);
                }
            });
        }
    }
}
