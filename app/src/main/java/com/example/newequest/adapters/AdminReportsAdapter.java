package com.example.newequest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newequest.R;
import com.example.newequest.model.AdminReportItem;

import java.util.ArrayList;

public class AdminReportsAdapter extends  RecyclerView.Adapter<AdminReportsAdapter.ViewHolder> {

    private ArrayList<AdminReportItem> items;
    private Context context;

    public AdminReportsAdapter(Context context) {
        items = new ArrayList<>();
        this.context = context;
    }

    public void addToDataset(AdminReportItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void setDataset(ArrayList<AdminReportItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminReportItem item = items.get(position);
        holder.nameView.setText(item.getName());
        holder.completeView.setText(String.valueOf(item.getCompleteQuestionnaireCount()));
        holder.incompleteView.setText(String.valueOf(item.getIncompleteQuestionnaireCount()));
        if ((position % 2) == 0){
            holder.rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_blue));
        } else {
            holder.rowView.setBackgroundColor(ContextCompat.getColor(context, R.color.lighter_blue));
        }
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout rowView;
        private final TextView nameView;
        private final TextView completeView;
        private final TextView incompleteView;
        public ViewHolder(View itemView) {
            super(itemView);
            rowView = itemView.findViewById(R.id.row_ll);
            nameView = itemView.findViewById(R.id.name);
            completeView = itemView.findViewById(R.id.complete);
            incompleteView = itemView.findViewById(R.id.incomplete);
        }
    }
}
