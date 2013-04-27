package com.wenbo.piao.Fragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.wenbo.piao.R;
import com.wenbo.piao.adapter.StationAdapter;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.ConfigInfo;
import com.wenbo.piao.enums.ParameterEnum;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.service.RobitOrderService;
import com.wenbo.piao.sqllite.SqlliteHelper;
import com.wenbo.piao.sqllite.domain.Station;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.sqllite.service.StationService;
import com.wenbo.piao.task.GetPersonConstanct;
import com.wenbo.piao.task.GetRandCodeTask;
import com.wenbo.piao.task.GetTrainNoTast;
import com.wenbo.piao.util.HttpClientUtil;

public class RobitOrderFragment extends Fragment {

	private Activity activity;
	private EditText trainDate;
	private DatePickerDialog datePickerDialog;
	private ProgressDialog progressDialog;
	private EditText orderPeople;
	private int mYear;
	private int mMonth;
	private int mDay;
	private String[] contacts;
	private boolean[] checkedItems;
	private Map<String, UserInfo> userInfoMap;
	private AlertDialog dialog;
	private AutoCompleteTextView fromStation;
	private AutoCompleteTextView toStation;
	private EditText trainNo;
	private EditText rangeCode;
	private Button orderButton;
	private EditText selectSeatText;
	private AlertDialog selectSeatDialog;
	private EditText selectTimeText;
	private AlertDialog selectTimeDialog;
	private EditText selectTrainTypeText;
	private AlertDialog selectTrainTypeDialog;
	private Intent intent;
	private int type = 0;
	private int status = 0;
	private StationService stationService;
	private ConfigInfo configInfo;
	private List<Station> fromStations;
	private List<Station> toStations;
	private EditText trainCode;

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
		super.onStart();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("onActivityCreated", "onActivityCreated");
		activity = getActivity();
		SqlliteHelper sqlliteHelper = new SqlliteHelper(activity);
		stationService = sqlliteHelper.getStationService();
		trainCode = (EditText)activity.findViewById(R.id.trainCode);
		trainDate = (EditText) activity.findViewById(R.id.startTime);
		trainDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				datePickerDialog = new DatePickerDialog(activity,
						mDateSetListener, mYear, mMonth, mDay);
				datePickerDialog.show();
				return false;
			}
		});
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		setDateTime();
		fromStation = (AutoCompleteTextView) activity.findViewById(R.id.startArea);
		fromStation.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(s.length() < 2){
					return;
				}
				fromStations =  stationService.findStationLike(s.toString());
				StationAdapter adapter = new StationAdapter(activity,android.R.layout.simple_dropdown_item_1line,fromStations);
				fromStation.setAdapter(adapter);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		toStation = (AutoCompleteTextView) activity.findViewById(R.id.endArea);
		toStation.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() < 2){
					return;
				}
				toStations =  stationService.findStationLike(s.toString());
				StationAdapter adapter = new StationAdapter(activity,android.R.layout.simple_dropdown_item_1line,toStations);
				toStation.setAdapter(adapter);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
//		toStation.addTextChangedListener(watcher);
		trainNo = (EditText) activity.findViewById(R.id.startTrainNo);
		trainNo.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					getConfigInfo();
					GetTrainNoTast trainNoTast = new GetTrainNoTast(activity,configInfo);
					trainNoTast.execute("");
				}
			}
		});
		orderPeople = (EditText) activity.findViewById(R.id.orderPeople);
		orderPeople.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
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
			}
		});
		orderButton = (Button) activity.findViewById(R.id.orderButton);
		orderButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				intent = new Intent();
				intent.setClass(activity, RobitOrderService.class);
				if (type == 0) {
					getConfigInfo();
					String code = trainCode.getText().toString();
					configInfo.setTrainNo(code);
					configInfo.setOrderPerson(orderPeople.getText().toString());
					Bundle bundle = new Bundle();
					if ("全部".equals(selectTrainTypeText.getText().toString())) {
						configInfo.setTrainClass("QB#D#Z#T#K#QT#");
					} else {
						configInfo.setTrainClass(HttpClientUtil.getTrainTypeMap().get(selectTrainTypeText.getText().toString())+ "#");
					}
					StringBuilder sbBuilder = new StringBuilder();
					String[] seats = StringUtils.split(selectSeatText.getText()
							.toString(), ",");
					for (String seat : seats) {
						sbBuilder.append(HttpClientUtil.getSeatMap().get(seat)
								+ ",");
					}
					configInfo.setOrderSeat(sbBuilder.toString());
					configInfo.setOrderTime(selectTimeText.getText().toString());
					intent.putExtra(ParameterEnum.ROBIT_STATE.getValue(),
							status);
					intent.putExtras(bundle);
					activity.startService(intent);
					type = 1;
					orderButton.setText("停止抢票");
//					progressDialog = ProgressDialog.show(activity, "订票中",
//							"正在努力抢票...", true, false);
					//创建ProgressDialog对象
					progressDialog = new ProgressDialog(activity);
	                // 设置进度条风格，风格为圆形，旋转的
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	                // 设置ProgressDialog 标题
					progressDialog.setTitle("订票中");
	                // 设置ProgressDialog 提示信息
					progressDialog.setMessage("正在努力抢票...");
	                // 设置ProgressDialog 标题图标
	                // 设置ProgressDialog 的进度条是否不明确
					progressDialog.setIndeterminate(false);
	                // 设置ProgressDialog 是否可以按退回按键取消
					progressDialog.setCancelable(false);
	                // 设置ProgressDialog 的一个Button
					progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"停止抢票",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							activity.stopService(intent);
							orderButton.setText("开始抢票");
							type = 0;
						}
					});
	                // 让ProgressDialog显示
					progressDialog.show();
				} else {
					activity.stopService(intent);
					orderButton.setText("开始抢票");
					type = 0;
				}
			}
		});
		selectSeatText = (EditText) activity.findViewById(R.id.seatText);
		selectSeatText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showSeatDialog();
				}
			}
		});
		selectTimeText = (EditText) activity.findViewById(R.id.timeText);
		selectTimeText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					showTimeDialog();
				}
			}
		});
		selectTimeText.setText("00:00--24:00");
		selectTrainTypeText = (EditText) activity
				.findViewById(R.id.trainTypeText);
		selectTrainTypeText
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							showTrainTypeDialog();
						}
					}
				});
		selectTrainTypeText.setText("全部");
		// 注册监听service
		IntentFilter intentFilter = new IntentFilter(
				"com.wenbo.piao.robitService");
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
		trainDate.clearFocus();
		trainCode.requestFocus();
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

	private void showTrainTypeDialog() {
		if (selectTrainTypeDialog == null) {
			final String[] trainType = { "全部", "动车", "Z字头", "T字头", "K字头", "其它" };
			AlertDialog.Builder builder = new AlertDialog.Builder(activity)
					.setSingleChoiceItems(trainType, 0,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									selectTrainTypeText
											.setText(trainType[which]);
									selectTrainTypeText.clearFocus();
									trainCode.requestFocus();
									selectTrainTypeDialog.dismiss();
									closeSoftInput();
								}
							}).setIcon(android.R.drawable.btn_star);
			builder.setTitle("选择车次类型");
			selectTrainTypeDialog = builder.create();
			selectTrainTypeDialog.show();
		} else {
			selectTrainTypeDialog.show();
		}
	}

	private void showTimeDialog() {
		if (selectTimeDialog == null) {
			final String[] times = { "00:00--24:00", "00:00--06:00",
					"06:00--12:00", "12:00--18:00", "18:00--24:00" };
			AlertDialog.Builder builder = new AlertDialog.Builder(activity)
					.setSingleChoiceItems(times, 0,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									selectTimeText.setText(times[which]);
									selectTimeText.clearFocus();
									trainCode.requestFocus();
									selectTimeDialog.dismiss();
									closeSoftInput();
								}
							}).setIcon(android.R.drawable.btn_dropdown);
			builder.setTitle("选择时间段");
			selectTimeDialog = builder.create();
			selectTimeDialog.show();
		} else {
			selectTimeDialog.show();
		}
	}

	private void showSeatDialog() {
		if (selectSeatDialog == null) {
			final String[] seats = { "商务座", "特等座", "一等座", "二等座", "高级软卧", "软卧",
					"硬卧", "软座", "硬座", "无座" };
			final boolean[] selectSeats = new boolean[seats.length];
			AlertDialog.Builder builder = new AlertDialog.Builder(activity)
					.setMultiChoiceItems(seats, selectSeats,
							new OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
								}
							}).setIcon(android.R.drawable.btn_dropdown);
			builder.setTitle("选择乘客坐席")
					.setPositiveButton("取消", null)
					.setNegativeButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									StringBuilder sbBuilder = new StringBuilder();
									for (int i = 0; i < selectSeats.length; i++) {
										if (selectSeats[i]) {
											sbBuilder.append(seats[i] + ",");
										}
									}
									selectSeatText.setText(sbBuilder.toString());
									selectSeatText.clearFocus();
									trainCode.requestFocus();
									closeSoftInput();
								}
							});
			selectSeatDialog = builder.create();
			selectSeatDialog.show();
		} else {
			selectSeatDialog.show();
		}
	}

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
								}).setIcon(android.R.drawable.btn_dropdown);
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
										orderPeople.setText(sbBuilder.toString());
										orderPeople.clearFocus();
										trainCode.requestFocus();
										closeSoftInput();
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

	// EditText中输入内容监视
	// TextWatcher中重写的三个方法在EditText中每输入一个字符都执行一遍
	TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			Log.d("=================", "onTextChanged is called!");
			// String selection = MydbHelper.WORD+" LIKE ?";
			// String[] selectionArgs= new String[]{s.toString()+"%"};
			// myQuery(selection,selectionArgs);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			Log.d("=================", "afterTextChanged is called!");
			Log.d("=================",s.toString());
			String str = s.toString();
		}
	};

	private void getPersonInfo() {
		GetPersonConstanct getPersonConstanct = new GetPersonConstanct(
				activity, userInfoMap, this);
		getPersonConstanct.execute("");
	}
	
	private void getConfigInfo(){
		if(configInfo == null){
			configInfo = new ConfigInfo();
		}
		String from = fromStation.getText().toString();
		if(StringUtils.isBlank(from)){
			LoginDialog.newInstance("请输入始发地！").show(
					activity.getFragmentManager(), "dialog");
			return;
		}
		Station dbFromStation = null;
		if(fromStations != null && !fromStations.isEmpty()){
			for(Station station:fromStations){
				if(station.getZhCode().equals(from)){
					dbFromStation = station;
					break;
				}
			}
		}else{
			dbFromStation = stationService.findStationByZHName(from);
		}
		if(dbFromStation == null){
			LoginDialog.newInstance("没有找到该始发地！").show(
					activity.getFragmentManager(), "dialog");
			return;
		}
		configInfo.setFromStation(dbFromStation.getSimpleCode());
		String to = toStation.getText().toString();
		if(StringUtils.isBlank(to)){
			LoginDialog.newInstance("请输入目的地！").show(
					activity.getFragmentManager(), "dialog");
			return;
		}
		if(toStations != null && !toStations.isEmpty()){
			for(Station station:toStations){
				if(station.getZhCode().equals(to)){
					dbFromStation = station;
					break;
				}
			}
		}else{
			dbFromStation = stationService.findStationByZHName(to);
		}
		if(dbFromStation == null){
			LoginDialog.newInstance("没有找到该目的地！").show(
					activity.getFragmentManager(), "dialog");
			return;
		}
		configInfo.setToStation(dbFromStation.getSimpleCode());
		String date = trainDate.getText().toString();
		if(StringUtils.isBlank(date)){
			LoginDialog.newInstance("请选择乘车日期！").show(
					activity.getFragmentManager(), "dialog");
			return;
		}
		configInfo.setOrderDate(date);
		String time = selectTimeText.getText().toString();
		if(StringUtils.isBlank(time)){
			LoginDialog.newInstance("请选择出发时间！").show(
					activity.getFragmentManager(), "dialog");
			return;
		}
		configInfo.setOrderTime(time);
		HttpClientUtil.setConfigInfo(configInfo);
	}

	public class MyReceiver extends BroadcastReceiver {

		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent receiveIntent) {
			Bundle bundle = receiveIntent.getExtras();
			status = bundle.getInt("status");
			// progressDialog.dismiss();
			switch (status) {
			case 1:
				LoginDialog.newInstance("系统维护中！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 2:
				break;
			case 3:
				LoginDialog.newInstance("车次输入错误！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 4:
				LoginDialog.newInstance("还有未处理的订单！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 5:
				LoginDialog.newInstance("预订坐席填写不正确！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 6:
				LoginDialog.newInstance("订票人格式填写不正确！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 7:
				LoginDialog.newInstance("一个账号最多只能预定5张火车票！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 8:
				LoginDialog.newInstance("输入的验证码不正确！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 9:
				LoginDialog.newInstance("票数不够！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 10:
				LoginDialog.newInstance("非法的订票请求！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 11:
				LoginDialog.newInstance("订票成功！").show(
						activity.getFragmentManager(), "dialog");
				break;
			case 12:
				LayoutInflater li = LayoutInflater.from(activity);
				View orderCodeView = li.inflate(R.layout.rangcodeview, null);
				ImageView imageView = (ImageView) orderCodeView
						.findViewById(R.id.orderCodeImg);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						GetRandCodeTask getRandCode = new GetRandCodeTask(
								(ImageView) view, 2);
						getRandCode.execute(UrlEnum.DO_MAIN.getPath()
								+ UrlEnum.LOGIN_RANGCODE_URL.getPath());
					}
				});
				GetRandCodeTask getRandCode = new GetRandCodeTask(imageView, 2);
				getRandCode.execute(UrlEnum.DO_MAIN.getPath()
						+ UrlEnum.LOGIN_RANGCODE_URL.getPath());
				rangeCode = (EditText) orderCodeView
						.findViewById(R.id.orderCode);
				new AlertDialog.Builder(activity)
						.setTitle("请输入验证码！")
						.setIcon(android.R.drawable.ic_dialog_info)
						.setView(orderCodeView)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										progressDialog.show();
										intent.putExtra(
												ParameterEnum.ROBIT_STATE
														.getValue(), status);
										intent.putExtra(ParameterEnum.RANGECODE
												.getValue(), rangeCode
												.getText().toString());
										activity.startService(intent);
										type = 1;
										orderButton.setText("停止抢票");
									}
								}).setNegativeButton("取消", null).show();
				break;
			default:
				break;
			}
			Log.i("onReceive", status + "");
			activity.stopService(intent);
			orderButton.setText("开始抢票");
			type = 0;
			progressDialog.dismiss();
			// pb.setProgress(a);
			// tv.setText(String.valueOf(a));
			// 处理接收到的内容
		}
	}
	
	public void closeSoftInput(){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}
}
