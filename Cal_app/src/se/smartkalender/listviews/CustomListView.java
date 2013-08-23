package se.smartkalender.listviews;

import se.smartkalender.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class CustomListView extends ListView {

	public CustomListView(Context context) {
		super(context);
		init();
	}

	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		this.setDivider(getResources().getDrawable(R.drawable.divider));
		this.setSelector(R.drawable.table_row_bkg);
		setCacheColorHint(0); 
	
	}
}
