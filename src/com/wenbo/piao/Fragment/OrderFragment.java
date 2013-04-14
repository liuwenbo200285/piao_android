package com.wenbo.piao.Fragment;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.wenbo.androidpiao.R;

public class OrderFragment extends Fragment {
	
	private static Activity activity = null;
	
	private Spinner spinner = null;
	
	private static final int SHOW_DATAPICK = 0;
	
	private static final int DATE_DIALOG_ID = 1;
	
	private int mYear;  
    private int mMonth;
    private int mDay;
    
    private EditText beginDate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info1,null);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
//		Log.i("method","onDestroy");
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
//		Log.i("method","onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
//		Log.i("method","onResume");
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		activity = getActivity();
//		beginDate = (EditText)activity.findViewById(R.id.editText3);
//		final Button button = (Button)activity.findViewById(R.id.button1);
//		spinner = (Spinner)activity.findViewById(R.id.spinner1);
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
//		        R.array.planets_array, android.R.layout.simple_spinner_item);
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		spinner.setAdapter(adapter);
//		button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				new DatePickerDialog(activity,mDateSetListener, mYear, mMonth,  
//		                  mDay);
//			}
//		});
//		final Calendar c = Calendar.getInstance();
//        mYear = c.get(Calendar.YEAR);  
//        mMonth = c.get(Calendar.MONTH);  
//        mDay = c.get(Calendar.DAY_OF_MONTH);
//        setDateTime(); 
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
//		Log.i("method","onStop");
		super.onStop();
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
		beginDate.setText(new StringBuilder().append(mYear).append("-")
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
