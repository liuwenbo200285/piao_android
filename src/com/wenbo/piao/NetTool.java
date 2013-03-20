package com.wenbo.piao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;

public class NetTool {

	/**
	 * 获得url代码数据
	 * */

	public static String getHtml(String path, String encoding) throws Exception {
		String str ="http://10.10.10.14:80/code/getinfo";
		HttpEntityEnclosingRequestBase request = new HttpPost(str);
		HttpResponse httpResponse = new SSLHttpClient().execute(request);
		if(httpResponse.getStatusLine().getStatusCode() == 200){
			
		}
		return null;
	}

	public static byte[] getImage(String urlpath) throws Exception {
		FakeX509TrustManager.allowAllSSL();
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpUriRequest request = new HttpGet(urlpath);
//		HttpResponse response = httpClient.execute(request);
//		if(response.getStatusLine().getStatusCode() == 200){
//			InputStream inputStream = response.getEntity().getContent();
//			return readStream(inputStream);
//		}
		URL postUrl = new URL(urlpath);
		HttpURLConnection con = (HttpURLConnection) postUrl.openConnection();
		InputStream inputStream = con.getInputStream();
		return readStream(inputStream);
	}

	/**
	 * 读取数据 输入流
	 * 
	 * */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		inStream.close();
		return outstream.toByteArray();
	}

}
