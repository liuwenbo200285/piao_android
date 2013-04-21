package com.wenbo.piao.Fragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.enums.ParameterEnum;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.service.RobitOrderService;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.task.GetPersonConstanct;
import com.wenbo.piao.task.GetRandCodeTask;
import com.wenbo.piao.util.HttpClientUtil;

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
	private Map<String, UserInfo> userInfoMap;
	private AlertDialog dialog;
	private EditText fromStation;
	private EditText toStation;
	private EditText trainNo;
	private EditText rangeCode;
	private CheckBox firstSeat;
	private CheckBox secondSeat;
	private CheckBox hardSleeper;
	private CheckBox hardSeat;
	private CheckBox noSeat;
	private Button orderButton;
	private int type=0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info2, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("onCreate", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i("onStart", "onStart");
		GetRandCodeTask getRandCode = new GetRandCodeTask(activity, 2);
		getRandCode.execute(UrlEnum.DO_MAIN.getPath()
				+ UrlEnum.LOGIN_RANGCODE_URL.getPath());
		super.onStart();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("onActivityCreated", "onActivityCreated");
		activity = getActivity();
		trainDate = (EditText) activity.findViewById(R.id.startTime);
		selectDate = (Button) activity.findViewById(R.id.selectDate);
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		setDateTime();
		fromStation = (EditText) activity.findViewById(R.id.startArea);
		toStation = (EditText) activity.findViewById(R.id.endArea);
		trainNo = (EditText) activity.findViewById(R.id.startTrainNo);
		firstSeat = (CheckBox) activity.findViewById(R.id.first_seat);
		secondSeat = (CheckBox) activity.findViewById(R.id.second_seat);
		hardSleeper = (CheckBox) activity.findViewById(R.id.hard_sleeper);
		hardSeat = (CheckBox) activity.findViewById(R.id.hard_seat);
		noSeat = (CheckBox) activity.findViewById(R.id.no_seat);
		rangeCode = (EditText) activity.findViewById(R.id.orderCode);
		selectDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				datePickerDialog = new DatePickerDialog(activity,
						mDateSetListener, mYear, mMonth, mDay);
				datePickerDialog.show();
			}
		});
		selectPeople = (Button) activity.findViewById(R.id.selectPeople);
		selectPeople.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				userInfoMap = HttpClientUtil.getUserInfoMap();
				if (userInfoMap == null) {
					userInfoMap = new HashMap<String, UserInfo>();
					getPersonInfo();
				} else {
					if (userInfoMap.isEmpty()) {
						getPersonInfo();
					}
					showDialog();
				}
			}
		});
		orderPeople = (EditText) activity.findViewById(R.id.orderPeople);
		orderCode = (ImageView) activity.findViewById(R.id.orderCodeImg);
		orderCode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GetRandCodeTask getRandCode = new GetRandCodeTask(activity, 2);
				getRandCode.execute(UrlEnum.DO_MAIN.getPath()
						+ UrlEnum.LOGIN_RANGCODE_URL.getPath());
			}
		});
		orderButton = (Button) activity.findViewById(R.id.orderButton);
		orderButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(activity, RobitOrderService.class);
				if(type == 0){
					Bundle bundle = new Bundle();
					bundle.putString(ParameterEnum.FROMSTATION.getValue(),
							fromStation.getText().toString());
					bundle.putString(ParameterEnum.TOSTATION.getValue(), toStation
							.getText().toString());
					bundle.putString(ParameterEnum.TRAINNO.getValue(), trainNo
							.getText().toString());
					bundle.putString(ParameterEnum.ORDERPERSON.getValue(),
							orderPeople.getText().toString());
					bundle.putString(ParameterEnum.ORDERDATE.getValue(), trainDate
							.getText().toString());
					bundle.putString(ParameterEnum.ORDERSEAT.getValue(),
							getOrderSet());
					bundle.putString(ParameterEnum.ORDERTIME.getValue(),
							"12:00--18:00");
					bundle.putString(ParameterEnum.RANGECODE.getValue(), rangeCode
							.getText().toString());
					intent.putExtras(bundle);
					activity.startService(intent);
					type = 1;
					orderButton.setText("停止抢票");
				}else{
					activity.stopService(intent);
					orderButton.setText("开始抢票");
					type = 0;
				}
			}
		});
		//注册监听service
		IntentFilter intentFilter = new IntentFilter("com.wenbo.piao.robitService");
		MyReceiver myReceiver = new MyReceiver();
		activity.registerReceiver(myReceiver, intentFilter);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.i("onAttach", "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.i("onDetach", "onDetach");
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i("onPause", "onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i("onResume", "onResume");
		super.onResume();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i("onStop", "onStop");
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("onViewCreated", "onViewCreated");
		super.onViewCreated(view, savedInstanceState);
	}

	/**
	 * 设置日期
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		updateDateDisplay();
	}

	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay() {
		trainDate.setText(new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay));
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

	public void showDialog() {
		if (dialog == null) {
			try {
				if (userInfoMap.isEmpty()) {
					LoginDialog.newInstance("此账号还没有添加联系人！").show(
							activity.getFragmentManager(), "dialog");
					return;
				}
				contacts = new String[userInfoMap.size()];
				int i = 0;
				for (String key : userInfoMap.keySet()) {
					contacts[i] = key;
					i++;
				}
				if (checkedItems == null) {
					checkedItems = new boolean[contacts.length];
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(activity)
						.setMultiChoiceItems(contacts, checkedItems,
								new OnMultiChoiceClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which, boolean isChecked) {
									}
								}).setIcon(android.R.drawable.btn_star);
				builder.setTitle("选择订票乘客")
						// 设置Dialog的标题
						.setPositiveButton("取消", null)
						.setNegativeButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										StringBuilder sbBuilder = new StringBuilder();
										for (int i = 0; i < checkedItems.length; i++) {
											if (checkedItems[i]) {
												sbBuilder.append(contacts[i]
														+ ",");
											}
										}
										orderPeople.setText(sbBuilder
												.toString());
									}
								});
				dialog = builder.create();
				dialog.show();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			dialog.show();
		}
	}

	private void getPersonInfo() {
		GetPersonConstanct getPersonConstanct = new GetPersonConstanct(
				activity, userInfoMap, this);
		getPersonConstanct.execute("");
	}

	private String getOrderSet() {
		StringBuilder builder = new StringBuilder();
		if (firstSeat.isChecked()) {
			builder.append("3,");
		}
		if (secondSeat.isChecked()) {
			builder.append("4,");
		}
		if (hardSleeper.isChecked()) {
			builder.append("7,");
		}
		if (hardSeat.isChecked()) {
			builder.append("9,");
		}
		if (noSeat.isChecked()) {
			builder.append("10,");
		}
		return builder.toString();
	}

	public class MyReceiver extends BroadcastReceiver {

		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("OnReceiver");
			Bundle bundle = intent.getExtras();
			int a = bundle.getInt("i");
			Log.i("onReceive",a+"");
			// pb.setProgress(a);
			// tv.setText(String.valueOf(a));
			// 处理接收到的内容
		}
	}
}
