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
	    
	    final int dvdID = 1;
	    final String dvdName = "DVD & Blu-ray Players";
	    final int dvdChecked = 0;
		
	    final int hometheaterID = 2;
	    final String hometheaterName = "Home Theater Systems";
	    final int hometheaterChecked = 0;
	    
	    final int hdmiID = 3;
	    final String hdmiName = "HDMI Cables";
	    final int hdmiChecked = 0;
	    
	    final int mp3ID = 4;
	    final String mp3Name = "MP3 Players";
	    final int mp3Checked = 0;
	    
	    final int headphoneID = 5;
	    final String headphoneName = "Headphones";
	    final int headphoneChecked = 0;
	    
	    final int pcID = 6;
	    final String pcName = "Desktop Computers";
	    final int pcChecked = 0;
	    
		final int laptopID = 7;
	    final String laptopName = "Laptops";
	    final int laptopChecked = 0;
	    
	    final int tabletID = 8;
	    final String tabletName = "Tablet Computers";
	    final int tabletChecked = 0;
	    
	    final int harddriveID = 9;
	    final String harddriveName = "Hard Drives";
	    final int harddriveChecked = 0;
	    
	    final int dvddriveID = 10;
	    final String dvddriveName = "DVD Drives";
	    final int dvddriveChecked = 0;
	    
	    final int usbdriveID = 11;
	    final String usbdriveName = "USB Flash Drives";
	    final int usbdriveChecked = 0;
	    
	    final int keyboardID = 12;
	    final String keyboardName = "Keyboard & Mouse Sets";
	    final int keyboardChecked = 0;
	    
	    final int ps3ID = 13;
	    final String ps3Name = "Playstation 3 Consoles";
	    final int ps3Checked = 0;
	    
	    final int xboxID = 14;
	    final String xboxName = "XBox 360 Consoles";
	    final int xboxChecked = 0;
	    
	    final int wiiID = 15;
	    final String wiiName = "Wii Consoles";
	    final int wiiChecked = 0;
	    
	    final int ds3ID = 16;
	    final String ds3Name = "Nintendo 3DS";
	    final int ds3Checked = 0;
	    
	    final int psvitaID = 17;
	    final String psvitaName = "Playstation Vita";
	    final int psvitaChecked = 0;
	    
	    final int usbcableID = 18;
	    final String usbcableName = "USB Cables";
	    final int usbcableChecked = 0;
	    
	    final int ethernetcableID = 19;
	    final String ethernetcableName = "Ethernet Cables";
	    final int ethernetcableChecked = 0;
	    
	    final String insert_flattv = "insert into " + TABLE_PRODUCTS + " values ("+flattvID+",'"+flattvName+"',"+flattvChecked+")";
	    final String insert_dvd = "insert into " + TABLE_PRODUCTS + " values ("+dvdID+",'"+dvdName+"',"+dvdChecked+")";
	    final String insert_hometheater = "insert into " + TABLE_PRODUCTS + " values ("+hometheaterID+",'"+hometheaterName+"',"+hometheaterChecked+")";
	    final String insert_hdmi = "insert into " + TABLE_PRODUCTS + " values ("+hdmiID+",'"+hdmiName+"',"+hdmiChecked+")";
	    final String insert_mp3 = "insert into " + TABLE_PRODUCTS + " values ("+mp3ID+",'"+mp3Name+"',"+mp3Checked+")";
	    final String insert_headphone = "insert into " + TABLE_PRODUCTS + " values ("+headphoneID+",'"+headphoneName+"',"+headphoneChecked+")";
	    final String insert_pc = "insert into " + TABLE_PRODUCTS + " values ("+pcID+",'"+pcName+"',"+pcChecked+")";
	    final String insert_laptop = "insert into " + TABLE_PRODUCTS + " values ("+laptopID+",'"+laptopName+"',"+laptopChecked+")";
	    final String insert_tablet = "insert into " + TABLE_PRODUCTS + " values ("+tabletID+",'"+tabletName+"',"+tabletChecked+")";
	    final String insert_harddrive = "insert into " + TABLE_PRODUCTS + " values ("+harddriveID+",'"+harddriveName+"',"+harddriveChecked+")";
	    final String insert_dvddrive = "insert into " + TABLE_PRODUCTS + " values ("+dvddriveID+",'"+dvddriveName+"',"+dvddriveChecked+")";
	    final String insert_usbdrive = "insert into " + TABLE_PRODUCTS + " values ("+usbdriveID+",'"+usbdriveName+"',"+usbdriveChecked+")";
	    final String insert_keyboard = "insert into " + TABLE_PRODUCTS + " values ("+keyboardID+",'"+keyboardName+"',"+keyboardChecked+")";
	    final String insert_ps3 = "insert into " + TABLE_PRODUCTS + " values ("+ps3ID+",'"+ps3Name+"',"+ps3Checked+")";
	    final String insert_xbox = "insert into " + TABLE_PRODUCTS + " values ("+xboxID+",'"+xboxName+"',"+xboxChecked+")";
	    final String insert_wii = "insert into " + TABLE_PRODUCTS + " values ("+wiiID+",'"+wiiName+"',"+wiiChecked+")";
	    final String insert_ds3 = "insert into " + TABLE_PRODUCTS + " values ("+ds3ID+",'"+ds3Name+"',"+ds3Checked+")";
	    final String insert_psvita = "insert into " + TABLE_PRODUCTS + " values ("+psvitaID+",'"+psvitaName+"',"+psvitaChecked+")";
	    final String insert_usbcable = "insert into " + TABLE_PRODUCTS + " values ("+usbcableID+",'"+usbcableName+"',"+usbcableChecked+")";
	    final String insert_ethernetcable = "insert into " + TABLE_PRODUCTS + " values ("+ethernetcableID+",'"+ethernetcableName+"',"+ethernetcableChecked+")";
	    
	    database.execSQL(insert_flattv);
	    database.execSQL(insert_dvd);
	    database.execSQL(insert_hometheater);
	    database.execSQL(insert_hdmi);
	    database.execSQL(insert_mp3);
	    database.execSQL(insert_headphone);
	    database.execSQL(insert_pc);
		database.execSQL(insert_laptop);
		database.execSQL(insert_tablet);
		database.execSQL(insert_harddrive);
		database.execSQL(insert_dvddrive);
		database.execSQL(insert_usbdrive);
		database.execSQL(insert_keyboard);
		database.execSQL(insert_ps3);
		database.execSQL(insert_xbox);
		database.execSQL(insert_wii);
		database.execSQL(insert_ds3);
		database.execSQL(insert_psvita);
		database.execSQL(insert_usbcable);
		database.execSQL(insert_ethernetcable);
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
