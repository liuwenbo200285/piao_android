package com.wenbo.piao.enums;

public enum UrlNewEnum {
	DO_MAIN("https://kyfw.12306.cn/otn/","","","",""),
	LOGIN_RANGCODE_URL("passcodeNew/getPassCodeNew.do?module=login&rand=sjrand&0.18459746381267905","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8","","",""),
	LONGIN_CONFIM("login/loginAysnSuggest","*/*",
			"https://kyfw.12306.cn/otn/login/init",
			"application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest"),
	//获取人物信息url
	GET_ORDER_PERSON("passengers/query","*/*","https://kyfw.12306.cn/otn/passengers/init","application/x-www-form-urlencoded","XMLHttpRequest"),
	SEARCH_TICKET("leftTicket/query","*/*","https://kyfw.12306.cn/otn/leftTicket/init?random=1387009135876","application/x-www-form-urlencoded","XMLHttpRequest"),
	GET_ORDER_RANGCODE("passcodeNew/getPassCodeNew?module=passenger&rand=randp","image/webp,*/*;q=0.8","https://kyfw.12306.cn/otn/confirmPassenger/initDc","",""),
	QUERY_MYORDER_NOCOMPLETE("queryOrder/queryMyOrderNoComplete","*/*","https://kyfw.12306.cn/otn/queryOrder/initNoComplete","application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest"),
	SUBMITORDERREQUEST("leftTicket/submitOrderRequest","*/*","https://kyfw.12306.cn/otn/leftTicket/init?random=1387009135876","application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest"),
	CHECKUSER("login/checkUser","*/*","https://kyfw.12306.cn/otn/leftTicket/init?random=1387009135876","application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest"),
	SUBMITINIT("leftTicket/init","*/*","https://kyfw.12306.cn/otn/leftTicket/init?random=1387009135876","application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest"),
	INITDC("confirmPassenger/initDc","*/*","https://kyfw.12306.cn/otn/leftTicket/init?random=1387009135876","",""),
	CHECKORDERINFO("confirmPassenger/checkOrderInfo","application/json, text/javascript, */*; q=0.01","https://kyfw.12306.cn/otn/confirmPassenger/initDc","application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest"),
	GETQUEUECOUNT("confirmPassenger/getQueueCount","","https://kyfw.12306.cn/otn/confirmPassenger/initDc","",""),
	PASSENGER_RANGCODE("passcodeNew/getPassCodeNew.do?module=passenger&rand=randp&0.15299957408569753","image/webp,*/*;q=0.8","https://kyfw.12306.cn/otn/confirmPassenger/initDc","",""),
	CONFIRMSINGLEFORQUEUE("confirmPassenger/confirmSingleForQueue","application/json, text/javascript, */*; q=0.01","https://kyfw.12306.cn/otn/confirmPassenger/initDc","",""),
	QUERYMYORDERNOCOMPLETE("queryOrder/queryMyOrderNoComplete","*/*","https://kyfw.12306.cn/otn/queryOrder/initNoComplete","",""),
	CONTINUE_PAY_NOCOMPLETEMYORDER("queryOrder/continuePayNoCompleteMyOrder","*/*","https://kyfw.12306.cn/otn/payfinish/init","",""),
	PAYORDER_INIT("payOrder/init","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8","https://kyfw.12306.cn/otn/payfinish/init","",""),
	CANCELNOCOMPLETEMYORDER("queryOrder/cancelNoCompleteMyOrder","*/*","","",""),
	QUERYMYORDER("queryOrder/queryMyOrder","*/*","https://kyfw.12306.cn/otn/queryOrder/init","",""),
	CHECKRANDCODEANSYN("passcodeNew/checkRandCodeAnsyn","*/*","https://kyfw.12306.cn/otn/confirmPassenger/initDc","","");
	private String path;
	
	private String accept;
	
	private String refer;
	
	private String contentType;
	
	private String xRequestWith;

	private UrlNewEnum(String path,String accept,String refer,String contentType,
			String xRequestWith){
		this.path = path;
		this.accept = accept;
		this.refer = refer;
		this.contentType = contentType;
		this.xRequestWith = xRequestWith;
	}

	public String getPath() {
		return path;
	}

	public String getAccept() {
		return accept;
	}

	public String getRefer() {
		return refer;
	}

	public String getContentType() {
		return contentType;
	}

	public String getxRequestWith() {
		return xRequestWith;
	}
}
