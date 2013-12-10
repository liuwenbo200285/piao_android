package com.wenbo.piao.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import android.util.Log;

import com.wenbo.piao.domain.Order;
import com.wenbo.piao.domain.OrderInfo;
import com.wenbo.piao.domain.PayInfo;
import com.wenbo.piao.service.RobitOrderService;

public class JsoupUtil {
	
	public static boolean isSearch = true;

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws Exception {
		String str = "Mon, 09 Sep 2013 09:49:21 GMT";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		simpleDateFormat.parse(str);
	}	
	
	
	/**
	 * 获取页面对象
	 * @param url
	 * @return
	 */
	public static  Document getPageDocument(InputStream inputStream) {
		Document doc = null;
		try {
			doc = Jsoup.parse(inputStream,"UTF-8","https://dynamic.12306.cn/");
		} catch (Exception e) {
			Log.e("JsoupUtil.getPageDocument","getPageDocument error!",e);
		}finally{
			IOUtils.closeQuietly(inputStream);
		}
		return doc;
	}
	
	
	/**
	 * 获取订票信息
	 * @param document
	 */
	public static String [] getTicketInfo(Document document){
		String str = document.getElementsByTag("a").attr("onclick");
		int begin = StringUtils.indexOf(str,"'");
		int end = StringUtils.lastIndexOf(str,"'");
		str = StringUtils.substring(str, begin+1, end);
		Log.i("JsoupUtil.getTicketInfo",str);
		String [] params = StringUtils.split(str,"#");
		return params;
	}
	
	
	/**
	 * 检测有没有票
	 */
	public static int checkHaveTicket(Document document,String type,RobitOrderService robitOrderService,Date serverDate){
		isSearch = true;
		int max = 10000000;
		Integer maxType = 0;
		String trainNo = null;
		try {
			if(document == null){
				throw new IllegalAccessException("document is null");
			}
			Elements elements = document.getElementsByTag("span");
			if(elements.size() == 0){
				return maxType;
			}
			trainNo = elements.get(0).childNode(0).toString();
			List<Node> nodes = document.childNode(0).childNodes().get(1).childNodes();
			Node node = null;
			int n = 1;
			int index = 0;
			for(int i = 5; i < nodes.size(); i++){
				node = nodes.get(i);
			    if(StringUtils.contains(node.toString(),"--")){
			    	String[] nos = StringUtils.split(node.toString(),",");
			    	if(nos != null && nos.length > 0){
			    		for(String nn:nos){
			    			if("--".equals(nn)){
			    				n++;
			    			}else if(StringUtils.isNumeric(nn)){
			    				if((index = StringUtils.indexOf(type, n+",")) != -1){
			    					Log.i("JsoupUtil:checkHaveTicket",trainNo+"有票:"+nn+"张!");
			    					robitOrderService.sendInfo(trainNo+"有"+HttpClientUtil.getSeatTypeMap(n)+"票:"+nn+"张!");
			    					max = compare(max,index);
			    					if(max != 10000000){
			    						return n;
			    					}
			    				}
			    				n++;
			    			}
			    		}
			    	}
			    }else if("darkgray".equals(node.attr("color"))){
			    	n++;
			    }else if("#008800".equals(node.attr("color"))){
			    	if((index = StringUtils.indexOf(type, n+",")) != -1){
			    		robitOrderService.sendInfo(trainNo+"有大量的"+HttpClientUtil.getSeatTypeMap(n)+"票!");
			    		max = compare(max,index);
			    		if(max != 10000000){
    						return n;
    					}
			    	}
			    	n++;
			    }else if("btn130".equals(node.attr("class"))){
			    	String info = node.childNode(0).toString();
					int bengin = StringUtils.indexOf(info,"日");
					int end = StringUtils.indexOf(info,"点起售");
					String clo = null;
					if(bengin != -1){
						Log.i("JsoupUtil.checkHaveTicket",trainNo+":"+info);
						robitOrderService.sendInfo(trainNo+":"+info);
						maxType = -1;
						break;
					}else if(end != -1){
						clo = StringUtils.substring(info,0,end);
					}
					if(StringUtils.isNumeric(clo)){
						int hour = Integer.valueOf(clo);
						Calendar calender = Calendar.getInstance();
						calender.setTime(serverDate);
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String str = calender.get(Calendar.YEAR)+"-"+
								(calender.get(Calendar.MONTH)+1)+"-"+(calender.get(Calendar.DAY_OF_MONTH))+" "+hour+":00:00";
						Date beginDate = simpleDateFormat.parse(str);
						Date now = serverDate;
						Date clientDate = new Date();
						long timeReduce = serverDate.getTime()-clientDate.getTime();
						long waitTime = (beginDate.getTime()-now.getTime());
						while(waitTime > 2*1000 && isSearch){
							waitTime = waitTime-3*1000;
							Log.i("JsoupUtil.checkHaveTicket","等待"+waitTime/(1000*60)+"分钟！");
							long waitMinutes = 1000*60;
							if(waitTime > 1000*60){
								robitOrderService.sendInfo(trainNo+":"+info+"，等待"+waitTime/(1000*60)+"分钟！");
							}else{
								robitOrderService.sendInfo(trainNo+":"+info+"，等待"+waitTime/(1000)+"秒！");
								waitMinutes = 1000;
							}
							Thread.sleep(waitMinutes);
							waitTime = beginDate.getTime()-new Date().getTime()+timeReduce;
						}
						if(isSearch){
							robitOrderService.sendInfo(trainNo+":正在等待出票，请做好输入验证码准备!");
						}
					}
			    }
			}
		} catch (Exception e) {
			Log.e("JsoupUtil.checkHaveTicket","checkHaveTicket error!",e);
		}
		if(maxType > 0){
			Log.i("JsoupUtil.checkHaveTicket",trainNo+"有票!");
		}else if(maxType < 0){
			
		}else{
			Log.i("JsoupUtil.checkHaveTicket",trainNo+"没有票!");
			robitOrderService.sendInfo(trainNo+"没有票!");
		}
		return maxType;
	}
	
	
	/**
	 * 比较坐席位置，放在前面的优先
	 * @param max
	 * @param index
	 * @return
	 */
	private static int compare(int max,int index){
		if(max == 0
				|| max > index){
			max = index;
		}
		return max;
	}
	
	/**
	 * 检测登录状态
	 * @param inputStream
	 */
	public static int validateLogin(Document document){
		Element element = document.getElementById("randErr");
		if(element != null){
			String errorString = element.child(1).childNode(0).toString();
			if(StringUtils.contains(errorString,"验证码")){
				return 3;
			}
		}
		element = document.getElementById("bookTicket");
		if(element != null){
			Log.i("Jsoup.validateLogin","登录成功!");
			return 0;
		}
		Elements elements = document.getElementsByAttributeValue("language","javascript");
		if(elements.size() > 0){
			String errorMessage = elements.get(0).childNode(0).toString();
			Log.w("Jsoup.validateLogin","登录失败!原因："+errorMessage);
			if(StringUtils.contains(errorMessage,"登录名")){
			    return 1;
			}else if(StringUtils.contains(errorMessage,"密码")){
				return 2;
			}
		}
		return 0;
	}
	
	/**
	 * 获取未完成订单
	 * @param inputStream
	 * @return
	 */
	public static List<Order> getNoCompleteOrders(InputStream inputStream){
		List<Order> orders = new ArrayList<Order>();
		try {
			Document document = getPageDocument(inputStream);
			Element tokenElement = document.getElementById("myOrderForm");
			String token = tokenElement.getElementsByTag("input").get(0).attr("value");
			Elements elements = document.getElementsByClass("tab_conw");
			Order order = null;
			for(Element element:elements){
				order = new Order();
				order.setToken(token);
				Element element2 = element.getElementsByClass("jdan_tfont").get(0);
				Elements elements2 = element2.getElementsByTag("li");
				for(Element element3:elements2){
					if(StringUtils.isBlank(order.getOrderDate())){
						order.setOrderDate(element3.text());
					}else{
						order.setOrderNum(StringUtils.split(element3.text(),"：")[1]);
					}
				}
				Elements elements3 = element.getElementsByTag("tbody").get(0).getElementsByTag("tr");
				List<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
				for(int i = 0; i < elements3.size(); i++){
					Element element3 = elements3.get(i);
					if(i !=0 && i != elements3.size()-1){
						Element element4 = element3.getElementById("checkbox_pay");
						if(element4 == null){
							continue;
						}
						if(StringUtils.isBlank(order.getOrderNo())
								&& element4 != null){
							order.setOrderNo(StringUtils.split(element4.attr("name"),"_")[2]);
						}
						OrderInfo orderInfo = new OrderInfo();
						orderInfo.setTicketNo(element4.attr("value"));
						String [] infos = StringUtils.split(element3.text()," ");
						StringBuilder sbBuilder = new StringBuilder();
						if(order.getOrderNo() == null){
							if(i == 1){
								for(int n = 0; n < infos.length;n++){
									sbBuilder.append(infos[n]+"\n");
									if(n == 3){
										orderInfo.setTrainInfo(sbBuilder.toString());
										order.setTrainInfo(StringUtils.replace(orderInfo.getTrainInfo(),"\n"," "));
										sbBuilder.delete(0,sbBuilder.length());
									}
									if(n == 5){
										orderInfo.setSeatInfo(sbBuilder.toString());
										sbBuilder.delete(0,sbBuilder.length());
									}else if(n == 7){
										orderInfo.setPassengersInfo(sbBuilder.toString());
										sbBuilder.delete(0,sbBuilder.length());
									}else if(n == 9){
										orderInfo.setStatusInfo(sbBuilder.toString());
										order.setOrderStatus(orderInfo.getStatusInfo());
										sbBuilder.delete(0,sbBuilder.length());
									}
								}
							}else{
								for(int n = 0; n < infos.length;n++){
									orderInfo.setStatusInfo(order.getOrderStatus());
									orderInfo.setTrainInfo(StringUtils.replace(order.getTrainInfo()," ","\n"));
									sbBuilder.append(infos[n]+"\n");
									if(n == 1){
										orderInfo.setSeatInfo(sbBuilder.toString());
										sbBuilder.delete(0,sbBuilder.length());
									}else if(n == 3){
										orderInfo.setPassengersInfo(sbBuilder.toString());
										sbBuilder.delete(0,sbBuilder.length());
									}
								}
							}
							order.setOrderStatus(StringUtils.replace(order.getOrderStatus(),"\n","，"));
						}else{
							for(int n = 0; n < infos.length;n++){
								sbBuilder.append(infos[n]+"\n");
								if(n == 3){
									orderInfo.setTrainInfo(sbBuilder.toString());
									order.setTrainInfo(StringUtils.replace(orderInfo.getTrainInfo(),"\n"," "));
									sbBuilder.delete(0,sbBuilder.length());
								}
								if(n == 8){
									String seatInfo = sbBuilder.toString();
									orderInfo.setSeatInfo(seatInfo);
									seatInfo = StringUtils.split(seatInfo,"\n")[4];
									order.setAllMoney(order.getAllMoney()+Double.parseDouble(StringUtils.remove(seatInfo,"元")));
									sbBuilder.delete(0,sbBuilder.length());
								}else if(n == 10){
									orderInfo.setPassengersInfo(sbBuilder.toString());
									sbBuilder.delete(0,sbBuilder.length());
								}else if(n == 11){
									orderInfo.setStatusInfo(sbBuilder.toString());
									order.setOrderStatus(orderInfo.getStatusInfo());
									sbBuilder.delete(0,sbBuilder.length());
								}
							}
						}
						orderInfos.add(orderInfo);
					}
				}
				if(order.getOrderInfo() == null
						|| StringUtils.isEmpty(order.getOrderStatus())){
					Elements errorElements = document.getElementsByClass("blue_bold");
					if(errorElements != null){
						Element errorElement = errorElements.get(0).parent();
						errorElements = errorElement.children();
					}
					OrderInfo orderInfo = new OrderInfo();
					int i = 0;
					for(Element errorElement:errorElements){
						if(i == 0){
							orderInfo.setTrainInfo(errorElement.text());
							order.setTrainInfo(orderInfo.getTrainInfo());
						}else if(i == 1){
							orderInfo.setSeatInfo(errorElement.text());
						}else if(i == 2){
							orderInfo.setPassengersInfo(errorElement.text());
						}else if(i == 3){
							orderInfo.setStatusInfo(errorElement.text());
							order.setOrderStatus(orderInfo.getStatusInfo());
						}
						i++;
					}
					order.setOrderInfo(orderInfo);
				}
				order.setOrderInfos(orderInfos);
				orders.add(order);
			}
		} catch (Exception e) {
			Log.e("getNoCompleteOrders","解析未完成订单出错!",e);
		}finally{
			IOUtils.closeQuietly(inputStream);
		}
		return orders;
	}
	
	
	/**
	 * 获取已经完成订单
	 * @param inputStream
	 * @return
	 */
	public static List<Order> myOrders(InputStream inputStream){
		List<Order> orders = new ArrayList<Order>();
		try {
			Document document = getPageDocument(inputStream);
			Element tokenElement = document.getElementById("myOrderForm");
			String token = tokenElement.getElementsByTag("input").get(0).attr("value");
			Elements elements = document.getElementsByAttributeValueStarting("id","form_all_");
			for(Element element:elements){
				Order order = new Order();
				order.setToken(token);
				Element element2 = element.getElementsByClass("jdan_tfont").get(0);
				Elements elements2 = element2.getElementsByTag("li");
				if(elements2 == null){
					continue;
				}
				order.setOrderNo(StringUtils.split(elements2.get(0).text(),"：")[1]);
				order.setOrderDate(elements2.get(1).text());
				order.setOrderNum(elements2.get(2).text());
				Elements elements3 = element.getElementsByTag("tbody").get(0).getElementsByTag("tr");
				List<OrderInfo> orderInfos = new ArrayList<OrderInfo>();
				for(int i = 0; i < elements3.size(); i++){
					Element element3 = elements3.get(i);
					if(i !=0 && i != elements3.size()-1){
						OrderInfo orderInfo = new OrderInfo();
						String [] infos = StringUtils.split(element3.text()," ");
						StringBuilder sbBuilder = new StringBuilder();
						for(int n = 0; n < infos.length;n++){
							sbBuilder.append(infos[n]+"\n");
							if(n == 3){
								orderInfo.setTrainInfo(sbBuilder.toString());
								order.setTrainInfo(StringUtils.replace(orderInfo.getTrainInfo(),"\n"," "));
								sbBuilder.delete(0,sbBuilder.length());
							}else if(n == 8){
								String seatInfo = sbBuilder.toString();
								orderInfo.setSeatInfo(seatInfo);
								seatInfo = StringUtils.split(seatInfo,"\n")[4];
								order.setAllMoney(order.getAllMoney()+Double.parseDouble(StringUtils.remove(seatInfo,"元")));
								sbBuilder.delete(0,sbBuilder.length());
							}else if(n == 10){
								orderInfo.setPassengersInfo(sbBuilder.toString());
								sbBuilder.delete(0,sbBuilder.length());
							}else if(n == 11){
								orderInfo.setStatusInfo(sbBuilder.toString());
								order.setOrderStatus(orderInfo.getStatusInfo());
								sbBuilder.delete(0,sbBuilder.length());
							}
						}
						orderInfos.add(orderInfo);
					}
				}
				order.setOrderInfos(orderInfos);
				orders.add(order);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		IOUtils.closeQuietly(inputStream);
		return orders;
	}
	
	/**
	 * 获取token
	 * @param inputStream
	 * @return
	 */
	public static String getMyOrderInit(InputStream inputStream,int type){
		Document document = getPageDocument(inputStream);
		if(document != null){
			Element element = null;
			if(type == 1){
				element = document.getElementById("myOrderForm");
			}else if(type == 2){
				element = document.getElementById("transferForm");
			}
			return element.getElementsByTag("input").get(0).attr("value");
		}
		return null;
	}
	
	/**
	 * 获取点击付款按钮后的信息
	 * @param inputStream
	 */
	public static PayInfo getPayInitParam(InputStream inputStream){
		try {
			Document document = getPageDocument(inputStream);
			if(document != null){
				Element element = document.getElementById("epayForm");
				if(element != null){
					PayInfo payInfo = new PayInfo();
					payInfo.setPayUrl(element.attr("action"));
					List<Node> nodes =  element.childNodes();
					for(Node node:nodes){
						if("interfaceName".equals(node.attr("name"))){
							payInfo.setInterfaceName(node.attr("value"));
						}else if("interfaceVersion".equals(node.attr("name"))){
							payInfo.setInterfaceVersion(node.attr("value"));
						}else if("tranData".equals(node.attr("name"))){
							payInfo.setTranData(node.attr("value"));
						}else if("merSignMsg".equals(node.attr("name"))){
							payInfo.setMerSignMsg(node.attr("value"));
						}else if("appId".equals(node.attr("name"))){
							payInfo.setAppId(node.attr("value"));
						}else if("transType".equals(node.attr("name"))){
							payInfo.setTransType(node.attr("value"));
						}
					}
					return payInfo;
				}
			}
		} catch (Exception e) {
			Log.e("JsoupUtil","getPayInitParam",e);
		}
		return null;
	}

}
