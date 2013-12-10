package com.wenbo.piao.enums;

public enum UrlNewEnum {
	DO_MAIN("https://kyfw.12306.cn/otn/","","","",""),
	
	LONGIN_CONFIM("login/loginAysnSuggest","*/*",
			"https://kyfw.12306.cn/otn/login/init",
			"application/x-www-form-urlencoded; charset=UTF-8","XMLHttpRequest");

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
