package at.ac.uniklu.smartshopping;

import java.io.IOException;
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
import android.view.View;

import android.app.Activity;
import android.app.ProgressDialog;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Main activity.
 * 
 * @author Arda Akcay <ardaakcay@gmail.com>
 *
 */
public class MainActivity extends Activity {
	private TextView mStatusTv;
	private Button mPairedBtn;
	private Button mConnectBtn;
	private Button mCancelBtn;
	
	private ProgressDialog mProgressDlg;
	private ProgressDialog firstScreen;
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket socket = null;
	private BluetoothDevice device;
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	
	private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
	
//	private String serverName = "Arda's iPhone";
//	private String serverMacAddress = "80:EA:96:08:44:20";
	
//	private String serverName = "Serjinator";
//	private String serverMacAddress = "98:D6:F7:B2:3E:E9";
	
	private String serverName = "raspberrypi-0";
	private String serverMacAddress = "00:1B:DC:06:B5:B3";
	
	private Boolean connectionSuccessful;
	private Boolean isPairingFinished;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		connectionSuccessful = true;
		isPairingFinished = false;
		
		setContentView(R.layout.activity_main);
		
		mStatusTv 			= (TextView) findViewById(R.id.tv_status);
//		mActivateBtn 		= (Button) findViewById(R.id.btn_enable);
		mPairedBtn 			= (Button) findViewById(R.id.btn_view_paired);
		mConnectBtn 		= (Button) findViewById(R.id.btn_connect);
		mCancelBtn          = (Button) findViewById(R.id.btn_cancel);
		
		mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.enable();
		
		
		mProgressDlg 		= new ProgressDialog(this);
		firstScreen			= new ProgressDialog(this);
		
		
		mProgressDlg.setMessage("Connecting...");
		mProgressDlg.setCancelable(false);
		mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        
		        mBluetoothAdapter.cancelDiscovery();
		    }
		});
		
		firstScreen.setMessage("Loading...");
		firstScreen.setCancelable(false);
		
		
		if (mBluetoothAdapter == null) {
			showUnsupported();
		} else {
			mPairedBtn.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
					
					if (pairedDevices == null || pairedDevices.size() == 0) { 
						showToast("No Paired Devices Found");
					} else {
						ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
						
						list.addAll(pairedDevices);
						
						Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
						
						intent.putParcelableArrayListExtra("device.list", list);
						
						startActivity(intent);						
					}
				}
			});
			
			mConnectBtn.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					mBluetoothAdapter.startDiscovery();
				}
			});
			
			mCancelBtn.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					try {
						socket.close();
						enableConnectBtn();
						showToast("Connection terminated!");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			if (mBluetoothAdapter.isEnabled()) {
				showEnabled();
			} else {
				showDisabled();
			}
		}
		
		IntentFilter filter = new IntentFilter();
		
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,120);
		startActivity(discoverableIntent);
		
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		
		registerReceiver(mReceiver, filter);
			
	}
	
	@Override
	public void onPause() {
		if (mBluetoothAdapter != null) {
			if (mBluetoothAdapter.isDiscovering()) {
				mBluetoothAdapter.cancelDiscovery();
			}
		}
		//mBluetoothAdapter.disable();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		mBluetoothAdapter.disable();
		
		if(socket != null){
			if(socket.isConnected()){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		super.onDestroy();
	}
	
	@Override
	public void onStop(){
		mBluetoothAdapter.disable();
		if(socket != null){
			if(socket.isConnected()){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		super.onStop();
	}
	
	@Override
	public void onRestart(){
		mBluetoothAdapter.enable();
		enableConnectBtn();
		super.onRestart();
	}
	
	private void enableConnectBtn() {
		mConnectBtn.setEnabled(true);
		mCancelBtn.setEnabled(false);
	}
	
	private void enableCancelBtn() {
		mConnectBtn.setEnabled(false);
		mCancelBtn.setEnabled(true);
	}
	
	private void showEnabled() {
		mStatusTv.setText("Bluetooth ON");
		mStatusTv.setTextColor(Color.BLUE);
		
		mPairedBtn.setEnabled(true);
		mConnectBtn.setEnabled(true);
	}
	
	private void showDisabled() {
		mStatusTv.setText("Bluetooth OFF");
		mStatusTv.setTextColor(Color.RED);
		
		mPairedBtn.setEnabled(false);
		mConnectBtn.setEnabled(false);
		mCancelBtn.setEnabled(false);
	}
	
	private void showUnsupported() {
		mStatusTv.setText("Bluetooth is unsupported by this device");
		
		mPairedBtn.setEnabled(false);
		mConnectBtn.setEnabled(false);
		mCancelBtn.setEnabled(false);
	}
	
	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {	    	
	        String action = intent.getAction();
	        
	        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	        	final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
	        	 
	        	if (state == BluetoothAdapter.STATE_ON) {
	        		showEnabled();
	        	}
	        	
	        	final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
	        		
        		if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
        			showToast("Paired");
        			isPairingFinished = true;
        		}
        		else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
        			showToast("Unpaired");
        		}
	        }
	        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				mProgressDlg.show();
	        } 
	        else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	        	device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	        	
	        	if(device.getName().equals(serverName) && device.getAddress().equals(serverMacAddress)){
	        		showToast("Found the server");
	        		mBluetoothAdapter.cancelDiscovery();
	        		
	        		pairWithServer();

	        	    final Handler handler = new Handler();
	        	    handler.postDelayed(new Runnable() {
	        	      @Override
	        	      public void run() {
	        	    	  new BluetoothConnectionTask().execute();
	        	      }
	        	    }, 6000);
	        		
	        		mDeviceList.add(device);
	        	}	
	        }
	    }
	};
	
	protected class BluetoothConnectionTask extends AsyncTask<String, Void, String> {
	
		@Override
		protected String doInBackground(String... params) {
			
			connectToServerAndSendString();
			return null;
		}
		
		@Override
		protected void onPostExecute(String args) {
			if (connectionSuccessful) {
	        	showToast("Connection established...");
				enableCancelBtn();
    		}
    		else if (!connectionSuccessful) {
    			showToast("Connection CANNOT be established!");
		   		enableConnectBtn();
    		}
			
    		mProgressDlg.dismiss();
		}
	}
	
	
	
	private void connectToServerAndSendString() {
		try {
//			Method m=device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
//          socket = (BluetoothSocket) m.invoke(device, 1);
			
			// Connection to server
			socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
			mBluetoothAdapter.cancelDiscovery();
			socket.connect();
			
			// Send a string to server
			String data = "DATA SENT";
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(data.getBytes());
			socket.close();
		
		} catch (Exception connect) {
			connectionSuccessful = false;
			connect.printStackTrace();
 		   	try {
 		   		socket.close();
 		   	} catch (IOException close) {
 		   		close.printStackTrace();
 		   	}
		}
		mProgressDlg.dismiss();
	}
	
	private void pairWithServer() {
		Boolean isPaired = false;
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		for(BluetoothDevice bt : pairedDevices){
			if(bt.getName().equals(device.getName()) && bt.getAddress().equals(device.getAddress())){
				showToast("Already paired!");
				isPaired = true;
				break;
			}
		 }
		
		 if(!isPaired) {
			 showToast("Pairing...");
		
			 if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
				 try {
					 Method method = device.getClass().getMethod("removeBond", (Class[]) null);
					 method.invoke(device, (Object[]) null);
		
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
			 } 
			 else {
				 try {
					 Method method = device.getClass().getMethod("createBond", (Class[]) null);
					 method.invoke(device, (Object[]) null);
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
		
			 }
		}
		
	}
	
//	private class PairingThread implements Runnable {
//
//		@Override
//		public void run() {	
//			synchronized(lock) {
//           		pairWithServer();
//				lock.notifyAll();
//			}
//		}
//	}
//	private class ConnectionThread implements Runnable {
//
//		@Override
//		public void run() {
//			synchronized (lock) {
//	            try{
//	                lock.wait();
//	            }catch(InterruptedException e){
//	                e.printStackTrace();
//	            }
//	            
//	            showToast("Lock is unlocked.");
//	        }
//		}
//		
//	}
	

}
