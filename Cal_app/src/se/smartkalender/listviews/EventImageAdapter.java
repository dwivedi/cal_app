package se.smartkalender.listviews;

import java.util.ArrayList;

import se.smartkalender.EventDetailsActivity;
import se.smartkalender.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EventImageAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private ArrayList<EventIconPOJO> eventIconPOJOs;

	public EventImageAdapter(EventDetailsActivity eventDetailsActivity,
			ArrayList<EventIconPOJO> eventIconPOJOs) {

		inflater = LayoutInflater.from(eventDetailsActivity);
		this.eventIconPOJOs = eventIconPOJOs;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return eventIconPOJOs.get(position).getViewType();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return eventIconPOJOs.size();
	}

	@Override
	public Object getItem(int position) {
		
		return eventIconPOJOs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (eventIconPOJOs == null || eventIconPOJOs.size() <= 0) {
			return null;
		}

		EventIconPOJO iconPOJO = eventIconPOJOs.get(position);

		v = inflater.inflate(R.layout.icon_list_item, null);
		ImageView iv = (ImageView) v.findViewById(R.id.eventIcon);

		int itemType = getItemViewType(position);
		if (itemType == EventDetailsActivity.DEFULT_ICON_VIEW_TYPE) {
			v.setTag(iconPOJO.getIconId());
 			iv.setImageDrawable(inflater.getContext().getResources()
					.getDrawable(iconPOJO.getIconId()));

		} else {
			if (BitmapFactory.decodeFile(iconPOJO.getImagePath())!=null) {
				 iv.setImageBitmap(BitmapFactory.decodeFile(iconPOJO.getImagePath()));
			}else{
				iv.setImageResource(R.drawable.ic_launcher);
			}
			
		}

		return v;
	}
}
