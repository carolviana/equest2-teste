package com.example.newequest.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.newequest.R;
import com.example.newequest.activity.ExportActivity;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private String date;
    private String indicator;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int day, month, year;
        Bundle bundle = getArguments();
        indicator = bundle.getString("indicator");

        String strDate = bundle.getString("currentlyChosenDate");

        day = Integer.valueOf(strDate.substring(0, 2));
        month = Integer.valueOf(strDate.substring(3, 5)) - 1;
        year = Integer.valueOf(strDate.substring(6));

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        month = month + 1;

        String strDay = String.format("%02d", day);
        String strMonth = String.format("%02d", month);

        date = strDay + "/" + strMonth + "/" + String.valueOf(year);

        ((ExportActivity)getActivity()).refreshDate(date, indicator);
    }
}