package com.android.callblocker;

import android.net.Uri;
//pojo class
public class ConvItem {

	private String address = null;
	private String displayName = null;
	
	public ConvItem()
	{
		
	}

	ConvItem(String num)
	{
		this.address=num;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
}
