package com.wenbo.piao.domain;

/**
 * 预定配置VO
 * @author wenbo
 *
 */
public class ConfigInfo {
	private String username;
	
	private String userpass;
	
	private String orderDate;
	
	private String fromStation;
	
	private String fromStationName;
	
	private String toStation;
	
	private String toStationName;
	
	private String trainNo;
	
	private String[] trainClass;
	
	private String orderPerson;
	
	private String orderSeat;
	
	private String orderTime;
	
	private Integer searchSleepTime;
	
	private Integer searchWatiTime;
	
	private String rangeCode;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpass() {
		return userpass;
	}

	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getFromStation() {
		return fromStation;
	}

	public void setFromStation(String fromStation) {
		this.fromStation = fromStation;
	}

	public String getToStation() {
		return toStation;
	}

	public void setToStation(String toStation) {
		this.toStation = toStation;
	}

	public String getTrainNo() {
		return trainNo;
	}

	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	public String[] getTrainClass() {
		return trainClass;
	}

	public void setTrainClass(String[] trainClass) {
		this.trainClass = trainClass;
	}

	public String getOrderPerson() {
		return orderPerson;
	}

	public void setOrderPerson(String orderPerson) {
		this.orderPerson = orderPerson;
	}

	public String getOrderSeat() {
		return orderSeat;
	}

	public void setOrderSeat(String orderSeat) {
		this.orderSeat = orderSeat;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public Integer getSearchSleepTime() {
		return searchSleepTime;
	}

	public void setSearchSleepTime(Integer searchSleepTime) {
		this.searchSleepTime = searchSleepTime;
	}

	public Integer getSearchWatiTime() {
		return searchWatiTime;
	}

	public void setSearchWatiTime(Integer searchWatiTime) {
		this.searchWatiTime = searchWatiTime;
	}

	public String getRangeCode() {
		return rangeCode;
	}

	public void setRangeCode(String rangeCode) {
		this.rangeCode = rangeCode;
	}

	public String getFromStationName() {
		return fromStationName;
	}

	public void setFromStationName(String fromStationName) {
		this.fromStationName = fromStationName;
	}

	public String getToStationName() {
		return toStationName;
	}

	public void setToStationName(String toStationName) {
		this.toStationName = toStationName;
	}
}
