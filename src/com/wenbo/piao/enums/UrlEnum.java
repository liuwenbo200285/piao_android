package com.wenbo.piao.enums;

public enum UrlEnum {
	//域名
	DO_MAIN("https://dynamic.12306.cn/otsweb/","","","",""),
	//首页
	INDEX_URL("/otsweb","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8","","",""),
	//登录初始页面url
	LOGIN_INIT_URL("loginAction.do?method=loginAysnSuggest","","","",""),
	//登录初始页面url
	LOGIN_INIT_JS("dynamicJsAction.do?jsversion=5519&method=loginJs","","","",""),
	//登录验证码
	LOGIN_RANGCODE_URL("passCodeNewAction.do?module=login&rand=sjrand&","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8","","",""),
	//登录url
	LONGIN_CONFIM("loginAction.do?method=login","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
			"https://dynamic.12306.cn/otsweb/loginAction.do?method=init",
			"application/x-www-form-urlencoded",""),
	//获取人物信息url
	GET_ORDER_PERSON("passengerAction.do?method=getPagePassengerAll","application/json, text/javascript, */*",
			"https://dynamic.12306.cn/otsweb/passengerAction.do?method=initUsualPassenger12306","application/x-www-form-urlencoded",
			"XMLHttpRequest"),
	//查询火车票
	SEARCH_TICKET("order/querySingleAction.do","","https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init","",""),
	//点击预定
	BOOK_TICKET("order/querySingleAction.do","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
			"https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init","application/x-www-form-urlencoded",""),
	GET_ORDER_INFO("order/confirmPassengerAction.do","application/json, text/javascript, */*",
			"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init",
			"application/x-www-form-urlencoded","XMLHttpRequest"),
	//确认订单验证码
	ORDER_RANGCODE_URL("passCodeNewAction.do?module=passenger&rand=randp","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8","","",""),
	//获取坐席值的url
	GET_SEAT_VALUE("passCodeAction.do?rand:randp","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8","","",""),
	//查询预订车票余票信息
	SEARCH_TICKET_INFO("order/confirmPassengerAction.do","application/json, text/javascript, */*","","",""),
	//按出发和到达站查询车次
	SEARCH_TRAINNO("order/querySingleAction.do?method=queryststrainall","application/json, text/javascript, */*","https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init",
				"application/x-www-form-urlencoded",""),
	//未完成订单
	NO_NOTCOMPLETE("order/myOrderAction.do?method=queryMyOrderNotComplete&leftmenu=N","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
						"https://dynamic.12306.cn/otsweb/order/myOrderAction.do?method=queryMyOrderNotComplete&leftmenu=Y",
						"",""),
	SEARCH_COMPLETED_ORDER_INIT("order/myOrderAction.do?method=init&showMessage=Y","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
								"https://dynamic.12306.cn/otsweb/order/myOrderAction.do?method=init&showMessage=Y","",""),
	SEARCH_COMPLETED_ORDER("order/myOrderAction.do","","https://dynamic.12306.cn/otsweb/order/myOrderAction.do?method=queryMyOrder",
								"application/x-www-form-urlencoded",""),
	CANCEL_ORDER("order/orderAction.do","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
								"https://dynamic.12306.cn/otsweb/order/payConfirmOnlineSingleAction.do?method=payConfirmOnlineSingleEpaySuccess","application/x-www-form-urlencoded",""),
	PAY_ORDER_INIT("order/myOrderAction.do","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
			"https://dynamic.12306.cn/otsweb/order/myOrderAction.do?method=queryMyOrderNotComplete&leftmenu=Y",
			"application/x-www-form-urlencoded",""),
	TO_PAY("https://epay.12306.cn/pay/payGateway","","","",""),
	SELECT_BANK("https://epay.12306.cn/pay/webBusiness","","","",""),
	PAY_BY_UNIONPAY("https://unionpaysecure.com/api/Pay.action","","","",""),
	INIT_REFUND_TICKET("order/ticketAction.do?method=initRefundTicket","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
			"https://dynamic.12306.cn/otsweb/order/myOrderAction.do?method=queryMyOrder","application/x-www-form-urlencoded",
			""),
	CONFIRM_REFUND_TICKET("order/ticketAction.do?method=confirmRefundTicket","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
			"https://dynamic.12306.cn/otsweb/order/ticketAction.do?method=initRefundTicket","application/x-www-form-urlencoded",""),
	CHECK_LOGIN("order/myOrderAction.do?method=queryOrderWaitTime","application/json, text/javascript, */*","","","");
	private String path;
	
	private String accept;
	
	private String refer;
	
	private String contentType;
	
	private String xRequestWith;

	private UrlEnum(String path,String accept,String refer,String contentType,
			String xRequestWith){
		this.path = path;
		this.accept = accept;
		this.refer = refer;
		this.contentType = contentType;
		this.xRequestWith = xRequestWith;
	}

	public String getValue() {
		return path;
	}

	public String getPath() {
		return path;
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

	public String getAccept() {
		return accept;
	}
	
}
