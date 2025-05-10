package com.example.newequest.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.newequest.R;
import com.example.newequest.model.User;

import java.util.ArrayList;

public class UserConfigAdapter extends ArrayAdapter<User> {

    public interface onButtonClickListener {
        void onEditClick(User user);
        void onDeleteClick(User user);
    }

    onButtonClickListener mCallback;

    public UserConfigAdapter(@NonNull Context context, @NonNull ArrayList<User> users, onButtonClickListener callback) {
        super(context, 0, users);
        mCallback = callback;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_user_config, parent, false);
        }

        final User currentUser = getItem(position);

        ImageView editView = itemView.findViewById(R.id.edit);
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onEditClick(currentUser);
                //setNotifyOnChange(true);
            }
        });

        ImageView deleteView = itemView.findViewById(R.id.delete);
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                builder.setTitle("Atenção!");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setMessage("Deseja apagar usuário?");
                builder.setPositiveButton("Sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onDeleteClick(currentUser);
                        remove(currentUser);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        TextView nameView = itemView.findViewById(R.id.name);
        nameView.setText(currentUser.getName());

        return itemView;
    }
}
