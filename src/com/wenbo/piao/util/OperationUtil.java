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

import android.util.Log;

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

}
