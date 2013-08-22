package se.smartkalender;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import se.smartkalender.dialogs.CustomAlertDialog;
import se.smartkalender.dialogs.EventFunctionsDialog;
import se.smartkalender.dialogs.TemplateCategoriesDialog;
import se.smartkalender.dialogs.TransparentProgressDialog;
import se.smartkalender.types.SmallEventReturnType;
import se.smartkalender.types.SmartCalendarEvent;
import se.smartkalender.widgets.DatePicker;

import se.smartkalender.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class SmartCalendarActivity extends Activity implements
		android.widget.DatePicker.OnDateChangedListener,
		ISmartCalendarInterface {

	// Intent request codes
	private static final int RESULT_ADD_EVENT = 1;
	private static final int RESULT_SHOW_ALL = 2;
	private static final int RESULT_ABOUT = 3;
	private static final int RESULT_SETTINGS = 4;
	private static final int RESULT_CHANGE_EVENT = 5;
	private static final int RESULT_ZOOM_VIEW = 6;
	// private static final int RESULT_DELETE_EVENT = 6;
	public static final int UPDATE_VIEW_BY_TIMER = 1;

	private Handler mHandler = new Handler();

	TextView timeTitleString;
	TextView dateTitleString;
	ViewSwitcher viewSwitcher;
	Integer eventIdForZoom = null;
	Integer zoomForZoom = null;
	FrameLayout timeMarker;
	DatePicker datePicker;
	ImageButton btnDayBack;
	ImageButton btnDayForward;
	ArrayList<View> activeEventsViews = new ArrayList<View>(0);
	int zoom;
	SmartCalendarEvent eventToScroll;
	private Timer timer;
	RelativeLayout calendarBkgPlace;
	TransparentProgressDialog progressDialog = null;
	View smallEventActiveView = null;
	View calendarSeparatorPlace;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			globals.addRecordToLog("SmartCalendarActivity onCreate - started");

			if (globals.selectedDay == null)
				globals.selectedDay = new Date();

			Bundle bundle = getIntent().getExtras();
			zoom = 15;
			if (bundle != null) {
				zoom = bundle.getInt("zoom");

				// get event that we should show to user
				if (bundle.containsKey("eventId")) {
					int eventId = bundle.getInt("eventId");
					if (eventId != 0)
						eventToScroll = new SmartCalendarEvent(
								EventsManager.getEvent(eventId));
				}
				if (bundle.containsKey("selectedDay")) {
					globals.selectedDay = new Date(
							bundle.getLong("selectedDay"));
				}
			} else {
				globals.init(this);
				EventsManager.setAlarmForFirstEvent(this);
			}
			globals.addRecordToLog("SmartCalendarActivity - zoom ="
					+ Integer.toString(zoom));
			if (zoom == 0)
				zoom = 15;

			View v;
			if (zoom != 15) {
				if (globals.zoomedView != null)
					v = globals.zoomedView;
				else
					v = SmartCalendarFactory.makeCalendarView(this, zoom);
			} else {
				v = SmartCalendarFactory.makeCalendarView(this, zoom);
			}
			this.setContentView(v);

			calendarSeparatorPlace = this
					.findViewById(R.id.calendarBkgPlaceSparator);
			calendarBkgPlace = (RelativeLayout) findViewById(R.id.calendarBkgPlace);
			calendarBkgPlace
					.setOnClickListener(calendarBackgroundClickListener);
			timeTitleString = (TextView) findViewById(R.id.time);
			dateTitleString = (TextView) findViewById(R.id.date);
			LinearLayout headerTimeDate = (LinearLayout) findViewById(R.id.headerTimeDate);
			btnDayBack = (ImageButton) findViewById(R.id.btnDayBack);
			btnDayForward = (ImageButton) findViewById(R.id.btnDayForward);
			Button btnBack = (Button) findViewById(R.id.btnBack);
			if (zoom == 15) {
				btnDayBack.setOnClickListener(headerBtnsClickListener);
				btnDayForward.setOnClickListener(headerBtnsClickListener);
				headerTimeDate.setOnClickListener(headerTimeDateClickListener);
				headerTimeDate
						.setOnLongClickListener(headerTimeDateLongClickListener);
			} else {
				btnDayBack.setVisibility(View.GONE);
				btnDayForward.setVisibility(View.GONE);
				btnBack.setVisibility(View.VISIBLE);
				btnBack.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						globals.addRecordToLog("SmartCalendarActivity:btnBack clicked");
						finish();
					}
				});
			}

			timeMarker = (FrameLayout) findViewById(R.id.timeMarker);
			boolean goToCurrentTime = (eventToScroll == null);
			timer = new Timer();// every minute we should update the view
			// updateView();
			// now if user just launch application we should check if small
			// event active in the current time
			// if yes we should perform smallEventClickListener on it
			if (smallEventActiveView != null)
				smallEventClickListener.onClick(smallEventActiveView);

			if (goToCurrentTime)
				SmartCalendarFactory.scrollToTime(this, new Date());
			globals.addRecordToLog("SmartCalendarActivity onCreate - finished");
		} catch (Exception ex) {
			globals.addRecordToLog("SmartCalendarActivity:onCreate "
					+ ex.getMessage());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		globals.addRecordToLog("onStart - The activity is about to become visible.");
		// The activity is about to become visible.
	}

	@Override
	public void onResume() {
		super.onResume();
		globals.addRecordToLog("onResume - " + Integer.toString(zoom));
		updateView();
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			timer.cancel();
			timer.purge();
		} catch (Exception e) {
			globals.addRecordToLog("exception when trying to cancel timer in onPause");
		}
		globals.addRecordToLog("onPause - " + Integer.toString(zoom));
	}

	@Override
	public void onStop() {
		super.onStop();
		globals.addRecordToLog("onStop - " + Integer.toString(zoom));
	}

	@Override
	public void onRestart() {
		super.onRestart();
		globals.addRecordToLog("onRestart - " + Integer.toString(zoom));
	}

	protected void onDestroy() {
		super.onDestroy();
		globals.addRecordToLog("onDestroy - " + Integer.toString(zoom));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public View.OnClickListener calendarBackgroundClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			Intent intent;
			intent = new Intent(SmartCalendarActivity.this,
					TemplateCategoriesDialog.class);
			intent.putExtra("selectedDay", globals.selectedDay.getTime());
			startActivityForResult(intent, RESULT_ADD_EVENT);
		}
	};

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_add:
			intent = new Intent(SmartCalendarActivity.this,
					TemplateCategoriesDialog.class);
			intent.putExtra("selectedDay", globals.selectedDay.getTime());
			startActivityForResult(intent, RESULT_ADD_EVENT);
			return true;
		case R.id.menu_show_all:
			intent = new Intent(SmartCalendarActivity.this,
					EventsListActivity.class);
			startActivityForResult(intent, RESULT_SHOW_ALL);
			return true;
		case R.id.menu_show_about:
			intent = new Intent(SmartCalendarActivity.this,
					EventAboutActivity.class);
			startActivityForResult(intent, RESULT_ABOUT);
			return true;
		case R.id.menu_show_settings:
			intent = new Intent(SmartCalendarActivity.this,
					EventSettingsActivity.class);
			startActivityForResult(intent, RESULT_SETTINGS);
			return true;
		}
		return false;
	}

	private Runnable createViewThread = new Runnable() {
		@Override
		public void run() {
			globals.addRecordToLog("createViewThread runnable is running");
			globals.zoomedView = SmartCalendarFactory.makeCalendarView(
					SmartCalendarActivity.this, zoomForZoom);
			runOnUiThread(startNewActivity);
		}
	};

	private Runnable startNewActivity = new Runnable() {
		@Override
		public void run() {
			globals.addRecordToLog("startNewActivity runnable is running");
			Intent i = new Intent(SmartCalendarActivity.this,
					SmartCalendarActivity.class);
			if (eventIdForZoom != null)
				i.putExtra("eventId", eventIdForZoom);
			if (zoomForZoom != null)
				i.putExtra("zoom", zoomForZoom);
			i.putExtra("selectedDay", globals.selectedDay.getTime());
			startActivityForResult(i, RESULT_ZOOM_VIEW);
		}
	};

	void updateTimeMarkerPosition(Date time) {
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) timeMarker
				.getLayoutParams();
		params.topMargin = SmartCalendarFactory.getLocationForTime(this, time);
		timeMarker.setLayoutParams(params);
	}

	void updateView() {
		updateView(false);
	}

	synchronized void updateView(boolean justUpdateTimesInfo) {
		try {
			globals.addRecordToLog("updateView "
					+ Boolean.toString(justUpdateTimesInfo));
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			SmartCalendarFactory.setZoom(zoom);
			Date curDate = new Date();
			smallEventActiveView = null;
			if (globals.selectedDay == null)
				globals.selectedDay = new Date();

			// cancel timer
			try {
				mHandler.removeCallbacks(mUpdateTimeTask);
			} catch (Exception e) {
				globals.addRecordToLog("exception when trying to cancel timer in updateView");
			}
			globals.addRecordToLog("updateView - timer canceled");
			SmartCalendarFactory.recalculate(this);

			if (globals.isEqualDate(curDate, globals.selectedDay)) {
				updateTimeMarkerPosition(curDate);
				timeMarker.setVisibility(View.VISIBLE);
				timeTitleString.setVisibility(View.VISIBLE);
				timeTitleString.setText(DateFormat.format("kk:mm", curDate)
						.toString());
			} else {
				timeMarker.setVisibility(View.GONE);
				timeTitleString.setVisibility(View.GONE);
			}
			dateTitleString.setText(DateFormat.format("EEEE d MMM",
					globals.selectedDay).toString());
			if (!justUpdateTimesInfo) {
				globals.addRecordToLog("updateView - remove events views");
				// remove all events from page
				for (View v : activeEventsViews) {
					SmartCalendarFactory.removeEventView(this, v);
				}
				activeEventsViews.clear();
				//
				SmartCalendarFactory.updateTimeCircleIcon(this);
				globals.addRecordToLog("updateView - circles updated");
				// get all events for day
				Date startTimeForEvent = globals.getBeginOfDayPlusSeconds(
						globals.selectedDay, 0);
				ArrayList<SmartCalendarEvent> allDayEvents = EventsManager
						.getEventsForPeriod(startTimeForEvent,
								globals.MINUTES_PER_DAY - 1);
				while (allDayEvents.size() > 0) {
					SmartCalendarEvent mainEvent = allDayEvents.get(0);
					startTimeForEvent.setHours(mainEvent.getStartTime()
							.getHours());
					int minutes = (mainEvent.getStartTime().getMinutes() / zoom)
							* zoom;
					startTimeForEvent.setMinutes(minutes);
					ArrayList<SmartCalendarEvent> periodEvents = EventsManager
							.getEventsForPeriod(startTimeForEvent, zoom);
					if (periodEvents.size() > 1) {
						ArrayList<SmartCalendarEvent> tmpPeriodEvents = new ArrayList<SmartCalendarEvent>(
								0);
						for (SmartCalendarEvent e : periodEvents) {
							if (EventsManager.getEventPeriod(e) >= zoom * 2
									|| globals
											.isTimeAfter(
													e.getFinishTime(),
													startTimeForEvent,
													zoom
															* globals.MILLISECONDS_IN_1_MINUTES)) {
								activeEventsViews.add(SmartCalendarFactory
										.addEventToPage(this, e,
												e.getStartTime(),
												layoutInflater));
								continue;
							}
							tmpPeriodEvents.add(e);
						}
						if (tmpPeriodEvents.size() > 0) {
							SmallEventReturnType results = SmartCalendarFactory
									.addSetOfEventsToPage(this,
											tmpPeriodEvents, startTimeForEvent,
											layoutInflater);
							activeEventsViews.add(results.mainView);
							if (globals.isTimeAfter(curDate, startTimeForEvent)
									&& !globals
											.isTimeAfter(
													curDate,
													startTimeForEvent,
													zoom
															* globals.MILLISECONDS_IN_1_MINUTES)) {
								SmartCalendarEvent eventActive = SmartCalendarFactory
										.getActiveEvent(tmpPeriodEvents,
												curDate);
								if (eventActive != null) {
									for (View v : results.smallEventsViews) {
										if (((SmartCalendarEvent) v.getTag())
												.getId() == eventActive.getId()) {
											smallEventActiveView = v;
											break;
										}
									}
								}
							}
						}
					} else if (periodEvents.size() == 1) {
						SmartCalendarEvent event = periodEvents.get(0);
						Date tmpDate = new Date();
						tmpDate.setTime(startTimeForEvent.getTime() + zoom * 2
								* globals.MILLISECONDS_IN_1_MINUTES);
						if (EventsManager.getEventPeriod(event) < zoom * 2) {
							SmallEventReturnType results = SmartCalendarFactory
									.addSetOfEventsToPage(this, periodEvents,
											startTimeForEvent, layoutInflater);
							activeEventsViews.add(results.mainView);
							if (globals.isTimeAfter(curDate, startTimeForEvent)
									&& !globals
											.isTimeAfter(
													curDate,
													startTimeForEvent,
													zoom
															* globals.MILLISECONDS_IN_1_MINUTES)) {
								SmartCalendarEvent eventActive = SmartCalendarFactory
										.getActiveEvent(periodEvents, curDate);
								if (eventActive != null) {
									for (View v : results.smallEventsViews) {
										if (((SmartCalendarEvent) v.getTag())
												.getId() == eventActive.getId()) {
											smallEventActiveView = v;
											break;
										}
									}
								}
							}
						} else
							activeEventsViews.add(SmartCalendarFactory
									.addEventToPage(this, periodEvents.get(0),
											event.getStartTime(),
											layoutInflater));
					}
					for (SmartCalendarEvent e : periodEvents)
						allDayEvents.remove(e);
				}

			}
			globals.addRecordToLog("updateView - update event views");
			// update event view
			if (globals.isEqualDate(curDate, globals.selectedDay)) {
				for (View v : activeEventsViews) {
					SmartCalendarFactory.updateEventView(this, v, curDate);
				}
			}
			globals.addRecordToLog("updateView - update time circles");
			// set right pictures for timecircles
			SmartCalendarFactory.updateTimeCircleViews(this,
					globals.selectedDay);
			// runOnUiThread(updateViewRunnable);
			if (eventToScroll != null
					&& EventsManager.isEventOnDate(eventToScroll,
							globals.selectedDay)) {
				globals.addRecordToLog("updateView - scrollToEvent was called");
				SmartCalendarFactory.scrollToEvent(this, eventToScroll);
				eventToScroll = null;
			} else {
				globals.addRecordToLog("updateView - scrollToEvent was not called");
			}

			if (!justUpdateTimesInfo) {
				int week_day = globals.selectedDay.getDay();
				if (week_day == 0) {
					calendarBkgPlace.setBackgroundDrawable(this.getResources()
							.getDrawable(R.drawable.calendar_bgd_1));
					calendarSeparatorPlace.setBackgroundColor(Color
							.parseColor("#ff0000"));
				} else if (week_day == 1) {
					calendarBkgPlace.setBackgroundDrawable(this.getResources()
							.getDrawable(R.drawable.calendar_bgd_2));
					calendarSeparatorPlace.setBackgroundColor(Color
							.parseColor("#00ff00"));

				} else if (week_day == 2) {
					calendarBkgPlace.setBackgroundDrawable(this.getResources()
							.getDrawable(R.drawable.calendar_bgd_3));
					calendarSeparatorPlace.setBackgroundColor(Color
							.parseColor("#d2f00"));

				} else if (week_day == 3) {
					calendarBkgPlace.setBackgroundDrawable(this.getResources()
							.getDrawable(R.drawable.calendar_bgd_4));
					calendarSeparatorPlace.setBackgroundColor(Color
							.parseColor("#ff00ed"));

				} else if (week_day == 4) {
					calendarBkgPlace.setBackgroundDrawable(this.getResources()
							.getDrawable(R.drawable.calendar_bgd_5));
					calendarSeparatorPlace.setBackgroundColor(Color
							.parseColor("#ce0a76"));

				} else if (week_day == 5) {
					calendarBkgPlace.setBackgroundDrawable(this.getResources()
							.getDrawable(R.drawable.calendar_bgd_6));
					calendarSeparatorPlace.setBackgroundColor(Color
							.parseColor("#ff1d1a"));

				} else if (week_day == 6) {
					calendarBkgPlace.setBackgroundDrawable(this.getResources()
							.getDrawable(R.drawable.calendar_bgd_7));
					calendarSeparatorPlace.setBackgroundColor(Color
							.parseColor("#ff0000"));
					 

				}
				calendarBkgPlace.setPadding(0, 0, 0, 0);
			}

			int secondsToRun = 60 - curDate.getSeconds();
			globals.addRecordToLog("updateView - reschedule timer to "
					+ secondsToRun + " seconds");
			// reschedule timer
			mHandler.removeCallbacks(mUpdateTimeTask);
			mHandler.postDelayed(mUpdateTimeTask, secondsToRun * 1000);
			globals.addRecordToLog("updateView - finished");
		} catch (Exception e) {
			globals.addRecordToLog("updateView ex: " + e.getMessage());
		}
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			updateView();
		}
	};

	public View.OnClickListener showEventClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			globals.addRecordToLog("showEventClickListener");
			Intent i = new Intent(SmartCalendarActivity.this,
					EventFunctionsDialog.class);
			i.putExtra("eventId", ((SmartCalendarEvent) v.getTag()).getId());
			i.putExtra("selectedDay", globals.selectedDay.getTime());
			startActivityForResult(i, RESULT_CHANGE_EVENT);
		}
	};

	public View.OnClickListener smallEventClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			globals.addRecordToLog("smallEventClickListener");
			eventIdForZoom = ((SmartCalendarEvent) v.getTag()).getId();
			SmartCalendarEvent event = new SmartCalendarEvent(
					EventsManager.getEvent(eventIdForZoom));
			if (zoom == 15) {
				if (EventsManager.getEventPeriod(event) > 5)
					zoomForZoom = 5;
				else
					zoomForZoom = 1;
			} else if (zoom == 5)
				zoomForZoom = 1;
			else
				return;

			progressDialog = TransparentProgressDialog
					.show(SmartCalendarActivity.this,
							"",
							SmartCalendarActivity.this
									.getString(R.string.pleaseWaitGeneratingView),
							true);

			Thread thread = new Thread(null, createViewThread,
					"create view thread");
			thread.start();
		}
	};

	public View.OnLongClickListener smallEventLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			globals.addRecordToLog("smallEventLongClickListener");
			Intent i = new Intent(SmartCalendarActivity.this,
					EventFunctionsDialog.class);
			i.putExtra("eventId", ((SmartCalendarEvent) v.getTag()).getId());
			i.putExtra("selectedDay", globals.selectedDay.getTime());
			startActivityForResult(i, RESULT_CHANGE_EVENT);
			return true;
		}
	};

	public View.OnClickListener headerTimeDateClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			globals.addRecordToLog("headerTimeDateClickListener");
			globals.selectedDay.setTime((new Date()).getTime());
			updateView();
			SmartCalendarFactory.scrollToTime(SmartCalendarActivity.this,
					globals.selectedDay);
		}
	};

	public View.OnLongClickListener headerTimeDateLongClickListener = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			globals.addRecordToLog("headerTimeDateLongClickListener");
			showDatePickerDlg();
			return true;
		}
	};

	public View.OnClickListener headerBtnsClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			globals.addRecordToLog("headerBtnsClickListener");
			if (v.equals(btnDayBack)) {
				globals.selectedDay.setTime(globals.selectedDay.getTime()
						- globals.MILLISECONDS_IN_DAY);
			} else
				globals.selectedDay.setTime(globals.selectedDay.getTime()
						+ globals.MILLISECONDS_IN_DAY);
			updateView();
		}
	};

	private void showDatePickerDlg() {
		globals.addRecordToLog("showDatePickerDlg");
		CustomAlertDialog.Builder alt_bld = new CustomAlertDialog.Builder(
				SmartCalendarActivity.this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_picker, null);
		alt_bld.setContentView(view);
		datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		datePicker.init(globals.selectedDay.getYear() + 1900,
				globals.selectedDay.getMonth(), globals.selectedDay.getDate(),
				this);
		// EditText textview = (EditText)
		// datePicker.findViewById(Resources.getSystem().getIdentifier("timepicker_input",
		// "id", "android"));
		// textview.setTextColor(Color.GREEN);

		alt_bld.setTitle(getString(R.string.setDate))
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Date newDate = new Date(datePicker.getYear() - 1900,
								datePicker.getMonth(), datePicker
										.getDayOfMonth());
						globals.selectedDay = newDate;
						updateView();
					}
				})
				.setNegativeButton(getString(R.string.Cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
		alt_bld.show();
	}

	@Override
	public void onDateChanged(android.widget.DatePicker view, int year,
			int monthOfYear, int dayOfMonth) {
		// TODO Auto-generated method stub

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		globals.addRecordToLog("onActivityResult "
				+ Integer.toString(requestCode) + ","
				+ Integer.toString(resultCode));
		switch (requestCode) {
		case RESULT_ADD_EVENT:
		case RESULT_CHANGE_EVENT:
			if (resultCode == RESULT_OK) {
				if (data != null)
					eventToScroll = EventsManager.getEvent(data.getExtras()
							.getInt("eventId"));
			}
			break;
		case RESULT_SHOW_ALL:
			break;
		case RESULT_ABOUT:
			break;
		case RESULT_SETTINGS:
			break;
		case RESULT_ZOOM_VIEW:
			if (progressDialog != null && progressDialog.isShowing())
				progressDialog.cancel();
			break;
		}
		updateView();
	}

	private final Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			globals.addRecordToLog("messageHandler - timerfunction");
			switch (msg.what) {
			case UPDATE_VIEW_BY_TIMER:
				globals.addRecordToLog("messageHandler - UPDATE_VIEW_BY_TIMER");
				Date curDate = new Date();
				if (globals.isEqualDate(curDate, globals.selectedDay)) {
					if (globals.isTimeAfter(curDate, globals.selectedDay))
						updateView(true);
				}
				break;
			}
		}
	};

	@Override
	public OnClickListener getSmallEventClickListener() {
		return smallEventClickListener;
	}

	@Override
	public OnLongClickListener getSmallEventLongClickListener() {
		return smallEventLongClickListener;
	}

	@Override
	public OnClickListener getShowEventClickListener() {
		return showEventClickListener;
	}

}