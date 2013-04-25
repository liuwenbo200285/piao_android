package com.wenbo.piao.sqllite.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//zqi|枣强|ZVP|zaoqiang|zq|2070
@DatabaseTable(tableName = "station")
public class Station {
	
	@DatabaseField(id = true)
	private String simpleCode;
	
	@DatabaseField(canBeNull = false)
	private String zhCode;
	
	@DatabaseField(canBeNull = false)
	private String stationCode;
	
	@DatabaseField(canBeNull = false)
	private String pinyingCode;
	
	@DatabaseField(canBeNull = false)
	private String simplePinyingCode;
	
	@DatabaseField(canBeNull = false)
	private String code;

	public String getSimpleCode() {
		return simpleCode;
	}

	public void setSimpleCode(String simpleCode) {
		this.simpleCode = simpleCode;
	}

	public String getZhCode() {
		return zhCode;
	}

	public void setZhCode(String zhCode) {
		this.zhCode = zhCode;
	}

	public String getStationCode() {
		return stationCode;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getPinyingCode() {
		return pinyingCode;
	}

	public void setPinyingCode(String pinyingCode) {
		this.pinyingCode = pinyingCode;
	}

	public String getSimplePinyingCode() {
		return simplePinyingCode;
	}

	public void setSimplePinyingCode(String simplePinyingCode) {
		this.simplePinyingCode = simplePinyingCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
