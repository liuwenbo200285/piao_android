package com.wenbo.piao.enums;

public enum InfoCodeEnum {
	//提示消息
	INFO_TIPS(1000),
	//提示消息
	INFO_NOTIFICATION(1001);
	
	private int code;
	
	private InfoCodeEnum(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
