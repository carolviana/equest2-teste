package com.example.newequest.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
//import android.app.DialogFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.newequest.R;
import com.example.newequest.adapters.UserConfigAdapter;
import com.example.newequest.fragment.UserDetailsFragment;
import com.example.newequest.model.Session;
import com.example.newequest.model.User;
import com.example.newequest.provider.RemoteUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserConfigActivity extends AppCompatActivity implements
        UserConfigAdapter.onButtonClickListener,
        UserDetailsFragment.onSaveClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);
        try{
            getSupportActionBar().hide();
        }catch (Exception e){
            e.printStackTrace();
        }

        ImageView addView = findViewById(R.id.add);
        ListView usersView = findViewById(R.id.users);

        //UserConfigAdapter adapter = new UserConfigAdapter(this, users, this);
        final UserConfigAdapter adapter = new UserConfigAdapter(this, new ArrayList<User>(), this);

        DatabaseReference db = RemoteUser.getDBRef();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    users.add(child.getValue(User.class));
                }

                ArrayList<User> filteredUsers = new ArrayList<>();
                filteredUsers.addAll(users);
                for (int i = 0; i <= filteredUsers.size(); i++) {
                    if (i == filteredUsers.size()){
                        break;
                    }else{
                        if ((!filteredUsers.get(i).getCity().equals(Session.getUser().getCity()))
                                || (filteredUsers.get(i).getType() == 2)
                                || (!filteredUsers.get(i).isActive())) {
                            filteredUsers.remove(i);
                            i--;
                        }
                    }
                }

                if(Session.getUser().getType() == 1){
                    adapter.clear();
                    adapter.addAll(filteredUsers);
                }else{
                    if(Session.getUser().getType() == 2){
                        adapter.clear();
                        adapter.addAll(users);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersView.setAdapter(adapter);

        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new UserDetailsFragment();
                dialog.setArguments(null);
                dialog.show(getSupportFragmentManager().beginTransaction(), "UserDetailsFragment");
            }
        });
    }

    @Override
    public void onEditClick(User user) {
        DialogFragment dialog = new UserDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager().beginTransaction(), "UserDetailsFragment");
    }

    @Override
    public void onDeleteClick(User user) {
        //TODO: acrescentar confirmação de exclusão
        RemoteUser.deactivateUser(user, UserConfigActivity.this);
    }

    @Override
    public void OnSaveClick(User user) {
        //TODO CAROL: falta implementar ação SAVE e CREATE
        RemoteUser.createUser(user, UserConfigActivity.this);
    }
}
