package org.emphie.fod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class FoDActivity extends Activity {
	SharedPreferences preferences;
	private Button send_insult;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Initialise preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (isdebug(this)) {

		}
		send_insult = (Button) findViewById(R.id.send_insult);

		send_insult.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (preferences.getBoolean("send_SMS", true)) {
					sendSMS(preferences.getString("SMS_number",
							getString(R.string.dawsons_number)), preferences
							.getString("SMS_message",
									getString(R.string.SMS_message)));
				} else {
					Toast.makeText(getBaseContext(),
							getString(R.string.SMS_message), Toast.LENGTH_SHORT)
							.show();
				}
				;
			}
		});
		
/*		if (preferences.getBoolean("send_SMS", true)) {
			send_insult.setText(R.string.send_insult);
		} else {
			send_insult.setText(R.string.view_insult);
		}*/
		
		Button victim_jpg = (Button) findViewById(R.id.victim_jpg);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_title);
		builder.setMessage(R.string.dialog_text);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.setNegativeButton(android.R.string.cancel, null);
		victim_jpg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				builder.show();
			}
		});
	}
	


	@Override
	public void onResume() {
		super.onResume();
		if (preferences.getBoolean("send_SMS", true)) {
			send_insult.setText(R.string.send_insult);
		} else {
			send_insult.setText(R.string.view_insult);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_prefs:
			updatePrefs();
			break;
		case R.id.menu_about:
			ShowAbout();
			break;
		/*
		 * case R.id.menu_manage: Intent i = new Intent(this, Insults.class);
		 * startActivity(i); break;
		 */
		default:
			break;
		}

		return true;
	}

	// ---sends an SMS message to another device---
	// from http://mobiforge.com/developing/story/sms-messaging-android
	private void sendSMS(String phoneNumber, String message) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU",
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// ---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered",
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	}

	public void updatePrefs() {
		/*
		 * Show the preferences menu and let the user select their options
		 */
		// Launch Preference activity
		Intent i = new Intent(FoDActivity.this, preferences.class);
		i.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
				"org.emphie.fod.preferences$prefFrag1");
		i.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
		startActivity(i);

		// Some feedback to the user
		// Toast.makeText(FoDActivity.this, "Enter your user credentials.",
		// Toast.LENGTH_LONG).show();
		// prefs.
		/*
		 * Editor edit = preferences.edit(); String username =
		 * preferences.getString("username", "n/a"); // We will just revert the
		 * current user name and save again StringBuffer buffer = new
		 * StringBuffer(); for (int i = username.length() - 1; i >= 0; i--) {
		 * buffer.append(username.charAt(i)); } edit.putString("username",
		 * buffer.toString()); edit.commit(); // A toast is a view containing a
		 * quick little message for the // user. We give a little feedback
		 * Toast.makeText(this, "Reverted string sequence of user name.",
		 * Toast.LENGTH_LONG).show();
		 */
	}

	public void ShowAbout() {
		/*
		 * Pop a custom alert dialog with the app details
		 */
		AlertDialog.Builder about_dialog_builder;
		LayoutInflater about_inflater;
		View about_view;
		ImageView image;
		TextView about_text;
		TextView about_version;
		TextView about_copyright;
		Button about_button;
		final AlertDialog about_dialog;

		about_dialog_builder = new AlertDialog.Builder(this);

		about_inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		about_view = about_inflater.inflate(R.layout.about,
				(ViewGroup) findViewById(R.id.about_root));

		// set the custom dialog components
		about_version = (TextView) about_view.findViewById(R.id.about_version);
		try {
			about_version
					.setText(getText(R.string.version_desc)
							+ " "
							+ getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		image = (ImageView) about_view.findViewById(R.id.image);
		image.setImageResource(R.drawable.ic_launcher);

		// following line: option if not wrapping text in a scrollview
		// about_text.setMovementMethod(new ScrollingMovementMethod());
		about_text = (TextView) about_view.findViewById(R.id.about_text);
		about_text.setText(R.string.about_text);

		about_copyright = (TextView) about_view
				.findViewById(R.id.about_copyright);
		about_copyright.setText(R.string.about_copyright);

		about_button = (Button) about_view.findViewById(R.id.about_ok_button);

		about_dialog_builder.setView(about_view);
		about_dialog = about_dialog_builder.create();
		about_dialog.setTitle(R.string.app_name_long);

		// if button is clicked, close the custom dialog
		about_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				about_dialog.dismiss();
			}
		});

		about_dialog.show();

	}

	public static boolean isdebug(Activity context) {
		boolean debug = false;
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					context.getApplication().getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageInfo != null) {
			int flags = packageInfo.applicationInfo.flags;
			if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
				debug = true;
			} else
				debug = false;
		}
		return debug;
	}
}
