package se.smartkalender.listviews;

import se.smartkalender.R;

import android.content.Context;
import android.widget.ListView;

public class CustomListView extends ListView {

	public CustomListView(Context context) {
		super(context);
		this.setDivider(getResources().getDrawable(R.drawable.divider));
		this.setSelector(R.drawable.table_row_bkg);
		setCacheColorHint(0); 
	}

}
