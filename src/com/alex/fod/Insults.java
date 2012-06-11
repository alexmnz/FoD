package com.alex.fod;



import android.app.Activity;
import android.os.Bundle;

public class Insults extends Activity {
	private InsultsDBAdaptor mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insults);
		 mDbHelper = new InsultsDBAdaptor(this);
	        mDbHelper.open();
	}
	
	private void fillData() {
   	

   }
}

