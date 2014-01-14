package com.wenbo.piao.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.domain.ConfigInfo;
import com.wenbo.piao.domain.OrderParameter;
import com.wenbo.piao.enums.InfoCodeEnum;
import com.wenbo.piao.enums.ParameterEnum;
import com.wenbo.piao.enums.StatusCodeEnum;
import com.wenbo.piao.enums.UrlNewEnum;
import com.wenbo.piao.fragment.RobitOrderFragment;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.JsoupUtil;

public class RobitOrderService extends Service {
	
	private static ConfigInfo configInfo;
	
	private Map<String,UserInfo> userInfoMap;
	
	private boolean isBegin;
	
	private int status;
	
	private String orderCode;
	
	private static OrderParameter orderParameter;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.i("RobitOrderService","onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i("RobitOrderService","onDestroy");
		isBegin = false;
		JsoupUtil.isSearch = false;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("RobitOrderService","onStartCommand");
		userInfoMap = HttpClientUtil.getUserInfoMap();
		isBegin = true;
		status = intent.getExtras().getInt(ParameterEnum.ROBIT_STATE.getValue());
		configInfo = HttpClientUtil.getConfigInfo();
		orderCode = intent.getExtras().getString(ParameterEnum.RANGECODE.getValue());
		configInfo.setSearchWatiTime(10);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(isBegin){
					try {
						if(status == StatusCodeEnum.INPUT_ORDERCODE.getCode()){
							checkOrderInfo(orderParameter);
						}else{
							searchTicket(configInfo.getOrderDate());
						}
						Log.i("run",""+isBegin);
					} catch (Exception e) {
						Log.i("run:Exception",""+isBegin);
						isBegin = false;
						Log.i("run:Exception",""+isBegin);
						Log.e("RobitOrderService","onStartCommand",e);
					}
					Log.i("run:end",""+isBegin);
				}
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}
	
	/***
	 * 构造查询火车票对象
	 * 
	 * @throws URISyntaxException
	 **/
	public void searchTicket(String date) {
		String info = null;
		Date serverDate = null;
		try {
			String dateStr = HttpClientUtil.getServerHead("Date",0);
			while(dateStr == null){
				dateStr = HttpClientUtil.getServerHead("Date",0);
			}
			dateStr = StringUtils.replace(dateStr,",","");
			serverDate =  new SimpleDateFormat("EEE dd MMM yyyy hh:mm:ss z",Locale.ENGLISH).parse(dateStr);
			Map<String, String> paraMap = new LinkedHashMap<String, String>();
			paraMap.put("leftTicketDTO.train_date",date);
			paraMap.put("leftTicketDTO.from_station",configInfo.getFromStation());
			paraMap.put("leftTicketDTO.to_station",configInfo.getToStation());
			paraMap.put("purpose_codes","ADULT");
			int num = 0;
			while(isBegin){
				info = HttpClientUtil.doGet(UrlNewEnum.SEARCH_TICKET, paraMap,0);
				if(info == null){
					continue;
				}
				orderParameter = checkTickeAndOrder(info, date,serverDate);
				num++;
				if(orderParameter == null || StringUtils.isEmpty(orderParameter.getTicketType())){
					sendInfo("没有余票,继续刷票!第"+num+"次刷票!",InfoCodeEnum.INFO_TIPS);
				}else{
					break;
				}
				Thread.sleep(500);
			}
			if(orderParameter != null){
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(serverDate);
//				calendar.add(Calendar.DAY_OF_MONTH,1);
//				orderParameter.setBackDate(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH));
				submitOrderRequest(date,orderParameter);
			}
		} catch (Exception e) {
			Log.e("searchTicket","searchTicket error!", e);
			sendStatus(StatusCodeEnum.NET_ERROR);
			isBegin = false;
			return;
		} finally {
//			HttpClientUtils.closeQuietly(response);
		}
	}

	/**
	 * 检测出票结果以及刷票
	 * 
	 * @param inputStream
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public OrderParameter checkTickeAndOrder(String message, String date,Date serverDate) {
		OrderParameter orderParameter = null;
		try {
			JSONObject jsonObject = JSON.parseObject(message);
			if(jsonObject.containsKey("data")){
				JSONArray jsonArray = jsonObject.getJSONArray("data");
				JSONObject object = null;
				JSONObject trainObject = null;
				boolean isCheckTrainNo = false;
				String[] orderSeats = StringUtils.split(configInfo.getOrderSeat(),",");
				if(StringUtils.isNotEmpty(configInfo.getTrainNo())){
					isCheckTrainNo = true;
				}
				if(jsonArray.size() > 0 && StringUtils.contains(jsonArray.getJSONObject(0).getString("buttonTextInfo"),"系统维护时间")){
					sendInfo("23:00-07:00系统维护时间",InfoCodeEnum.INFO_NOTIFICATION);
					sendStatus(StatusCodeEnum.SYSTEM_MAINTENANCE);
					return orderParameter;
				}
				boolean isCheck = true;
				boolean isALl = false;
				if(configInfo.getTrainClass().length == 1
						&& "QB".equals(configInfo.getTrainClass()[0])){
					isALl = true;
				}
				boolean isAllTime = false;
				if(("00:00--24:00").equals(configInfo.getOrderTime())){
					isAllTime = true;
				}
				int isSeat = 0;
				int isNoSeat = 0;
				for(int i = 0; i < jsonArray.size(); i++){
					if(!isCheck){
						break;
					}
					object = jsonArray.getJSONObject(i);
					trainObject = object.getJSONObject("queryLeftNewDTO");
					if(isCheckTrainNo){
						if(!trainObject.getString("train_no").equals(configInfo.getTrainNo())){
							continue;
						}
					}
					if(!isALl){
						boolean isHave = false;
						for(String type:configInfo.getTrainClass()){
							if(StringUtils.contains(trainObject.getString("train_no"),type)){
								isHave = true;
							}
						}
						if(!isHave){
							continue;
						}
					}
					if(!isAllTime){
						String [] times = StringUtils.split(configInfo.getOrderTime(),"--");
						int beginTime = Integer.parseInt(StringUtils.split(times[0],":")[0]);
						int endTime = Integer.parseInt(StringUtils.split(times[1],":")[0]);
						int trainStartTime = Integer.parseInt(StringUtils.split(trainObject.getString("start_time"),":")[0]);
						if(trainStartTime < beginTime
								|| trainStartTime > endTime){
							continue;
						}
					}
					for(String seat:orderSeats){
						String seatState = trainObject.getString(seat+"_num");
						if("--".equals(seatState)){
							isSeat++;
						}else{
							isCheck = false;
							String info = "正在预定"+trainObject.getString("station_train_code")+RobitOrderFragment.seatMaps.get(seat)+"票";
//							if(StringUtils.isNumeric(seatState)){
//								info = info+"有"+RobitOrderFragment.seatMaps.get(seat)+"票"+seatState+"张!";
//								String[] peoples = StringUtils.split(configInfo.getOrderPerson(),",");
//								if(Integer.parseInt(seatState) < peoples.length){
//									sendInfo(info,InfoCodeEnum.INFO_NOTIFICATION);
//									Thread.sleep(200);
//									continue;
//								}
//							}else{
//								info = info+"有大量的"+RobitOrderFragment.seatMaps.get(seat)+"票";
//							}
							sendInfo(info,InfoCodeEnum.INFO_NOTIFICATION);
							orderParameter = new OrderParameter();
							orderParameter.setTicketType(seat);
							orderParameter.setSecretStr(object.getString("secretStr"));
							orderParameter.setTrainObject(trainObject);
							orderParameter.setSearchDate(date);
							break;
						}
					}
				}
				if(isSeat == orderSeats.length
						&& isNoSeat == 0){
					isBegin = false;
					sendStatus(StatusCodeEnum.NO_TRAIN_SEAT);
				}
			}else{
				sendInfo(jsonObject.getString("messages"),InfoCodeEnum.INFO_TIPS);
				sendStatus(StatusCodeEnum.NO_ORDER);
			}
		} catch (Exception e) {
			Log.e("checkTickeAndOrder","checkTickeAndOrder error!", e);
			sendStatus(StatusCodeEnum.NET_ERROR);
		} finally {

		}
		return orderParameter;
	}

	/**
	 * 点击预定
	 * 
	 * @param date
	 * @param params
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public void submitOrderRequest(String date,OrderParameter orderParameter)
			throws IllegalStateException, IOException {
		try {
			Map<String,String> paraMap = new LinkedHashMap<String, String>();
			paraMap.put("secretStr",URLDecoder.decode(orderParameter.getSecretStr(),"utf-8"));
			paraMap.put("train_date",date);
			paraMap.put("back_train_date",orderParameter.getBackDate());
			paraMap.put("tour_flag","dc");
			paraMap.put("purpose_codes","ADULT");
			paraMap.put("query_from_station_name",configInfo.getFromStationName());
			paraMap.put("query_to_station_name",configInfo.getToStationName());
			paraMap.put("undefined","");
			String info = HttpClientUtil.doPost(UrlNewEnum.SUBMITORDERREQUEST, paraMap,0);
			JSONObject object = checkSubmitOrder(info);
			while(object == null){
				info = HttpClientUtil.doPost(UrlNewEnum.SUBMITORDERREQUEST, paraMap,0);
				object = checkSubmitOrder(info);
			}
			if(!isBegin){
				return;
			}
			if(object.containsKey("status") && object.getBooleanValue("status")){
				String str = HttpClientUtil.doGet(UrlNewEnum.INITDC,new HashMap<String, String>(),0);
				int n = StringUtils.indexOf(str,"init_seatTypes");
				int m = StringUtils.indexOf(str,";",n);
				String ticket = StringUtils.substring(str,n+15,n+(m-n));
				JSONArray seatObjectArray = JSON.parseArray(ticket);
				if(seatObjectArray.size() > 0){
					Map<String, String> seatMap = new HashMap<String, String>();
					for(int i = 0; i < seatObjectArray.size(); i++){
						JSONObject jsonObject = seatObjectArray.getJSONObject(i);
						seatMap.put(URLDecoder.decode(jsonObject.getString("value"),"utf-8"),jsonObject.getString("id"));
					}
					String seatName = RobitOrderFragment.seatMaps.get(orderParameter.getTicketType());
					if(!seatMap.containsKey(seatName)){
						submitOrderRequest(date, orderParameter);
					}
				}
				n = StringUtils.indexOf(str,"globalRepeatSubmitToken");
				info = StringUtils.substring(str,n+27,n+59);
				orderParameter.setToken(info);
				n = StringUtils.indexOf(str,"key_check_isChange");
				String key_check_isChange = StringUtils.substring(str,n+21,n+77);
				orderParameter.setKeyCheck(key_check_isChange);
				sendStatus(StatusCodeEnum.INPUT_ORDERCODE);
			}else{
				sendInfo(object.getString("messages"),InfoCodeEnum.INFO_TIPS);
				isBegin = false;
			}
		} catch (Exception e) {
			Log.e("orderTicket","orderTicket error!", e);
			sendStatus(StatusCodeEnum.NET_ERROR);
		} finally {
			
		}
	}
	
	/**
	 * 检测是否可以点击预定
	 * @param info
	 * @return
	 */
	public JSONObject checkSubmitOrder(String info){
		if(StringUtils.isNotBlank(info)){
			Log.i("checkSubmitOrder",info);
			JSONObject object = JSON.parseObject(info);
			if(object.getBooleanValue("status")){
				return object;
			}
		}
		return null;
	}

	/**
	 * 检测票
	 * 
	 * @param ticketNo
	 * @param seatNum
	 * @param token
	 * @param params
	 * @param date
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public void checkOrderInfo(OrderParameter orderParameter) {
		try {
			sendInfo(orderParameter.getTrainObject().getString("station_train_code")+"有票,正在提交订单...",InfoCodeEnum.INFO_NOTIFICATION);
			String[] peoples = StringUtils.split(configInfo.getOrderPerson(),",");
			if(peoples == null || peoples.length ==0){
				sendStatus(StatusCodeEnum.NOT_HAVE_PERSON);
				return;
			}
			Map<String,String> paraMap = new LinkedHashMap<String, String>();
			paraMap.put("cancel_flag","2");
			paraMap.put("bed_level_order_num","000000000000000000000000000000");
			StringBuilder passengerBuilder = new StringBuilder();
			StringBuilder oldPassengerBuilder = new StringBuilder();
			String seatName = RobitOrderFragment.seatMaps.get(orderParameter.getTicketType());
			if("无座".equals(seatName)){
				seatName = "硬座";
			}
			String seatid = orderParameter.getSeatMap().get(seatName);
			for(int i = 0; i < peoples.length; i++){
				UserInfo userInfo = userInfoMap.get(peoples[i]);
				if(i == 0){
					passengerBuilder.append(seatid+",");
					oldPassengerBuilder.append(userInfo.getPassenger_name()+",");
				}else{
					passengerBuilder.append("N_"+seatid+",");
					oldPassengerBuilder.append("1_"+userInfo.getPassenger_name()+",");
				}
				oldPassengerBuilder.append(userInfo.getPassenger_flag()+",")
				.append(userInfo.getPassenger_id_no()+",");
				passengerBuilder.append(userInfo.getPassenger_flag()+",")
								.append(userInfo.getPassenger_id_type_code()+",")
								.append(userInfo.getPassenger_name()+",")
								.append(userInfo.getPassenger_type()+",")
								.append(userInfo.getPassenger_id_no()+","+userInfo.getMobile_no()+",");
			}
			passengerBuilder.append("N");
			oldPassengerBuilder.append("1_");
//			paraMap.put("passengerTicketStr","1,0,1,刘文波,1,430981198702272830,18606521059,N");
			orderParameter.setPassenger(passengerBuilder.toString());
			orderParameter.setOldPassengerStr(oldPassengerBuilder.toString());
			paraMap.put("passengerTicketStr",orderParameter.getPassenger());
			paraMap.put("oldPassengerStr",orderParameter.getOldPassengerStr());
//			paraMap.put("oldPassengerStr","刘文波,1,430981198702272830,1_");
			paraMap.put("tour_flag","dc");
			paraMap.put("randCode",orderCode);
			paraMap.put("_json_att","");
			paraMap.put("REPEAT_SUBMIT_TOKEN",orderParameter.getToken());
			String info = HttpClientUtil.doPost(UrlNewEnum.CHECKORDERINFO, paraMap,0);
			while(checkGoing(info,orderParameter) && isBegin){
				info = HttpClientUtil.doPost(UrlNewEnum.CHECKORDERINFO, paraMap,0);
			}
			if(!isBegin){
				return;
			}
			JSONObject jsonObject = JSON.parseObject(info);
			if(jsonObject.getBooleanValue("status")
					&& jsonObject.getJSONObject("data").getBooleanValue("submitStatus")){
				getQueueCount(orderParameter);
			}else{
				if(jsonObject.containsKey("data")){
					String message = jsonObject.getJSONObject("data").getString("errMsg");
					sendInfo(message,InfoCodeEnum.INFO_TIPS);
					if(StringUtils.contains(message,"验证码输入错误")){
						sendStatus(StatusCodeEnum.INPUT_ORDERCODE);
					}else if(StringUtils.contains(message,"非法的席别")){
						sendStatus(StatusCodeEnum.ORDER_SEAT_ERROR);
					}else{
						sendStatus(StatusCodeEnum.CANCEL_ORDER_MANY);
					}
				}else{
					sendInfo(jsonObject.getString("messages"),InfoCodeEnum.INFO_TIPS);
					sendStatus(StatusCodeEnum.SYSTEM_ERROR);
				}
			}
		} catch (Exception e) {
			Log.e("checkOrderInfo","checkOrderInfo error!", e);
			sendStatus(StatusCodeEnum.NET_ERROR);
		} finally {

		}
	}
	
	public boolean checkGoing(String info,OrderParameter orderParameter) throws InterruptedException{
		if(StringUtils.isBlank(info)){
			return true;
		}
		Log.i("checkGoing",info);
		JSONObject jsonObject = JSONObject.parseObject(info);
		String message = jsonObject.getJSONObject("data").getString("errMsg");
		if(StringUtils.contains(message,"非法的席别")){
			String str = "正在等待"+orderParameter.getTrainObject().getString("station_train_code")+"出票！";
			Thread.sleep(200);
			sendInfo(str,InfoCodeEnum.INFO_NOTIFICATION);
			return true;
		}
		if(jsonObject.getBooleanValue("status")
				&& jsonObject.getJSONObject("data").getBooleanValue("submitStatus")){
			return false;
		}
		return true;
	}

	/**
	 * 检测有没有票
	 * 
	 * @param ticketNo
	 * @param params
	 */
	public void getQueueCount(OrderParameter orderParameter) {
		try {
			Map<String,String> paraMap = new LinkedHashMap<String, String>();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			simpleDateFormat.setTimeZone(TimeZone.getDefault());
			paraMap.put("train_date",simpleDateFormat.parse(orderParameter.getSearchDate()).toGMTString());
			paraMap.put("train_no",orderParameter.getTrainObject().getString("train_no"));
			paraMap.put("stationTrainCode",orderParameter.getTrainObject().getString("station_train_code"));
			paraMap.put("seatType","1");
			paraMap.put("fromStationTelecode",orderParameter.getTrainObject().getString("from_station_telecode"));
			paraMap.put("toStationTelecode",orderParameter.getTrainObject().getString("to_station_telecode"));
			paraMap.put("leftTicket",orderParameter.getTrainObject().getString("yp_info"));
			paraMap.put("purpose_codes","00");
			paraMap.put("_json_att","");
			paraMap.put("REPEAT_SUBMIT_TOKEN",orderParameter.getToken());
			String info = HttpClientUtil.doPost(UrlNewEnum.GETQUEUECOUNT, paraMap,0);
			while(info == null && isBegin){
				info = HttpClientUtil.doPost(UrlNewEnum.GETQUEUECOUNT, paraMap,0);
			}
			JSONObject jsonObject = JSON.parseObject(info);
			if(jsonObject.getBooleanValue("status")){
				confirmSingleForQueue(orderParameter,jsonObject.getJSONObject("data").getString("ticket"));
			}else{
				if(jsonObject.containsKey("data")){
					String message = jsonObject.getJSONObject("data").getString("errMsg");
					sendInfo(message,InfoCodeEnum.INFO_TIPS);
					if(StringUtils.contains(message,"验证码输入错误")){
						sendStatus(StatusCodeEnum.INPUT_ORDERCODE);
					}
				}else{
					sendInfo(jsonObject.getString("messages"),InfoCodeEnum.INFO_TIPS);
					sendStatus(StatusCodeEnum.SYSTEM_ERROR);
				}
				Log.i("getQueueCount", info);
			}
		} catch (Exception e) {
			Log.e("checkTicket","checkTicket error!", e);
			sendStatus(StatusCodeEnum.NET_ERROR);
		} finally {
//			HttpClientUtils.closeQuietly(response);
		}
	}

	public void confirmSingleForQueue(OrderParameter orderParameter,String ticket) {
		try {
			Map<String,String> paraMap = new LinkedHashMap<String, String>();
			paraMap.put("passengerTicketStr",orderParameter.getPassenger());
			paraMap.put("oldPassengerStr",orderParameter.getOldPassengerStr());
			paraMap.put("randCode",orderCode);
			paraMap.put("purpose_codes","00");
			paraMap.put("key_check_isChange",orderParameter.getKeyCheck());
			paraMap.put("leftTicketStr",ticket);
			paraMap.put("train_location",orderParameter.getTrainObject().getString("location_code"));
			paraMap.put("_json_att","");
			String info = HttpClientUtil.doPost(UrlNewEnum.CONFIRMSINGLEFORQUEUE, paraMap,0);
			while(info == null && isBegin){
				info = HttpClientUtil.doPost(UrlNewEnum.CONFIRMSINGLEFORQUEUE, paraMap,0);
			}
			Log.i("confirmSingleForQueue info:",info);
			JSONObject jsonObject = JSON.parseObject(info);
			if(jsonObject.getBooleanValue("status")
					&& jsonObject.getJSONObject("data").getBooleanValue("submitStatus")){
				orderParameter = null;
				sendStatus(StatusCodeEnum.ORDER_SUCCESS);
			}else{
				sendStatus(StatusCodeEnum.INPUT_ORDERCODE);
				Log.i("confirmSingleForQueue",jsonObject.getString("messages"));
			}
		} catch (Exception e) {
			Log.e("orderTicketToQueue","orderTicketToQueue error!", e);
			sendStatus(StatusCodeEnum.NET_ERROR);
		} finally {
			
		}
	}
	
	private void sendStatus(StatusCodeEnum statusCodeEnum){
		Log.i("sendStatus",""+statusCodeEnum.getCode());
		Thread.interrupted();
		status = statusCodeEnum.getCode();
		isBegin = false;
		Intent intent = new Intent("com.wenbo.piao.robitService");
		intent.putExtra("status",status);
		sendBroadcast(intent);
	}
	
	/**
	 * 发送订票提示消息
	 * @param info
	 */
	public void sendInfo(String info,InfoCodeEnum infoCodeEnum){
		Log.i("sendInfo：",""+info);
		Intent intent = new Intent("com.wenbo.piao.robitService");
		intent.putExtra("status",infoCodeEnum.getCode());
		intent.putExtra("tips",info);
		sendBroadcast(intent);
	}

}
