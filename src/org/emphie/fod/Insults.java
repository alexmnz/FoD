package org.emphie.fod;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Insults extends Activity {
	private InsultsDBAdaptor mDbHelper;
	public static final int INSERT_ID = Menu.FIRST;
	public static final int DELETE_ID = Menu.FIRST + 1;

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

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

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { InsultsDBAdaptor.KEY_TITLE };

		// and an array of the fields we want to bind those fields to (in this
		// case just list1)
		int[] to = new int[] { R.id.list1 };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter insults = new SimpleCursorAdapter(this,
				R.layout.insults, insultsCursor, from, to);
		setListAdapter(insults);

	}

	private void setListAdapter(SimpleCursorAdapter insults) {
		// TODO Auto-generated method stub

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

	private void createNote() {
		Intent i = new Intent(this, Insults.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//		Intent i = new Intent(this, Insults.class);
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
