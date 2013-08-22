package se.smartkalender;

import android.view.View;

public interface ISmartCalendarInterface {
	public View.OnClickListener getSmallEventClickListener();
	public View.OnLongClickListener getSmallEventLongClickListener();
	public View.OnClickListener getShowEventClickListener();
}
