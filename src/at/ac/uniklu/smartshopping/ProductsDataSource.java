package at.ac.uniklu.smartshopping;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ProductsDataSource {
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
									MySQLiteHelper.COLUMN_NAME, 
									MySQLiteHelper.COLUMN_CHECKED };

	public ProductsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
	    dbHelper.close();
	}
	
	public void updateProductChecked(ShoppingItem product, Boolean checked) {
		String name = product.getText();
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CHECKED, (checked) ? 1 : 0);

		database.update(MySQLiteHelper.TABLE_PRODUCTS, values, MySQLiteHelper.COLUMN_NAME + " =?",
				  new String[] { String.valueOf(name) });
	}
	
	public ArrayList<ShoppingItem> getAllProducts() {
	    ArrayList<ShoppingItem> products = new ArrayList<ShoppingItem>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_PRODUCTS,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	ShoppingItem product = cursorToProduct(cursor);
	    	products.add(product);
	    	cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return products;
	  }
	
	private ShoppingItem cursorToProduct(Cursor cursor) {
	    ShoppingItem product = new ShoppingItem();
	    product.setText(cursor.getString(1));
	    product.setChecked((cursor.getInt(2)) == 0 ? false : true);
	    return product;
	}


}
