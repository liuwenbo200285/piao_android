package com.wenbo.piao.fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.domain.OrderInfo;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.OperationUtil;

public class OrderDetailFragment extends Fragment {
	
	private Activity activity;
	
	private ListView listView;
	
	private AlertDialog alertDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.nocompletedorder, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity = getActivity();
		listView = (ListView)activity.findViewById(R.id.noCompleteOrderView);
		OrderAdapter adapter = new OrderAdapter(activity,0,HttpClientUtil.getSelectOrder().getOrderInfos());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
			
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setIcon(android.R.drawable.btn_dropdown)
				.setTitle("取消订单")
				.setMessage("您确认要取消该订单吗？")
				.setNegativeButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new AsyncTask<String,Integer,String>(){
							private ProgressDialog progressDialog;
							@Override
							protected String doInBackground(
									String... params) {
								return null;
							}
							@Override
							protected void onPostExecute(
									String result) {
								progressDialog.dismiss();
								if(result.equals(OperationUtil.OPERATION_SUCCESS)){
									LoginDialog.newInstance( "退票成功！").show(activity.getFragmentManager(),"dialog");
								}else{
									LoginDialog.newInstance( "退票失败！").show(activity.getFragmentManager(),"dialog"); 
								}
								super.onPostExecute(result);
							}

							@Override
							protected void onPreExecute() {
								progressDialog = ProgressDialog.show(activity,"退票","正在退票...",true,false);
								super.onPreExecute();
							}
						}.execute("");
					}
				})
				.setPositiveButton("取消",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.hide();
					}
				});
				alertDialog = builder.create();
				alertDialog.show();
				return true;
			}
		});
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private class OrderAdapter extends ArrayAdapter<OrderInfo> {

		private List<OrderInfo> items;

		public OrderAdapter(Context context, int textViewResourceId,
				List<OrderInfo> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.nocompeletedorderview, null);
			}
			OrderInfo orderInfo = items.get(position);
			if(orderInfo != null){
				TextView trainInfo = (TextView) view
						.findViewById(R.id.trainInfoTextView);
				trainInfo.setText(orderInfo.getTrainInfo());
				TextView seatInfo = (TextView) view
						.findViewById(R.id.seatInfoTextView);
				seatInfo.setText(orderInfo.getSeatInfo());
				TextView passengersInfo = (TextView) view
						.findViewById(R.id.passengersInfoTextView);
				passengersInfo.setText(orderInfo.getPassengersInfo());
				TextView statusInfo = (TextView) view
						.findViewById(R.id.statusInfoTextView);
				statusInfo.setText(orderInfo.getStatusInfo());
			}
			return view;
		}
	}

}
