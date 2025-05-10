package com.example.newequest.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newequest.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class QuestionFragment extends Fragment {

    public interface OnNextButtonClickListener {
        void onNextButtonClick();
        void onPreviousButtonClick();
        void onPersonCreatorNextButtonClick(String personName, int personID, String option, String direction);
        void onCityCheckNextButtonClick(String cityName, String direction);
        void onAddNewPersonButtonClick();
    }

    public OnNextButtonClickListener mCallback;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseCrashlytics.getInstance().log(
                this.getClass().getName()
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNextButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNextButtonClickListener");
        }
    }
}