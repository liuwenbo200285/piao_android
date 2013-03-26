package com.wenbo.piao.activity;

import com.wenbo.androidpiao.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

public class TestMyActivityGroup extends AbstractMyActivityGroup{
    //加载的Activity的名字，LocalActivityManager就是通过这些名字来查找对应的Activity的。
    private static final String CONTENT_ACTIVITY_NAME_1 = "contentActivity1";
    private static final String CONTENT_ACTIVITY_NAME_2 = "contentActivity2";
    private static final String CONTENT_ACTIVITY_NAME_3 = "contentActivity3";
    private static final String CONTENT_ACTIVITY_NAME_4 = "contentActivity4";
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.my_activity_group);
        super.onCreate(savedInstanceState);
        
        ((RadioButton)findViewById(R.id.button1)).setChecked(true);
    }
    
    /**
     * 找到自定义id的加载Activity的View
     */
    @Override
    protected ViewGroup getContainer() {
        return (ViewGroup) findViewById(R.id.container);
    }
    
    /**
     * 初始化按钮
     */
    @Override
    protected void initRadioBtns() {
        initRadioBtn(R.id.button1);
        initRadioBtn(R.id.button2);
        initRadioBtn(R.id.button3);
        initRadioBtn(R.id.button4);
    }
    
           

	@Override
	public void onClick(View view) {
		 switch (view.getId()) {
         case R.id.button1:
             setContainerView(CONTENT_ACTIVITY_NAME_1, Info1Activity.class);
             break;
         case R.id.button2:
             setContainerView(CONTENT_ACTIVITY_NAME_2, Info2Activity.class);
             break;
         case R.id.button3:
             setContainerView(CONTENT_ACTIVITY_NAME_3, Info3Activity.class);
             break;
         case R.id.button4:
             setContainerView(CONTENT_ACTIVITY_NAME_4, Info4Activity.class);
             break;
         default:
             break;
         }		
	}      
    
}
