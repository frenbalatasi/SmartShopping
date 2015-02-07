package at.ac.uniklu.smartshopping;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_PRODUCTS = "products";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_CHECKED = "checked";

	private static final String DATABASE_NAME = "products.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
	      + TABLE_PRODUCTS + "(" + COLUMN_ID
	      + " integer primary key autoincrement, " + COLUMN_NAME
	      + " text not null, " + COLUMN_CHECKED
	      + " int not null);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		
		final int flattvID = 0;
	    final String flattvName = "Flat Panel Televisions";
	    final int flattvChecked = 0;
		
		final int laptopID = 8;
	    final String laptopName = "Laptops";
	    final int laptopChecked = 0;
	    
	    final int harddriveID = 9;
	    final String harddriveName = "Hard Drives";
	    final int harddriveChecked = 0;
	    
	    final String insert_flattv = "insert into " + TABLE_PRODUCTS + " values ("+flattvID+",'"+flattvName+"',"+flattvChecked+")";
	    final String insert_laptop = "insert into " + TABLE_PRODUCTS + " values ("+laptopID+",'"+laptopName+"',"+laptopChecked+")";
	    final String insert_harddrive = "insert into " + TABLE_PRODUCTS + " values ("+harddriveID+",'"+harddriveName+"',"+harddriveChecked+")";
		
	    database.execSQL(insert_flattv);
		database.execSQL(insert_laptop);
		database.execSQL(insert_harddrive);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
	    onCreate(db);
	}

}
