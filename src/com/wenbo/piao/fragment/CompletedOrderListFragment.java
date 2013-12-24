package com.wenbo.piao.fragment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wenbo.piao.R;
import com.wenbo.piao.activity.UserActivity;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.util.HttpClientUtil;

public class CompletedOrderListFragment extends Fragment {
	
	private UserActivity activity;
	
	private ListView listView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nocompletedorder, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = (UserActivity)getActivity();
		listView = (ListView)activity.findViewById(R.id.noCompleteOrderView);
		final List<Order> orders = HttpClientUtil.getMyOrders();
		OrderAdapter adapter = new OrderAdapter(activity,0,orders);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HttpClientUtil.setSelectOrder(orders.get(arg2));
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
		super.onActivityCreated(savedInstanceState);
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

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
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
					Button refundButton = (Button)view.findViewById(R.id.refund);
					Button payButton = (Button)view.findViewById(R.id.pay);
					Button lastTimeButton = (Button)view.findViewById(R.id.lastTimeButton);
					payButton.setVisibility(View.GONE);
					lastTimeButton.setVisibility(View.GONE);
					refundButton.setVisibility(View.GONE);
					if(StringUtils.contains(order.getOrderStatus(),"已出票")){
						refundButton.setVisibility(View.GONE);
					}else{
						refundButton.setText("退票");
					}
					TextView orderInfo = (TextView) view
							.findViewById(R.id.orderTextView);
					orderInfo.setText("订单日期： "+order.getOrderDate()+"\n订  单  号： "+order.getOrderNo()+"\n车次信息： "+formatTrainInfo(order.getTrainInfo()).trim()
							+"\n总  价  格： "+order.getAllMoney()+"元\n总  张  数： "+order.getOrderNum()+"张\n订单状态： "+order.getOrderStatus());
				}
			}
			return view;
		}
		
		private String formatTrainInfo(String info){
			if(StringUtils.isNotEmpty(info) && StringUtils.isNotBlank(info)){
				info = StringUtils.replace(info,"\"","");
				info = StringUtils.replace(info,"[","");
				info = StringUtils.replace(info,"]","");
				return info;
			}
			return "";
		}
	}

}
