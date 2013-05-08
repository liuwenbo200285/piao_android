package com.wenbo.piao.domain;

public class PayInfo {
	
	private String payUrl;
	
	private String interfaceName;
	
	private String interfaceVersion;

	private String tranData;
	
	private String merSignMsg;
	
	private String appId;
	
	private String transType;
	
	private String lastTime;
	
	private String merCustomIp;
	
	private String orderTimeoutDate;
	
	private String bankId;
	
	private String payMoney;

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getInterfaceVersion() {
		return interfaceVersion;
	}

	public void setInterfaceVersion(String interfaceVersion) {
		this.interfaceVersion = interfaceVersion;
	}

	public String getTranData() {
		return tranData;
	}

	public void setTranData(String tranData) {
		this.tranData = tranData;
	}

	public String getMerSignMsg() {
		return merSignMsg;
	}

	public void setMerSignMsg(String merSignMsg) {
		this.merSignMsg = merSignMsg;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getMerCustomIp() {
		return merCustomIp;
	}

	public void setMerCustomIp(String merCustomIp) {
		this.merCustomIp = merCustomIp;
	}

	public String getOrderTimeoutDate() {
		return orderTimeoutDate;
	}

	public void setOrderTimeoutDate(String orderTimeoutDate) {
		this.orderTimeoutDate = orderTimeoutDate;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
}
