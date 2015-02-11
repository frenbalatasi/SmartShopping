package at.ac.uniklu.smartshopping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;

import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

/**
 * Main activity.
 * 
 * @author Arda Akcay <ardaakcay@gmail.com>
 *
 */
public class MainActivity extends Activity {	
	private ProgressDialog mProgressDlg;
	private Button buttonCheckout;
	private ListView listView;
	
	private ProductsDataSource datasource;
	private ArrayList<String> habitsList;
	
	private ShoppingListAdapter slAdapter;
	private ArrayList<ShoppingItem> shoppingList;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket socket;
	private BluetoothServerSocket  mServerSocket;
	private BluetoothDevice device;
	private OutputStream mmOutputStream;
    private InputStream mmInputStream;
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private String receivedString = "";
    private String sentString = "";
	
//	private String serverName = "Arda's iPhone";
//	private String serverMacAddress = "80:EA:96:08:44:20";
	
//	private String serverName = "Serjinator";
//	private String serverMacAddress = "98:D6:F7:B2:3E:E9";
	
	private String serverName = "raspberrypi-0";
	private String serverMacAddress = "00:1B:DC:06:B5:B3";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	   
		listView = (ListView)findViewById(R.id.listView);
		buttonCheckout = (Button)findViewById(R.id.buttonCheckout);
		
		shoppingList = new ArrayList<ShoppingItem>();
		
		datasource = new ProductsDataSource(this);
		datasource.open();
		shoppingList = datasource.getAllProducts();
		habitsList = datasource.getAllHabitInfo();
		
		for (int i = 0; i < shoppingList.size(); i++) {
			switch(i) {
				case 0:
					shoppingList.get(i).setTable("Televisions");
					break;
				case 1:
					shoppingList.get(i).setTable("Video Players & Recorders");
					break;
				case 2:
					shoppingList.get(i).setTable("Audio Players & Recorders");
					break;
				case 3:
					shoppingList.get(i).setTable("Audio & Video Cables");
					break;
				case 4:
					shoppingList.get(i).setTable("Audio Players & Recorders");
					break;
				case 5:
					shoppingList.get(i).setTable("Audio Components");
					break;
				case 6:
					shoppingList.get(i).setTable("Computers");
					break;
				case 7:
					shoppingList.get(i).setTable("Computers");
					break;
				case 8:
					shoppingList.get(i).setTable("Computers");
					break;
				case 9:
					shoppingList.get(i).setTable("Storage Devices");
					break;
				case 10:
					shoppingList.get(i).setTable("Optical Drives");
					break;
				case 11:
					shoppingList.get(i).setTable("Storage Devices");
					break;
				case 12:
					shoppingList.get(i).setTable("Computer Accessories");
					break;
				case 13:
					shoppingList.get(i).setTable("Home Game Consoles");
					break;
				case 14:
					shoppingList.get(i).setTable("Home Game Consoles");
					break;
				case 15:
					shoppingList.get(i).setTable("Home Game Consoles");
					break;
				case 16:
					shoppingList.get(i).setTable("Portable Game Consoles");
					break;
				case 17:
					shoppingList.get(i).setTable("Portable Game Consoles");
					break;
				case 18:
					shoppingList.get(i).setTable("Data Transfer Cables");
					break;
				case 19:
					shoppingList.get(i).setTable("Network Cables");
					break;
			}
			
		}
		
		slAdapter = new ShoppingListAdapter(this);
		slAdapter.setData(shoppingList);
		listView.setAdapter(slAdapter);
		slAdapter.notifyDataSetChanged();
		
		mProgressDlg = new ProgressDialog(this);	
		mProgressDlg.setMessage("Connecting...");
		mProgressDlg.setCancelable(false);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);
		startActivity(discoverableIntent);
		
//		for (int i = 0; i < shoppingList.size(); i++) {
//			if(shoppingList.get(i).isChecked()) {
//				sentString = sentString + (shoppingList.get(i).getTable()+">")+(shoppingList.get(i).getText()) + ",";
//			}
//		}
		
		sentString = habitsList.get(0);
		
		if (mBluetoothAdapter != null) {
			
			final Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					new BluetoothConnectionTask().execute();
				}
			});
			
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		datasource.open();
		
		toggleListeners();
		 
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		datasource.close();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		datasource.close();	
	}
	
	@Override
	public void onStop(){
		super.onStop();
		
		datasource.close();
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
		if (id == R.id.action_refresh) {
			
			final Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					new BluetoothConnectionTask().execute();
				}
			});
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	private void closeBluetoothSocket() {
		if(socket != null){
			if(socket.isConnected()){
				try {
					socket.close();
				} catch (IOException ex) {
					Log.e("BluetoothSocket","Couldn't close the bluetooth socket."+ex.getMessage());
				}
			}
		}
	}
	
	private void toggleListeners() {
		
		slAdapter.setRadioButtonClickListener(new ShoppingListAdapter.OnRadioButtonClickListener() {			
			@Override
			public void onRadioButtonClick(int position){
				ShoppingItem selectedItem = shoppingList.get(position);
				if(selectedItem.isChecked() == false) {
					selectedItem.setChecked(true);
					datasource.updateProductChecked(selectedItem, true);
				}
				else if(selectedItem.isChecked() == true){
					selectedItem.setChecked(false);
					datasource.updateProductChecked(selectedItem, false);
				}
				
				slAdapter.notifyDataSetChanged();
			}
		});
		
		buttonCheckout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				habitsList = datasource.getAllHabitInfo();
				String checkedItemString = "";
				
				for (int i = 0; i < shoppingList.size(); i++) {
					if(shoppingList.get(i).isChecked()) {
						checkedItemString =  checkedItemString + (shoppingList.get(i).getTable()+">")+(shoppingList.get(i).getText()) + ",";
					}
				}
				
				sentString = habitsList.get(0).concat(checkedItemString);
				datasource.updateHabitInfo(sentString);
				
			}
	    });
	}
	
	private class BluetoothConnectionTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			
		}
	
		@Override
		protected String doInBackground(String... params) {
			waitForIncomingConnections();
			
			try {
				socket.close();
			} catch (IOException ex) {
				Log.e("BluetoothSocket","Couldn't close the bluetooth socket."+ex.getMessage());
			}
			
			connectToServerAndSendString();
			return null;
		}
		
		@Override
		protected void onPostExecute(String args) {
			
			final Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					new BluetoothConnectionTask().execute();
				}
			});
		}
	}

	private void waitForIncomingConnections() {
		mBluetoothAdapter.disable();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			Log.e("ThreadWaiting","Thread couldn't sleep."+ex.getMessage());
		}
		
		mBluetoothAdapter.enable();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			Log.e("ThreadWaiting","Thread couldn't sleep."+ex.getMessage());
		}
		
		BluetoothServerSocket tmp = null;
		
		try {
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("SmartShopping", uuid);
        } catch (IOException ex) { 
        	Log.e("BluetoothServer","Couldn't listen the server dispatch."+ex.getMessage());
        }
        mServerSocket = tmp;
        socket = null;
        
        while(true){
        	try {
                socket = mServerSocket.accept();
            } catch (IOException ex) {
            	Log.e("BluetoothServer","Couldn't accept the incoming message."+ex.getMessage());
            	break;
            }
            
            if (socket != null) {
            	beginListenForData();
                break;
            }
        }
	}
	
	private void beginListenForData() {
		int bufferSize = 1024; 
		byte[] readBuffer = new byte[1024];
		
		 try {
			 mmInputStream = socket.getInputStream();
			 int bytesRead = -1;
			 receivedString = "";
			 
			 while (true) {
				 bytesRead = mmInputStream.read(readBuffer);
				 if (bytesRead != -1) {
					 
					 while ((bytesRead==bufferSize)&&(readBuffer[bufferSize-1] != 0)) {
						 receivedString = receivedString + new String(readBuffer, 0, bytesRead);
						 bytesRead = mmInputStream.read(readBuffer);
					 }
					 
					 receivedString = receivedString + new String(readBuffer, 0, bytesRead);
					 socket.getInputStream();
				 }

			 }
		 } catch (IOException ex) { 
			 Log.e("BluetoothServer","Couldn't read the inputstream."+ex.getMessage());
		 }
	}
	
	private void connectToServerAndSendString() {
		BluetoothSocket tmp = null;
		
		// Connection to server
		try {
			device = mBluetoothAdapter.getRemoteDevice(serverMacAddress);
			tmp = device.createRfcommSocketToServiceRecord(uuid);
		}
		catch (IOException ex) {
			Log.e("BluetoothClient","Couldn't create the socket."+ex.getMessage());
		}
	
		socket = tmp;
		
		try {
			socket.connect();
		} catch (IOException ex) {
			Log.e("BluetoothClient","Couldn't connect to server."+ex.getMessage());
		}
		
		// Send the checkout information to server
		try {
			mmOutputStream = socket.getOutputStream();
			mmOutputStream.write(sentString.getBytes());
		} catch (IOException ex) {
			Log.e("BluetoothClient","Couldn't write to outputstream."+ex.getMessage());
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ex) {
			Log.e("BluetoothClient","Thread couldn't sleep."+ex.getMessage());
		}

		try {
			socket.close();
		} catch (IOException ex) {
			Log.e("BluetoothSocket","Couldn't close the bluetooth socket."+ex.getMessage());
		}
	}
}
