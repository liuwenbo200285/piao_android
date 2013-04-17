package com.wenbo.piao.task;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;

/**
 * 获取账号联系人
 * @author wenbo
 *
 */
public class GetPersonConstanct extends AsyncTask<String,Integer,String>{
	
	private Activity activity;
	
	private ProgressDialog progressDialog;
	
	public GetPersonConstanct(Activity activity){
		this.activity = activity;
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		return getOrderPerson();
	}

	@Override
	protected void onPostExecute(String result) {
		progressDialog.dismiss();
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(activity,"获取联系人","正在获取联系人...",true,false);
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}
	
	/**
	 * 获取登录账号用户信息
	 * 
	 * @throws URISyntaxException
	 */
	private String getOrderPerson() {
		HttpResponse response = null;
		try {
			HttpClient httpClient = HttpClientUtil.getHttpClient();
			URI uri = new URI(UrlEnum.DO_MAIN.getPath()+UrlEnum.GET_ORDER_PERSON.getPath());
			HttpPost httpPost = HttpClientUtil.getHttpPost(uri,
					UrlEnum.GET_ORDER_PERSON);
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			parameters.add(new BasicNameValuePair("method", "getPagePassengerAll"));
			parameters.add(new BasicNameValuePair("pageIndex",
					"0"));
			parameters.add(new BasicNameValuePair("pageSize",
					"100"));
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

}
