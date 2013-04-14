package com.wenbo.piao.Fragment;

import java.util.Calendar;

import com.wenbo.androidpiao.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class RobitOrderFragment extends Fragment {
	
	private Activity activity;
	
	private EditText trainDate;
	
	private Button selectDate;
	
	private DatePickerDialog datePickerDialog;
	
	private int mYear;  
    private int mMonth;
    private int mDay;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info2,null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		activity = getActivity();
		trainDate = (EditText)activity.findViewById(R.id.startTime);
		selectDate = (Button)activity.findViewById(R.id.selectDate);
		final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);  
        mMonth = c.get(Calendar.MONTH);  
        mDay = c.get(Calendar.DAY_OF_MONTH);
        setDateTime();
        selectDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				datePickerDialog = new DatePickerDialog(activity,mDateSetListener, mYear, mMonth,  
		                  mDay);
				datePickerDialog.show();
			}
		});
		super.onStart();
	}

	/**
     * 设置日期
     */
	private void setDateTime(){
       final Calendar c = Calendar.getInstance();  
       mYear = c.get(Calendar.YEAR);  
       mMonth = c.get(Calendar.MONTH);  
       mDay = c.get(Calendar.DAY_OF_MONTH); 
       updateDateDisplay(); 
	}
	
	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay(){
		trainDate.setText(new StringBuilder().append(mYear).append("-")
    		   .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
               .append((mDay < 10) ? "0" + mDay : mDay)); 
	}
	
    /** 
     * 日期控件的事件 
     */  
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {  
  
       public void onDateSet(DatePicker view, int year, int monthOfYear,  
              int dayOfMonth) {  
           mYear = year;  
           mMonth = monthOfYear;  
           mDay = dayOfMonth;  
           updateDateDisplay();
       }  
    };
	
	
}
