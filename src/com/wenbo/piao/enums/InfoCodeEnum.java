package com.wenbo.piao.enums;

public enum InfoCodeEnum {
	//提示消息
	INFO_TIPS(1000);
	
	private int code;
	
	private InfoCodeEnum(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
