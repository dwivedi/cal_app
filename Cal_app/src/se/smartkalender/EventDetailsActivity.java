package se.smartkalender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import se.smartkalender.dialogs.ColorPickerActivity;
import se.smartkalender.dialogs.CustomAlertDialog;
import se.smartkalender.dialogs.TemplateCategoriesDialog;
import se.smartkalender.listviews.CustomListView;
import se.smartkalender.listviews.EventIconPOJO;
import se.smartkalender.listviews.EventImageAdapter;
import se.smartkalender.listviews.IconsArrayAdapter;
import se.smartkalender.listviews.YourImageIconAdapter;
import se.smartkalender.types.SmartCalendarEvent;
import se.smartkalender.widgets.DatePicker;
import se.smartkalender.widgets.TimePicker;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetailsActivity extends Activity implements   View.OnClickListener {
	private static final int RESULT_PLAN_BACKWARD = 0;
	private static final int RESULT_PLAN_FORWARD = 1;
	private static final int PICK_COLOR_REQUEST = 2;
	private static final int PICK_CAMARA_REQUEST = 3;
	private static final int PICK_GALLARE_REQUEST = 4;
	public static final int DEFULT_ICON_VIEW_TYPE = 0;
	public static final int YOUR_IMAGE_ICON_VIEW_TYPE = 1;
	
	private SmartCalendarEvent event;
	private boolean readOnly;
	private LinearLayout eventColor;
	private ImageView eventIcon;
	EditText startTime;
	EditText finishTime;
	EditText reminderTime;
	EditText eventName;
	TextView evenDate;
	EditText repeatDays;
	EditText eventDescript;
	Button btnSave;
	//Button planBackward;
	//Button planForward;
	Button btnBack;
	DatePicker datePicker;	
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_details);		
		// get event
		event = new SmartCalendarEvent(EventsManager.getEvent(getIntent().getExtras().getInt("eventId")));
		readOnly = getIntent().getExtras().getBoolean("readOnly");
		if (getIntent().getExtras().getBoolean("planBackward")) {
			Long startTime = getIntent().getExtras().getLong("startTime");
			long period = event.getFinishTime().getTime() - event.getStartTime().getTime();
			event.getFinishTime().setTime(startTime);
			event.getStartTime().setTime(startTime - period);	
		}
		else if (getIntent().getExtras().getBoolean("planForward")) {
			Long startTime = getIntent().getExtras().getLong("startTime");
			long period = event.getFinishTime().getTime() - event.getStartTime().getTime();
			event.getStartTime().setTime(startTime);	
			event.getFinishTime().setTime(startTime + period);	
		}	
		if (getIntent().getExtras().containsKey("selectedDay")) {
			Long startTime = getIntent().getExtras().getLong("selectedDay");
			event.getDate().setTime(startTime);			
		}
		eventIcon = (ImageView) findViewById(R.id.eventIcon); 
        eventColor = (LinearLayout) findViewById(R.id.eventColor); 	
		eventName = (EditText) findViewById(R.id.eventName); 		
		startTime = (EditText) findViewById(R.id.startTime); 
		finishTime = (EditText) findViewById(R.id.finishTime); 
		reminderTime = (EditText) findViewById(R.id.reminderTime); 
		evenDate = (TextView) findViewById(R.id.evenDate); 
		repeatDays = (EditText) findViewById(R.id.repeatDays); 
		eventDescript = (EditText) findViewById(R.id.eventDescript); 
		btnSave = (Button) findViewById(R.id.btnSave);
		//planBackward = (Button) findViewById(R.id.planBackward);
		//planForward = (Button) findViewById(R.id.planForward);
		btnBack = (Button) findViewById(R.id.btnBack);
		Button btnSetDate = (Button) findViewById(R.id.btnSetDate);
		LinearLayout placeForButtons = (LinearLayout) findViewById(R.id.placeForButtons); 
		updateView();
		
		btnBack.setOnClickListener( new View.OnClickListener() {
		        public void onClick(View v) {
		        	finish();		        	
		        }
		});
				
		if (readOnly) {
			placeForButtons.setVisibility(View.GONE);	
			btnSetDate.setVisibility(View.GONE);
			eventName.setEnabled(false);
			startTime.setEnabled(false);
			finishTime.setEnabled(false);
			repeatDays.setEnabled(false);
			reminderTime.setEnabled(false);
			eventDescript.setEnabled(false);
		}
		if (! readOnly) {
			btnSave.setOnClickListener(btnSaveClickListener);
			//planBackward.setOnClickListener(planBackwardClickListener);
			//planForward.setOnClickListener(planForwardClickListener);
			eventIcon.setOnClickListener(eventIconClickListener);
			eventColor.setOnClickListener(this);
			startTime.setOnClickListener(startTimeClickListener);
			finishTime.setOnClickListener(startTimeClickListener);
			reminderTime.setOnClickListener(startTimeClickListener);			
			repeatDays.setOnClickListener( new View.OnClickListener() {
		        public void onClick(View v) {
		        	final CustomListView daysList = new CustomListView(EventDetailsActivity.this);
		            daysList.setAdapter(new ArrayAdapter<String>(EventDetailsActivity.this, R.layout.list_item_single_choise, globals.adjustFirstDayOfWeek(globals.weekDaysFull)));
		            daysList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);	   
		            SparseBooleanArray checkedItemPositions = daysList.getCheckedItemPositions();
		            for (int i = 0; i < globals.weekDays.length; i++) {
		                if (event.getRepeatDays().contains(globals.adjustFirstDayOfWeek(globals.weekDays)[i]))
		                    checkedItemPositions.put(i, true);
		            }  
		            
		            saveTextInTextBoxes();
		            
		        	CustomAlertDialog.Builder alt_bld = new CustomAlertDialog.Builder(EventDetailsActivity.this);
		        	alt_bld.setContentView(daysList);
		            alt_bld.setTitle(
		            		getString(R.string.repeatDays))
		                    .setPositiveButton("OK",
		                    		new DialogInterface.OnClickListener() {
		                                public void onClick(DialogInterface dialog, int id) {
		                                	 SparseBooleanArray checkedItemPositions = daysList.getCheckedItemPositions();
		                                	 event.setRepeatDays("");
		                                     int size = checkedItemPositions.size();	                                     
		                                     if (size > 0) {
		                                         for (int i = 0; i < checkedItemPositions.size(); i++) {
		                                             if (!checkedItemPositions.valueAt(i)) continue;
		                                             String day = globals.adjustFirstDayOfWeek(globals.weekDays)[(checkedItemPositions.keyAt(i))];
		                                             event.addRepeatDay(day);
		                                         }
		                                     } 
		                                     updateView();
		                                }
		                            }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
		                                public void onClick(DialogInterface dialog, int id) {

		                                }
		                            });
		            alt_bld.show();     	        	
		        }
		    });	
			TableRow tablerowSetDate = (TableRow) findViewById(R.id.tablerowSetDate);
           	tablerowSetDate.setOnClickListener(new OnClickListener() {
	                public void onClick(View v) {
	                	showDatePickerDlg();                	
	                }
	        });       	   	
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.eventColor:
	        	saveTextInTextBoxes();
	        	
	            Intent i = new Intent(this, ColorPickerActivity.class);
	            Bundle b = new Bundle();
	            b.putInt("color", event.getColor());
	            i.putExtras(b);
	            startActivityForResult(i, PICK_COLOR_REQUEST);

				break;
		}
		
	}

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	 
		Bitmap photo = null;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PICK_COLOR_REQUEST:

				  Bundle b = data.getExtras();
				    int mColor = b.getInt("color");
	    			event.setColor(mColor);
	    			updateView();	
				break;
			case PICK_CAMARA_REQUEST:

				photo = (Bitmap) data.getExtras().get("data");
				eventIcon.setImageBitmap(photo);
				break;

			case PICK_GALLARE_REQUEST:

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				photo = BitmapFactory.decodeFile(filePath);
				event.setYourImageFlag(true);
				eventIcon.setImageBitmap(photo);
				event.setYourImagePath(filePath);
				event.setIconId("N/A");
				showYourImageListViewDialogForAdd(filePath);
				updateView();
				break;

			default:
				break;
			}
		}
    }

   	private void showYourImageListViewDialogForAdd(final String filePath) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Want to add in Your Image List ?");
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
				
				addYourImagePathInLocalStorage(filePath);
				
				
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	protected void addYourImagePathInLocalStorage(String filePath) { 
		
		SharedPreferences pref = getSharedPreferences("Dwivedi_Pref", 0);
		String paths = pref.getString("Paths", "NoN");
		Editor editor = pref.edit();
		if (paths.equalsIgnoreCase("NoN")) {
			editor.putString("Paths", filePath);
		} else {
			editor.putString("Paths", paths+","+filePath);
		}
		editor.commit();
		
 	}
	
	


	/*
    
	;public View.OnClickListener eventColorClickListener = new View.OnClickListener() {
        public void onClick(View v) {
   	
        	
    		AmbilWarnaDialog dialog = new AmbilWarnaDialog(EventDetailsActivity.this, event.getColor(), new OnAmbilWarnaListener()
    		{
    		@Override
    		public void onCancel(AmbilWarnaDialog dialog) {
    		}
    		@Override
    		public void onOk(AmbilWarnaDialog dialog, int color) {
    			event.setColor(color);
    			updateView();	
    		}
    		
    		});
    		dialog.show();       	        	
    		
        }
    };
    */
	public View.OnClickListener eventIconClickListener = new View.OnClickListener() {
        public void onClick(View v) {
    /*    	saveTextInTextBoxes();
        	
        
        	
        	final CustomListView iconsList = new CustomListView(EventDetailsActivity.this);       	
            iconsList.setAdapter(new IconsArrayAdapter(EventDetailsActivity.this, R.layout.icon_list_item, globals.iconsIds));
            iconsList.setChoiceMode(ListView.CHOICE_MODE_NONE);	   
                       
        	CustomAlertDialog.Builder alt_bld = new CustomAlertDialog.Builder(EventDetailsActivity.this);
        	alt_bld.setContentView(iconsList);
            alt_bld.setTitle(getString(R.string.chooseIcon));
            final CustomAlertDialog dlg = alt_bld.create();     
            dlg.show();
            
        	OnItemClickListener itemClickListener = new OnItemClickListener() {
        		@Override
        		public void onItemClick(AdapterView<?> a, View v, int position,
        				long id) {
        			event.setIconId(globals.getIconPath((Integer)v.getTag()));
        			updateView();		
        			dlg.dismiss();
        		}

        	};	
        	iconsList.setOnItemClickListener(itemClickListener);*/
        	
       	
        	saveTextInTextBoxes();
        	
         	LayoutInflater inflater = LayoutInflater.from(EventDetailsActivity.this);
        	View view = inflater.inflate(R.layout.dwivedi_change_icon_dialog_view, null);
        
        	
        	ArrayList<EventIconPOJO> eventIconPOJOs = new ArrayList<EventIconPOJO>();
        	
        	final CustomListView iconsList = (CustomListView) view.findViewById(R.id.listViewIconList);    	
          //  iconsList.setAdapter(new IconsArrayAdapter(EventDetailsActivity.this, R.layout.icon_list_item, globals.iconsIds));
            int size = globals.iconsIds.size();
            for (int i = 0; i < size; i++) {
            	EventIconPOJO eventIconPOJO = new EventIconPOJO();
            	eventIconPOJO.setIconId(globals.iconsIds.get(i));
            	eventIconPOJO.setViewType(DEFULT_ICON_VIEW_TYPE);
            	eventIconPOJOs.add(eventIconPOJO);
			}
            size = globals.iconsPath.size();
            for (int i = 0; i < size; i++) {
            	EventIconPOJO eventIconPOJO = new EventIconPOJO();
            	eventIconPOJO.setImagePath(globals.iconsPath.get(i));
            	eventIconPOJO.setViewType(YOUR_IMAGE_ICON_VIEW_TYPE);
            	eventIconPOJOs.add(eventIconPOJO);
			}
            
            iconsList.setChoiceMode(ListView.CHOICE_MODE_NONE);	
            
            EventImageAdapter eventImageAdapter = new EventImageAdapter(EventDetailsActivity.this,eventIconPOJOs);
            iconsList.setAdapter(eventImageAdapter);
          /*	final CustomListView yourImageIconListView = (CustomListView) view.findViewById(R.id.listViewIconListYourImages);    	
        	yourImageIconListView.setAdapter(new YourImageIconAdapter(EventDetailsActivity.this, R.layout.icon_list_item, globals.iconsPath));
        	//yourImageIconListView.setChoiceMode(ListView.CHOICE_MODE_NONE);	
        	globals.setListViewHeightBasedOnChildren(yourImageIconListView);
        	TextView yourImageIconTitleTV = (TextView) view.findViewById(R.id.tvListYourImagesTitle);    	
        	if (yourImageIconListView.getAdapter().getCount()==0) 
        		yourImageIconTitleTV.setVisibility(View.GONE);
			else 
				yourImageIconTitleTV.setVisibility(View.VISIBLE);
   
            */
                       
        	CustomAlertDialog.Builder alt_bld = new CustomAlertDialog.Builder(EventDetailsActivity.this);
        	alt_bld.setContentView(view);
            alt_bld.setTitle(getString(R.string.chooseIcon));
            final CustomAlertDialog dlg = alt_bld.create();     
            dlg.show();
            
        	OnItemClickListener itemClickListener = new OnItemClickListener() {
        		@Override
        		public void onItemClick(AdapterView<?> a, View v, int position,
        				long id) {
        			
        			
        			EventIconPOJO eventIconPOJO = (EventIconPOJO)a.getItemAtPosition(position);
        			if (eventIconPOJO.getViewType() == DEFULT_ICON_VIEW_TYPE) {
        				String abcdf = globals.getIconPath((Integer)v.getTag());
        				event.setIconId(globals.getIconPath((Integer)v.getTag()));
            			event.setYourImageFlag(false);
					} else {
						String filePath = eventIconPOJO.getImagePath();
						Bitmap photo = BitmapFactory.decodeFile(filePath);
						event.setYourImageFlag(true);
						eventIcon.setImageBitmap(photo);
						event.setYourImagePath(filePath);
						event.setIconId("N/A");
					}
        		 
        			updateView();		
        			dlg.dismiss();
        		}

        	};	
        	iconsList.setOnItemClickListener(itemClickListener);
        	
        	final Button changeButton = (Button) view.findViewById(R.id.btnYourImage);   
        	changeButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

		        	
		        	Toast.makeText(getApplicationContext(), "change Image", Toast.LENGTH_LONG).show();
		        	
		        	AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailsActivity.this);
		    		builder.setTitle("Your Image");
		    		/*builder.setPositiveButton("Take Photo",
		    				new DialogInterface.OnClickListener() {

		    					@Override
		    					public void onClick(DialogInterface dialog, int which) {

		    						Intent cameraIntent = new Intent(
		    								android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    						startActivityForResult(cameraIntent, PICK_CAMARA_REQUEST);
		    						dialog.dismiss();
		    						dlg.dismiss();
		    					}
		    				});*/
		    		builder.setNegativeButton("Choose Existing",
		    				new DialogInterface.OnClickListener() {

		    					@Override
		    					public void onClick(DialogInterface dialog, int which) {
		    						dialog.dismiss();

		    						Intent galleryIntent = new Intent(
		    								Intent.ACTION_PICK,
		    								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		    						startActivityForResult(galleryIntent, PICK_GALLARE_REQUEST);
		    						dlg.dismiss();
		    					}
		    				});

		    		builder.create().show();

		         					
				}
			});

        }
	};
	
	 
	public View.OnClickListener startTimeClickListener = new View.OnClickListener() {
        public void onClick(View v) {
        	showTimePickerDlg(v);
        }
	};
	
	private void saveEvent() {
		saveTextInTextBoxes();
		
		int newid = EventsManager.changeEvent(event.getId(), event, EventDetailsActivity.this);
 		event.setId(newid);
	}
	
	private void saveTextInTextBoxes(){
		event.setName(eventName.getText().toString());
		event.setDescription(eventDescript.getText().toString());
	}

	
	public View.OnClickListener btnSaveClickListener = new View.OnClickListener() {
        public void onClick(View v) {
        	saveEvent();
        	// send result back
        	Intent resultIntent = new Intent();
            resultIntent.putExtra("eventId", event.getId());
        	setResult(RESULT_OK, resultIntent);
        	finish();
        }
	};
	
	public View.OnClickListener planBackwardClickListener = new View.OnClickListener() {
        public void onClick(View v) {
        	saveEvent();
        	Intent intent = new Intent(EventDetailsActivity.this, TemplateCategoriesDialog.class);
            intent.putExtra("planBackward", true);
            intent.putExtra("startTime", event.getStartTime().getTime());
            if (getIntent().getExtras().containsKey("selectedDay")) {
    			intent.putExtra("selectedDay",getIntent().getExtras().getLong("selectedDay"));
    		}
            startActivityForResult(intent, RESULT_PLAN_BACKWARD);
        	finish();
        }
	};
	
	public View.OnClickListener planForwardClickListener = new View.OnClickListener() {
        public void onClick(View v) {
        	saveEvent();
        	Intent intent = new Intent(EventDetailsActivity.this, TemplateCategoriesDialog.class);
        	intent.putExtra("planForward", true);
        	intent.putExtra("startTime", event.getFinishTime().getTime());
        	if (getIntent().getExtras().containsKey("selectedDay")) {
    			intent.putExtra("selectedDay",getIntent().getExtras().getLong("selectedDay"));
    		}
            startActivityForResult(intent, RESULT_PLAN_FORWARD);
        	finish();
        }
	};
	
	void updateView(){
		eventName.setText(event.getName());
		eventColor.setBackgroundColor(event.getColor());
		if (event.getIconId() != null){ 
			if (event.getYourImageFlag()) {
				Bitmap bitmap = BitmapFactory.decodeFile(event.getYourImagePath());
				eventIcon.setImageBitmap(bitmap);
				
 			}else{
 				Integer abcd = globals.getIconId(this, event.getIconId());
			eventIcon.setImageDrawable(getResources().getDrawable(globals.getIconId(this, event.getIconId())));
			
 			}
		}
		 
		startTime.setText(DateFormat.format("kk:mm", event.getStartTime()).toString());
		startTime.setTag(event.getStartTime());
		finishTime.setText(DateFormat.format("kk:mm", event.getFinishTime()).toString());	
		finishTime.setTag(event.getFinishTime());
		startTime.setText(DateFormat.format("kk:mm", event.getStartTime()).toString());
		startTime.setTag(event.getStartTime());	
		if (event.getReminderTime() != null) {
			reminderTime.setText(DateFormat.format("kk:mm", event.getReminderTime()).toString());
			reminderTime.setTag(event.getReminderTime());		
		}
		else {
			reminderTime.setText("");
			reminderTime.setTag(event.getStartTime());		
		}	
		globals.addRecordToLog("Update view month: " + DateFormat.format("EEEE d MMM", event.getDate()).toString());
		Date newDate = event.getDate();
		String weekDay = globals.weekDaysFull[newDate.getDay()];
		int dayInMonth = newDate.getDate();
		String monthName = globals.monthsFull[newDate.getMonth()];
		evenDate.setText(weekDay + " " + dayInMonth + " " + monthName);
		repeatDays.setText(event.getRepeatDays());	
		eventDescript.setText(event.getDesciption());	
	}
	
	
	private void showTimePickerDlg(final View v) {
		saveTextInTextBoxes();
		
		CustomAlertDialog.Builder alt_bld = new CustomAlertDialog.Builder(EventDetailsActivity.this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.time_picker, null);
		alt_bld.setContentView(view);
		final TimePicker timePicker = (TimePicker)view.findViewById(R.id.timePicker);
		timePicker.setCurrentHour(((Date)v.getTag()).getHours());
        timePicker.setCurrentMinute(((Date)v.getTag()).getMinutes());
        timePicker.setIs24HourView(true);
		
	    alt_bld.setTitle(
	    		getString(R.string.setTime))
	            .setPositiveButton("OK",
	            		new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {	
	                        	Date time = (Date)v.getTag();
	                        	long period = event.getFinishTime().getTime() - event.getStartTime().getTime(); 
	                        	time.setHours(timePicker.getCurrentHour());
	                        	time.setMinutes(timePicker.getCurrentMinute());
	                        	if (v == startTime) {
	                        		event.getFinishTime().setTime(event.getStartTime().getTime() + period);
	                        		event.setStartTime(time);
	                        	}
	                        	else if (v == finishTime) {
	                        		event.setFinishTime(time);
	                        	}
	                        	else { 
	                        		// Remindertime
	                        		if( globals.isTimeAfter(event.getStartTime(), time) ){
		                        		int differenceInMinutes = globals.getDifferenceInMinutes(event.getStartTime(), time);
		                        		globals.addRecordToLog("Difference in minutes: " + differenceInMinutes);
										event.setReminder(differenceInMinutes);
	                        		}
	                        		else{
	                        			event.setReminder(0);
	                        		}
	                        	}
	                        	updateView();
	                        }
	                    }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {
	
	                        }
	                    });
	    alt_bld.show();     
	}
	

	private void showDatePickerDlg() {
		saveTextInTextBoxes();
		
		CustomAlertDialog.Builder alt_bld = new CustomAlertDialog.Builder(EventDetailsActivity.this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.date_picker, null);
		alt_bld.setContentView(view);
		datePicker = (DatePicker)view.findViewById(R.id.datePicker);
		datePicker.init(event.getDate().getYear() + 1900, event.getDate().getMonth(), event.getDate().getDate(), this);
		
	    alt_bld.setTitle(
	    		getString(R.string.setDate))
	            .setPositiveButton("OK",
	            		new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {	
	                        	Date date = new Date(datePicker.getYear() - 1900,datePicker.getMonth(),datePicker.getDayOfMonth());	                        	
	                        	globals.addRecordToLog("> Month: " + datePicker.getMonth());
	                        	globals.addRecordToLog(">>> Month: " + date.getMonth());
	                        	event.setDate(date);
	                        	updateView();
	                        }
	                    }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int id) {
	
	                        }
	                    });
	    alt_bld.show();     
	}

	 
	
	 



}
