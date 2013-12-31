package com.wenbo.piao.task;

import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.wenbo.piao.R;
import com.wenbo.piao.dialog.LoginDialog;
import com.wenbo.piao.enums.UrlNewEnum;
import com.wenbo.piao.util.HttpClientUtil;
import com.wenbo.piao.util.gifview.GifFrame;
import com.wenbo.piao.util.gifview.GifView;

/**
 * 获取验证码task
 * @author wenbo
 *
 */
public class GetRandCodeTask extends AsyncTask<String,Integer,Bitmap> {
	
	private ImageView imageView = null;
	
	private Activity activity = null;
	
	private int type;
	
	private ProgressDialog progressDialog;
	
	private GifView gifView;
	
	private static DefaultHttpClient httpClient = null;
	
	
	public GetRandCodeTask(Activity activity,ImageView imageView,int type){
		httpClient = HttpClientUtil.getHttpClient();
		this.activity = activity;
		this.type = type;
		this.imageView = imageView;
		gifView = new GifView(activity);
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		UrlNewEnum urlEnum = null;
		if(type == 1){
			this.imageView = (ImageView)activity.findViewById(R.id.rangCodeImg);
			urlEnum = UrlNewEnum.LOGIN_RANGCODE_URL;
		}else if(type == 2){
			urlEnum = UrlNewEnum.GET_ORDER_RANGCODE;
		}
		return getRandCode(urlEnum);
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		progressDialog.dismiss();
		if(result == null){
			LoginDialog.newInstance("请检测网络是否正常！").show(activity.getFragmentManager(),"dialog"); 
			return;
		}
		imageView.setImageBitmap(result);
	}

	@Override
	protected void onPreExecute() {
		progressDialog = ProgressDialog.show(activity,"获取验证码","正在获取验证码...",true,false);
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
	private Bitmap getRandCode(UrlNewEnum urlEnum) {
		HttpGet httpGet = HttpClientUtil.getNewHttpGet(urlEnum);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				byte[] bb= EntityUtils.toByteArray(response.getEntity());
				gifView.setGifImage(bb);
				GifFrame gifFrame = null;
				while(gifFrame == null){
					gifFrame = gifView.getGifDecoder().getFrame(4);
				}
				return gifFrame.image;
			} else {
				getRandCode(urlEnum);
			}
		} catch (Exception e) {
			Log.e("GetRandCodeTask.getRandCode","获取验证码失败!",e.fillInStackTrace());
			return null;
		} finally {
			httpGet.abort();
		}
		return null;
	}
	
}
