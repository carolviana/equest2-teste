package com.example.newequest.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newequest.R;

import java.util.ArrayList;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.MyViewHolder> {

    private final ArrayList<String> items = new ArrayList<>();
    private final PeopleAdapter.PeopleAdapterOnClickHandler onClickHandler;
    private final Context context;
    private int positionClicked = 0;

    public interface PeopleAdapterOnClickHandler {
        void onClickPeopleAdd();
        void onClickPeopleDelete(int personID);
        void onClickPeople(int personID);
    }

    public PeopleAdapter(PeopleAdapterOnClickHandler onClickHandler) {
        this.items.add("Todos");
        this.items.add("Last");

        this.onClickHandler = onClickHandler;
        this.context = (Context) onClickHandler;
    }

    public void setNewPositionClicked(int personID){
        positionClicked = personID + 1;
        notifyDataSetChanged();
    }

    public void setPeopleDataset(ArrayList<String> peopleItems) {
        this.items.clear();
        this.items.add("Todos");
        this.items.addAll(peopleItems);
        this.items.add("Last");

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemPeople = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_people, viewGroup, false);
        return new MyViewHolder(itemPeople);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(items.get(i));
        if(i == 0){
            myViewHolder.name.setVisibility(View.VISIBLE);
            myViewHolder.delete.setVisibility(View.GONE);
            myViewHolder.add.setVisibility(View.GONE);
        }else {
            if(i == items.size() - 1){
                myViewHolder.name.setVisibility(View.GONE);
                myViewHolder.delete.setVisibility(View.GONE);
                myViewHolder.add.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.name.setVisibility(View.VISIBLE);
                myViewHolder.add.setVisibility(View.GONE);
                if(i==1){
                    myViewHolder.delete.setVisibility(View.GONE);
                }else{
                    myViewHolder.delete.setVisibility(View.VISIBLE);
                }
            }
        }

        if(positionClicked == i){
            myViewHolder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_orange_whiteborder));
            myViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            myViewHolder.name.setBackground(ContextCompat.getDrawable(context, R.drawable.shape_round_white_orangeborder));
            myViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.tx_orange));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView delete;
        private TextView add;

        public MyViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tx_name);
            delete = itemView.findViewById(R.id.bt_delete);
            add = itemView.findViewById(R.id.bt_add);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Atenção!");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setMessage("Deseja apagar esta pessoa?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onClickHandler.onClickPeopleDelete(getAdapterPosition() - 1);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Atenção!");
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setMessage("Deseja adicionar outra pessoa?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onClickHandler.onClickPeopleAdd();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    positionClicked = getAdapterPosition();
                    onClickHandler.onClickPeople(positionClicked - 1);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
