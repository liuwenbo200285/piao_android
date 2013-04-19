package com.wenbo.piao.Fragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.wenbo.androidpiao.R;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.task.GetPersonConstanct;
import com.wenbo.piao.task.GetRandCodeTask;

public class RobitOrderFragment extends Fragment {
	
	private Activity activity;
	
	private EditText trainDate;
	
	private Button selectDate;
	
	private Button selectPeople;
	
	private DatePickerDialog datePickerDialog;
	
	private EditText orderPeople;
	
	private ImageView orderCode;
	
	private int mYear;  
    private int mMonth;
    private int mDay;
    private String[] contacts;
    private boolean[] checkedItems;
    private Map<String,UserInfo> userInfoMap;
    private AlertDialog dialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info2,null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("onCreate","onCreate");
		super.onCreate(savedInstanceState);
	}
	
	
	
	
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i("onStart","onStart");
		GetRandCodeTask getRandCode = new GetRandCodeTask(activity,2);
        getRandCode.execute(UrlEnum.DO_MAIN.getPath()+UrlEnum.LOGIN_RANGCODE_URL.getPath());
		super.onStart();
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("onActivityCreated","onActivityCreated");
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
        selectPeople = (Button)activity.findViewById(R.id.selectPeople);
        selectPeople.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(userInfoMap == null){
					userInfoMap = new HashMap<String,UserInfo>();
					getPersonInfo();
				}else{
					if(userInfoMap.isEmpty()){
						getPersonInfo();
					}
					showDialog();
				}
			}
		});
        orderPeople = (EditText)activity.findViewById(R.id.orderPeople);
        orderCode = (ImageView)activity.findViewById(R.id.orderCodeImg);
        orderCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GetRandCodeTask getRandCode = new GetRandCodeTask(activity,2);
		        getRandCode.execute(UrlEnum.DO_MAIN.getPath()+UrlEnum.LOGIN_RANGCODE_URL.getPath());
			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.i("onAttach","onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.i("onDetach","onDetach");
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i("onPause","onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i("onResume","onResume");
		super.onResume();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i("onStop","onStop");
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("onViewCreated","onViewCreated");
		super.onViewCreated(view, savedInstanceState);
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
	
	public void showDialog(){
		if(dialog == null){
	        try {
	        	if(userInfoMap.isEmpty()){
	    			new AlertDialog.Builder(activity).setTitle("订票人!")
	    			.setMessage("此账号还没有添加联系人!").show();
	    			return;
	    		}
	        	contacts = new String[userInfoMap.size()];
	        	int i = 0;
	        	for(String key:userInfoMap.keySet()){
	        		contacts[i] = key;
	        		i++;
	        	}
				if(checkedItems == null){
					checkedItems = new boolean[contacts.length];
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setMultiChoiceItems(contacts, checkedItems,new OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					}
				})
				.setIcon(android.R.drawable.btn_star);
				builder.setTitle("选择订票乘客")// 设置Dialog的标题
				.setPositiveButton("取消",null)
			    .setNegativeButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						StringBuilder sbBuilder = new StringBuilder();
						for(int i = 0; i < checkedItems.length; i++){
							if(checkedItems[i]){
								sbBuilder.append(contacts[i]+",");
							}
						}
						orderPeople.setText(sbBuilder.toString());
					}
				});
				dialog = builder.create();
		        dialog.show();
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			dialog.show();
		}
	}
	
	private void getPersonInfo(){
		GetPersonConstanct getPersonConstanct = new GetPersonConstanct(activity,userInfoMap,this);
		AsyncTask<String, Integer, String> task = getPersonConstanct.execute("");
	}
}
