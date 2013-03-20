package com.wenbo.piao.domain;

/**
 * 用户信息domian
 * @author wenbo
 *
 */
public class UserInfo {
	//序号，订票时需要用到
	private Integer index;
	private String first_letter;
	private String isUserSelf;
    private String mobile_no;
    private String old_passenger_id_no;
    private String old_passenger_id_type_code;
    private String old_passenger_name;
    private String passenger_flag;
    private String passenger_id_no;
    private String passenger_id_type_code;
    private String passenger_id_type_name;
    private String passenger_name;
    private String passenger_type;
    private String passenger_type_name;
    private String recordCount;
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public String getFirst_letter() {
		return first_letter;
	}
	public void setFirst_letter(String first_letter) {
		this.first_letter = first_letter;
	}
	public String getIsUserSelf() {
		return isUserSelf;
	}
	public void setIsUserSelf(String isUserSelf) {
		this.isUserSelf = isUserSelf;
	}
	public String getMobile_no() {
		return mobile_no;
	}
	public void setMobile_no(String mobile_no) {
		this.mobile_no = mobile_no;
	}
	public String getOld_passenger_id_no() {
		return old_passenger_id_no;
	}
	public void setOld_passenger_id_no(String old_passenger_id_no) {
		this.old_passenger_id_no = old_passenger_id_no;
	}
	public String getOld_passenger_id_type_code() {
		return old_passenger_id_type_code;
	}
	public void setOld_passenger_id_type_code(String old_passenger_id_type_code) {
		this.old_passenger_id_type_code = old_passenger_id_type_code;
	}
	public String getOld_passenger_name() {
		return old_passenger_name;
	}
	public void setOld_passenger_name(String old_passenger_name) {
		this.old_passenger_name = old_passenger_name;
	}
	public String getPassenger_flag() {
		return passenger_flag;
	}
	public void setPassenger_flag(String passenger_flag) {
		this.passenger_flag = passenger_flag;
	}
	public String getPassenger_id_no() {
		return passenger_id_no;
	}
	public void setPassenger_id_no(String passenger_id_no) {
		this.passenger_id_no = passenger_id_no;
	}
	public String getPassenger_id_type_code() {
		return passenger_id_type_code;
	}
	public void setPassenger_id_type_code(String passenger_id_type_code) {
		this.passenger_id_type_code = passenger_id_type_code;
	}
	public String getPassenger_id_type_name() {
		return passenger_id_type_name;
	}
	public void setPassenger_id_type_name(String passenger_id_type_name) {
		this.passenger_id_type_name = passenger_id_type_name;
	}
	public String getPassenger_name() {
		return passenger_name;
	}
	public void setPassenger_name(String passenger_name) {
		this.passenger_name = passenger_name;
	}
	public String getPassenger_type() {
		return passenger_type;
	}
	public void setPassenger_type(String passenger_type) {
		this.passenger_type = passenger_type;
	}
	public String getPassenger_type_name() {
		return passenger_type_name;
	}
	public void setPassenger_type_name(String passenger_type_name) {
		this.passenger_type_name = passenger_type_name;
	}
	public String getRecordCount() {
		return recordCount;
	}
	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}
    
}
