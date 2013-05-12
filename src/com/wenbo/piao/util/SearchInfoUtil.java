package com.wenbo.piao.util;

import com.wenbo.piao.sqllite.domain.SearchInfo;

/**
 * 
 * @author wenbo
 *
 */
public class SearchInfoUtil {

	public static SearchInfo findSearchInfo(String fromStation,String toStation){
		if(!HttpClientUtil.getSearchInfos().isEmpty()){
			for(SearchInfo searchInfo:HttpClientUtil.getSearchInfos()){
				if(searchInfo.getFromStation().equals(fromStation)
						&& searchInfo.getToStation().equals(toStation)){
					return searchInfo;
				}
			}
		}
		return null;
	}
}
