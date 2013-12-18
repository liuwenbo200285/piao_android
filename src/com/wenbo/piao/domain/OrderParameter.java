package com.wenbo.piao.domain;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONObject;

public class OrderParameter {

	private Document document;
	
	private String ticketType;
	
	private String[] parameters;
	
	private String message;
	
	private JSONObject trainObject;
	
	private String secretStr;
	
	private String backDate = "2013-12-31";
	
	private String keyCheck;
	
	private Map<String,String> seatMap;
	
	private String orderCode;
	
	private String token;
	
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public JSONObject getTrainObject() {
		return trainObject;
	}

	public void setTrainObject(JSONObject trainObject) {
		this.trainObject = trainObject;
	}

	public String getSecretStr() {
		return secretStr;
	}

	public void setSecretStr(String secretStr) {
		this.secretStr = secretStr;
	}

	public String getBackDate() {
		return backDate;
	}

	public void setBackDate(String backDate) {
		this.backDate = backDate;
	}

	public String getKeyCheck() {
		return keyCheck;
	}

	public void setKeyCheck(String keyCheck) {
		this.keyCheck = keyCheck;
	}

	public Map<String, String> getSeatMap() {
		return seatMap;
	}

	public void setSeatMap(Map<String, String> seatMap) {
		this.seatMap = seatMap;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
