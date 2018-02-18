package ht.mbds.nytimesarticlessearch.utils;

/**
 * Created by Ermano
 * on 1/8/2017.
 */

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {

    OnDateSetListener ondateSet;
    DatePickerDialog.OnCancelListener onCancel;

    public DatePickerFragment() {
    }

    public void setCallBack(OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    public void setCancelCallBack(DatePickerDialog.OnCancelListener oncancel){
        onCancel = oncancel;
    }

    private int year, month, day;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog datePickerDialog =  new DatePickerDialog(
                getActivity(),
                ondateSet,
                year,
                month,
                day);

        return datePickerDialog;
    }
}
