package com.callblocker.loginandregistration;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.android.callblocker.IncomingCallReciever;
import com.android.callblocker.MainActivity;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.callblocker.loginandregistration.app.AppConfig;
import com.callblocker.loginandregistration.app.AppController;
import com.callblocker.loginandregistration.helper.SQLiteHandler;
import com.callblocker.loginandregistration.helper.SessionManager;

public class IntentSer extends IntentService{
	
	Context context=this;
	String name=null;
	String email=null;
	String password=null;
	private SQLiteHandler db;
	private SessionManager session;
	
	public IntentSer() {
		super("IntentSer");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

		/**
		 * Function to store user in MySQL database will post params(tag, name,
		 * email, password) to register url
		 * */
		db = new SQLiteHandler(getApplicationContext());
		email=  intent.getExtras().getString("email");
		password=  intent.getExtras().getString("password");
		// Session manager
		session = new SessionManager(getApplicationContext());
				

		String tag_string_req = "req_login";
		Toast.makeText(getApplicationContext(),
				"logging in", Toast.LENGTH_LONG).show();

		
		StringRequest strReq = new StringRequest(Method.POST,
				AppConfig.URL_REGISTER, new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						

						try {
							JSONObject jObj = new JSONObject(response);
							boolean error = jObj.getBoolean("error");

							// Check for error node in json
							if (!error) {
								// user successfully logged in
								// Create login session
								session.setLogin(true);

								// Launch main activity
								ComponentName receiver = new ComponentName(context, IncomingCallReciever.class);

								PackageManager pm = context.getPackageManager();

								pm.setComponentEnabledSetting(receiver,
								        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
								        PackageManager.DONT_KILL_APP);
								// Launch main activity
								Intent dialogIntent = new Intent(context, MainActivity.class);
								dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(dialogIntent);
							} else {
								// Error in login. Get the error message
								String errorMsg = jObj.getString("error_msg");
								Toast.makeText(getApplicationContext(),
										errorMsg, Toast.LENGTH_LONG).show();
							}
						} catch (JSONException e) {
							// JSON error
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(),
								error.getMessage(), Toast.LENGTH_LONG).show();
						
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting parameters to login url
				Map<String, String> params = new HashMap<String, String>();
				params.put("tag", "login");
				params.put("email", email);
				params.put("password", password);

				return params;
			}

		};

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
		}

	    
		

}
