package se.smartkalender.dialogs;

import java.util.Date;

import se.smartkalender.EventDetailsActivity;
import se.smartkalender.EventsManager;
import se.smartkalender.SmartCalendarActivity;
import se.smartkalender.globals;
import se.smartkalender.listviews.CategoryListView;
import se.smartkalender.listviews.EventsListView;
import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.view.View.OnTouchListener;

public class TemplateCategoriesDialog extends Activity implements
		OnTouchListener {
	// Intent request codes
	private static final int RESULT_ADD_EVENT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		Window window = this.getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
		window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

		setContentView(R.layout.template_categories_dialog);

		View view = findViewById(R.id.mainlayout);
		view.setOnTouchListener(this);

		// find place for events list
		LinearLayout ll = (LinearLayout) findViewById(R.id.placeForEventsList);
		CategoryListView elv = new CategoryListView(this,
				EventsManager.getTemplateCategories(),
				R.layout.event_item_category, itemClickListener);
		ll.addView(elv);
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			System.out
					.println("TOuch outside the dialog ******************** ");

			finish();
		}
		return false;
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			Intent intent = new Intent(TemplateCategoriesDialog.this,
					EventsTemplateDialog.class);
			intent.putExtra("selectedDay", globals.selectedDay.getTime());

			String chosenCategory = (String) v.getTag();
			intent.putExtra("category", chosenCategory);

			if (getIntent().getExtras() != null) {
				if (getIntent().getExtras().getBoolean("planBackward")) {
					intent.putExtra("planBackward", true);
					intent.putExtra("startTime", getIntent().getExtras()
							.getLong("startTime"));
				} else if (getIntent().getExtras().getBoolean("planForward")) {
					intent.putExtra("planForward", true);
					intent.putExtra("startTime", getIntent().getExtras()
							.getLong("startTime"));
				}

				if (getIntent().getExtras().containsKey("selectedDay")) {
					intent.putExtra("selectedDay", getIntent().getExtras()
							.getLong("selectedDay"));
				}
			}

			startActivityForResult(intent, RESULT_ADD_EVENT);
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		globals.addRecordToLog("requestCode=" + requestCode + " resultCode="
				+ resultCode);
		if (resultCode == RESULT_OK) {
			setResult(resultCode, data);
			finish();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
			System.out
					.println("TOuch outside the dialog ******************** ");

			finish();
		}
		// TODO Auto-generated method stub
		return false;
	}
}
