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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
	
	private ProgressBar progressBar = null;
	
	private static DefaultHttpClient httpClient = null;
	
	public GetRandCodeTask(Activity activity){
		this.activity = activity;
		httpClient = HttpClientUtil.getHttpClient();
		progressBar = (ProgressBar)activity.findViewById(R.id.progressBar1);
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		return getRandCode(UrlEnum.LOGIN_RANGCODE_URL);
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		ImageView imageView = (ImageView)activity.findViewById(R.id.imageView1);
		imageView.setImageBitmap(result);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onPreExecute() {
		progressBar.setVisibility(View.VISIBLE);
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
