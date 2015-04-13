package iainsmart.carcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.ClipData;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.MotionEvent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

// TODO: Joysticks
// TODO: Accellerometers
// TODO: Bluetooth things
// TODO: Make an app that actually works?

public class MainActivity extends ActionBarActivity {
	private static final String TAG = "CarController";

	private static TextView yPos, xPos, txtRot, dir, orient;

	private SensorManager mSensorManager = null;
	private Sensor mRotationSensor = null;

	private bluetoothHandler mBluetoothHandler = null;

	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Loading");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		yPos = (TextView) findViewById(R.id.yPos);
		xPos = (TextView) findViewById(R.id.xPos);
		txtRot = (TextView) findViewById(R.id.rot);
		dir = (TextView) findViewById(R.id.dir);
		orient = (TextView) findViewById(R.id.orient);
		Log.d(TAG, "onCreate Finished");
	}

	@Override
	public void onStart() {
		super.onStart();

		Log.d(TAG, "onStart Running");
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mSensorManager.registerListener(mySensorEventListener, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

		if (!mBluetoothAdapter.isEnabled()) { // If Bluetooth is off
			Log.d(TAG, "Bluetooth Disabled");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //Request it be enabled
			startActivityForResult(enableBtIntent, 1);
		}
		else {
			Log.d(TAG, "Bluetooth Enabled");
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

			ArrayAdapter<String> devicesAdapter =
					new ArrayAdapter<String>(this, R.layout.list_layout);
			Log.d(TAG, "ArrayAdapter Created");

			// If there are paired devices
			if (pairedDevices.size() > 0) {
				Log.d(TAG, "Devices Paired: " + pairedDevices.size());
				// Loop through paired devices
				for (BluetoothDevice device : pairedDevices) {
					// Add the name and address to an array adapter to show in a ListView
					Log.d(TAG, "Adding device to Device Adapter");
					devicesAdapter.add(device.getName() + "\n" + device.getAddress());
				}
			}

			ListView pairedDevs = (ListView) findViewById(R.id.connected);
			pairedDevs.setAdapter(devicesAdapter);
			pairedDevs.setOnItemClickListener(mDeviceClickListener);
			Log.d(TAG, "ListView Adapter set.");
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mBluetoothHandler != null) {
			if (mBluetoothHandler.getState() == bluetoothHandler.STATE_NONE) {
				mBluetoothHandler.start();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mBluetoothHandler != null) {
			mBluetoothHandler.stop();
		}
	}

	private SensorEventListener mySensorEventListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int Accuracy) {
			// Do something? This shouldn't change
			// TODO: Add a Toast?
			Log.d(TAG, "Sensor Accuracy changed");
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			int devOrientation = getResources().getConfiguration().orientation;
			orient.setText("Orientation: " + String.valueOf(devOrientation));

			try {
				float azimuth = event.values[0];
				float pitch = event.values[1];
				float roll = event.values[2];

				float dPitch = (pitch * 10) + 6;

				String roundedPitch = String.format("Up/Down: %.1f\n(Corrected)", dPitch);
				String roundedRoll = String.format("Flat: %f", roll);
				String roundedAzimuth = String.format("Left/Right: %f", azimuth);

				txtRot.setText(roundedPitch + "\n" + roundedRoll + "\n" + roundedAzimuth);

				// Treat roundedPitch = 2.0 as neutral
				// TODO: Add button to reset this on a per-user dynamic basis

				if (dPitch < 1.0) {
					dir.setText(R.string.dirBackwards);
				} else if (dPitch < 2.5) {
					dir.setText(R.string.dirNone);
				} else {
					dir.setText(R.string.dirForwards);
				}
			} catch (Exception e) {
				txtRot.setText("Something went wrong. Oops.");
			}
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		String xTouch = Float.toString(event.getX());
		String yTouch = Float.toString(event.getY());
		yPos.setText("Y Touch Coordinate: " + yTouch);
		xPos.setText("X Touch Coordinate: " + xTouch);
		return true;
	}

	private AdapterView.OnItemClickListener mDeviceClickListener
			= new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int position, long id) {
			Log.d(TAG, "Item Clicked: " + id);

			String itemText = av.getItemAtPosition(position).toString();
			itemText = itemText.substring(itemText.length() - 17);
			Log.d(TAG, itemText);
			// Cancel discovery because it's costly and we're about to connect
			mBluetoothAdapter.cancelDiscovery();
			Log.d(TAG, "Discovery Cancelled");
		}
	};
}