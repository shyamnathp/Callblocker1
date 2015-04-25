package com.android.callblocker;



import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;


import com.kalypzo.ui.fab.FloatingActionButton;


public class MainActivity extends ActionBarActivity  implements	OnClickListener,LoaderCallbacks<Cursor>{

	ListView ls;
	LoaderManager lm;
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
	Context context=this;
	Cursor cursor;
	
	//DB d=new DB(context);
	ArrayList<Contact> list= new ArrayList<Contact>();
	SimpleCursorAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.primlist);
        DB db=new DB(context);
        ls = (ListView)findViewById(R.id.listPrim);
        
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.button_floating_action);
        floatingActionButton.attachToListView(ls);
        floatingActionButton.setOnClickListener(this);

        
        if(!db.checkDBIsNull())
			Log.d("database", "notnull");
		
        
        if(!db.checkDBIsNull())
        	Log.d("database", "cool");
        
        mAdapter = new SimpleCursorAdapter(context,
                R.layout.convitem,
                null,
                new String[] { DB.KEY_NAME, DB.KEY_PHONE},
                new int[] { R.id.tv_name, R.id.tv_num }, 0);
 
        ls.setAdapter(mAdapter);
        
        mCallbacks = this;
		lm = getSupportLoaderManager();
		// Initiating the loader
 
        /** Creating a loader for populating listview from sqlite database */
        /** This statement, invokes the method onCreatedLoader() */
        lm.initLoader(0, null, this);
        registerForContextMenu(ls);
	        
    }


    private Context getActionBarActivity() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.button_floating_action:
			Intent i=new Intent(MainActivity.this,Add.class);
			startActivity(i);
			break;
		}
		
	}


	 /** A callback method invoked by the loader when initLoader() is called */
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
    	
        Uri uri = CoProvider.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    	
        
    }
 
    /** A callback method, invoked after the requested content provider returned all the data */
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0, Cursor arg1) {
    	//Log.d("shyam", arg1.getString(arg1.getColumnIndex(DB.KEY_NAME)));
    	if(mAdapter!=null && arg1!=null)
        mAdapter.swapCursor(arg1);
    	else
    	{
    		
    			Log.d("database", "notnull");
    	}
    }
 
    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }

    @Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		

		if (item.getTitle() == "Delete") {
			DB db = new DB(context);
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			cursor = (Cursor) ls.getItemAtPosition(info.position);
			cursor.moveToPosition(info.position);
			
			
			Contact silinen=new Contact(cursor.getString(cursor.getColumnIndex("cust_name")),cursor.getString(cursor.getColumnIndex("cust_phone")));  
		   
			//Contact silinen = mAdapter.getItem(info.position);
			Log.d("hi",cursor.getString(cursor.getColumnIndex("cust_name")) );
			db.deleteContact(silinen);
			cursor.requery();
			
		    mAdapter.notifyDataSetChanged();
		    //ls.setAdapter(mAdapter);
			
			return super.onContextItemSelected(item);

		} 
		else {
			return false;
		}
		//return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
       
		menu.setHeaderTitle("Select The Action");
		menu.add(0, v.getId(), 0, "Delete");// groupId, itemId, order, title
		//menu.add(0, v.getId(), 0, "Move Social");
	}
	
	@Override
	public void onResume() {
		
		// TODO Auto-generated method stub
		super.onResume();
		
		//mAdapter.getCursor().requery();
		getSupportLoaderManager().restartLoader(0, null, this);
		mAdapter.notifyDataSetChanged();


	}
	
	public boolean isTablet(Context context) {
	    boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large);
	}

}
