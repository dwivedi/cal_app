package se.smartkalender.dialogs;

import java.io.IOException;
import java.util.Date;

import se.smartkalender.EventDetailsActivity;
import se.smartkalender.EventsManager;
import se.smartkalender.SmartCalendarActivity;
import se.smartkalender.globals;
import se.smartkalender.types.SmartCalendarEvent;

import se.smartkalender.R;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class EventNotificationDialog extends Activity {
	MediaPlayer mediaPlayer = null;
	PowerManager.WakeLock wl = null;
	KeyguardManager.KeyguardLock kl = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aaaaa);

		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		/*
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
		 * WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
		 * WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
		 * WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN |
		 * WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
		 * WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
		 * WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		 */

		SmartCalendarEvent event = (SmartCalendarEvent) getIntent()
				.getSerializableExtra("SELECTED_EVENT");
		final int eventId = event.getId();

		// SmartCalendarEvent event = new
		// SmartCalendarEvent(EventsManager.getEvent(getIntent().getExtras().getInt("eventId")));

		TextView title = (TextView) findViewById(R.id.title);
		title.setText("Event \"" + event.getName() + "\"");

		TextView message = (TextView) findViewById(R.id.message);

	/*	ImageView eventIcon = (ImageView) findViewById(R.id.imageViewEventIcon);
		try {
			if (event.getYourImageFlag()) {
				try {
					eventIcon.setImageBitmap(globals.getBitmapFromResurce(event
							.getYourImagePath()));
				} catch (Exception e) {
					eventIcon.setImageResource(R.drawable.application_icon);
					e.printStackTrace();
				}
			} else {
				eventIcon.setImageDrawable(getResources().getDrawable(
						globals.getIconId(EventNotificationDialog.this,
								event.getIconId())));

			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			eventIcon.setImageResource(R.drawable.application_icon);
		}*/

		Date currentTime = new Date();
		if (event.getStartTime().before(currentTime))
			message.setText(globals.TheEventWillStartNow);
		else {
			long minutesBeforeEvent = (event.getStartTime().getTime() - currentTime
					.getTime()) / globals.MILLISECONDS_IN_1_MINUTES;
			message.setText(globals.TheEventWillStartIn + " "
					+ Long.toString(minutesBeforeEvent) + " " + globals.minutes);
		}
		// use PowerManager to on screen and KeyguardManager to disable keyguard
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.FULL_WAKE_LOCK, "smartcalendar");
		wl.acquire();
		/*
		 * KeyguardManager km = (KeyguardManager)
		 * getSystemService(KEYGUARD_SERVICE); kl =
		 * km.newKeyguardLock("smartcalendar"); kl.disableKeyguard();
		 */

		Button btnGoToCalendar = (Button) findViewById(R.id.positiveButton);
		btnGoToCalendar.setText(getString(R.string.goToCalendar));
		btnGoToCalendar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(EventNotificationDialog.this,
						SmartCalendarActivity.class);
				i.putExtra("eventId", eventId);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(i, 0);
				// if (kl != null) kl.reenableKeyguard();
				if (wl != null && wl.isHeld())
					wl.release();
				if (mediaPlayer != null)
					mediaPlayer.stop();
			}
		});

		/*
		 * Button btnShowDetails = (Button) findViewById(R.id.neutralButton);
		 * btnShowDetails.setText(getString(R.string.showDetails));
		 * btnShowDetails.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { Intent intent = new
		 * Intent(EventNotificationDialog.this, EventDetailsActivity.class);
		 * intent.putExtra("eventId", eventId); intent.putExtra("readOnly",
		 * true); startActivityForResult(intent, 1); //if (kl != null)
		 * kl.reenableKeyguard(); if (wl != null && wl.isHeld()) wl.release();
		 * if (mediaPlayer != null) mediaPlayer.stop(); } });
		 */
		Button btnSnooze = (Button) findViewById(R.id.negativeButton);
		btnSnooze.setText(getString(R.string.snooze));
		btnSnooze.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// if (kl != null) kl.reenableKeyguard();
				if (wl != null && wl.isHeld())
					wl.release();
				if (mediaPlayer != null)
					mediaPlayer.stop();
				EventsManager.setAlarmForFirstEvent(
						EventNotificationDialog.this, true);
				finish();
			}
		});
		try {
			playSound();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		// if (wl != null && wl.isHeld()) wl.release();
		// if (mediaPlayer != null && mediaPlayer.isPlaying())
		// mediaPlayer.stop();
		// EventsManager.setAlarmForFirstEvent(EventNotificationDialog.this,
		// true);
		// finish();
	}

	void playSound() throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(this, alert);
		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mediaPlayer.setLooping(true);
			mediaPlayer.prepare();
			mediaPlayer.start();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode, data);
		finish();
	}

}
