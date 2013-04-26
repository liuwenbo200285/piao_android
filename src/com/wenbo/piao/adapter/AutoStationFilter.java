package com.wenbo.piao.adapter;

import java.util.List;

import com.wenbo.piao.sqllite.domain.Station;

import android.widget.Filter;


public class AutoStationFilter extends Filter {
	
	private List<Station> stations;

	@Override
	protected FilterResults performFiltering(CharSequence constraint) {
		// TODO Auto-generated method stub
		FilterResults results = new FilterResults();
		
		if(stations != null && !stations.isEmpty()){
			
		}
		return null;
	}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		List<Station> stations = (List<Station>)results.values;
		if(stations.isEmpty()){
			
		}else{
//			notifyDataSetInvalidated();
		}
	}

}
