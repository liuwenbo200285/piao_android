package com.wenbo.piao.enums;

public enum ParameterEnum {
	IS_RUSH("isRush"),
	
	TRAIN_TYPE("trainType"),
	
	ROBIT_STATE("state"),
	
	RANGECODE("rangeCode"),
	//
	ORDERTIME("orderTime"),
	//
	ORDERSEAT("orderSeat"),
	//
	ORDERDATE("orderDate"),
	//
	ORDERPERSON("orderPerson"),
	//
	TRAINNO("trainNo"),
	//
	TOSTATION("toStation"),
	//始发地
	FROMSTATION("fromStation");

	private String value;
	
	private ParameterEnum(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
