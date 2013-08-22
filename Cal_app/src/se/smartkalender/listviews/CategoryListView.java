package se.smartkalender.listviews;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import se.smartkalender.globals;
import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

/**
 * listview that display all events
 *
 */
public class CategoryListView extends CustomListView  {

	private CategoryAdapter category_adapter;
	private Activity activity;
	int layoutResourceId;

	public CategoryListView(Activity a, ArrayList<String> categories, int layoutResourceId,
			OnItemClickListener itemClickListener) {
		super(a);
		activity = a;
		this.layoutResourceId = layoutResourceId;
		Reload(a, categories);
		setOnItemClickListener(itemClickListener);
	}
	
	public synchronized void Reload(Context context, ArrayList<String> categories) {
		ArrayList<String>  _categories = new ArrayList<String>();
		for (String c : categories) _categories.add(c);
		Collections.sort(_categories);
		
        this.category_adapter = new CategoryAdapter(context, layoutResourceId, _categories);
        setAdapter(this.category_adapter);
	}	
	
	private class CategoryAdapter extends ArrayAdapter<String> {

		private ArrayList<String> items;

		public CategoryAdapter(Context context, int layoutResourceId,
				ArrayList<String> items) {
			super(context, layoutResourceId, items);
			this.items = items;
		}

		@Override
		public synchronized View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
            if(items == null || items.size() <= 0 ){
                return null;
            }

        	String e = items.get(position);

            LayoutInflater vi = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(layoutResourceId, null);

            if (e != null) 
                v.setTag(e);{
                TextView t = (TextView) v.findViewById(R.id.categoryName);
                t.setText(e);    
            }

			return v;
		}
	}
	
}
