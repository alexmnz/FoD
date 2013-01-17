package org.emphie.fod;

import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
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
	// developers and friends phone numbers, encoded for obfuscation
	public static String[] invalid_numbers;
	public static String[] SMS_messages;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		invalid_numbers = this.getResources().getStringArray(R.array.invalid_numbers);
		SMS_messages = this.getResources().getStringArray(R.array.SMS_messages);
		// Initialise preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		send_insult = (Button) findViewById(R.id.send_insult);
		send_insult.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO - haptic feedback on send click
				// TODO - lookup how to get this value at compile time
				// final int VIRTUAL_KEY = 1;
				// v.performHapticFeedback(VIRTUAL_KEY);
				if (preferences.getBoolean("just_dawson", true)) {
					SMS_number = getString(R.string.dawsons_number);
				} else {
					// default to dawsons number if preference is not
					// initialised yet
					SMS_number = preferences.getString("SMS_number", getString(R.string.dawsons_number));
				}
				if (preferences.getBoolean("send_SMS", true)) {
					// first validate the number
					if (valid_victim((String) SMS_number)) {
						if (SMS_number.length() > 0) {
							// all good - send it
							sendSMS(SMS_number, get_SMS_message());
						} else {
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
											get_SMS_message());
								}
							});
							builder.setNegativeButton("No, insult Dawson", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									// send it to Dawson, the wanker
									sendSMS(getString(R.string.dawsons_number), get_SMS_message());
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
					// view message
					Toast.makeText(getBaseContext(), get_SMS_message(), Toast.LENGTH_LONG).show();
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
		if (isdebug(this)) {
			menu.add("Contacts");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_share:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			shareIntent.setType("text/plain");

			// For a file in shared storage. For data in private storage, use a
			// ContentProvider.
			// shareIntent.putExtra(Intent.EXTRA_STREAM,
			// Uri.parse("http://fuckoffdawson.com/downloads/fod.apk"));
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Link to Fuck off Dawson");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "http://fuckoffdawson.com/downloads/fod.apk");
			startActivity(Intent.createChooser(shareIntent, "Share with?"));
			break;
		case R.id.menu_prefs:
			/*
			 * Show the preferences menu and let the user select their options
			 */
			Intent pref = new Intent(FoDActivity.this, preferences.class);
			pref.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, "org.emphie.fod.preferences$basics");
			pref.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
			startActivity(pref);
			break;
		case R.id.menu_about:
			ShowAbout();
			break;
		//case R.id.market_rate:
		//	Uri uri = Uri.parse("market://search?q=pname:" + this.getPackageName());
		//	Intent rate = new Intent("ACTION_VIEW", uri);
		//	try {
		//		startActivity(rate);
		//	} catch (ActivityNotFoundException e) {
		//		e.printStackTrace();
		//		Toast.makeText(getBaseContext(), "Google Play is not installed", Toast.LENGTH_LONG).show();
		//	}
		//	break;

		/*
		 * case R.id.menu_manage: Intent i = new Intent(this, Insults.class);
		 * startActivity(i); break;
		 */
		default:
			if (item.getTitle() == "Contacts") {
				// TODO
			}
			break;
		}

		return true;
	}

	private void sendSMS(String phoneNumber, String message) {
		// ---sends an SMS message to another device---
		// from http://mobiforge.com/developing/story/sms-messaging-android
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
		final AlertDialog.Builder err_dialog = new AlertDialog.Builder(this);
		err_dialog.setTitle("Failed");
		final ProgressDialog progress_dialog = new ProgressDialog(this);
		progress_dialog.setMessage("Sending \"" + message + "\"");
		progress_dialog.setIndeterminate(true);
		progress_dialog.show();

		// dialog.setCancelable(true);
		// *** UNUSED *** shown for reference only
		// String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

		// *** UNUSED *** shown for reference only
		// PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new
		// Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				String err_text = null;
				switch (getResultCode()) {
				/*
				 * case Activity.RESULT_OK:
				 * Toast.makeText(getBaseContext(), "SMS sent",
				 * Toast.LENGTH_SHORT).show();
				 * break;
				 */
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					err_text = "SMS not sent - Generic failure";
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					err_text = "SMS not sent - No service";
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					err_text = "SMS not sent - Null PDU";
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					err_text = "SMS not sent - Radio off";
					break;
				}
				if (progress_dialog.isShowing()) {
					progress_dialog.dismiss();
				}
				if (err_text != null) {
					err_dialog.setMessage(err_text);
					err_dialog.show();
				}
			}
		}, new IntentFilter(SENT));

		// *** UNUSED *** shown for reference only
		// ---when the SMS has been delivered---
		/*
		 * registerReceiver(new BroadcastReceiver() {
		 * 
		 * @Override
		 * public void onReceive(Context arg0, Intent arg1) {
		 * switch (getResultCode()) {
		 * case Activity.RESULT_OK:
		 * Toast.makeText(getBaseContext(), "SMS delivered",
		 * Toast.LENGTH_SHORT).show();
		 * break;
		 * case Activity.RESULT_CANCELED:
		 * Toast.makeText(getBaseContext(), "SMS not delivered",
		 * Toast.LENGTH_SHORT).show();
		 * break;
		 * }
		 * }
		 * }, new IntentFilter(DELIVERED));
		 */


		SmsManager sms = android.telephony.SmsManager.getDefault();
		// sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
		sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
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
		String this_number;
		Integer this_len;
		Integer new_len;
		Boolean valid_number = true;

		new_number.replace(" ", "");
		new_len = new_number.length();
		for (this_one = 0; this_one < (Integer) invalid_numbers.length; this_one++) {
			this_number = invalid_numbers[this_one];
			this_number = String.valueOf(Integer.parseInt(this_number, 6 * 6));
			/*
			 * following code only works for sdk v8 onwards
			 * 
			 * // the invalid numbers are encoded multiple times...
			 * Integer decode_iterations = 2;
			 * byte[] byteArray;
			 * for (int i = 0; i < decode_iterations; i++) {
			 * byteArray = Base64.decode(this_number, Base64.DEFAULT);
			 * try {
			 * this_number = new String(byteArray, "UTF-8");
			 * } catch (UnsupportedEncodingException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 * }
			 */
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

	public String get_SMS_message() {
		String SMS_message = null;
		if (preferences.getBoolean("random_SMS", true)) {
			Random r = new Random();
			int i = r.nextInt(SMS_messages.length);
			SMS_message = SMS_messages[i];
		} else {
			SMS_message = preferences.getString("SMS_message", getString(R.string.SMS_message));
		}
		return SMS_message;
	}
}
