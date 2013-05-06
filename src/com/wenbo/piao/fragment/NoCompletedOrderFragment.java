package com.wenbo.piao.fragment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.JsoupUtil;

public class NoCompletedOrderFragment extends Fragment {
	private UserActivity activity;
	
	private List<Order> noCompletedOrders;
	
	private ProgressDialog progressDialog;
	
	private ListView listView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nocompletedorder, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		activity = (UserActivity)getActivity();
		listView = (ListView)activity.findViewById(R.id.noCompleteOrderView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HttpClientUtil.setSelectOrder(noCompletedOrders.get(arg2));
				FragmentManager fm = activity.getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
		    	Fragment listFragment = null;
		    	listFragment = fm.findFragmentByTag("orderDetail");
		    	if(listFragment == null){
		    		listFragment = new OrderDetailFragment();
		    	}
				ft.replace(R.id.details,listFragment,"orderDetail");
				ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out); 
				ft.addToBackStack(null);
				ft.commit();
				activity.setCurrentFragment(listFragment);
			}
		});
		noCompletedOrders = HttpClientUtil.getNoCompletedOrders();
		if(noCompletedOrders == null || noCompletedOrders.isEmpty()){
			new AsyncTask<Integer,Integer,Integer>() {
				@Override
				protected Integer doInBackground(Integer... params) {
					HttpResponse response = null;
					try {
						HttpGet httpGet = HttpClientUtil.getHttpGet(UrlEnum.NO_NOTCOMPLETE);
						response = HttpClientUtil.getHttpClient().execute(httpGet);
						if (response.getStatusLine().getStatusCode() == 200) {
//							noCompletedOrders = JsoupUtil.getNoCompleteOrders(response.getEntity().getContent());
							noCompletedOrders = JsoupUtil.getNoCompleteOrders(activity.getAssets().open("Noname5.txt"));
							HttpClientUtil.setNoCompletedOrders(noCompletedOrders);
						}
					} catch (Exception e) {
						Log.e("GetNoCompletedOrder","onTabSelected", e);
					} finally {
						
					}
					return null;
				}

				@Override
				protected void onPostExecute(Integer result) {
					progressDialog.dismiss();
					showView();
					super.onPostExecute(result);
				}

				@Override
				protected void onPreExecute() {
					progressDialog = ProgressDialog.show(activity,"获取未付款订单","正在获取未付款订单...",true,false);
					super.onPreExecute();
				}
			}.execute(0);
		}else{
			showView();
		}
		super.onActivityCreated(savedInstanceState);
	}
	
	private void showView(){
		if(noCompletedOrders.isEmpty()){
    		noCompletedOrders = null;
			LoginDialog.newInstance( "没有未付款订单！").show(activity.getFragmentManager(),"dialog"); 
			return;
		}
		OrderAdapter adapter = new OrderAdapter(activity,0,noCompletedOrders);
		listView.setAdapter(adapter);
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
	
	private class OrderAdapter extends ArrayAdapter<Order> {

		private List<Order> items;

		public OrderAdapter(Context context, int textViewResourceId,
				List<Order> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.nocompeletedorderdetail, null);
			}
			Order order = items.get(position);
			if (order != null) {
				if(StringUtils.isNotBlank(order.getOrderDate())){
					TextView orderInfo = (TextView) view
							.findViewById(R.id.orderTextView);
					orderInfo.setText(order.getOrderDate()+"\n订  单  号： "+order.getOrderNo()+"\n车次信息： "+order.getTrainInfo().trim()
							+"\n总  张  数： "+order.getOrderNum()+"张");
				}
			}
			return view;
		}
	}
	
	public void closeSoftInput(){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE); 
		if (imm.isActive()) {
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
		}
	}
}
