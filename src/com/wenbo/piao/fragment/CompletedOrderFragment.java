package com.wenbo.piao.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

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

import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.OrderInfo;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.task.GetPersonConstanct;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.JsoupUtil;

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
			updateDateDisplay(currentEditText);
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
				HttpResponse response = null;
				List<Order> turnOrders = new ArrayList<Order>();
				try {
					HttpGet httpGet = HttpClientUtil.getHttpGet(UrlEnum.SEARCH_COMPLETED_ORDER_INIT);
					response = HttpClientUtil.getHttpClient().execute(httpGet);
					if (response.getStatusLine().getStatusCode() == 200) {
						String token = JsoupUtil.getMyOrderInit(response.getEntity().getContent(),1);
						List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
						parameters.add(new BasicNameValuePair("method","queryMyOrder"));
						parameters.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN",token));
						parameters.add(new BasicNameValuePair("queryOrderDTO.location_code",""));
						parameters.add(new BasicNameValuePair("leftmenu","Y"));
						parameters.add(new BasicNameValuePair("queryDataFlag",""+flag));
						parameters.add(new BasicNameValuePair("queryOrderDTO.from_order_date",orderTimeText.getText().toString()));
						parameters.add(new BasicNameValuePair("queryOrderDTO.to_order_date",endTimeText.getText().toString()));
						parameters.add(new BasicNameValuePair("queryOrderDTO.sequence_no",orderNoText.getText().toString()));
						parameters.add(new BasicNameValuePair("queryOrderDTO.train_code",trainNoText.getText().toString()));
						parameters.add(new BasicNameValuePair("queryOrderDTO.name",passengersNameText.getText().toString()));
						UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,"UTF-8");
						HttpPost httpPost = HttpClientUtil.getHttpPost(UrlEnum.SEARCH_COMPLETED_ORDER);
						httpPost.setEntity(uef);
						response = HttpClientUtil.getHttpClient().execute(httpPost);
						if (response.getStatusLine().getStatusCode() == 200) {
							List<Order> orders = JsoupUtil.myOrders(response.getEntity().getContent());
							if(!orders.isEmpty()){
								Iterator<Order> iterator = orders.iterator();
						    	while(iterator.hasNext()){
						    		Order order = iterator.next();
						    		int n = 0;
						    		if(order.getOrderInfos() != null && !order.getOrderInfos().isEmpty()){
						    			for(OrderInfo orderInfo:order.getOrderInfos()){
						    				if(n == 0){
						    					order.setOrderInfo(orderInfo);
						    					turnOrders.add(order);
						    				}else{
						    					Order order2 = new Order();
						    					order2.setOrderInfo(orderInfo);
						    					turnOrders.add(order2);
						    				}
						    				n++;
						    			}
						    			orders = null;
						    		}
						    	}
							}
						}
					}
				} catch (Exception e) {
					Log.e("CompletedOrderFragment","checkTicket error!", e);
				} finally {
//					HttpClientUtils.closeQuietly(response);
				}
				return turnOrders;
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
								closeSoftInput();
								dialog.dismiss();
							}
						}).setIcon(android.R.drawable.btn_dropdown);
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
				datePickerDialog = new DatePickerDialog(activity,
						mDateSetListener, mYear, mMonth, mDay);
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
