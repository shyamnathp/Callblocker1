package com.android.callblocker;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Add extends ActionBarActivity {
	public static StringBuilder numbers;
	ImageButton btnSendSMS;
	EditText txtPhoneNo;
	Context context=this;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		// getActionBar().setDisplayShowHomeEnabled(false); // remove app icon
		//getSupportActionBar().setHomeButtonEnabled(true);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setBackgroundDrawable(
				//new ColorDrawable(Color.parseColor("#00CFEE")));

		btnSendSMS = (ImageButton) findViewById(R.id.btnSendSMS);
		txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
		//txtMessage = (EditText) findViewById(R.id.txtMessage);

		btnSendSMS.setOnClickListener(getNewSendSmsListener());

        String[] proj = { BaseColumns._ID, Phone.DISPLAY_NAME, Phone.NUMBER, Phone.TYPE, };
        Cursor peopleCursor = this.getContentResolver().query(Phone.CONTENT_URI,proj, null, null,null);
		ContactListAdapter contactadapter = new ContactListAdapter(this,peopleCursor);
		MultiAutoCompleteTextView textView = (MultiAutoCompleteTextView) findViewById(R.id.txtPhoneNo);
		textView.setThreshold(1);
		textView.setAdapter(contactadapter);
		textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			overridePendingTransition(R.anim.left_in,R.anim.right_out);
			return true;
		}
		return (super.onOptionsItemSelected(menuItem));
	}

	public static class ContactListAdapter extends CursorAdapter implements
			Filterable {
		public Context con;
		public ContactListAdapter(Context context, Cursor c) {
			super(context, c);
			con=context;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			final TextView view = (TextView) inflater.inflate(
					android.R.layout.simple_dropdown_item_1line, parent, false);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			int typeInt = cursor.getInt(3); // Phone.TYPE
			CharSequence type = Phone.getTypeLabel(con.getResources(), typeInt, null);
			((TextView) view).setSingleLine(false);
			((TextView) view).setText(cursor.getString(1) + "\n" + cursor.getString(2)
					+ " " + type);
		}

		@Override 
		public String convertToString(Cursor cursor) 
		{
			return (cursor.getString(1)+"("+cursor.getString(2)+")");
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (getFilterQueryProvider() != null) {
				return getFilterQueryProvider().runQuery(constraint);
			}

			ContentResolver cr = con.getContentResolver();
			Uri uri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI,
					constraint.toString());
			String[] proj = { BaseColumns._ID, Phone.DISPLAY_NAME,
					Phone.NUMBER, Phone.TYPE, };
			return cr.query(uri, proj, null, null, null);
		}
	}

	private OnClickListener getNewSendSmsListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				String phoneNo = txtPhoneNo.getText().toString();
				Log.d("num", phoneNo);
				Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(phoneNo);//get value inside the parenthesis
				String ph = null;
			     while(m.find()) {
			      ph= m.group(1).replaceAll("-","");
			      ph.replaceAll(" ","");
			      ph.replace("+91","");
			      Log.d("sms",ph);
			     }
				//String message = txtMessage.getText().toString();
				if (pollIsValid(ph.toString()))
				{
					//sendSMS(ph.toString(), message);
					DB db=new DB(context);
					String name=getName(ph);
					if(name.equals(null))
						name="unknown";
					Toast.makeText(getBaseContext(),
							"Added to database",
							Toast.LENGTH_SHORT).show();
					db.addContact(new Contact(name,ph));
					}
				else
					Toast.makeText(getBaseContext(),
							"enter phone number",
							Toast.LENGTH_SHORT).show();
			}
		};
	}

	private boolean pollIsValid(String phoneNo) {
		return phoneNo.length() > 0;
	}

	public String getName(String address)// For title of conversation class
	{
		Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(address));
		Cursor cs = context.getContentResolver().query(Nameuri,
				new String[] { PhoneLookup.DISPLAY_NAME, PhoneLookup._ID },
				PhoneLookup.NUMBER + "='" + address + "'", null, null);
		String contactName = null;
		if (cs.getCount() > 0) {
			while (cs.moveToNext()) {
				contactName = cs.getString(cs
						.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} else {
			contactName = address;
		}
		cs.close();
		return contactName;
	}

}
