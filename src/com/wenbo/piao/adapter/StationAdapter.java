package com.wenbo.piao.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.wenbo.piao.sqllite.domain.Station;

/**
 * 车站适配器
 * @author wenbo
 *
 */
public class StationAdapter extends BaseAdapter implements Filterable {
	
	private List<Station> stations;
	
	private AutoStationFilter autoStationFilter;
	
	private List<Station> resultsStations;
	
	private int mResource;

	private int mFieldId = 0;
	
	private Context mContext;
	
	private LayoutInflater mInflater;
	
	private final Object mLock = new Object();
	
	public StationAdapter(Context context,int textViewResourceId, List<Station> stations){
		this.stations = stations;
		this.mContext = context;
		this.mResource = textViewResourceId;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return stations.size();
	}

	@Override
	public Object getItem(int position) {
		Station station = stations.get(position);
		return station.getZhCode();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return  createViewFromResource(position, convertView, parent, mResource);
	}
	
	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View view;
		TextView text;
		if (convertView == null) {
			view = mInflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}
		try {
			if (mFieldId == 0) {
				text = (TextView) view;
			} else {
				text = (TextView) view.findViewById(mFieldId);
			}
		} catch (ClassCastException e) {
			Log.e("ArrayAdapter",
					"You must supply a resource ID for a TextView");
			throw new IllegalStateException(
					"ArrayAdapter requires the resource ID to be a TextView", e);
		}
		text.setText(getItem(position).toString());
		return view;
	}

	@Override
	public Filter getFilter() {
		if(autoStationFilter == null){
			autoStationFilter = new AutoStationFilter();
		}
		return autoStationFilter;
	}
	
	private class AutoStationFilter extends Filter {
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (resultsStations == null) {
				synchronized (mLock) {
					resultsStations = new ArrayList<Station>();//
				}
			}
			resultsStations.clear();
			if (constraint == null || constraint.length() == 0) {
				synchronized (mLock) {
					results.values = stations;
					results.count = stations.size();
				}
			}else{
				results.values = stations;
				results.count = stations.size();
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			List<Station> stations = (List<Station>)results.values;
			if(stations == null || stations.isEmpty()){
				notifyDataSetChanged();
			}else{
				notifyDataSetInvalidated();
			}
		}

	}

}
