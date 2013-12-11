package com.wenbo.piao.enums;

public enum UrlNewEnum {
	DO_MAIN("https://kyfw.12306.cn/otn/","","","",""),
	LOGIN_RANGCODE_URL("passcodeNew/getPassCodeNew.do?module=login&rand=sjrand&0.18459746381267905","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8","","",""),
	LONGIN_CONFIM("login/loginAysnSuggest","*/*",
			"https://kyfw.12306.cn/otn/login/init",
			"application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest"),
	//获取人物信息url
	GET_ORDER_PERSON("passengers/query","*/*","https://kyfw.12306.cn/otn/passengers/init","application/x-www-form-urlencoded","XMLHttpRequest"),
	SEARCH_TICKET("leftTicket/query","*/*","https://kyfw.12306.cn/otn/leftTicket/init","application/x-www-form-urlencoded","XMLHttpRequest"),
	GET_ORDER_RANGCODE("passcodeNew/getPassCodeNew?module=passenger&rand=randp","image/webp,*/*;q=0.8","https://kyfw.12306.cn/otn/confirmPassenger/initDc","",""),
	QUERY_MYORDER_NOCOMPLETE("queryOrder/queryMyOrderNoComplete","*/*","https://kyfw.12306.cn/otn/queryOrder/initNoComplete","application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest"),
	CHECKUSER("login/checkUser","*/*","https://kyfw.12306.cn/otn/leftTicket/init","application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest");

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
