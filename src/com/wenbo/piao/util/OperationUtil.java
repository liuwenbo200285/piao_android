package com.wenbo.piao.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method","cancelMyOrderNotComplete"));
			parameters.add(new BasicNameValuePair("org.apache.struts.taglib.html.TOKEN",token));
			parameters.add(new BasicNameValuePair("sequence_no",orderNo));
			parameters.add(new BasicNameValuePair("orderRequest.tour_flag",""));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,
					"UTF-8");
			HttpPost httpPost = HttpClientUtil.getHttpPost(UrlEnum.CANCEL_ORDER);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				String info = EntityUtils.toString(response.getEntity());
				if(StringUtils.contains(info,"取消订单成功")){
					return OPERATION_SUCCESS;
				}
			}
		} catch (Exception e) {
			Log.e("OperationUtil","canelOrder",e);
			return OPERATION_FAILURE;
		} finally {
			
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
					long time1 = Long.parseLong(beginTime);
					long time2 = Long.parseLong(loseTime);
					return ""+((time2-time1)/(1000*60));
				}
			}
		} catch (Exception e) {
			Log.e("OperationUtil","canelOrder",e);
			return null;
		} finally {
			
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
				PayInfo payInfo = JsoupUtil.getPayInitParam(response.getEntity().getContent());
				return payInfo;
			}
		} catch (Exception e) {
			Log.e("OperationUtil","canelOrder",e);
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
    public static PayInfo toPaySubmit(PayInfo payInfo){
    	HttpResponse response = null;
		try {
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("interfaceName",payInfo.getInterfaceName()));
			parameters.add(new BasicNameValuePair("interfaceVersion",payInfo.getInterfaceVersion()));
			parameters.add(new BasicNameValuePair("tranData",payInfo.getTranData()));
			parameters.add(new BasicNameValuePair("merSignMsg",payInfo.getMerSignMsg()));
			parameters.add(new BasicNameValuePair("appId",payInfo.getAppId()));
			parameters.add(new BasicNameValuePair("transType",payInfo.getTransType()));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,"UTF-8");
			HttpPost httpPost = new HttpPost(UrlEnum.TO_PAY.getPath());
			httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17");
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				Document document = JsoupUtil.getPageDocument(response.getEntity().getContent());
				Element element = document.getElementsByAttributeValue("name","merCustomIp").get(0);
				payInfo.setMerCustomIp(element.attr("value"));
				element = document.getElementsByAttributeValue("name","orderTimeoutDate").get(0);
				payInfo.setOrderTimeoutDate(element.attr("value"));
				payInfo.setPayMoney(document.getElementsByClass("nav3_5").get(0).text());
				return payInfo;
			}else{
				Log.i("toPaySubmit",EntityUtils.toString(response.getEntity()));
			}
		} catch (Exception e) {
			Log.e("OperationUtil","canelOrder",e);
			return null;
		} finally {
			
		}
		return null;
    }
    
    
    
    

}
