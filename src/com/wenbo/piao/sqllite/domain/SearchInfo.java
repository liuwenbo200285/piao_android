package com.wenbo.piao.sqllite.domain;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "searchinfo")
public class SearchInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private Integer id;
	
	@DatabaseField(canBeNull = false)
	private String fromStation;
	
	@DatabaseField(canBeNull = false)
	private String toStation;
	
	@DatabaseField(canBeNull = false)
	private String accountName;
	
	@DatabaseField(canBeNull = true)
	private String orderDate;
	
	@DatabaseField(canBeNull = true)
	private String trainNo;
	
	@DatabaseField(canBeNull = true)
	private String trainCode;
	
	@DatabaseField(canBeNull = true)
	private String trainClass;
	
	@DatabaseField(canBeNull = true)
	private String orderPerson;
	
	@DatabaseField(canBeNull = true)
	private String orderSeat;
	
	@DatabaseField(canBeNull = true)
	private String orderTime;

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

	public String getTrainClass() {
		return trainClass;
	}

	public void setTrainClass(String trainClass) {
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getTrainCode() {
		return trainCode;
	}

	public void setTrainCode(String trainCode) {
		this.trainCode = trainCode;
	}
}
