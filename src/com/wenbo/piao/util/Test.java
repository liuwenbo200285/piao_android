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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;
import android.widget.EditText;

import com.wenbo.piao.R;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.enums.UrlNewEnum;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HttpResponse response;
		try {
			String username = "liuwenbo200285";
			String password = "2580233";
			String randCode = "e85n";
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("loginUserDTO.user_name",username));
			parameters.add(new BasicNameValuePair("userDTO.password", password));
			parameters.add(new BasicNameValuePair("randCode", randCode));
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,"UTF-8");
			HttpPost httpPost = HttpClientUtil.getNewHttpPost(UrlNewEnum.LONGIN_CONFIM);
			httpPost.setEntity(uef);
			HttpClient httpClient = HttpClientUtil.getHttpClient();
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 302) {
			} else if (response.getStatusLine().getStatusCode() == 404) {
			} else if (response.getStatusLine().getStatusCode() == 200) {
				String info = EntityUtils.toString(response.getEntity());
				Document document = Jsoup.parse(info);
				// 判断登录状态
				if(StringUtils.contains(info,"系统维护中")){
					Log.i("Login","系统维护中，请明天订票!");
					
				}else{
					
				}
			}
		}catch (Exception e) {
			Log.e("Login","登录出错!",e);
		}
		
	}

}
