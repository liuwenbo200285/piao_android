package com.wenbo.piao.util;

import org.apache.commons.lang3.StringUtils;

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
}
