package com.android.callblocker;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

public class IntSer extends IntentService {

	Context context=this;
	String name,ph;
	
	public IntSer() {
		super("IntSer");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		DB db=new DB(context);
	    name = intent.getExtras().getString("name");
	    ph = intent.getExtras().getString("phone");
	    db.addContact(new Contact(name,ph));
	    
		
	}

}
