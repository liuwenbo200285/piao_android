package com.wenbo.piao.activity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;

/**
 * 自己实现的一个通用ActivityGroup。
 * 可以通过简单的重写它来制作有导航按钮和用导航按钮控制动态加载Activity的ActivityGroup。
 * 开发者需要在实现类中实现三个方法：
 *     1.指定动态加载Activity的容器的对象，getContainer()方法。
 *     2.初始化所有的导航按钮，initRadioBtns()方法，开发者要遍历所有的导航按钮并执行initRadioBtn(int id)方法。
 *     3.实现导航按钮动作监听器的具体方法，onCheckedChanged(...)方法。这个方法将实现某个导航按钮与要启动对应的Activity的关联关系，可以调用setContainerView(...)方法。
 * @author zet
 *
 */
public abstract class AbstractMyActivityGroup extends ActivityGroup implements OnCheckedChangeListener{
    
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        initRadioBtns();
	    }
	    
	    //加载Activity的View容器，容器应该是ViewGroup的子类
	    private ViewGroup container;
	    
	    private LocalActivityManager localActivityManager;
	    
	    /**
	     * 加载Activity的View容器的id并不是固定的，将命名规则交给开发者
	     * 开发者可以在布局文件中自定义其id，通过重写这个方法获得这个View容器的对象
	     * @return
	     */
	    abstract protected ViewGroup getContainer();
	    
	    /**
	     * 供实现类调用，根据导航按钮id初始化按钮
	     * @param id
	     */
	    protected void initRadioBtn(int id){
	        RadioButton btn = (RadioButton) findViewById(id);
	        btn.setOnCheckedChangeListener(this);
	    }
	    
	    /**
	     * 开发者必须重写这个方法，来遍历并初始化所有的导航按钮
	     */
	    abstract protected void initRadioBtns();
	    
	    /**
	     * 为启动Activity初始化Intent信息
	     * @param cls
	     * @return
	     */
	    private Intent initIntent(Class<?> cls){
	        return new Intent(this,    cls).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    }
	    
	    /**
	     * 供开发者在实现类中调用，能将Activity容器内的Activity移除，再将指定的某个Activity加入
	     * @param activityName 加载的Activity在localActivityManager中的名字
	     * @param activityClassTye    要加载Activity的类型
	     */
	    protected void setContainerView(String activityName, Class<?> activityClassTye){
	        if(null == localActivityManager){
	            localActivityManager = getLocalActivityManager();
	        }
	        
	        if(null == container){
	            container = getContainer();
	        }
	        
	        //移除内容部分全部的View
	        container.removeAllViews();
	        
	        Activity contentActivity = localActivityManager.getActivity(activityName);
	        if (null == contentActivity) {
	            localActivityManager.startActivity(activityName, initIntent(activityClassTye));
	        }
	        
	        //加载Activity
	        container.addView(
	                localActivityManager.getActivity(activityName)
	                        .getWindow().getDecorView(),
	                new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
	                        LayoutParams.FILL_PARENT));
	    }
    
}
