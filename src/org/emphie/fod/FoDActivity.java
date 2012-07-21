package org.emphie.fod;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class FoDActivity extends Activity {
	SharedPreferences preferences;
	private Button send_insult;
	private String SMS_number;
	// developers phone numbers, base 64 encoded for obfuscation
	public static String[] invalid_numbers;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		invalid_numbers = this.getResources().getStringArray(R.array.invalid_numbers);
		// Initialise preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		send_insult = (Button) findViewById(R.id.send_insult);
		send_insult.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (preferences.getBoolean("just_dawson", true)) {
					SMS_number = getString(R.string.dawsons_number);
				}else{
					SMS_number = preferences.getString("SMS_number", getString(R.string.dawsons_number));
				}
				if (preferences.getBoolean("send_SMS", true)) {
					// first validate the number
					if (valid_victim((String) SMS_number)) {
						if (SMS_number.length() > 0){
							// all good - send it
							sendSMS(SMS_number,	preferences.getString("SMS_message", getString(R.string.SMS_message)));
						}else{
							AlertDialog.Builder builder = new AlertDialog.Builder(FoDActivity.this);
							builder.setTitle(R.string.bad_number_title);
							builder.setMessage(R.string.bad_number_message);
							builder.setPositiveButton(android.R.string.ok, null);
							builder.show();
						}
					} else {
						// bad number (probably ours)
						AlertDialog.Builder builder = new AlertDialog.Builder(FoDActivity.this);
						if (isdebug(FoDActivity.this)) {
							// allow if debugging, but confirm sending
							builder.setTitle("Really be a cunt?");
							builder.setMessage("Are you sure you want to insult your co-dev?");
							builder.setPositiveButton("Yes, I'm a cunt", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									// ok - send it
									sendSMS(preferences.getString("SMS_number", getString(R.string.dawsons_number)),
											preferences.getString("SMS_message", getString(R.string.SMS_message)));
								}
							});
							builder.setNegativeButton("No, insult Dawson", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									// send it to Dawson, the wanker
									sendSMS(getString(R.string.dawsons_number),
											preferences.getString("SMS_message", getString(R.string.SMS_message)));
								}
							});
							builder.setNeutralButton("Cancel", null);
						} else {
							// not sending sms - tell user.
							builder.setTitle(R.string.banned_number_title);
							builder.setMessage(R.string.banned_number_message);
							builder.setPositiveButton(android.R.string.ok, null);
						}
						builder.show();
					}
				} else {
					// view sms
					Toast.makeText(getBaseContext(), getString(R.string.SMS_message), Toast.LENGTH_SHORT).show();
				}
				;
			}
		});
		/*
		 * if (preferences.getBoolean("send_SMS", true)) {
		 * send_insult.setText(R.string.send_insult);
		 * } else {
		 * send_insult.setText(R.string.view_insult);
		 * }
		 */

		Button victim_jpg = (Button) findViewById(R.id.victim_jpg);
		victim_jpg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(FoDActivity.this);
				builder.setTitle(R.string.dialog_title);
				builder.setMessage(R.string.dialog_text);
				builder.setPositiveButton("Yes!", null);
				builder.setNegativeButton("Fuck yes!", null);
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

		/*
		 * final AlertDialog.Builder progress_dialog_builder;
		 * final AlertDialog progress_dialog;
		 * LayoutInflater inflater;
		 * View progress_view;
		 * inflater = (LayoutInflater)
		 * this.getSystemService(LAYOUT_INFLATER_SERVICE);
		 * progress_view = inflater.inflate(R.layout.progress_overlay,
		 * (ViewGroup) findViewById(R.id.progress_frame));
		 * progress_dialog_builder = new AlertDialog.Builder(FoDActivity.this);
		 * progress_dialog_builder.setView(progress_view);
		 * progress_dialog = progress_dialog_builder.create();
		 */
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Sending...");
		dialog.setIndeterminate(true);
		// dialog.setCancelable(true);
		// *** UNUSED *** shown for reference only
		// String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

		// *** UNUSED *** shown for reference only
		// PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "SMS not sent - Generic failure", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "SMS not sent - No service", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "SMS not sent - Null PDU", Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "SMS not sent - Radio off", Toast.LENGTH_SHORT).show();
					break;
				}
				dialog.dismiss();
			}
		}, new IntentFilter(SENT));

		// *** UNUSED *** shown for reference only
		// ---when the SMS has been delivered---
		/*
  		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));
		*/

		dialog.show();

		SmsManager sms = SmsManager.getDefault();
		//sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
	}

	public void updatePrefs() {
		/*
		 * Show the preferences menu and let the user select their options
		 */
		// Launch Preference activity
		Intent p = new Intent(FoDActivity.this, preferences.class);
		p.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "org.emphie.fod.preferences$prefFrag1");
		p.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
		startActivity(p);
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

		about_inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		about_view = about_inflater.inflate(R.layout.about, (ViewGroup) findViewById(R.id.about_root));

		// set the custom dialog components
		about_version = (TextView) about_view.findViewById(R.id.about_version);
		try {
			about_version.setText(getText(R.string.version_desc) + " "
					+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
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

		about_copyright = (TextView) about_view.findViewById(R.id.about_copyright);
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

	public static boolean valid_victim(String new_number) {
		Integer this_one;
		Integer decode_iterations = 2;
		String this_number;
		Integer this_len;
		Integer new_len;
		byte[] byteArray;
		Boolean valid_number = true;

		

		new_number.replace(" ", "");
		new_len = new_number.length();
		for (this_one = 0; this_one < (Integer) invalid_numbers.length; this_one++) {
			this_number = invalid_numbers[this_one];
			// the invalid numbers are encoded multiple times...
			for (int i = 0; i < decode_iterations; i++) {
				byteArray = Base64.decode(this_number, Base64.DEFAULT);
				try {
					this_number = new String(byteArray, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this_len = this_number.length();
			if (new_len >= this_len) {
				if (new_number.substring(new_len - this_len, new_len).equals(this_number)) {
					valid_number = false;
					break;
				}
			}
		}
		return valid_number;
	}
	public static boolean isdebug(Activity context) {
		boolean debug = false;
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(context.getApplication().getPackageName(),
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
