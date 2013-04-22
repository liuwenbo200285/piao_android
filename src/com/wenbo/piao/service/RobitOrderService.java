package com.wenbo.piao.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wenbo.piao.domain.ConfigInfo;
import com.wenbo.piao.domain.OrderParameter;
import com.wenbo.piao.enums.StatusCodeEnum;
import com.wenbo.piao.enums.ParameterEnum;
import com.wenbo.piao.enums.TrainSeatEnum;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.JsoupUtil;

public class RobitOrderService extends Service {
	
	private HttpClient httpClient;
	
	private ConfigInfo configInfo;
	
	private Map<String,UserInfo> userInfoMap;
	
	private UserInfo userInfo;
	
	private String ticketNo;
	private String seatNum;
	private String token;
	private String[] params;
	
	private boolean isBegin;
	
	private int status;

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
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("RobitOrderService","onStartCommand");
		httpClient = HttpClientUtil.getHttpClient();
		Bundle bundle = intent.getExtras();
		userInfoMap = HttpClientUtil.getUserInfoMap();
		isBegin = true;
		if(configInfo == null && params == null){
			configInfo = new ConfigInfo();
//			configInfo.setFromStation(bundle.getString(ParameterEnum.FROMSTATION.getValue()));
//			configInfo.setOrderDate(bundle.getString(ParameterEnum.ORDERDATE.getValue()));
//			configInfo.setOrderPerson(bundle.getString(ParameterEnum.ORDERPERSON.getValue()));
//			configInfo.setOrderSeat(bundle.getString(ParameterEnum.ORDERSEAT.getValue()));
//			configInfo.setOrderTime(bundle.getString(ParameterEnum.ORDERTIME.getValue()));
//			configInfo.setRangeCode(bundle.getString(ParameterEnum.RANGECODE.getValue()));
//			configInfo.setToStation(bundle.getString(ParameterEnum.TOSTATION.getValue()));
//			configInfo.setTrainNo(bundle.getString(ParameterEnum.TRAINNO.getValue()));
			configInfo.setSearchSleepTime(10);
			configInfo.setSearchWatiTime(10);
			configInfo.setFromStation("CSQ");
			configInfo.setToStation("SZQ");
			configInfo.setOrderDate("2013-04-25");
			configInfo.setTrainClass("D#");
			configInfo.setOrderPerson("阳茜,");
			configInfo.setOrderSeat("4,");
			configInfo.setOrderTime("12:00--18:00");
			configInfo.setTrainNo("");
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(isBegin){
						try {
							searchTicket(configInfo.getOrderDate());
						} catch (Exception e) {
							e.printStackTrace();
							isBegin = false;
						}
					}
				}
			}).start();
		}else{
			configInfo.setRangeCode(bundle.getString(ParameterEnum.RANGECODE.getValue()));
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(isBegin){
						try {
							checkOrderInfo(ticketNo, seatNum, token, params,configInfo.getOrderDate());
						} catch (Exception e) {
							e.printStackTrace();
							isBegin = false;
						}
					}
				}
			}).start();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	/***
	 * 构造查询火车票对象
	 * 
	 * @throws URISyntaxException
	 **/
	public void searchTicket(String date) {
		HttpResponse response = null;
		String info = null;
		OrderParameter orderParameter = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method","queryLeftTicket"));
			parameters.add(new BasicNameValuePair("orderRequest.train_date",date));
			parameters.add(new BasicNameValuePair("orderRequest.from_station_telecode",configInfo.getFromStation()));
			parameters.add(new BasicNameValuePair("orderRequest.to_station_telecode",configInfo.getToStation()));
			if (StringUtils.isNotEmpty(configInfo.getTrainNo())) {
				parameters.add(new BasicNameValuePair("orderRequest.train_no",configInfo.getTrainNo()));
			} else {
				parameters.add(new BasicNameValuePair("orderRequest.train_no",""));
			}
			parameters.add(new BasicNameValuePair("trainPassType","QB"));
			parameters.add(new BasicNameValuePair("trainClass",configInfo.getTrainClass()));
			parameters.add(new BasicNameValuePair("includeStudent","00"));
			parameters.add(new BasicNameValuePair("seatTypeAndNum",""));
			parameters.add(new BasicNameValuePair("orderRequest.start_time_str",configInfo.getOrderTime()));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(parameters);
			HttpGet httpGet = HttpClientUtil.getHttpGet(UrlEnum.SEARCH_TICKET);
			httpGet.setURI(new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.SEARCH_TICKET.getPath()+"?"+EntityUtils.toString(urlEncodedFormEntity)));
			response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 200){
				info = EntityUtils.toString(response.getEntity());
			}
			while(StringUtils.isBlank(info) || (orderParameter=checkTickeAndOrder(info, date)) == null){
				Log.i("searchTicket","没有余票,休息2秒，继续刷票");
				Thread.sleep(2*1000);
				response = httpClient.execute(httpGet);
				info = EntityUtils.toString(response.getEntity());
				if(StringUtils.contains(info, "系统维护中")){
					sendStatus(StatusCodeEnum.SYSTEM_MAINTENANCE.getCode());
					return;
				}
				if("-10".equals(info)|| StringUtils.isBlank(info)){
					Log.i("searchTicket","刷新太过频繁，休息"+configInfo.getSearchWatiTime()+"秒");
					Thread.sleep(configInfo.getSearchWatiTime()*1000);
				}
			}
			Log.i("searchTicket","有票了，开始订票~~~~~~~~~");
			params = JsoupUtil.getTicketInfo(orderParameter.getDocument());
			Log.i("searchTicket","ticketType:" + orderParameter.getTicketType());
			orderTicket(date, params, orderParameter.getTicketType());
		} catch (Exception e) {
			Log.e("searchTicket","searchTicket error!", e);
			isBegin = false;
			return;
		} finally {
			Log.i("searchTicket","close searchTicket");
//			HttpClientUtils.closeQuietly(response);
		}
//		if (StringUtils.isNotEmpty(info)) {
//			checkTickeAndOrder(info, date);
//		} else {
//			try {
//				Thread.sleep(10000);
//			} catch (Exception e) {
//				Log.e("searchTicket","searchTicket Thread error!", e);
//			}
//			searchTicket(date);
//		}
	}

	/**
	 * 检测出票结果以及刷票
	 * 
	 * @param inputStream
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public OrderParameter checkTickeAndOrder(String message, String date) {
		Document document = null;
		OrderParameter orderParameter = null;
		try {
			message = StringUtils.remove(message, "&nbsp;");
			if (StringUtils.isEmpty(message)) {
				Log.w("checkTickeAndOrder","车次配置错误，没有查询到车次！");
				sendStatus(StatusCodeEnum.TRAIN_NO_ERROR.getCode());
				return null;
			}
			int m = 1;
			int n = 0;
			int lastIndex = 0;
			boolean isLast = false;
			String trainInfo = null;
			int ticketType = 0;
			while ((n = StringUtils.indexOf(message, m + ",<span")) != -1
					|| !isLast) {
				if (n == -1) {
					trainInfo = StringUtils.substring(message, lastIndex,
							message.length());
					isLast = true;
				} else {
					trainInfo = StringUtils.substring(message, lastIndex, n);
				}
				document = Jsoup.parse(trainInfo);
				ticketType = JsoupUtil.checkHaveTicket(document,
						configInfo.getOrderSeat());
				if (ticketType > 0) {
					break;
				}
				document = null;
				m++;
				lastIndex = n;
			}
			if (document != null) {
				orderParameter = new OrderParameter();
				orderParameter.setDocument(document);
				orderParameter.setTicketType(ticketType);
			}
		} catch (Exception e) {
			Log.e("checkTickeAndOrder","checkTickeAndOrder error!", e);
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
	public void orderTicket(String date, String[] params, int ticketType)
			throws IllegalStateException, IOException {
		HttpPost httpPost = null;
		OutputStream outputStream = null;
		HttpResponse response = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method",
					"submutOrderRequest"));
			parameters.add(new BasicNameValuePair("arrive_time", params[6]));
			parameters.add(new BasicNameValuePair("from_station_name",
					params[7]));
			parameters
					.add(new BasicNameValuePair("from_station_no", params[9]));
			parameters.add(new BasicNameValuePair("from_station_telecode",
					params[4]));
			parameters.add(new BasicNameValuePair("from_station_telecode_name",
					params[7]));
			parameters.add(new BasicNameValuePair("include_student", "00"));
			parameters.add(new BasicNameValuePair("lishi", params[1]));
			parameters.add(new BasicNameValuePair("locationCode", params[13]));
			parameters.add(new BasicNameValuePair("mmStr", params[12]));
			parameters.add(new BasicNameValuePair("round_start_time_str",configInfo.getOrderTime()));
			parameters.add(new BasicNameValuePair("round_train_date", date));
			parameters.add(new BasicNameValuePair("seattype_num", ""));
			parameters.add(new BasicNameValuePair("single_round_type", "1"));
			parameters.add(new BasicNameValuePair("start_time_str",configInfo.getOrderTime()));
			parameters.add(new BasicNameValuePair("station_train_code",
					params[0]));
			parameters
					.add(new BasicNameValuePair("to_station_name", params[8]));
			parameters.add(new BasicNameValuePair("to_station_no", params[10]));
			parameters.add(new BasicNameValuePair("to_station_telecode",
					params[5]));
			parameters.add(new BasicNameValuePair("to_station_telecode_name",
					params[8]));
			parameters.add(new BasicNameValuePair("train_class_arr", configInfo
					.getTrainClass()));
			parameters.add(new BasicNameValuePair("train_date", date));
			parameters.add(new BasicNameValuePair("train_pass_type", "QB"));
			parameters
					.add(new BasicNameValuePair("train_start_time", params[2]));
			parameters.add(new BasicNameValuePair("trainno4", params[3]));
			parameters.add(new BasicNameValuePair("ypInfoDetail", params[11]));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			httpPost = HttpClientUtil.getHttpPost(UrlEnum.BOOK_TICKET);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 302) {
				Header locationHeader = response.getFirstHeader("Location");
				Log.i("orderTicket",locationHeader.getValue());
			} else if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = response.getEntity();
				Document document = JsoupUtil.getPageDocument(httpEntity
						.getContent());
				Element element = document.getElementById("left_ticket");
				if (element != null) {
					ticketNo = element.attr("value");
					Log.i("orderTicket",ticketNo);
				} else {
					Log.i("orderTicket","~~~~~~~~~~~~~~~~~~~~~可能还有未处理的订单或者系统维护等其它异常~~~~~~~~~~~~~~~~!");
					sendStatus(StatusCodeEnum.HAVA_NO_DETAIL_ORDER.getCode());
					return;
				}
				element = document.getElementById("passenger_1_seat");
				if (element != null) {
					TrainSeatEnum trainSeatEnum = HttpClientUtil
							.getSeatEnum(ticketType);
					if (trainSeatEnum == null) {
						Log.w("orderTicket","预订坐席填写不正确，请重新填写!");
						sendStatus(StatusCodeEnum.ORDER_SEAT_ERROR.getCode());
						return;
					}
					Elements elements = element
							.getElementsContainingOwnText(trainSeatEnum
									.getName());
					seatNum = elements.get(0).attr("value");
					Log.i("orderTicket",seatNum);
				}
				Elements elements = document.getElementsByAttributeValue(
						"name", "org.apache.struts.taglib.html.TOKEN");
				if (elements != null) {
					token = elements.get(0).attr("value");
				}
				sendStatus(StatusCodeEnum.INPUT_ORDERCODE.getCode());
			} else {
				Log.w("orderTicket",EntityUtils.toString(response.getEntity()));
			}
		} catch (Exception e) {
			Log.e("orderTicket","orderTicket error!", e);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
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
	public void checkOrderInfo(String ticketNo, String seatNum, String token,
			String[] params, String date) {
		HttpResponse response = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method", "checkOrderInfo"));
			parameters.add(new BasicNameValuePair(
					"org.apache.struts.taglib.html.TOKEN", token));
			parameters.add(new BasicNameValuePair("leftTicketStr", ticketNo));
			parameters.add(new BasicNameValuePair("textfield", "中文或拼音首字母"));
			// 一个人只有一个checkbox0
			parameters.add(new BasicNameValuePair("orderRequest.train_date",
					date));
			parameters.add(new BasicNameValuePair("orderRequest.train_no",
					params[3]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.station_train_code", params[0]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.from_station_telecode", params[4]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.to_station_telecode", params[5]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.seat_type_code", ""));
			parameters.add(new BasicNameValuePair(
					"orderRequest.ticket_type_order_num", ""));

			parameters.add(new BasicNameValuePair(
					"orderRequest.bed_level_order_num",
					"000000000000000000000000000000"));
			parameters.add(new BasicNameValuePair("orderRequest.start_time",
					params[2]));
			parameters.add(new BasicNameValuePair("orderRequest.end_time",
					params[6]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.from_station_name", params[7]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.to_station_name", params[8]));
			parameters.add(new BasicNameValuePair("orderRequest.cancel_flag",
					"1"));
			parameters.add(new BasicNameValuePair("orderRequest.id_mode", "Y"));

			// 处理订票信息
			if (!StringUtils.contains(configInfo.getOrderPerson(), ",")) {
				Log.w("checkOrderInfo","订票人格式填写不正确！");
				sendStatus(StatusCodeEnum.ORDER_PERSON_ERROR.getCode());
				return;
			}
			String[] orders = StringUtils.split(configInfo.getOrderPerson(),
					",");
			if (orders.length == 0) {
				Log.w("checkOrderInfo","订票人格式填写不正确！");
				sendStatus(StatusCodeEnum.ORDER_PERSON_ERROR.getCode());
				return;
			}
			if (orders.length > 5) {
				Log.w("checkOrderInfo","一个账号最多只能预定5张火车票！");
				sendStatus(StatusCodeEnum.ORDER_NUM_ERROR.getCode());
				return;
			}
			int n = 1;
			for (int i = 0; i < orders.length; i++) {
				userInfo = userInfoMap.get(orders[i]);
				if (userInfo == null) {
					Log.w("checkOrderInfo","this name is not have!name:" + orders[i]);
					continue;
				}
				parameters.add(new BasicNameValuePair("checkbox"
						+ userInfo.getIndex(), "" + userInfo.getIndex()));
				parameters
						.add(new BasicNameValuePair("passengerTickets", seatNum
								+ ",0,1," + userInfo.getPassenger_name()
								+ ",1," + userInfo.getPassenger_id_no() + ",,Y"));
				parameters.add(new BasicNameValuePair("oldPassengers", userInfo
						.getPassenger_name()
						+ ",1,"
						+ userInfo.getPassenger_id_no() + ""));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_seat", seatNum));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_ticket", "1"));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_name", userInfo.getPassenger_name()));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_cardtype", "1"));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_cardno", userInfo.getPassenger_id_no()));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_mobileno", ""));
				parameters.add(new BasicNameValuePair("checkbox9", "Y"));
			}
			parameters.add(new BasicNameValuePair("orderRequest.reserve_flag",
					"A"));
			parameters.add(new BasicNameValuePair("tFlag", "dc"));
			parameters.add(new BasicNameValuePair("rand", configInfo.getRangeCode()));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			HttpPost httpPost = HttpClientUtil.getHttpPost(UrlEnum.GET_ORDER_INFO);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject jsonObject = JSON.parseObject(EntityUtils
						.toString(response.getEntity()));
				if (jsonObject != null
						&& "Y".equals(jsonObject.getString("errMsg"))) {
					String msg = jsonObject.getString("msg");
					if (StringUtils.isNotEmpty(msg)) {
						Log.i("checkOrderInfo",msg);
					} else {
						checkTicket(ticketNo, seatNum, token, params, date,configInfo.getRangeCode());
					}
				} else {
					String errorMessage = jsonObject.getString("errMsg");
					Log.i("checkOrderInfo",errorMessage);
					if(StringUtils.contains(errorMessage,"验证码不正确")){
						sendStatus(StatusCodeEnum.ORDER_CODE_ERROR.getCode());
						return;
					}
					checkOrderInfo(ticketNo, seatNum, token, params, date);
				}
			}
		} catch (Exception e) {
			Log.e("checkOrderInfo","checkOrderInfo error!", e);
		} finally {
//			HttpClientUtils.closeQuietly(response);
		}
	}

	/**
	 * 检测有没有票
	 * 
	 * @param ticketNo
	 * @param params
	 */
	public void checkTicket(String ticketNo, String seatNum, String token,
			String[] params, String date, String rangCode) {
		HttpResponse response = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method","getQueueCount"));
			parameters.add(new BasicNameValuePair("train_date",date));
			parameters.add(new BasicNameValuePair("train_no",params[3]));
			parameters.add(new BasicNameValuePair("station",params[0]));
			parameters.add(new BasicNameValuePair("seat",seatNum));
			parameters.add(new BasicNameValuePair("from",params[4]));
			parameters.add(new BasicNameValuePair("to",params[5]));
			parameters.add(new BasicNameValuePair("ticket",ticketNo));
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(parameters);
			HttpGet httpGet = HttpClientUtil.getHttpGet(UrlEnum.SEARCH_TICKET_INFO);
			httpGet.setURI(new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.SEARCH_TICKET_INFO.getPath()+"?"+EntityUtils.toString(urlEncodedFormEntity)));
			response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				Log.i("checkTicket",EntityUtils.toString(response.getEntity()));
				Thread.sleep(1000);
				orderTicketToQueue(ticketNo, seatNum, token, params, date,
						rangCode);
			}
		} catch (Exception e) {
			Log.e("checkTicket","checkTicket error!", e);
		} finally {
//			HttpClientUtils.closeQuietly(response);
		}
	}

	public void orderTicketToQueue(String ticketNo, String seatNum,
			String token, String[] params, String date, String rangCode) {
		HttpResponse response = null;
		HttpPost httpPost = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method",
					"confirmSingleForQueue"));
			parameters.add(new BasicNameValuePair(
					"org.apache.struts.taglib.html.TOKEN", token));
			parameters.add(new BasicNameValuePair("leftTicketStr", ticketNo));
			parameters.add(new BasicNameValuePair("textfield", "中文或拼音首字母"));
			// 一个人只有一个checkbox0
			parameters.add(new BasicNameValuePair("orderRequest.train_date",
					date));
			parameters.add(new BasicNameValuePair("orderRequest.train_no",
					params[3]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.station_train_code", params[0]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.from_station_telecode", params[4]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.to_station_telecode", params[5]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.seat_type_code", ""));
			parameters.add(new BasicNameValuePair(
					"orderRequest.ticket_type_order_num", ""));

			parameters.add(new BasicNameValuePair(
					"orderRequest.bed_level_order_num",
					"000000000000000000000000000000"));
			parameters.add(new BasicNameValuePair("orderRequest.start_time",
					params[2]));
			parameters.add(new BasicNameValuePair("orderRequest.end_time",
					params[6]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.from_station_name", params[7]));
			parameters.add(new BasicNameValuePair(
					"orderRequest.to_station_name", params[8]));
			parameters.add(new BasicNameValuePair("orderRequest.cancel_flag",
					"1"));
			parameters.add(new BasicNameValuePair("orderRequest.id_mode", "Y"));

			// 订票人信息 第一个人
			String[] orders = StringUtils.split(configInfo.getOrderPerson(),
					",");
			int n = 1;
			for (int i = 0; i < orders.length; i++) {
				userInfo = userInfoMap.get(orders[i]);
				if (userInfo == null) {
					Log.w("orderTicketToQueue","this name is not have!name:" + orders[i]);
					continue;
				}
				parameters.add(new BasicNameValuePair("checkbox"
						+ userInfo.getIndex(), "" + userInfo.getIndex()));
				parameters
						.add(new BasicNameValuePair("passengerTickets", seatNum
								+ ",0,1," + userInfo.getPassenger_name()
								+ ",1," + userInfo.getPassenger_id_no() + ",,Y"));
				parameters.add(new BasicNameValuePair("oldPassengers", userInfo
						.getPassenger_name()
						+ ",1,"
						+ userInfo.getPassenger_id_no() + ""));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_seat", seatNum));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_ticket", "1"));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_name", userInfo.getPassenger_name()));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_cardtype", "1"));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_cardno", userInfo.getPassenger_id_no()));
				parameters.add(new BasicNameValuePair("passenger_" + n
						+ "_mobileno", ""));
				parameters.add(new BasicNameValuePair("checkbox9", "Y"));
			}
			parameters.add(new BasicNameValuePair("orderRequest.reserve_flag",
					"A"));
			parameters.add(new BasicNameValuePair("randCode", rangCode));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			httpPost = new HttpPost(UrlEnum.DO_MAIN.getPath()+UrlEnum.SEARCH_TICKET_INFO.getPath());
			httpPost.setEntity(uef);
			httpPost.addHeader("Accept",
					"application/json, text/javascript, */*");
			httpPost.addHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
			httpPost.addHeader("Connection", "keep-alive");
			httpPost.addHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httpPost.addHeader("Origin", "https://dynamic.12306.cn");
			httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
			httpPost.addHeader("Host", "dynamic.12306.cn");
			httpPost.addHeader("Referer",
					"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
			httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
			httpPost.addHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17");
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				JSONObject jsonObject = JSONObject.parseObject(EntityUtils
						.toString(entity));
//				HttpClientUtils.closeQuietly(response);
				Log.i("orderTicketToQueue",jsonObject.toJSONString());
				String errorMessage = jsonObject.getString("errMsg");
				if ("Y".equals(errorMessage)
						|| StringUtils.isEmpty(errorMessage)) {
					Log.i("orderTicketToQueue","订票成功了，赶紧付款吧!");
					sendStatus(StatusCodeEnum.ORDER_SUCCESS.getCode());
				} else if (StringUtils.contains(errorMessage, "验证码")) {
					sendStatus(StatusCodeEnum.ORDER_CODE_ERROR.getCode());
				} else if (StringUtils.contains(errorMessage, "排队人数现已超过余票数")) {
					sendStatus(StatusCodeEnum.TICKET_IS_NOT_ENOUGH.getCode());
				} else if (StringUtils.contains(errorMessage, "非法的订票请求")) {
					sendStatus(StatusCodeEnum.NO_ALLOW_ORDER.getCode());
				} else {
					Log.i("orderTicketToQueue",errorMessage);
					searchTicket(date);
				}
			}
		} catch (Exception e) {
			Log.e("orderTicketToQueue","orderTicketToQueue error!", e);
			httpPost.abort();
		} finally {
//			HttpClientUtils.closeQuietly(response);
		}
	}
	
	private void sendStatus(int code){
		status = code;
		isBegin = false;
		Intent intent = new Intent("com.wenbo.piao.robitService");
		intent.putExtra("status",status);
		sendBroadcast(intent);
	}

}
