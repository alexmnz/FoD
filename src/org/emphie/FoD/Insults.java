package org.emphie.FoD;



import org.emphie.FoD.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class Insults extends Activity {
	private InsultsDBAdaptor mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insults);
		 mDbHelper = new InsultsDBAdaptor(this);
	        mDbHelper.open();
	}
	
	@SuppressWarnings("deprecation")

	private void fillData() {
		 Cursor insultsCursor = mDbHelper.fetchAllInsults();
	     // Get all of the notes from the database and create the item list
	      startManagingCursor(insultsCursor);

	         // Create an array to specify the fields we want to display in the list (only TITLE)
	         String[] from = new String[]{InsultsDBAdaptor.KEY_TITLE};

	         // and an array of the fields we want to bind those fields to (in this case just list1)
	         int[] to = new int[]{R.id.list1};

	         // Now create a simple cursor adapter and set it to display
	         SimpleCursorAdapter insults = 
	             new SimpleCursorAdapter(this, R.layout.insults, insultsCursor, from, to);
	         setListAdapter(insults);

   }

	private void setListAdapter(SimpleCursorAdapter insults) {
		// TODO Auto-generated method stub
		
	}
}

