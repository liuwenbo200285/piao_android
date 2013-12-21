package com.wenbo.piao.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.wenbo.piao.domain.ConfigInfo;

public class ConfigUtil {

	/**
	 * @param args
	 */
	public static ConfigInfo loadConfigInfo() {
		InputStream inputStream = null;
		ConfigInfo configInfo = null;
		try {
			Properties properties = new Properties();
			inputStream = ConfigUtil.class.getResourceAsStream("/config.properties");
			properties.load(inputStream);
			configInfo = new ConfigInfo();
			configInfo.setUsername(properties.getProperty("username",""));
			configInfo.setUserpass(properties.getProperty("userpass",""));
			configInfo.setOrderDate(properties.getProperty("orderDate",""));
			configInfo.setFromStation(properties.getProperty("fromStation",""));
			configInfo.setToStation(properties.getProperty("toStation",""));
			configInfo.setTrainNo(properties.getProperty("trainNo",""));
			configInfo.setTrainClass(StringUtils.split(properties.getProperty("trainClass",""),","));
			configInfo.setOrderPerson(new String(properties.getProperty("orderPerson","").getBytes("ISO8859-1"),"UTF-8"));
			configInfo.setOrderSeat(properties.getProperty("orderSeat",""));
			configInfo.setOrderTime(properties.getProperty("orderTime","00:00--24:00"));
			configInfo.setSearchSleepTime(Integer.valueOf(properties.getProperty("searchSleepTime","1")));
			configInfo.setSearchWatiTime(Integer.valueOf(properties.getProperty("searchWatiTime","5")));
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(inputStream);
		}
		return configInfo;
	}

}
