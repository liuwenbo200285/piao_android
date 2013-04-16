package com.wenbo.piao.task;

import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.wenbo.androidpiao.R;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.util.HttpClientUtil;

/**
 * 获取验证码task
 * @author wenbo
 *
 */
public class GetRandCodeTask extends AsyncTask<String,Integer,Bitmap> {
	
	private Activity activity = null;
	
	private static DefaultHttpClient httpClient = null;
	
	private int type;
	
	public GetRandCodeTask(Activity activity,int type){
		this.activity = activity;
		httpClient = HttpClientUtil.getHttpClient();
		this.type = type;
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		return getRandCode(UrlEnum.LOGIN_RANGCODE_URL);
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		ImageView imageView = null;
		if(type == 1){
			imageView = (ImageView)activity.findViewById(R.id.imageView1);
			
		}else if(type == 2){
			imageView = (ImageView)activity.findViewById(R.id.orderCodeImg);
		}
		imageView.setImageBitmap(result);
	}

	@Override
	protected void onPreExecute() {
//		progressDialog = ProgressDialog.show(activity,"获取验证码","正在获取验证码...",true,false);
		Log.i("GetRandCodeTask.onPreExecute","开始获取验证码...");
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Log.i("GetRandCodeTask.onProgressUpdate","正在获取验证码...");
	}
	
	/**
	 * 获取登录验证码
	 * 
	 * @param uri
	 * @return
	 * @throws URISyntaxException
	 */
	private Bitmap getRandCode(UrlEnum urlEnum) {
		HttpGet httpGet = HttpClientUtil.getHttpGet(urlEnum);
		HttpResponse response = null;
		Bitmap bitmap = null;
		try {
			response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				byte[] bb = EntityUtils.toByteArray(response.getEntity());
				Log.d("验证码大小:",""+bb.length);
				bitmap = BitmapFactory.decodeByteArray(bb, 0, bb.length);
			} else {
				getRandCode(urlEnum);
			}
		} catch (Exception e) {
			Log.e("GetRandCodeTask.getRandCode","获取验证码失败!",e);
		} finally {
			
		}
		return bitmap;
	}
	
}
