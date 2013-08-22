package se.smartkalender.listviews;

import java.util.ArrayList;

import se.smartkalender.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


public class IconsArrayAdapter  extends ArrayAdapter<Integer> {
	private ArrayList<Integer> items;

	public IconsArrayAdapter(Context context, int layoutResourceId, ArrayList<Integer> iconsIds) {
		super(context, layoutResourceId, iconsIds);
		this.items = iconsIds;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
        if(items == null || items.size() <= 0 ){
            return null;
        }

        Integer iconId = items.get(position);

        LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.icon_list_item, null);

        if (iconId != null)  {
            v.setTag(iconId);
            ImageView iv = (ImageView) v.findViewById(R.id.eventIcon);
            iv.setImageDrawable(this.getContext().getResources().getDrawable(iconId));
        }

		return v;
	}
}
