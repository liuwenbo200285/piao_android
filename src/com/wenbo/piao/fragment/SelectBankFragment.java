package com.wenbo.piao.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenbo.piao.R;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.PayInfo;
import com.wenbo.piao.util.HttpClientUtil;

/**
 * 选择银行界面fragment
 * @author wenbo
 *
 */
public class SelectBankFragment extends Fragment implements OnTouchListener {
	
	private Activity activity;
	
	private TextView textView;
	
	private ImageView icbcImage;
	
	private ImageView ccbImage;
	
	private ImageView abcImage;
	
	private ImageView bocImage;
	
	private ImageView unionPayImage;
	
	private ImageView cmbcImage;
	
	private ImageView baikeImage;
	
	private PayInfo payInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.selectbank, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		activity = getActivity();
		payInfo = HttpClientUtil.getPayInfo();
		Order order = HttpClientUtil.getNoCompletedOrders().get(0);
		textView = (TextView)activity.findViewById(R.id.payInfoText);
		textView.setText("车次信息："+order.getTrainInfo()+"\n应付金额："+payInfo.getPayMoney()+"\n\n请选择付款银行：");
		icbcImage = (ImageView)activity.findViewById(R.id.icbcImage);
		icbcImage.setOnTouchListener(this);
		ccbImage = (ImageView)activity.findViewById(R.id.ccbImage);
		ccbImage.setOnTouchListener(this);
		abcImage = (ImageView)activity.findViewById(R.id.abcImage);
		abcImage.setOnTouchListener(this);
		bocImage = (ImageView)activity.findViewById(R.id.bocImage);
		bocImage.setOnTouchListener(this);
		unionPayImage = (ImageView)activity.findViewById(R.id.unionPayImage);
		unionPayImage.setOnTouchListener(this);
		cmbcImage = (ImageView)activity.findViewById(R.id.cmbcImage);
		cmbcImage.setOnTouchListener(this);
		baikeImage = (ImageView)activity.findViewById(R.id.baikeImage);
		baikeImage.setOnTouchListener(this);
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
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.icbcImage:
			
			break;
		case R.id.ccbImage:
			
			break;
		default:
			break;
		}
		return false;
	}

}
