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
import android.view.View;

import android.app.Activity;
import android.app.ProgressDialog;

import android.widget.Button;
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
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket socket;
	private BluetoothServerSocket  mServerSocket;
	private BluetoothDevice device;
	private OutputStream mmOutputStream;
    private InputStream mmInputStream;
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private String receivedString;
	
	private String serverName = "Arda's iPhone";
	private String serverMacAddress = "80:EA:96:08:44:20";
	
//	private String serverName = "Serjinator";
//	private String serverMacAddress = "98:D6:F7:B2:3E:E9";
	
//	private String serverName = "raspberrypi-0";
//	private String serverMacAddress = "00:1B:DC:06:B5:B3";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	
		mProgressDlg = new ProgressDialog(this);	
		mProgressDlg.setMessage("Connecting...");
		mProgressDlg.setCancelable(false);
		
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,120);
		startActivity(discoverableIntent);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		mBluetoothAdapter.enable();
		
		if (mBluetoothAdapter != null) {
			mProgressDlg.show();
			
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					new BluetoothConnectionTask().execute();
				}
			}, 5000);
			
		} 
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		closeBluetoothSocket();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		closeBluetoothSocket();
		mBluetoothAdapter.disable();	
	}
	
	@Override
	public void onStop(){
		super.onStop();
		
		closeBluetoothSocket();
		mBluetoothAdapter.disable();
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
	
	private class BluetoothConnectionTask extends AsyncTask<String, Void, String> {
	
		@Override
		protected String doInBackground(String... params) {
//			waitForIncomingConnections();
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
			showToast(receivedString);
    		mProgressDlg.dismiss();
		}
	}

	private void waitForIncomingConnections() {
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
		
		// Send ack to server
		String data = "ack";
		OutputStream outputStream;
		
		try {
			outputStream = socket.getOutputStream();
			outputStream.write(data.getBytes());
		} catch (IOException ex) {
			Log.e("BluetoothClient","Couldn't write to outputstream."+ex.getMessage());
		}
		
		try {
			Thread.sleep(3000);
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
