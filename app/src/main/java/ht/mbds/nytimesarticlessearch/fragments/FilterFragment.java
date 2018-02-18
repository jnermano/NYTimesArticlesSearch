package ht.mbds.nytimesarticlessearch.fragments;

import android.app.DatePickerDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ht.mbds.nytimesarticlessearch.R;
import ht.mbds.nytimesarticlessearch.utils.MyDatePickerFragment;

/**
 * Created by Ermano
 * on 2/18/2018.
 */

public class FilterFragment extends DialogFragment
        implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener{


    public interface FilterFragmentListener {
        void onSubmitFilters(String begin_date, String sort, String news_desk);
    }

    @BindView(R.id.filter_frag_tv_begin_date)
    TextView tvBeginDate;

    @BindView(R.id.filter_frag_sp_sort)
    Spinner spSortOrder;

    @BindView(R.id.filter_frag_ll_news_desk)
    LinearLayout llNewsDesk;

    List<CheckBox> checkBoxes;


    public FilterFragment() {
    }

    public static FilterFragment newInstance(String title, String beginDate, String sortOrder, String newsDesk) {
        FilterFragment frag = new FilterFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("begin_date", beginDate);
        args.putString("sort_order", sortOrder);
        args.putString("news_desk", newsDesk);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkBoxes = new ArrayList<>();

        ButterKnife.bind(this, view);

        tvBeginDate.setText(getArguments().getString("begin_date"));
        if (("oldest").equals(getArguments().getString("sort_order")))
            spSortOrder.setSelection(1);

        String[] news_desk = getArguments().getString("news_desk")!= null?  getArguments().getString("news_desk").split(",") : new String[0];

        for (String desk : getResources().getStringArray(R.array.news_desk_values)) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(desk);
            llNewsDesk.addView(checkBox);
            checkBoxes.add(checkBox);

            for (int i = 0; i < news_desk.length; i++){
                if (desk.equals(news_desk[i])){
                    checkBox.setChecked(true);
                    break;
                }

            }
        }

        view.findViewById(R.id.filter_frag_btn_ok).setOnClickListener(this);
        tvBeginDate.setOnClickListener(this);
        view.findViewById(R.id.filter_frag_btn_clear_begin_date).setOnClickListener(this);

    }

    private void setValues(){
        String news_desk = "";

        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                news_desk += news_desk.length() > 0? "," + checkBoxes.get(i).getText().toString() : checkBoxes.get(i).getText().toString();
            }

            Log.v("filter fragment", news_desk);
        }

        FilterFragmentListener listener = (FilterFragmentListener) getActivity();
        listener.onSubmitFilters(
                tvBeginDate.getText().toString(),
                (String)spSortOrder.getSelectedItem(),
                news_desk
        );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter_frag_btn_ok:
                setValues();
                dismiss();
                break;
            case R.id.filter_frag_tv_begin_date:
                MyDatePickerFragment datePickerFragment = new MyDatePickerFragment();
                datePickerFragment.setTargetFragment(FilterFragment.this, 102);
                datePickerFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.filter_frag_btn_clear_begin_date:
                tvBeginDate.setText("");
                break;
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        tvBeginDate.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month, day));
    }



    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.97), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }
}
