package org.emphie.fod;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Insults extends Activity {
	private InsultsDBAdaptor mDbHelper;
	public static final int INSERT_ID = Menu.FIRST;
	public static final int DELETE_ID = Menu.FIRST + 1;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	public static ListView lv;
	public static String[] from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insults);
		lv = (ListView) findViewById(R.id.list1);

		mDbHelper = new InsultsDBAdaptor(this);
		mDbHelper.open();
        fillData();
    //    lv.setAdapter(new ArrayAdapter<String>(this, R.layout.insults, from));
  //   registerForContextMenu(getViewById());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.insults_menu, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	private void fillData() {
		Cursor insultscursor = mDbHelper.fetchAllInsults();
		// Get all of the notes from the database and create the item list
		startManagingCursor(insultscursor);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		from = new String[] { InsultsDBAdaptor.KEY_TITLE };

		// and an array of the fields we want to bind those fields to (in this
		// case just list1)
		int[] to = new int[] { R.id.text1 };
		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter insults = new SimpleCursorAdapter(this,
		R.layout.insults, insultscursor, from, to);
//		setListAdapter(insults);


 	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu1:
			createinsult();
			break;
		case R.id.menu2:
			deleteinsult();
			break;
		default:
			break;
		}

		return true;
	}

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			mDbHelper.deleteInsult(info.id);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createinsult() {
		Intent i = new Intent(this, Insultedit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	private void deleteinsult() {
		// Intent i = new Intent(this, Insultedit.class);
		// startActivityForResult(i, ACTIVITY_CREATE);
	}

//	@Override
//	protected void onitemClick(ListView l, View v, int position, long id) {
//		super.onItemClick(l, v, position, id);
//		Intent i = new Intent(this, Insultedit.class);
//		i.putExtra(InsultsDBAdaptor.KEY_ROWID, id);
//		startActivityForResult(i, ACTIVITY_EDIT);
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}
}
