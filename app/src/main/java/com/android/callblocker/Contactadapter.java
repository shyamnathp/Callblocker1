package com.android.callblocker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Contactadapter extends ArrayAdapter<Contact> {
	private Context my_context;
	private List<Contact> cont=null;
	private ArrayList<Contact> arraylist;

	// View lookup cache
	private static class ViewHolder {
		TextView name,num;
		RelativeLayout rl;
	}
	//public Typeface type;

	public Contactadapter(Context context, List<Contact> Contacts) {
		super(context, R.layout.convitem, Contacts);
		my_context = context;
		cont=Contacts;
		this.arraylist= new ArrayList<Contact>();
		this.arraylist.addAll(Contacts);
		
		
		Log.d("Name: ", "errorone");
		//type = Typeface.createFromAsset(my_context.getAssets(),"fonts/Ubuntu-R.ttf"); 
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		Contact Contact = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.contactdum, null);
			 
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.num = (TextView) convertView.findViewById(R.id.tv_num);
		//	viewHolder.id = (TextView) convertView.findViewById(R.id.tv_id);

			viewHolder.rl = (RelativeLayout) convertView.findViewById(R.id.rlayout);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		
		 
		 
		viewHolder.name.setText(Contact.getName());
		//viewHolder.name.setTypeface(type);
		
		viewHolder.num.setText(Contact.getPhoneNumber());
		//viewHolder.num.setVisibility(View.INVISIBLE);
		
		//viewHolder.id.setText(getid(Contact.getPhoneNumber()));
		//viewHolder.id.setVisibility(View.INVISIBLE);
		
		
		//viewHolder.body.setTypeface(type);
		
		// Return the completed view to render on screen
		/*
		Animation animationY=new TranslateAnimation(0,0,viewHolder.rl.getHeight()/4,0);
		animationY.setDuration(500);
		convertView.startAnimation(animationY);
		animationY=null;*/
		return convertView;
	}
	
	
	 
	
		
	    
}