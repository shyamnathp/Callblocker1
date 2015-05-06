package com.callblocker.loginandregistration;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.callblocker.loginandregistration.app.AppConfig;
import com.callblocker.loginandregistration.app.AppController;
import com.callblocker.loginandregistration.helper.SQLiteHandler;

public class IntSer extends IntentService {

	Context context=this;
	String name=null;
	String email=null;
	String password=null;
	private SQLiteHandler db;
	
	public IntSer() {
		super("IntSer");
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
		name=  intent.getExtras().getString("name");
		email=  intent.getExtras().getString("email");
		password=  intent.getExtras().getString("password");
				

			String tag_string_req = "req_register";
			Toast.makeText(getApplicationContext(),
					"registering", Toast.LENGTH_LONG).show();
			
			
			StringRequest strReq = new StringRequest(Method.POST,
					AppConfig.URL_REGISTER, new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {

							try {
								JSONObject jObj = new JSONObject(response);
								boolean error = jObj.getBoolean("error");
								if (!error) {
									// User successfully stored in MySQL
									// Now store the user in sqlite
									String uid = jObj.getString("uid");

									JSONObject user = jObj.getJSONObject("user");
									String name = user.getString("name");
									String email = user.getString("email");
									String created_at = user
											.getString("created_at");

									// Inserting row in users table
									db.addUser(name, email, uid, created_at);

									// Launch login activity
									Intent dialogIntent = new Intent(context, LoginActivity.class);
									dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(dialogIntent);
								} else {

									// Error occurred in registration. Get the error
									// message
									String errorMsg = jObj.getString("error_msg");
									Log.d("net", "nonet");
									Toast.makeText(getApplicationContext(),
											errorMsg, Toast.LENGTH_LONG).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							//Log.e(TAG, "Registration Error: " + error.getMessage());
							Log.d("net", "net");
							Toast.makeText(getApplicationContext(),
									"no internet connection", Toast.LENGTH_LONG).show();
						
						}
					}) {

				@Override
				protected Map<String, String> getParams() {
					// Posting params to register url
					Map<String, String> params = new HashMap<String, String>();
					params.put("tag", "register");
					params.put("name", name);
					params.put("email", email);
					params.put("password", password);

					return params;
				}

			};

			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
		}

	    
		
	}

