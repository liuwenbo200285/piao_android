package com.wenbo.piao.enums;

/**
 * 坐席enum
 * @author Administrator
 *
 */
public enum TrainSeatEnum {

	BUSINESSSEAT(1,"商务座"),
	SPECIALSEAT(2,"特等座"),
	FRISTSEAT(3,"一等座"),
	SECONDSEAT(4,"二等座"),
	HIGHCUSHIONEDBERTHS(5,"高级软卧"),
	CUSHIONEDBERTHS(6,"软卧"),
	HARDSLEEPER(7,"硬卧"),
	SOFTSLEEPER(8,"软座"),
	HARDSEAT(9,"硬座"),
	NOSEAT(10,"硬座");
	
	private int code;
	
	private String name;
	
	private TrainSeatEnum(int code,String name){
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
