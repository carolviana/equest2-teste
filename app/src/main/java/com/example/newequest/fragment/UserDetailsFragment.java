package com.example.newequest.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
//import android.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newequest.R;
import com.example.newequest.model.User;

import java.util.ArrayList;

public class UserDetailsFragment extends DialogFragment {

    public interface onSaveClickListener {
        void OnSaveClick(User user);
    }

    onSaveClickListener mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText nameView = rootView.findViewById(R.id.name);
        final EditText emailView = rootView.findViewById(R.id.email);
        final Spinner cityView = rootView.findViewById(R.id.city);
        TextView saveView = rootView.findViewById(R.id.save_button);
        TextView cancelView = rootView.findViewById(R.id.cancel_button);

        ArrayList<String> cities = new ArrayList<>();
        cities.add("Campos dos Goytacazes");
        cities.add("Macaé");
        cities.add("São Francisco do Itabapoana");
        cities.add("São João da Barra");
        cities.add("Arraial do Cabo");
        cities.add("Cabo Frio");
        cities.add("Quissamã");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.item_user_config_spinner, cities);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        cityView.setAdapter(adapter);

        if (getArguments() != null){
            Bundle bundle = getArguments();
            User user;
            user = bundle.getParcelable("user");
            nameView.setText(user.getName());
            emailView.setText(user.getEmail());
            Log.e("maroto", user.getCity());
            for (int i = 0; i < cities.size(); i++){
                if (user.getCity().equals(cities.get(i))){
                    cityView.setSelection(i);
                }
            }
        }

        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameView.getText().toString();
                String email = emailView.getText().toString();
                String city = cityView.getSelectedItem().toString();

                if (!name.equals("") && !email.equals("")){
                    User editedUser = new User(name, email);
                    mCallback.OnSaveClick(editedUser);
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (onSaveClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onSaveClickListener");
        }
    }
}