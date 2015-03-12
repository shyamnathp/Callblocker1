package com.android.callblocker;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

	
	public class IncomingCallReciever extends BroadcastReceiver {
		 
	    private Context mContext;
	    private Intent mIntent;
	    private ITelephony telephonyService;
	 
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        mContext = context;
	        mIntent = intent;
	        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        int events = PhoneStateListener.LISTEN_CALL_STATE;
	        tm.listen(phoneStateListener, events);
	    }
	 
	    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {
	        @Override
	        public void onCallStateChanged(int state, String incomingNumber) {
	            String callState = "UNKNOWN";
	            switch (state) {
	            case TelephonyManager.CALL_STATE_IDLE:
	                callState = "IDLE";
	                break;
	            case TelephonyManager.CALL_STATE_RINGING:
	                // -- check international call or not
	                 DB db=new DB(mContext);
	                 String name= getName(incomingNumber);
	                 Log.d("HI", getName(incomingNumber));
	                if (db.getContacttwo(name)){
	                	TelephonyManager telephony = (TelephonyManager) 
	                			  mContext.getSystemService(Context.TELEPHONY_SERVICE);  
	                			  try {
	                			   Class c = Class.forName(telephony.getClass().getName());
	                			   Method m = c.getDeclaredMethod("getITelephony");
	                			   m.setAccessible(true);
	                			   telephonyService = (ITelephony) m.invoke(telephony);
	                			   //telephonyService.silenceRinger();
	                			   telephonyService.endCall();
	                			  } catch (Exception e) {
	                			   e.printStackTrace();
	                			  }
	                } else {
	                    Toast.makeText(mContext, "Local Call - " + incomingNumber, Toast.LENGTH_LONG).show();
	                    callState = "Local - Ringing (" + incomingNumber + ")";
	                }
	                break;
	            case TelephonyManager.CALL_STATE_OFFHOOK:
	                break;
	            }
	            Log.i(">>>Broadcast", "onCallStateChanged " + callState);
	            super.onCallStateChanged(state, incomingNumber);
	        }
	    };
	    
	    public String getName(String address)// For title of conversation class
		{
			Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(address));
			Cursor cs = mContext.getContentResolver().query(Nameuri,
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

