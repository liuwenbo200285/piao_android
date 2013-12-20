package com.wenbo.piao.util;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.wenbo.piao.domain.PayInfo;
import com.wenbo.piao.enums.UrlEnum;

public class OperationUtil {
	
	private static HttpClient httpClient = HttpClientUtil.getHttpClient();
	
	public static final String OPERATION_SUCCESS = "success";
	
	public static final String OPERATION_FAILURE = "failure";
	
	/**
	 * 取消订单
	 * @param orderNo
	 * @param token
	 * @return
	 */
	public static String canelOrder(String orderNo,String token){
		HttpResponse response = null;
		HttpPost httpPost = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method","cancelMyOrderNotComplete"));
			parameters.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN",token));
			parameters.add(new BasicNameValuePair("sequence_no",orderNo));
			parameters.add(new BasicNameValuePair("orderRequest.tour_flag",""));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			httpPost = HttpClientUtil.getHttpPost(UrlEnum.CANCEL_ORDER);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				String info = EntityUtils.toString(response.getEntity());
				if(StringUtils.contains(info,"取消订单成功")){
					return OPERATION_SUCCESS;
				}
			}
		}catch(SocketTimeoutException socketTimeoutException){
			Log.e("OperationUtil","canelOrder",socketTimeoutException);
			return canelOrder(orderNo, token);
		} catch (Exception e) {
			Log.e("OperationUtil","canelOrder",e);
			return OPERATION_FAILURE;
		} finally {
//			if(httpPost != null){
//				httpPost.abort();
//			}
		}
		return OPERATION_FAILURE;
	}
	
	/**
	 * 查询剩余付款时间
	 * @param inputStream
	 * @return
	 */
    public static String getLastTime(String orderNo,String token,String ticketNo){
    	HttpResponse response = null;
    	HttpPost httpPost = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method","laterEpay"));
			parameters.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN",token));
			parameters.add(new BasicNameValuePair("orderSequence_no",orderNo));
			parameters.add(new BasicNameValuePair("con_pay_type","epay"));
			parameters.add(new BasicNameValuePair("queryOrderDTO.from_order_date",""));
			parameters.add(new BasicNameValuePair("queryOrderDTO.to_order_date",""));
			parameters.add(new BasicNameValuePair("ticket_key",ticketNo));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			httpPost = HttpClientUtil.getHttpPost(UrlEnum.PAY_ORDER_INIT);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				Document document = JsoupUtil.getPageDocument(response.getEntity().getContent());
				if(document != null){
					Elements elements = document.getElementsByTag("script");
					String data = null;
					for(Element element:elements){
						data = element.data();
						if(StringUtils.contains(data,"loseTime")){
							break;
						}
					}
					String [] ss = StringUtils.split(data,";");
					String loseTime = null;
					String beginTime = null;
					for(String str:ss){
						if(StringUtils.contains(str,"loseTime")){
							loseTime = StringUtils.split(str,"=")[1].replace("\"","").trim();
							continue;
						}
						if(StringUtils.contains(str,"beginTime")){
							beginTime = StringUtils.split(str,"=")[1].replace("\"","").trim();
							break;
						}
					}
					if("null".equals(beginTime)){
						return null;
					}
					long time1 = Long.parseLong(beginTime);
					long time2 = Long.parseLong(loseTime);
					return ""+((time2-time1)/(1000*60));
				}
			}
		}catch(SocketTimeoutException socketTimeoutException){
			Log.e("OperationUtil","getLastTime",socketTimeoutException);
			return getLastTime(orderNo, token, ticketNo);
		}catch (Exception e) {
			Log.e("OperationUtil","getLastTime",e);
			return null;
		} finally {
//			if(httpPost != null){
//				httpPost.abort();
//			}
		}
		return null;
    }
	
	/**
	 * 付款初始数据
	 * @param inputStream
	 * @return
	 */
    public static PayInfo toPayinit(String orderNo,String token,String ticketNo){
    	HttpResponse response = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method","laterEpay"));
			parameters.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN",token));
			parameters.add(new BasicNameValuePair("orderSequence_no",orderNo));
			parameters.add(new BasicNameValuePair("con_pay_type","epay"));
			parameters.add(new BasicNameValuePair("queryOrderDTO.from_order_date",""));
			parameters.add(new BasicNameValuePair("queryOrderDTO.to_order_date",""));
			parameters.add(new BasicNameValuePair("ticket_key",ticketNo));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			HttpPost httpPost = HttpClientUtil.getHttpPost(UrlEnum.PAY_ORDER_INIT);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return JsoupUtil.getPayInitParam(response.getEntity().getContent());
			}
		}catch(SocketTimeoutException socketTimeoutException){
			Log.e("OperationUtil","toPayinit",socketTimeoutException);
			return toPayinit(orderNo, token, ticketNo);
		}catch (Exception e) {
			Log.e("OperationUtil","toPayinit",e);
			return null;
		} finally {
			
		}
		return null;
    }
    
    /**
	 * 提交付款请求
	 * @param inputStream
	 * @return
	 */
    public static InputStream toPaySubmit(String info){
    	HttpResponse response = null;
    	HttpPost httpPost = null;
		try {
			PayInfo payInfo = new PayInfo();
			String data = null;
			int n = StringUtils.indexOf(info,"interfaceName");
			int m = StringUtils.indexOf(info,";",n);
			data = StringUtils.substring(info,n+17,m-1);
			payInfo.setInterfaceName(data);
			n = StringUtils.indexOf(info,"interfaceVersion");
			m = StringUtils.indexOf(info,";",n);
			data = StringUtils.substring(info,n+20,m-1);
			payInfo.setInterfaceVersion(data);
			n = StringUtils.indexOf(info,"tranData");
			m = StringUtils.indexOf(info,";",n);
			data = StringUtils.substring(info,n+12,m-1);
			data = StringUtils.replace(data,"\\r\\n","");
			payInfo.setTranData(data);
			n = StringUtils.indexOf(info,"merSignMsg");
			m = StringUtils.indexOf(info,";",n);
			data = StringUtils.substring(info,n+14,m-1);
			data = StringUtils.replace(data,"\\r\\n","");
			payInfo.setMerSignMsg(data);
			n = StringUtils.indexOf(info,"appId");
			m = StringUtils.indexOf(info,";",n);
			data = StringUtils.substring(info,n+9,m-1);
			payInfo.setAppId(data);
			n = StringUtils.indexOf(info,"transType");
			m = StringUtils.indexOf(info,";",n);
			data = StringUtils.substring(info,n+13,m-1);
			payInfo.setTransType(data);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("interfaceName",payInfo.getInterfaceName()));
			parameters.add(new BasicNameValuePair("interfaceVersion",payInfo.getInterfaceVersion()));
			parameters.add(new BasicNameValuePair("tranData",payInfo.getTranData()));
			parameters.add(new BasicNameValuePair("merSignMsg",payInfo.getMerSignMsg()));
			parameters.add(new BasicNameValuePair("appId",payInfo.getAppId()));
			parameters.add(new BasicNameValuePair("transType",payInfo.getTransType()));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters);
			httpPost = new HttpPost(UrlEnum.TO_PAY.getPath());
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17");
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return response.getEntity().getContent();
			}else{
				Log.i("toPaySubmit",EntityUtils.toString(response.getEntity()));
			}
		}catch(SocketTimeoutException socketTimeoutException){
			Log.e("OperationUtil","getLastTime",socketTimeoutException);
			return toPaySubmit(info);
		}catch (Exception e) {
			Log.e("OperationUtil","toPaySubmit",e);
			return null;
		} finally {
//			if(httpPost != null){
//				httpPost.abort();
//			}
		}
		return null;
    }
    
    /**
	 * 选择银行付款
	 * @param inputStream
	 * @return
	 */
    public static String selectBank(PayInfo payInfo){
    	HttpResponse response = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("tranData",payInfo.getTranData()));
			parameters.add(new BasicNameValuePair("merSignMsg",payInfo.getMerSignMsg()));
			parameters.add(new BasicNameValuePair("appId",payInfo.getAppId()));
			parameters.add(new BasicNameValuePair("transType",payInfo.getTransType()));
			parameters.add(new BasicNameValuePair("channelId",payInfo.getChannelId()));
			parameters.add(new BasicNameValuePair("merCustomIp",payInfo.getMerCustomIp()));
			parameters.add(new BasicNameValuePair("orderTimeoutDate",payInfo.getOrderTimeoutDate()));
			parameters.add(new BasicNameValuePair("bankId",payInfo.getBankId()));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,"UTF-8");
			HttpPost httpPost = new HttpPost(UrlEnum.SELECT_BANK.getPath());
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17");
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				if("00011000".equals(payInfo.getBankId())){
					Document document = JsoupUtil.getPageDocument(response.getEntity().getContent());
					Element element = document.getElementsByTag("form").get(0);
					return payByUnionPay(element);
				}else{
					return EntityUtils.toString(response.getEntity());
				}
			}
		} catch (Exception e) {
			Log.e("OperationUtil","selectBank",e);
			return null;
		} finally {
			
		}
		return null;
    }
    
    /**
     * 用银联付款
     * @param document
     * @return
     */
    public static String payByUnionPay(Element element){
    	HttpResponse response = null;
		try {
			String url = element.attr("action");
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			Element fromElement = element.getElementsByAttributeValue("name","orderCurrency").get(0);
			parameters.add(new BasicNameValuePair("orderCurrency",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","orderTime").get(0);
			parameters.add(new BasicNameValuePair("orderTime",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","backEndUrl").get(0);
			parameters.add(new BasicNameValuePair("backEndUrl",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","charset").get(0);
			parameters.add(new BasicNameValuePair("charset",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","merId").get(0);
			parameters.add(new BasicNameValuePair("merId",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","version").get(0);
			parameters.add(new BasicNameValuePair("version",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","merAbbr").get(0);
			parameters.add(new BasicNameValuePair("merAbbr",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","signMethod").get(0);
			parameters.add(new BasicNameValuePair("signMethod",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","frontEndUrl").get(0);
			parameters.add(new BasicNameValuePair("frontEndUrl",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","signature").get(0);
			parameters.add(new BasicNameValuePair("signature",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","orderAmount").get(0);
			parameters.add(new BasicNameValuePair("orderAmount",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","customerIp").get(0);
			parameters.add(new BasicNameValuePair("customerIp",fromElement.attr("value")));	
			fromElement = element.getElementsByAttributeValue("name","transType").get(0);
			parameters.add(new BasicNameValuePair("transType",fromElement.attr("value")));	
			fromElement = element.getElementsByAttributeValue("name","merReserved").get(0);
			parameters.add(new BasicNameValuePair("merReserved",fromElement.attr("value")));
			fromElement = element.getElementsByAttributeValue("name","orderNumber").get(0);
			parameters.add(new BasicNameValuePair("orderNumber",fromElement.attr("value")));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,"UTF-8");
			HttpPost httpPost = new HttpPost(UrlEnum.PAY_BY_UNIONPAY.getPath());
			httpPost.addHeader("Referer",url);
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17");
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			String info = null;
			if (response.getStatusLine().getStatusCode() == 200) {
				info = EntityUtils.toString(response.getEntity());
			}else{
				info = EntityUtils.toString(response.getEntity());
			}
			System.out.println(info);
		} catch (Exception e) {
			Log.e("OperationUtil","selectBank",e);
			return null;
		} finally {
			
		}
		return null;
    }
    
    /**
     * 查询火车票，检测有没有登录
     * @return
     */
    public static String searchTicket(String date){
    	try {
    		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
    		parameters.add(new BasicNameValuePair("method","queryLeftTicket"));
    		parameters.add(new BasicNameValuePair("orderRequest.train_date","2013-05-13"));
    		parameters.add(new BasicNameValuePair("orderRequest.from_station_telecode","SZQ"));
    		parameters.add(new BasicNameValuePair("orderRequest.to_station_telecode","AEQ"));
    		parameters.add(new BasicNameValuePair("orderRequest.train_no",""));
    		parameters.add(new BasicNameValuePair("trainPassType","QB"));
    		parameters.add(new BasicNameValuePair("trainClass","QB#D#Z#T#K#QT#"));
    		parameters.add(new BasicNameValuePair("includeStudent","00"));
    		parameters.add(new BasicNameValuePair("seatTypeAndNum",""));
    		parameters.add(new BasicNameValuePair("orderRequest.start_time_str","00:00--24:00"));
    		UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(parameters);
    		HttpGet httpGet = HttpClientUtil.getHttpGet(UrlEnum.SEARCH_TICKET);
    		httpGet.setURI(new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.SEARCH_TICKET.getPath()+"?"+EntityUtils.toString(urlEncodedFormEntity)));
    		HttpResponse response = httpClient.execute(httpGet);
    		if(response.getStatusLine().getStatusCode() == 200){
    			return EntityUtils.toString(response.getEntity());
    		}else{
    			String info = EntityUtils.toString(response.getEntity());
    			Log.i("searchTicket",info);
    		}
		} catch (Exception e) {
			Log.e("canelOrder","searchTicket",e);
		}
    	return null;
    }
    
    /**
	 * 获取登录账号用户信息
	 * 
	 * @throws URISyntaxException
	 */
	public static String getOrderPerson() {
		HttpResponse response = null;
		try {
			HttpClient httpClient = HttpClientUtil.getHttpClient();
//			URI uri = new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.GET_ORDER_PERSON.getPath());
			HttpPost httpPost = HttpClientUtil.getHttpPost(UrlEnum.GET_ORDER_PERSON);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method", "getPagePassengerAll"));
			parameters.add(new BasicNameValuePair("pageIndex",
					"0"));
			parameters.add(new BasicNameValuePair("pageSize",
					"1"));
			parameters.add(new BasicNameValuePair("passenger_name",
					"请输入汉字或拼音首字母"));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			Log.e("GetPersonConstanct","getOrderPerson error!",e);
		} finally {
			Log.i("GetPersonConstanct","close getOrderPerson");
		}
		return null;
	}
	
	public static String initRefundTicket() {
		HttpResponse response = null;
		try {
			HttpClient httpClient = HttpClientUtil.getHttpClient();
//			URI uri = new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.GET_ORDER_PERSON.getPath());
			HttpPost httpPost = HttpClientUtil.getHttpPost(UrlEnum.INIT_REFUND_TICKET);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method", "initRefundTicket"));
			parameters.add(new BasicNameValuePair("ticket_key","E4100807031133005"));
			parameters.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN","67432f582867f4b3868e93fa9a777326"));
			parameters.add(new BasicNameValuePair("queryOrderDTO.from_order_date","2013-09-02"));
			parameters.add(new BasicNameValuePair("queryOrderDTO.to_order_date","2013-09-02"));
			parameters.add(new BasicNameValuePair("ticket_key","E4100807031133005"));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			Log.e("GetPersonConstanct","getOrderPerson error!",e);
		} finally {
			Log.i("GetPersonConstanct","close getOrderPerson");
		}
		return null;
	}
	
	/**
	 * 获取登录账号用户信息
	 * 
	 * @throws URISyntaxException
	 */
	public static String checkLogin() {
		HttpResponse response = null;
		try {
			HttpClient httpClient = HttpClientUtil.getHttpClient();
//			URI uri = new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.GET_ORDER_PERSON.getPath());
			HttpGet httpGet = HttpClientUtil.getHttpGet(UrlEnum.CHECK_LOGIN);
			response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			Log.e("GetPersonConstanct","getOrderPerson error!",e);
		}
		return null;
	}

}
