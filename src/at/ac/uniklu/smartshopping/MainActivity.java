package at.ac.uniklu.smartshopping;

import java.io.IOException;
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
import android.os.Bundle;
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
//	private Button mActivateBtn;
	private Button mPairedBtn;
	private Button mConnectBtn;
	private Button mCancelBtn;
	
	private ProgressDialog mProgressDlg;
	private ProgressDialog firstScreen;
	
	private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket socket;
	private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
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
						mConnectBtn.setEnabled(true);
						mCancelBtn.setEnabled(false);
						showToast("Connection terminated!");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
//			mActivateBtn.setOnClickListener(new View.OnClickListener() {				
//				@Override
//				public void onClick(View v) {
//					if (mBluetoothAdapter.isEnabled()) {
//						mBluetoothAdapter.disable();
//						
//						showDisabled();
//					} else {
//						Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//						
//					    startActivityForResult(intent, 1000);
//					}
//				}
//			});
			
			if (mBluetoothAdapter.isEnabled()) {
				showEnabled();
			} else {
				showDisabled();
			}
		}
		
		IntentFilter filter = new IntentFilter();
		
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
		
		mBluetoothAdapter.disable();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		mBluetoothAdapter.disable();
		super.onDestroy();
	}
	
	@Override
	public void onStop(){
		mBluetoothAdapter.disable();
		super.onRestart();
	}
	
	@Override
	public void onRestart(){
		mBluetoothAdapter.enable();
		super.onRestart();
	}
	
	private void showEnabled() {
		mStatusTv.setText("Bluetooth ON");
		mStatusTv.setTextColor(Color.BLUE);
		
//		mActivateBtn.setText("Disable");		
//		mActivateBtn.setEnabled(true);
		
		mPairedBtn.setEnabled(true);
		mConnectBtn.setEnabled(true);
	}
	
	private void showDisabled() {
		mStatusTv.setText("Bluetooth OFF");
		mStatusTv.setTextColor(Color.RED);
		
//		mActivateBtn.setText("Enable");
//		mActivateBtn.setEnabled(true);
		
		mPairedBtn.setEnabled(false);
		mConnectBtn.setEnabled(false);
		mCancelBtn.setEnabled(false);
	}
	
	private void showUnsupported() {
		mStatusTv.setText("Bluetooth is unsupported by this device");
		
//		mActivateBtn.setText("Enable");
//		mActivateBtn.setEnabled(false);
		
		mPairedBtn.setEnabled(false);
		mConnectBtn.setEnabled(false);
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
	        	
//	        	final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
//	        	 
//	        	if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
//	        		showToast("Paired");
//	        	}
//	        	else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
//	        		showToast("Unpaired");
//	        	}
	        }
	        
	        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
	        	mDeviceList = new ArrayList<BluetoothDevice>();
				mProgressDlg.show();
	        } 
//	        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//	        	mProgressDlg.dismiss();
//	        	
//	        	Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
//	        	
//	        	newIntent.putParcelableArrayListExtra("device.list", mDeviceList);
//				
//				startActivity(newIntent);
//	        }
	        
	        else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	        	BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//		        Boolean isPaired = false;
	        	
	        	if(device.getName().equals(serverName) && device.getAddress().equals(serverMacAddress)){
	        		showToast("Found the server");
	        		mProgressDlg.dismiss();
	        		mBluetoothAdapter.cancelDiscovery();
	        		
//	        		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//	        		for(BluetoothDevice bt : pairedDevices){
//		        		if(bt.getName().equals(device.getName()) && bt.getAddress().equals(device.getAddress())){
//		        			showToast("Already paired!");
//		        			isPaired = true;
//		        			break;
//		        		}
//		        	}
//	        		
//	        		if(!isPaired)
//	        		{ 
//	    				showToast("Pairing...");
//	    				
//	    				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
//	    					try {
//		    					Method method = device.getClass().getMethod("removeBond", (Class[]) null);
//		    		            method.invoke(device, (Object[]) null);
//
//		    		        } catch (Exception e) {
//		    		            e.printStackTrace();
//		    		        }
//	    				} else {
//	    					showToast("Still Pairing...");
//	    					try {
//		    					Method method = device.getClass().getMethod("createBond", (Class[]) null);
//		    		            method.invoke(device, (Object[]) null);
//
//		    		        } catch (Exception e) {
//		    		            e.printStackTrace();
//		    		        }
//	    					
//	    				}
//	        		}
	        		
//	        		final ProgressDialog pausingDialog = ProgressDialog.show(MainActivity.this, "", "Waiting..", true);
//	        		new Thread() {
//	        			public void run() {
//	        				try {
//								sleep(4000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							} // The length to 'pause' for				
//	        				pausingDialog.dismiss();
//	        			}
//	        		}.start();
	        		
	        		try {
						socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
					} catch (IOException create) {
						showToast("Creation problem!!");
						create.printStackTrace();
					}
	        		
	        		mBluetoothAdapter.cancelDiscovery();
	        		
	        		try {
	        			socket.connect();
						showToast("Connection established...");
					} catch (IOException connect) {
						try {
							socket.close();
							showToast("Connection establishment problem!!");
						} catch (IOException close) {
							close.printStackTrace();
						}
					}
	        		
	        		mConnectBtn.setEnabled(false);
	        		mCancelBtn.setEnabled(true);
	        	}
	        	
//	        	mDeviceList.add(device);
	        }
	    }
	};
    
}
