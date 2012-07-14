package org.emphie.FoD;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class preferences extends PreferenceActivity {

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Add a button to the header list.
//		if (hasHeaders()) {
//			Fragment f = new prefFrag1();
//			f.setMenuVisibility(true);
			//button.setText("Some action");
			//setListFooter(button);
//		}
	}

	/*
	 * Populate the activity with the top-level headers.
	 */
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.pref_headers, target);
	}

	public static class prefFrag1 extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	    private EditTextPreference SMS_number;
	    
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			// Build the fragment
			addPreferencesFromResource(R.xml.preferences);
			
			// Get a reference to the preferences
			SMS_number = (EditTextPreference)getPreferenceScreen().findPreference("SMS_number");
		}
		
		@Override
		public void onResume() {
	        super.onResume();

	        // Setup the initial values
	        SMS_number.setSummary(SMS_number.getText()); 

	        // Set up a listener whenever a key changes            
	        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	    }

	    @Override
		public void onPause() {
	        super.onPause();

	        // Unregister the listener whenever a key changes            
	        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
	    }

	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	        // Let's do something when a preference value changes
	        if (key.equals("send_SMS")) {
	        	// Not actually doing anything here. Switch implemented as sequence of ifs
        	} else if (key.equals("SMS_number")) {
		        SMS_number.setSummary(SMS_number.getText()); 
	        }
	    }

	}
}