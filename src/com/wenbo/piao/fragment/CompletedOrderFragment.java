package com.wenbo.piao.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.OrderInfo;
import com.wenbo.piao.enums.UrlNewEnum;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.task.GetPersonConstanct;
import com.wenbo.piao.util.HttpClientUtil;

public class CompletedOrderFragment extends Fragment implements OnCheckedChangeListener,OnFocusChangeListener,
OnClickListener,android.view.View.OnClickListener {
	private UserActivity activity;
	private CheckBox orderTimeCheckBox;
	private CheckBox takeTrainTimeCheckBox;
	private EditText orderTimeText;
	private EditText endTimeText;
	private EditText orderNoText;
	private EditText trainNoText;
	private EditText passengersNameText;
	private Button orderSearch;
	private int mYear;
	private int mMonth;
	private int mDay;
	private EditText currentEditText;
	private DatePickerDialog datePickerDialog;
	private Map<String, UserInfo> userInfoMap;
	private AlertDialog dialog;
	private String[] contacts;
	private ProgressDialog progressDialog;
	private FragmentManager fm;
	private int flag = 1;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.completedorder, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		activity = (UserActivity)getActivity();
		fm = activity.getFragmentManager();
		orderTimeCheckBox = (CheckBox)activity.findViewById(R.id.orderTimeCheckBox);
		orderTimeCheckBox.setOnCheckedChangeListener(this);
		takeTrainTimeCheckBox = (CheckBox)activity.findViewById(R.id.takeTrainTimeCheckBox);
		takeTrainTimeCheckBox.setOnCheckedChangeListener(this);
		orderTimeText = (EditText)activity.findViewById(R.id.orderTimeText);
		endTimeText = (EditText)activity.findViewById(R.id.endTimeText);
		setDateTime(orderTimeText);
		setDateTime(endTimeText);
		orderTimeText.setOnFocusChangeListener(this);
		endTimeText.setOnFocusChangeListener(this);
		orderNoText = (EditText)activity.findViewById(R.id.orderNoText);
		trainNoText = (EditText)activity.findViewById(R.id.trainNoText);
		passengersNameText = (EditText)activity.findViewById(R.id.passengersNameText);
		passengersNameText.setOnFocusChangeListener(this);
		orderSearch = (Button)activity.findViewById(R.id.orderSearch);
		orderSearch.setOnClickListener(this);
		super.onActivityCreated(savedInstanceState);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	public void closeSoftInput(){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			if(buttonView == orderTimeCheckBox){
				takeTrainTimeCheckBox.setChecked(false);
				flag = 1;
			}else{
				orderTimeCheckBox.setChecked(false);
				flag = 2;
			}
		}
	}
	
	/**
	 * 设置日期
	 */
	private void setDateTime(EditText editText) {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		if(editText == orderTimeText){
			mMonth--;
		}
		updateDateDisplay(editText);
	}
	
	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay(EditText editText) {
		String oldDate = editText.getText().toString();
		String newDate = new StringBuilder().append(mYear).append("-")
				.append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
				.append("-").append((mDay < 10) ? "0" + mDay : mDay).toString().trim();
		if(!oldDate.equals(newDate)){
			editText.setText(newDate);
		}
//		editText.clearFocus();
	}
	
	/**
	 * 日期控件的事件
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDateDisplay(currentEditText);
//			orderSearch.requestFocus();
			currentEditText.clearFocus();
//			closeSoftInput();
		}
	};

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		new AsyncTask<Integer,Integer,List<Order>>() {
			@Override
			protected List<Order> doInBackground(Integer... params) {
				List<Order> orders = null;
				try {
					Map<String, String> paraMap = new HashMap<String, String>();
					paraMap.put("queryType",flag+"");
					paraMap.put("queryStartDate",orderTimeText.getText().toString());
					paraMap.put("queryEndDate",endTimeText.getText().toString());
					paraMap.put("come_from_flag","my_order");
					paraMap.put("pageSize","100");
					paraMap.put("pageIndex","0");
					paraMap.put("sequeue_train_name","");
					String info = HttpClientUtil.doPost(UrlNewEnum.QUERYMYORDER, paraMap,0);
					if(StringUtils.isNotEmpty(info)){
						JSONObject jsonObject = JSON.parseObject(info);
						if(jsonObject.containsKey("status")
								&& jsonObject.getBooleanValue("status")){
							orders = new ArrayList<Order>();
							if(jsonObject.containsKey("data")){
								JSONObject object = jsonObject.getJSONObject("data");
								int n = object.getIntValue("order_total_number");
								if(n >= 0){
									JSONArray array = object.getJSONArray("OrderDTODataList");
									OrderInfo orderInfo = null;
									JSONObject ordeObject = null;
									Order order = null;
									for(int i = 0; i < n; i++){
										ordeObject = array.getJSONObject(i);
										order = new Order();
										order.setAllMoney(ordeObject.getDoubleValue("ticket_price_all")/100);
										order.setOrderDate(ordeObject.getString("order_date"));
										order.setOrderNo(ordeObject.getString("sequence_no"));
										order.setOrderNum(ordeObject.getString("ticket_totalnum"));
										order.setTrainInfo(ordeObject.getString("start_train_date_page")+"开 "+ordeObject.getString("train_code_page")
												+" "+ordeObject.getString("from_station_name_page")+"-"+ordeObject.getString("to_station_name_page"));
//										order.setOrderStatus(ordeObject.getJSONArray("tickets").getJSONObject(0).getString("ticket_status_name"));
										List<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
										JSONArray perpleArray = ordeObject.getJSONArray("tickets");
										for(int j = 0; j < perpleArray.size(); j++){
											JSONObject perpleObject = perpleArray.getJSONObject(j);
											order.setOrderStatus(perpleObject.getString("ticket_status_name"));
											orderInfo = new OrderInfo();
											orderInfo.setPassengersInfo(perpleObject.getJSONObject("passengerDTO").getString("passenger_name")
													+"\n"+perpleObject.getJSONObject("passengerDTO").getString("passenger_id_type_name"));
											orderInfo.setSeatInfo(perpleObject.getString("coach_name")+"\n"+perpleObject.getString("seat_name")+"\n"+
													perpleObject.getString("seat_type_name"));
											orderInfo.setStatusInfo(perpleObject.getString("ticket_status_name"));
											orderInfo.setTicketNo(perpleObject.getString("ticket_no"));
											orderInfo.setTrainInfo(perpleObject.getString("start_train_date_page")+" 开\n"+perpleObject.getJSONObject("stationTrainDTO").getString("station_train_code")
													+"\n"+perpleObject.getJSONObject("stationTrainDTO").getString("from_station_name")+"-"+perpleObject.getJSONObject("stationTrainDTO").getString("to_station_name"));
											orderInfos.add(orderInfo);
										}
										order.setOrderInfos(orderInfos);
										orders.add(order);
									}
								}
							}
						}
					}
				} catch (Exception e) {
					Log.e("CompletedOrderFragment","checkTicket error!", e);
				} finally {
				}
				return orders;
			}

			@Override
			protected void onPostExecute(List<Order> orders) {
				progressDialog.dismiss();
		    	if(orders == null ||
		    			orders.isEmpty()){
					LoginDialog.newInstance( "没有订单！").show(activity.getFragmentManager(),"dialog"); 
					return;
				}
		    	HttpClientUtil.setMyOrders(orders);
		    	FragmentTransaction ft = fm.beginTransaction();
		    	Fragment listFragment = null;
		    	listFragment = fm.findFragmentByTag("myorder");
		    	if(listFragment == null){
		    		listFragment = new CompletedOrderListFragment();
		    	}
				ft.replace(R.id.details,listFragment,"myorder");
				ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
				ft.addToBackStack(null);
				ft.commit();
				activity.setCurrentFragment(listFragment);
				super.onPostExecute(orders);
			}

			@Override
			protected void onPreExecute() {
				progressDialog = ProgressDialog.show(activity,"获取成功订单","正在获取成功订单...",true,false);
				super.onPreExecute();
			}
		}.execute(0);
	}
	
	private void getPersonInfo() {
		GetPersonConstanct getPersonConstanct = new GetPersonConstanct(
				activity, userInfoMap, this);
		getPersonConstanct.execute("");
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
				AlertDialog.Builder builder = new AlertDialog.Builder(activity)
						.setSingleChoiceItems(contacts,0,new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								passengersNameText.setText(contacts[which]);
								passengersNameText.clearFocus();
								closeSoftInput();
								dialog.dismiss();
							}
						}).setIcon(android.R.drawable.btn_star);
				builder.setTitle("选择查询乘客姓名")
						// 设置Dialog的标题
						.setPositiveButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								closeSoftInput();
								dialog.dismiss();
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			currentEditText = (EditText)v;
			switch (v.getId()) {
			case R.id.orderTimeText:
				String[] str = StringUtils.split(orderTimeText.getText().toString(),"-");
				datePickerDialog = new DatePickerDialog(activity,mDateSetListener,Integer.parseInt(str[0]), 
						Integer.parseInt(str[1])-1, Integer.parseInt(str[2]));
				datePickerDialog.show();
				break;
			case R.id.endTimeText:
				datePickerDialog = new DatePickerDialog(activity,
						mDateSetListener, mYear, mMonth, mDay);
				datePickerDialog.show();
				break;
			case R.id.passengersNameText:
				userInfoMap = HttpClientUtil.getUserInfoMap();
				if (userInfoMap == null) {
					userInfoMap = new HashMap<String, UserInfo>();
					getPersonInfo();
				} else {
					if (userInfoMap.isEmpty()) {
						getPersonInfo();
					}
				}
				showDialog();
				break;
			default:
				break;
			}
		}
	}
}
