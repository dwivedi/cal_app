package se.smartkalender.listviews;

import java.util.ArrayList;

import se.smartkalender.R;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class YourImageIconAdapter  extends ArrayAdapter<String> {
	private ArrayList<String> items;

	public YourImageIconAdapter(Context context, int layoutResourceId, ArrayList<String> pathsList) {
		super(context, layoutResourceId, pathsList);
		this.items = pathsList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
        if(items == null || items.size() <= 0 ){
            return null;
        }

        String iconPath = items.get(position);

        LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.icon_list_item, null);

        if (iconPath != null)  {
             ImageView iv = (ImageView) v.findViewById(R.id.eventIcon);
             iv.setImageBitmap(BitmapFactory.decodeFile(iconPath));
         }

		return v;
	}
}





/*extends BaseAdapter {

	public YourImageIconAdapter(Context context,ArrayList<String> pathList) {
		super();
 		this.pathList =pathList;
		inflater = LayoutInflater.from(context);
 	}

 	private ArrayList<String> pathList;
	private LayoutInflater inflater;

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pathList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return pathList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.icon_list_item, null);
			holder.iconImage = (ImageView) convertView
					.findViewById(R.id.eventIcon);

			convertView.setTag(holder);
		} else {

			holder = (ViewHolder) convertView.getTag();
		}
		String filePath = pathList.get(position);
		if (filePath != null) {
			Bitmap photo = BitmapFactory.decodeFile(filePath);
			holder.iconImage.setImageBitmap(photo);

		} else {
			holder.iconImage.setImageResource(R.drawable.ic_launcher);
		}

		return convertView;

	}

	class ViewHolder {
		ImageView iconImage;
	}



}
*/