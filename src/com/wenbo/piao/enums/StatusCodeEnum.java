package com.wenbo.piao.enums;

/**
 * 订票状态码
 * @author wenbo
 *
 */
public enum StatusCodeEnum {
	//取消次数过多，今日将不能继续受理您的订票请求！
	CANCEL_ORDER_MANY(13),
	//输入验证码
	INPUT_ORDERCODE(12),
	//订票成功
	ORDER_SUCCESS(11),
	//非法的订票请求
	NO_ALLOW_ORDER(10),
	//票数不够
	TICKET_IS_NOT_ENOUGH(9),
	//输入的验证码不正确
	ORDER_CODE_ERROR(8),
	//一个账号最多只能预定5张火车票
	ORDER_NUM_ERROR(7),
	//订票人格式填写不正确
	ORDER_PERSON_ERROR(6),
	//预订坐席填写不正确
	ORDER_SEAT_ERROR(5),
	//可能还有未处理的订单或者系统维护等其它异常
	HAVA_NO_DETAIL_ORDER(4),
	//车次输入错误
	TRAIN_NO_ERROR(3),
	//查询到有票
	HAVE_TICKET(2),
	//系统维护中
	SYSTEM_MAINTENANCE(1);

	private int code;
	
	private StatusCodeEnum(int code){
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
