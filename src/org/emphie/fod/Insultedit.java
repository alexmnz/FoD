package org.emphie.fod;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Insultedit extends Activity {
	 private EditText mTitleText;
	 private EditText mBodyText;
	 private Long mRowId;
	 private InsultsDBAdaptor mDbHelper;
	 
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
//		 mDbHelper = new InsultsDBAdaptor(this);

//		    mDbHelper.open();
//		 setContentView(R.layout.insult_edit);
//		 setTitle(R.string.editinsult);
//		 mTitleText = (EditText) findViewById(R.id.title);
//		 mBodyText = (EditText) findViewById(R.id.body);
//		 Button confirmButton = (Button) findViewById(R.id.confirm);
//		    mRowId = (savedInstanceState == null) ? null :
//	            (Long) savedInstanceState.getSerializable(InsultsDBAdaptor.KEY_ROWID);
//	        if (mRowId == null) {
//	            Bundle extras = getIntent().getExtras();
//	            mRowId = extras != null ? extras.getLong(InsultsDBAdaptor.KEY_ROWID)
//	                                    : null;
//	        }
 // 	     populateFields();
  	     
//	 	 confirmButton.setOnClickListener(new View.OnClickListener() {

//	 		public void onClick(View view) {
//	 		    setResult(RESULT_OK);
//	 		    finish();
//	 		}
	 				
//		});
}
	 @Override
	    protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        saveState();
	        outState.putSerializable(InsultsDBAdaptor.KEY_ROWID, mRowId);
	    }
	  @Override
	    protected void onPause() {
	        super.onPause();
	        saveState();
	    } 
	 @Override
  protected void onResume() {
      super.onResume();
      populateFields();
  }
	 private void saveState() {
      String title = mTitleText.getText().toString();
      String body = mBodyText.getText().toString();

      if (mRowId == null) {
          long id = mDbHelper.createInsult(title, body);
          if (id > 0) {
              mRowId = id;
          }
      } else {
          mDbHelper.updateInsult(mRowId, title, body);
      }
  }
	 private void populateFields() {
		    if (mRowId != null) {
		        Cursor insult = mDbHelper.fetchInsult(mRowId);
		        startManagingCursor(insult);
		        mTitleText.setText(insult.getString(
		                    insult.getColumnIndexOrThrow(InsultsDBAdaptor.KEY_TITLE)));
//		        mBodyText.setText(insult.getString(
//		                insult.getColumnIndexOrThrow(InsultsDBAdaptor.KEY_BODY)));
		    }
		 }
}



