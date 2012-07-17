package org.emphie.fod;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Base64;

public class preferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Add a button to the header list.
		// if (hasHeaders()) {
		// Fragment f = new prefFrag1();
		// f.setMenuVisibility(true);
		// button.setText("Some action");
		// setListFooter(button);
		// }
	}

	/*
	 * Populate the activity with the top-level headers.
	 */
	@TargetApi(11)
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.pref_headers, target);
	}

	@TargetApi(11)
	public static class prefFrag1 extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {
		private EditTextPreference SMS_number;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Build the fragment
			addPreferencesFromResource(R.xml.preferences);

			// Get a reference to the preferences
			SMS_number = (EditTextPreference) getPreferenceScreen()
					.findPreference("SMS_number");
			// Preference prefxx;
			// pref_SMS_number =
			// (Preference)getPreferenceScreen().findPreference("SMS_number");

			SMS_number
					.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							// my phone number, base 64 encoded for obfuscation
							String mynumber = "Mjk0ODk1MTUw";
							String alexnumber = "Mjc0NDQwNzU5";
							String newnumber;
							Integer newlen;
							Integer mylen;
							byte[] byteArray;
							Boolean rtnval = true;

							byteArray = Base64.decode(mynumber, Base64.DEFAULT);
							try {
								mynumber = new String(byteArray, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							byteArray = Base64.decode(alexnumber, Base64.DEFAULT);
							try {
								alexnumber = new String(byteArray, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							newnumber = newValue.toString();
							newnumber.replace(" ", "");
							newlen = newnumber.length();
							mylen = mynumber.length();
							if (newlen >= mylen) {
								newnumber = newnumber.substring(newlen - mylen, newlen);
								if (newlen != 0 & newlen > 9
										& (newnumber.equals(mynumber)) | newnumber.equals(alexnumber)) {
									final AlertDialog.Builder builder = new AlertDialog.Builder(
											getActivity());
									if (FoDActivity.isdebug(getActivity())) {
										builder.setTitle("Allowed for debug...");
										builder.setMessage("It's my app; you really thought that'd work? Well, OK, but just for a debug build");

									} else {
										builder.setTitle("no No NO!");
										builder.setMessage("It's my app; you really thought that'd work? Nice try Bozo.");
										rtnval = false;
										
									}
										builder.setPositiveButton(
												android.R.string.ok, null);
										builder.show();
								}
							}
							return rtnval;
						}
					});
		}

		@Override
		public void onResume() {
			super.onResume();

			// Setup the initial values
			SMS_number.setSummary(SMS_number.getText());

			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();

			// Unregister the listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// Let's do something when a preference value changes
			if (key.equals("send_SMS")) {
				// Not actually doing anything here. Switch implemented as
				// sequence of ifs
			} else if (key.equals("SMS_number")) {
				SMS_number.setSummary(SMS_number.getText());
			}
		}

	}
}