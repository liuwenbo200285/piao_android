package com.wenbo.piao.util;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CommonUtil {

	/**
	 * 将用户登录名展示在actionbar上，长度大于14用。。。表示
	 * @return
	 */
	public static String showTitileName(){
		if(StringUtils.isNotEmpty(HttpClientUtil.getAccount().getName())){
			if(HttpClientUtil.getAccount().getName().length() <= 14){
				return HttpClientUtil.getAccount().getName();
			}else{
				return StringUtils.substring(HttpClientUtil.getAccount().getName(),0,14)+"..";
			}
		}
		return null;
	}
	
	//关闭软键盘
	public static void closeSoftMethod(Context context,EditText editText){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
