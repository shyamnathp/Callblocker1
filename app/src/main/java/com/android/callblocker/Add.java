package com.android.callblocker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.SyncStateContract.Constants;
import android.support.v7.app.ActionBarActivity;
import android.telephony.gsm.SmsManager;
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

@SuppressLint("NewApi") public class Add extends ActionBarActivity {
	public static StringBuilder numbers;
	ImageButton btnSendSMS;
	EditText txtPhoneNo;
	Context context=this;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(isTablet(this))
		{
			getSupportActionBar().setTitle("CallBlocker-Tablet");
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#EE9000")));
		}
		else
		{
			getSupportActionBar().setTitle("CallBlocker-Phone");
			getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F44336")));	
		}
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
			((TextView) view).setText(cursor.getString(1) 
					+ "\n" + cursor.getString(2)
					+ " " + type);
		}

		@Override 
		public String convertToString(Cursor cursor) 
		{
			return (cursor.getString(1)+"("+cursor.getString(2)+")");
			//return (cursor.getString(2));
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
				
				int count = phoneNo.length() - phoneNo.replace(",", "").length();
				if((count-1)>0)
				{
					Log.d("times", "mre dan 2");
				}
				if(count==0 & !Character.isDigit(phoneNo.charAt(0)))
				{
					Toast.makeText(getBaseContext(),
							"enter from contacts",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
				String[] splitted=phoneNo.split(", ");
				for(int i=0;i<(splitted.length);++i)
				Log.d("hi", splitted[i]);
				for(int i=0;i<(splitted.length);++i)
				{
				Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(splitted[i]);//get value inside the parenthesis
				String ph = null;
				//ph=phoneNo;
				if(Character.isAlphabetic(splitted[i].charAt(0)))
				{
			     while(m.find()) {
			      ph= m.group(1).replaceAll("-","");
				  ph.replaceAll("-", "");
			      ph.replaceAll(" ","");
			      ph.replace("+91","");
			      Log.d("sms",ph);
			     }
				}
				else
				{
					ph=txtPhoneNo.getText().toString();
				}
				//String message = txtMessage.getText().toString();
			     
				if (pollIsValid(ph.toString()))
				{
					//sendSMS(ph.toString(), message);
					DB db=new DB(context);
					String name=getName(ph);
					
					if(Character.isDigit(name.charAt(1)))
						name="unknown";
					
					Log.d("name", name);
					Toast.makeText(getBaseContext(),
							"Added to database",
							Toast.LENGTH_SHORT).show();
					//db.addContact(new Contact(name,ph));
					//Calling the intentService
					Intent msgIntent = new Intent(Add.this, IntSer.class);
					msgIntent.putExtra("name", name);
					msgIntent.putExtra("phone", ph);
					context.startService(msgIntent);
					
					
					}
				else
					Toast.makeText(getBaseContext(),
							"enter phone number",
							Toast.LENGTH_SHORT).show();
						
			}
			}
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

	public boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}
}
