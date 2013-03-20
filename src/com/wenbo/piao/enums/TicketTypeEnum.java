package com.wenbo.piao.enums;

/**
 * 坐席枚举
 * @author wenbo
 *
 */
public enum TicketTypeEnum {

	SPECIAL(1);
	
	private int code;
	
	private TicketTypeEnum(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}
