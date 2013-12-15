package com.wenbo.piao.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.wenbo.piao.domain.ConfigInfo;
import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.PayInfo;
import com.wenbo.piao.enums.TrainSeatEnum;
import com.wenbo.piao.enums.UrlEnum;
import com.wenbo.piao.enums.UrlNewEnum;
import com.wenbo.piao.sqllite.domain.Account;
import com.wenbo.piao.sqllite.domain.SearchInfo;
import com.wenbo.piao.sqllite.domain.UserInfo;
import com.wenbo.piao.ssl.SSLSocketFactoryEx;

public class HttpClientUtil {

	private static DefaultHttpClient httpClient;
	
	private static Map<String,UserInfo> userinfoMap;
	
	private static Account account;
	
	private static String[] params;
	
	private static ConfigInfo configInfo;
	
	private static String seatNum;
	
	private static String token;
	
	private static String ticketNo;
	
	public static Map<String,String> seatMap;
	
	public static String[] seatTypeArray = {"商务座","特等座","一等座","二等座","高级软卧","软卧","硬卧","软座","硬座","无座"};
	
	public static Map<String,String> trainTypeMap;
	
	public static List<Order> myOrders;
	
	public static Order selectOrder;
	
	public static List<Order> noCompletedOrders;
	
	public static PayInfo payInfo;
	
	public static Map<String,String> bankMap;
	
	public static List<SearchInfo> searchInfos;
	
	public static SearchInfo selectSearchInfo;
	
	public static  DefaultHttpClient getHttpClient(){
        if(null == httpClient){
        	try {
        		//处理cookie
        		CookieSpecFactory csf = new CookieSpecFactory() {
        		    public BrowserCompatSpec newInstance(HttpParams params) {
        		        return new BrowserCompatSpec() {   
        		        	@Override
        		            public void validate(Cookie cookie, CookieOrigin origin)
        		            throws MalformedCookieException {
        		                // Oh, I am easy
        		            }
        		        };
        		    }
        		};
        		//异常自动恢复
        		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        			public boolean retryRequest(IOException exception, int executionCount,HttpContext context) {
//        			logger.error("httpclient error!",exception);
        			if (executionCount >= 5) {
        			    // 如果超过最大重试次数,那么就不要继续了
        			    return false; 
        			}
        			if (exception instanceof NoHttpResponseException) { // 如果服务器丢掉了连接,那么就重试
        				return true;
        			}
        			if (exception instanceof SSLHandshakeException) {
        			// 不要重试SSL握手异常
        				return false; 
        			}
        			HttpRequest request = (HttpRequest) context.getAttribute( ExecutionContext.HTTP_REQUEST);
        			boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
        			if (idempotent) {
        			    // 如果请求被认为是幂等的,那么就重试
        			    return true; 
        			}
        			return false;
        			} };
                //初始化工作
                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
                HttpProtocolParams.setUseExpectContinue(params, true);
                //设置连接管理器的超时
                ConnManagerParams.setTimeout(params, 1000);
                
                //设置连接超时
                HttpConnectionParams.setConnectionTimeout(params,5000);
                //设置Socket超时
                HttpConnectionParams.setSoTimeout(params,5000);
                SSLContext ctx = SSLContext.getInstance("TLS");
                X509TrustManager tm = new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
                };
                ctx.init(null, new TrustManager[] {tm}, null);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
                trustStore.load(null, null); 
                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore); 
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER); 
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); 
                registry.register(new Scheme("https",sf, 443));
                ClientConnectionManager conManager = new ThreadSafeClientConnManager(params,registry);
                httpClient = new DefaultHttpClient(conManager, params);
                httpClient.getCookieSpecs().register("easy", csf);
        		httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");
        		httpClient.setHttpRequestRetryHandler(myRetryHandler);
        		httpClient.setReuseStrategy(new ConnectionReuseStrategy() {                
					@Override
					public boolean keepAlive(HttpResponse response, HttpContext context) {
						return true;
					}
    		});
			} catch (Exception e) {
				Log.e("HttpClientUtil.getHttpClient","初始化httpclient失败",e);
			}
        }
        return httpClient;
    }
	
	public static final String REFER = "https://dynamic.12306.cn/otsweb/order/querySingleAction.do?method=init";
	/**
	 *requestType 1 GET 2 POST
	 */
	public static HttpPost getHttpPost(UrlEnum urlEnum){
		HttpPost httpPost = new HttpPost(UrlEnum.DO_MAIN.getPath()+urlEnum.getPath());
		if(StringUtils.isNotEmpty(urlEnum.getAccept())){
			httpPost.addHeader("Accept",urlEnum.getAccept());
		}
		httpPost.addHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		httpPost.addHeader("Cache-Control","max-age=0");
		httpPost.addHeader("Connection","keep-alive");
		httpPost.addHeader("Origin","https://dynamic.12306.cn");
		httpPost.addHeader("Accept-Language","zh-CN,zh;q=0.8");
		httpPost.addHeader("Host","dynamic.12306.cn");
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17");
		if(StringUtils.isNotEmpty(urlEnum.getxRequestWith())){
			httpPost.addHeader("X-Requested-With",urlEnum.getxRequestWith());
		}
		if(StringUtils.isNotEmpty(urlEnum.getContentType())){
			httpPost.addHeader("Content-Type",urlEnum.getContentType());
		}
		if(StringUtils.isNotEmpty(urlEnum.getRefer())){
			httpPost.addHeader("Referer",urlEnum.getRefer());
		}
		return httpPost;
	}
	
	public static HttpPost getNewHttpPost(UrlNewEnum urlEnum){
		HttpPost httpPost = new HttpPost(UrlNewEnum.DO_MAIN.getPath()+urlEnum.getPath());
		if(StringUtils.isNotEmpty(urlEnum.getAccept())){
			httpPost.addHeader("Accept",urlEnum.getAccept());
		}
		httpPost.addHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		httpPost.addHeader("Cache-Control","max-age=0");
		httpPost.addHeader("Connection","keep-alive");
		httpPost.addHeader("Origin","https://kyfw.12306.cn");
		httpPost.addHeader("Accept-Language","zh-CN,zh;q=0.8");
		httpPost.addHeader("Host","kyfw.12306.cn");
		httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17");
		if(StringUtils.isNotEmpty(urlEnum.getxRequestWith())){
			httpPost.addHeader("X-Requested-With",urlEnum.getxRequestWith());
		}
		if(StringUtils.isNotEmpty(urlEnum.getContentType())){
			httpPost.addHeader("Content-Type",urlEnum.getContentType());
		}
		if(StringUtils.isNotEmpty(urlEnum.getRefer())){
			httpPost.addHeader("Referer",urlEnum.getRefer());
		}
		return httpPost;
	}
	
	public static HttpGet getHttpGet(UrlEnum urlEnum){
		HttpGet httpGet = new HttpGet(UrlEnum.DO_MAIN.getPath()+urlEnum.getPath());
		if(StringUtils.isNotEmpty(urlEnum.getAccept())){
			httpGet.addHeader("Accept",urlEnum.getAccept());
		}
		httpGet.addHeader("Cache-Control","max-age=0");
		httpGet.addHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		httpGet.addHeader("Connection","keep-alive");
		httpGet.addHeader("Host","dynamic.12306.cn");
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.52 Safari/537.17");
		if(StringUtils.isNotEmpty(urlEnum.getxRequestWith())){
			httpGet.addHeader("X-Requested-With",urlEnum.getxRequestWith());
		}
		if(StringUtils.isNotEmpty(urlEnum.getContentType())){
			httpGet.addHeader("Content-Type",urlEnum.getContentType());
		}
		if(StringUtils.isNotEmpty(urlEnum.getRefer())){
			httpGet.addHeader("Referer",urlEnum.getRefer());
		}
		return httpGet;
	}
	
	public static HttpGet getNewHttpGet(UrlNewEnum urlEnum){
		HttpGet httpGet = new HttpGet(UrlNewEnum.DO_MAIN.getPath()+urlEnum.getPath());
		if(StringUtils.isNotEmpty(urlEnum.getAccept())){
			httpGet.addHeader("Accept",urlEnum.getAccept());
		}
		httpGet.addHeader("Cache-Control","max-age=0");
		httpGet.addHeader("Accept-Charset","GBK,utf-8;q=0.7,*;q=0.3");
		httpGet.addHeader("Connection","keep-alive");
		httpGet.addHeader("Host","kyfw.12306.cn");
		httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.52 Safari/537.17");
		if(StringUtils.isNotEmpty(urlEnum.getxRequestWith())){
			httpGet.addHeader("X-Requested-With",urlEnum.getxRequestWith());
		}
		if(StringUtils.isNotEmpty(urlEnum.getContentType())){
			httpGet.addHeader("Content-Type",urlEnum.getContentType());
		}
		if(StringUtils.isNotEmpty(urlEnum.getRefer())){
			httpGet.addHeader("Referer",urlEnum.getRefer());
		}
		return httpGet;
	}
	
	/**
	 * 获取坐席枚举
	 * @param trainSeat
	 * @return
	 */
	public static TrainSeatEnum getSeatEnum(Integer trainSeat){
		TrainSeatEnum[] trainSeatEnums = TrainSeatEnum.values();
		for(TrainSeatEnum trainSeatEnum:trainSeatEnums){
			if(trainSeatEnum.getCode() == trainSeat){
				return trainSeatEnum;
			}
		}
		return null;
	}
	
	public static String doPost(UrlNewEnum urlNewEnum,Map<String,String> paraMap,int num){
		HttpResponse response = null;
		// 获取验证码
		try {
			if(paraMap == null){
				Log.e("doPost","请求参数为null！action:"+urlNewEnum.getPath());
				return null;
			}
			Iterator<Entry<String,String>> iterator = paraMap.entrySet().iterator();
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			Entry<String,String> entry = null;
			while(iterator.hasNext()){
				entry = iterator.next();
				parameters.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
			}
			UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,"UTF-8");
			HttpPost httpPost = HttpClientUtil.getNewHttpPost(urlNewEnum);
			httpPost.setEntity(uef);
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 302) {
			} else if (response.getStatusLine().getStatusCode() == 404) {
			} else if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		}catch(SocketTimeoutException e){
			Log.e("doPost","网络连接超时!action:"+urlNewEnum.getPath(),e);
			if(num < 5){
				num++;
				doPost(urlNewEnum, paraMap, num);
			}
		}catch (Exception e) {
			Log.e("doPost","网络操作出错!action:"+urlNewEnum.getPath(),e);
		}
		return null;
	}
	
	public static String doGet(UrlNewEnum urlNewEnum,Map<String,String> paraMap,int num){
		HttpResponse response = null;
		// 获取验证码
		try {
			if(paraMap == null){
				Log.e("doGet","请求参数为null！action:"+urlNewEnum.getPath());
				return null;
			}
			Iterator<Entry<String,String>> iterator = paraMap.entrySet().iterator();
			List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
			Entry<String,String> entry = null;
			while(iterator.hasNext()){
				entry = iterator.next();
				parameters.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
			}
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(parameters);
			HttpGet httpGet = HttpClientUtil.getNewHttpGet(urlNewEnum);
			httpGet.setURI(new URI(UrlNewEnum.DO_MAIN.getPath()+urlNewEnum.getPath()+"?"+EntityUtils.toString(urlEncodedFormEntity)));
			response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity());
			}
		}catch(SocketTimeoutException e){
			Log.e("doGet","网络连接超时!action:"+urlNewEnum.getPath(),e);
			if(num < 5){
				num++;
				doPost(urlNewEnum, paraMap, num);
			}
		}catch (Exception e) {
			Log.e("doGet","网络操作出错!action:"+urlNewEnum.getPath(),e);
		}
		return null;
	}
	
	public static String getServerHead(String key,int num){
		HttpResponse response = null;
		// 获取验证码
		try {
			HttpGet httpGet = HttpClientUtil.getNewHttpGet(UrlNewEnum.CHECKUSER);
			httpGet.setURI(new URI(UrlNewEnum.DO_MAIN.getPath()+UrlNewEnum.CHECKUSER.getPath()));
			response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				return response.getHeaders("Date")[0].getValue();
			}
		}catch(SocketTimeoutException e){
			Log.e("doGet","网络连接超时!action:"+UrlNewEnum.CHECKUSER.getPath(),e);
			if(num < 5){
				num++;
				getServerHead(key,num);
			}
		}catch (Exception e) {
			Log.e("doGet","网络操作出错!action:"+UrlNewEnum.CHECKUSER.getPath(),e);
		}
		return null;
	}
	
	public static Account getAccount(){
		return HttpClientUtil.account;
	}
	
	public static void setAccount(Account account){
		HttpClientUtil.account = account;
	}
	
	public static void setUserInfoMap(Map<String,UserInfo> userinfoMap){
		HttpClientUtil.userinfoMap = userinfoMap;
	}
	
	public static String[] getParams() {
		return params;
	}

	public static void setParams(String[] params) {
		HttpClientUtil.params = params;
	}

	/**
	 * 获取订票联系人信息
	 * @return
	 */
	public static Map<String,UserInfo> getUserInfoMap(){
		if(userinfoMap == null){
			userinfoMap = new HashMap<String, UserInfo>();
		}
		return HttpClientUtil.userinfoMap;
	}

	public static ConfigInfo getConfigInfo() {
		return configInfo;
	}

	public static void setConfigInfo(ConfigInfo configInfo) {
		HttpClientUtil.configInfo = configInfo;
	}

	public static String getSeatNum() {
		return seatNum;
	}

	public static void setSeatNum(String seatNum) {
		HttpClientUtil.seatNum = seatNum;
	}

	public static String getToken() {
		return token;
	}

	public static void setToken(String token) {
		HttpClientUtil.token = token;
	}

	public static String getTicketNo() {
		return ticketNo;
	}

	public static void setTicketNo(String ticketNo) {
		HttpClientUtil.ticketNo = ticketNo;
	}

	public static Map<String, String> getSeatMap() {
		if(seatMap == null){
			seatMap = new HashMap<String, String>();
			seatMap.put("商务座","swz");
			seatMap.put("特等座","tz");
			seatMap.put("一等座","zy");
			seatMap.put("二等座","ze");
			seatMap.put("高级软卧","gr");
			seatMap.put("软卧","rw");
			seatMap.put("硬卧","yw");
			seatMap.put("软座","rz");
			seatMap.put("硬座","yz");
			seatMap.put("无座","wz");
			seatMap.put("其它","qt");
		}
		return seatMap;
	}
	
	public static String getSeatTypeMap(int n){
		return seatTypeArray[n-1];
	}

	public static Map<String, String> getTrainTypeMap() {
		if(trainTypeMap == null){
			trainTypeMap = new HashMap<String, String>();
			trainTypeMap.put("全部","QB");
			trainTypeMap.put("动车","D");
			trainTypeMap.put("Z字头","Z");
			trainTypeMap.put("T字头","T");
			trainTypeMap.put("K字头","K");
			trainTypeMap.put("其他","QT");
		}
		return trainTypeMap;
	}

	public static List<Order> getMyOrders() {
		return myOrders;
	}

	public static void setMyOrders(List<Order> myOrders) {
		HttpClientUtil.myOrders = myOrders;
	}

	public static Order getSelectOrder() {
		return selectOrder;
	}

	public static void setSelectOrder(Order selectOrder) {
		HttpClientUtil.selectOrder = selectOrder;
	}

	public static List<Order> getNoCompletedOrders() {
		return noCompletedOrders;
	}

	public static void setNoCompletedOrders(List<Order> noCompletedOrders) {
		HttpClientUtil.noCompletedOrders = noCompletedOrders;
	}

	public static PayInfo getPayInfo() {
		return payInfo;
	}

	public static void setPayInfo(PayInfo payInfo) {
		HttpClientUtil.payInfo = payInfo;
	}

	public static List<SearchInfo> getSearchInfos() {
		return searchInfos;
	}

	public static void setSearchInfos(List<SearchInfo> searchInfos) {
		HttpClientUtil.searchInfos = searchInfos;
	}

	public static SearchInfo getSelectSearchInfo() {
		return selectSearchInfo;
	}

	public static void setSelectSearchInfo(SearchInfo selectSearchInfo) {
		HttpClientUtil.selectSearchInfo = selectSearchInfo;
	}

	public static Map<String, String> getBankMap() {
		if(bankMap == null){
			bankMap = new HashMap<String, String>();
			bankMap.put("工商银行","01020000");
			bankMap.put("农业银行","01030000");
			bankMap.put("中国银行","01040000");
			bankMap.put("招商银行","03080000");
			bankMap.put("中国银联","00011000");
			bankMap.put("中铁银通卡","00011001");
			bankMap.put("建设银行","01050000");
		}
		return bankMap;
	}
}
