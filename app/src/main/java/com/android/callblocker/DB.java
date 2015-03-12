package com.android.callblocker;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper{
/** Database name */
private static String DBNAME = "sqllistviewdemo";

/** Version number of the database */
private static int VERSION = 1;

/** Field 1 of the table cust_master, which is the primary key */
public static final String KEY_ROW_ID = "_id";

/** Field 3 of the table cust_master, stores the customer name */
public static final String KEY_NAME = "cust_name";

/** Field 4 of the table cust_master, stores the phone number of the customer */
public static final String KEY_PHONE = "cust_phone";

/** A constant, stores the the table name */
private static final String DATABASE_TABLE = "cust_master";

/** An instance variable for SQLiteDatabase */
private SQLiteDatabase mDB;

/** Constructor */
public DB(Context context) {
    super(context, DBNAME, null, VERSION);
    this.mDB = getWritableDatabase();
}

/** This is a callback method, invoked when the method
* getReadableDatabase() / getWritableDatabase() is called
* provided the database does not exists
* */
@Override
public void onCreate(SQLiteDatabase db) {
    String sql =     "create table "+ DATABASE_TABLE + " ( "
                    + KEY_ROW_ID + " integer primary key autoincrement , "
                    + KEY_NAME + "  text  , "
                    + KEY_PHONE + "  text  ) " ;

    db.execSQL(sql);
    
}

public boolean checkDBIsNull() {
	
	SQLiteDatabase db = this.getReadableDatabase();
	Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + DATABASE_TABLE + "", null); 
	
	if (cur != null) { 
		cur.moveToFirst(); 
		//System.out.println("record : " + cur.getInt(0)); 
		if (cur.getInt(0) == 0) {
			//System.out.println("Table is Null"); 
		cur.close(); 
		return true; } 
		cur.close(); }
	else { 
		//System.out.println("Cursor is Null");
		return true;
	}
	//System.out.println("Table Not Null");
	return false;
}

void addContact(Contact contact) {
	SQLiteDatabase db = this.getWritableDatabase();
	InsertHelper ih = new InsertHelper(db, DATABASE_TABLE);
/*	ContentValues values = new ContentValues();
	values.put(KEY_NAME, contact.getName()); // Contact Name
	values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone
	values.put(KEY_DATE, contact.getDate());
    values.put(KEY_PHOTO,contact.getPhotoUri());
    values.put(KEY_REC,contact.getRec());
	// Inserting Row
	db.insert(TABLE_CONTACTS, null, values);
	db.close(); // Closing database connection
	*/
	final int greekColumn = ih.getColumnIndex(KEY_NAME);
    final int ionicColumn = ih.getColumnIndex(KEY_PHONE);
   
    try {
        
            // ... Create the data for this row (not shown) ...

            // Get the InsertHelper ready to insert a single row
            ih.prepareForInsert();

            // Add the data for each column
            ih.bind(greekColumn,contact.getName());
            ih.bind(ionicColumn,contact.getPhoneNumber());
            
            // Insert the row into the database.
            ih.execute();
        
    }
    finally {
        ih.close();  // See comment below from Stefan Anca
    }
    db.close();
}

public void deleteContact(Contact contact) {
	SQLiteDatabase db = this.getWritableDatabase();
	db.delete(DATABASE_TABLE, KEY_NAME + " = ?",
			new String[] { String.valueOf(contact.getName()) });
	db.close();
}

//Getting single contact
public Boolean getContacttwo(String num) {
	SQLiteDatabase db = this.getReadableDatabase();

	Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ROW_ID,
			KEY_NAME, KEY_PHONE}, KEY_NAME + "=?",
			new String[] { num }, null, null, null, null);
	
	if(cursor.moveToFirst())
	return true;
	else
   return false;				
}
/** Returns all the customers in the table */
public Cursor getAllCustomers(){
    return mDB.query(DATABASE_TABLE, new String[] { KEY_ROW_ID, KEY_NAME, KEY_PHONE } ,
                        null, null, null, null,
                        null);
}

@Override
public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    // TODO Auto-generated method stub
}
}