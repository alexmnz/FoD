package org.emphie.fod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.View;

// Uses reflection to work on older devices
// Code from: http://www.blackmoonit.com/2012/07/all_api_prefsactivity/
public class preferences extends PreferenceActivity {
	protected Method mLoadHeaders = null;
	protected Method mHasHeaders = null;
	public static AlertDialog.Builder builder;
	public static Boolean isdebug;

	/**
	 * Checks to see if using new v11+ way of handling PrefsFragments.
	 * 
	 * @return Returns false pre-v11, else checks to see if using headers.
	 */
	public boolean isNewV11Prefs() {
		if (mHasHeaders != null && mLoadHeaders != null) {
			try {
				return (Boolean) mHasHeaders.invoke(this);
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// onBuildHeaders() will be called during super.onCreate()
		try {
			mLoadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
			mHasHeaders = getClass().getMethod("hasHeaders");
		} catch (NoSuchMethodException e) {
		}
		super.onCreate(savedInstanceState);
		builder = new AlertDialog.Builder(this);
		isdebug = FoDActivity.isdebug((Activity) preferences.this);
		if (!isNewV11Prefs()) {
			final EditTextPreference SMS_number;
			final CheckBoxPreference just_dawson;
			// deprecated methods OK here...
			addPreferencesFromResource(R.xml.preferences);
			// addPreferencesFromResource(R.preferences2);
			// addPreferencesFromResource(R.xml.preferencesN);

			// Link to just dawson_checkbox to update SMS_number summary
			just_dawson = (CheckBoxPreference) getPreferenceScreen().findPreference("just_dawson");
			// Link to sms number for validation
			SMS_number = (EditTextPreference) getPreferenceScreen().findPreference("SMS_number");
			if (just_dawson.isChecked()) {
				SMS_number.setSummary((CharSequence) getString(R.string.dawsons_number));
			} else {
				SMS_number.setSummary((CharSequence) SMS_number.getText());
			}

			SMS_number.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				private Boolean rtnval = false;

				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					rtnval = check_number(builder, newValue);
					if (rtnval) {
						SMS_number.setSummary((CharSequence) newValue);
					}
					return rtnval;
				}
			});

			just_dawson.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					if ((Boolean) newValue) {
						SMS_number.setSummary((CharSequence) getString(R.string.dawsons_number));
					} else {
						SMS_number.setSummary((CharSequence) SMS_number.getText());
					}
					return true;
				}
			});
		}
	}

	/*
	 * Populate the activity with the top-level headers.
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		try {
			mLoadHeaders.invoke(this, new Object[] { R.xml.pref_headers, target });
			// loadHeadersFromResource(R.xml.pref_headers, target);
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}

	/*
	 * The menus for version 11 and onwards
	 */
	@TargetApi(11)
	public static class basics extends PreferenceFragment implements OnSharedPreferenceChangeListener {
		int os_version = Build.VERSION.SDK_INT;
		int SWITCH_MIN = 14;
		private EditTextPreference SMS_number;
		private View SMS_layout;
		private CheckBoxPreference just_dawson_check;
		private SwitchPreference just_dawson_switch;
		private boolean just_dawson_checked;

		@TargetApi(14)
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Build the fragment
			addPreferencesFromResource(R.xml.preferences);

			// Link to just dawson_checkbox/switch to update SMS_number summary
			if (Build.VERSION.SDK_INT < SWITCH_MIN) {
				just_dawson_check = (CheckBoxPreference) getPreferenceScreen().findPreference("just_dawson");
			} else {
				just_dawson_switch = (SwitchPreference) getPreferenceScreen().findPreference("just_dawson");
			}
			// Link to sms number for validation
			SMS_number = (EditTextPreference) getPreferenceScreen().findPreference("SMS_number");
			// int int_SMS_layout =
			// getPreferenceScreen().findPreference("SMS_number").getLayoutResource();

			// handle different OS versions
			// versions less than 14 don't have testable switch preferences, so use check boxes.
			if (Build.VERSION.SDK_INT < SWITCH_MIN) {
				just_dawson_checked = just_dawson_check.isChecked();
			}else{
				just_dawson_checked = just_dawson_switch.isChecked();
			}
			if (just_dawson_checked) {
				SMS_number.setSummary((CharSequence) getString(R.string.dawsons_number));
			} else {
				SMS_number.setSummary((CharSequence) SMS_number.getText());
			}
			//TODO
			/*
			 * final int CONTACT_PICKER_RESULT = 1001;
			 * Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
			 * Contacts.CONTENT_URI);
			 * startActivityForResult(contactPickerIntent,
			 * CONTACT_PICKER_RESULT);
			 * 
			 * 
			 * Bundle extras = data.getExtras();
			 * Set keys = extras.keySet();
			 * Iterator iterate = keys.iterator();
			 * while (iterate.hasNext()) {
			 * String key = iterate.next();
			 * Log.v(DEBUG_TAG, key + "[" + extras.get(key) + "]");
			 * }
			 * Uri result = data.getData();
			 * Log.v(DEBUG_TAG, "Got a result: "
			 * + result.toString());
			 * 
			 * // query for everything email
			 * cursor = getContentResolver().query(
			 * Email.CONTENT_URI, null,
			 * Email.CONTACT_ID + "=?",
			 * new String[]{id}, null);
			 * cursor.moveToFirst();
			 * String columns[] = cursor.getColumnNames();
			 * for (String column : columns) {
			 * int index = cursor.getColumnIndex(column);
			 * Log.v(DEBUG_TAG, "Column: " + column + " == ["
			 * + cursor.getString(index) + "]");
			 * onActivityResult(int requestCode, int resultCode, Intent data) {
			 * if (resultCode == RESULT_OK) {
			 * switch (requestCode) {
			 * case CONTACT_PICKER_RESULT:
			 * // handle contact results
			 * break;
			 * }
			 * 
			 * } else {
			 * // gracefully handle failure
			 * Log.w(DEBUG_TAG, "Warning: activity result not ok");
			 * }
			 * }
			 */
//TODO			
			SMS_number.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					return check_number(builder, newValue);
				}
			});
			
			if (Build.VERSION.SDK_INT < SWITCH_MIN) {
				just_dawson_check.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						if ((Boolean) newValue) {
							SMS_number.setSummary((CharSequence) getString(R.string.dawsons_number));
						} else {
							SMS_number.setSummary((CharSequence) SMS_number.getText());
						}
						return true;
					}
				});
			}else{
				just_dawson_switch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						if ((Boolean) newValue) {
							SMS_number.setSummary((CharSequence) getString(R.string.dawsons_number));
						} else {
							SMS_number.setSummary((CharSequence) SMS_number.getText());
						}
						return true;
					}
				});
				
			}
		}

		@TargetApi(14)
		@Override
		public void onResume() {
			super.onResume();

			// Setup the initial values
			if (Build.VERSION.SDK_INT < SWITCH_MIN) {
				just_dawson_checked = just_dawson_check.isChecked();
			}else{
				just_dawson_checked = just_dawson_switch.isChecked();
			}
			if (just_dawson_checked) {
				SMS_number.setSummary((CharSequence) getString(R.string.dawsons_number));
			} else {
				SMS_number.setSummary((CharSequence) SMS_number.getText());
			}

			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@TargetApi(11)
		@Override
		public void onPause() {
			super.onPause();
			// Unregister the listener whenever a key changes
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}

		@TargetApi(14)
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			// Update the item summary to reflect the change
			if (key.equals("just_dawson")) {
				if (Build.VERSION.SDK_INT < SWITCH_MIN) {
					just_dawson_checked = just_dawson_check.isChecked();
				}else{
					just_dawson_checked = just_dawson_switch.isChecked();
				}
				if (just_dawson_checked) {
					SMS_number.setSummary((CharSequence) getString(R.string.dawsons_number));
				} else {
					SMS_number.setSummary((CharSequence) SMS_number.getText());
				}
			} else if (key.equals("SMS_number")) {
				SMS_number.setSummary(SMS_number.getText());
			}
		}

	}

	/*
	 * Common code for phone number validation
	 */
	public static boolean check_number(AlertDialog.Builder builder, Object newValue) {
		boolean rtnval = false;
		// builder = new AlertDialog.Builder((Context) builder);
		if (FoDActivity.valid_victim((String) newValue)) {
			if (newValue.toString().length() > 0) {
				// valid number, well, close enough
				rtnval = true;
			} else {
				builder.setTitle(R.string.bad_number_title);
				builder.setMessage(R.string.bad_number_message);
				builder.setPositiveButton(android.R.string.ok, null);
				builder.show();
				rtnval = false;
			}
		} else {
			// invalid number (probably ours)
			if (isdebug) {
				// but it's ok because we're debugging
				builder.setTitle("Debug build");
				builder.setMessage("Allowed for debug... But don't be a cunt!");
				rtnval = true;
			} else {
				// invalid number message
				builder.setTitle(R.string.banned_number_title);
				builder.setMessage(R.string.banned_number_message);
				rtnval = false;
			}
			builder.setPositiveButton(android.R.string.ok, null);
			builder.show();
		}
		return rtnval;
	}
}