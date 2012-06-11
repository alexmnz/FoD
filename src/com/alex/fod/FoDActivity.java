package com.alex.fod;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class FoDActivity extends Activity {
	private InsultsDBAdaptor mDbHelper;
	public static final int INSERT_ID=Menu.FIRST;
	public static final int DELETE_ID=Menu.FIRST + 1;
	private static final int ACTIVITY_CREATE=0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
              
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_text);
        builder.setPositiveButton(R.string.dialog_ok, null);
        builder.setNegativeButton(R.string.dialog_cancel, null);
    
        Button btnDialog1 = (Button) findViewById(R.id.button1);
        btnDialog1.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
        	 Toast.makeText(FoDActivity.this, "Could send a message here", Toast.LENGTH_SHORT).show();
        }
    });
    
    Button btnDialog = (Button) findViewById(R.id.button2);
    btnDialog.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
            builder.show();
        }
      });
    }
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
	   boolean result = super.onCreateOptionsMenu(menu);
	   menu.add(0, INSERT_ID, 0, R.string.menu_insert);
	   menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	   return result;
   }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case INSERT_ID:
	    	Intent i = new Intent(this, Insults.class);
	    	startActivityForResult(i, ACTIVITY_CREATE);
	    	return true;
	    case DELETE_ID:
	    	return true;
	     }
	    return super.onOptionsItemSelected(item);
}
}
    