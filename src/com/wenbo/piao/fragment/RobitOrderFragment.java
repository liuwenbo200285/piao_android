package com.wenbo.piao.fragment;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class RobitOrderFragment extends Fragment implements OnFocusChangeListener {

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
	private MyReceiver myReceiver;
	private TextView dialogTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.activity_info2, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onStart");
//		closeSoftInput();
		super.onStart();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onActivityCreated");
		activity = getActivity();
		SqlliteHelper sqlliteHelper = new SqlliteHelper(activity);
		stationService = sqlliteHelper.getStationService();
		trainCode = (EditText)activity.findViewById(R.id.trainCode);
		trainDate = (EditText) activity.findViewById(R.id.startTime);
		trainDate.setOnFocusChangeListener(this);
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
				
			}
		});
//		toStation.addTextChangedListener(watcher);
		trainNo = (EditText) activity.findViewById(R.id.startTrainNo);
		trainNo.setOnFocusChangeListener(this);
		orderPeople = (EditText) activity.findViewById(R.id.orderPeople);
		orderPeople.setOnFocusChangeListener(this);
		orderButton = (Button) activity.findViewById(R.id.orderButton);
		orderButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				intent = new Intent();
				intent.setClass(activity, RobitOrderService.class);
				if (type == 0) {
					if(!getConfigInfo()){
						return;
					}
					String code = trainCode.getText().toString();
					configInfo.setTrainNo(code);
					String orderPerson = orderPeople.getText().toString();
					if(StringUtils.isBlank(orderPerson)){
						LoginDialog.newInstance("请选择订票乘客！").show(
								activity.getFragmentManager(), "dialog");
						return;
					}
					configInfo.setOrderPerson(orderPerson);
					String trainType = selectTrainTypeText.getText().toString();
					if(StringUtils.isBlank(trainType)){
						LoginDialog.newInstance("请选择坐席！").show(
								activity.getFragmentManager(), "dialog");
						return;
					}
					if ("全部".equals(trainType)) {
						configInfo.setTrainClass("QB#D#Z#T#K#QT#");
					} else {
						configInfo.setTrainClass(HttpClientUtil.getTrainTypeMap().get(selectTrainTypeText.getText().toString())+ "#");
					}
					Bundle bundle = new Bundle();
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
//					progressDialog.setTitle("订票中");
	                // 设置ProgressDialog 提示信息
					progressDialog.setMessage("正在努力抢票...");
					dialogTextView = new TextView(activity);
					dialogTextView.setText("            订票中");
					dialogTextView.setTextSize(20);
//					dialogTextView.set
					progressDialog.setCustomTitle(dialogTextView);
//					progressDialog.setContentView(dialogTextView);
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
		selectSeatText.setOnFocusChangeListener(this);
		selectTimeText = (EditText) activity.findViewById(R.id.timeText);
		selectTimeText.setOnFocusChangeListener(this);
		selectTimeText.setText("00:00--24:00");
		selectTrainTypeText = (EditText) activity.findViewById(R.id.trainTypeText);
		selectTrainTypeText.setOnFocusChangeListener(this);
		selectTrainTypeText.setText("全部");
		// 注册监听service
		if(myReceiver == null){
			IntentFilter intentFilter = new IntentFilter(
					"com.wenbo.piao.robitService");
			myReceiver = new RobitOrderFragment.MyReceiver();
			activity.registerReceiver(myReceiver, intentFilter);
		}
//		if(StringUtils.isBlank(fromStation.getText().toString())){
//			fromStation.requestFocus();
//		}
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onDetach");
		super.onDetach();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onResume");
		super.onResume();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onStop");
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i("RobitOrderFragment", "onViewCreated");
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
		String oldDate = trainDate.getText().toString().trim();
		String newDate = new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay).toString().trim();
		if(!oldDate.equals(newDate)){
			trainDate.setText(newDate);
//			trainDate.clearFocus();
//			trainCode.requestFocus();
//			closeSoftInput();
		}
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
			closeSoftInput();
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
					.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
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
						
					})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
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
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
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
							
						})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
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

	private void getPersonInfo() {
		GetPersonConstanct getPersonConstanct = new GetPersonConstanct(
				activity, userInfoMap, this);
		getPersonConstanct.execute("");
	}
	
	private boolean getConfigInfo(){
		if(configInfo == null){
			configInfo = new ConfigInfo();
		}
		String from = fromStation.getText().toString();
		if(StringUtils.isBlank(from)){
			LoginDialog.newInstance("请输入始发地！").show(
					activity.getFragmentManager(), "dialog");
			fromStation.requestFocus();
			return false;
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
			fromStation.requestFocus();
			return false;
		}
		configInfo.setFromStation(dbFromStation.getSimpleCode());
		String to = toStation.getText().toString();
		if(StringUtils.isBlank(to)){
			LoginDialog.newInstance("请输入目的地！").show(
					activity.getFragmentManager(), "dialog");
			toStation.requestFocus();
			return false;
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
			toStation.requestFocus();
			return false;
		}
		configInfo.setToStation(dbFromStation.getSimpleCode());
		String date = trainDate.getText().toString();
		if(StringUtils.isBlank(date)){
			LoginDialog.newInstance("请选择乘车日期！").show(
					activity.getFragmentManager(), "dialog");
			return false;
		}
		configInfo.setOrderDate(date);
		String time = selectTimeText.getText().toString();
		if(StringUtils.isBlank(time)){
			LoginDialog.newInstance("请选择出发时间！").show(
					activity.getFragmentManager(), "dialog");
			return false;
		}
		configInfo.setOrderTime(time);
		HttpClientUtil.setConfigInfo(configInfo);
		return true;
	}

	public class MyReceiver extends BroadcastReceiver {

		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent receiveIntent) {
			Bundle bundle = receiveIntent.getExtras();
			status = bundle.getInt("status");
			if(status >= 1000){
			    String info = bundle.getString("tips");
			    dialogTextView.setText("   "+info);
				return;
			}
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
				final ImageView imageView = (ImageView) orderCodeView.findViewById(R.id.orderCodeImg);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						GetRandCodeTask getRandCode = new GetRandCodeTask(activity,imageView,2);
						getRandCode.execute(UrlEnum.DO_MAIN.getPath()
								+ UrlEnum.LOGIN_RANGCODE_URL.getPath());
					}
				});
				GetRandCodeTask getRandCode = new GetRandCodeTask(activity,imageView,2);
				getRandCode.execute(UrlEnum.DO_MAIN.getPath()
						+ UrlEnum.LOGIN_RANGCODE_URL.getPath());
				rangeCode = (EditText) orderCodeView
						.findViewById(R.id.orderCode);
				AlertDialog.Builder orderCodeBuilder = new AlertDialog.Builder(activity);
				orderCodeBuilder.setTitle("请输入验证码！")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(orderCodeView)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialogTextView.setText("正在确认订票！");
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
						}).setNegativeButton("取消",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								status = 0;
							}
						});
				orderCodeBuilder.show();
				break;
			case 13:
				LoginDialog.newInstance("今日将不能继续受理您的订票请求！").show(
						activity.getFragmentManager(), "dialog");
				break;
			default:
				break;
			}
			Log.i("RobitOrderFragment:onReceive", status + "");
			orderButton.setText("开始抢票");
			type = 0;
			progressDialog.dismiss();
			activity.stopService(intent);
		}
	}
	
	public void closeSoftInput(){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}
	
	public void unRegisterService(){
		if(myReceiver != null){
			activity.unregisterReceiver(myReceiver);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			switch (v.getId()) {
			case R.id.startTime:
				datePickerDialog = new DatePickerDialog(activity,
						mDateSetListener, mYear, mMonth, mDay);
				datePickerDialog.show();
				break;
			case R.id.startTrainNo:
				if(getConfigInfo()){
					GetTrainNoTast trainNoTast = new GetTrainNoTast(activity,configInfo);
					trainNoTast.execute("");
				}
				break;
			case R.id.orderPeople:
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
				break;
			case R.id.seatText:
				showSeatDialog();
				break;
			case R.id.timeText:
				showTimeDialog();
				break;
			case R.id.trainTypeText:
				showTrainTypeDialog();
				break;
			default:
				break;
			}
		}
		
	}
}
