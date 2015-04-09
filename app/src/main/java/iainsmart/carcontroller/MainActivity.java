package iainsmart.carcontroller;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.MotionEvent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

// TODO: Joysticks
// TODO: Accellerometers
// TODO: Bluetooth things
// TODO: Make an app that actually works?

public class MainActivity extends ActionBarActivity {
	private static TextView yPos, xPos, txtRot, dir, orient;
	private static ListView pairedDevs;

	private SensorManager mSensorManager;
	private Sensor mRotationSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		yPos = (TextView) findViewById(R.id.yPos);
		xPos = (TextView) findViewById(R.id.xPos);
		txtRot = (TextView) findViewById(R.id.rot);
		dir = (TextView) findViewById(R.id.dir);
		orient = (TextView) findViewById(R.id.orient);
		pairedDevs = (ListView) findViewById(R.id.connected);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mSensorManager.registerListener(mySensorEventListener, mRotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) { // If there is a Bluetooth adapter
			if (!mBluetoothAdapter.isEnabled()) { // If Bluetooth is off
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //Request it be enabled
				startActivityForResult(enableBtIntent, 1);
			}
			else {
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

				ArrayAdapter<String> devicesAdapter =
						new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

				// If there are paired devices
				if (pairedDevices.size() > 0) {
					// Loop through paired devices
					for (BluetoothDevice device : pairedDevices) {
						// Add the name and address to an array adapter to show in a ListView
						devicesAdapter.add(device.getName() + "\n" + device.getAddress());
					}
				}
				pairedDevs.setAdapter(devicesAdapter);
			}
		}
		// TODO: Something if there ain't no Bluetooth?
	}

	private SensorEventListener mySensorEventListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int Accuracy) {
			// Do something? This shouldn't change
			// TODO: Add a Toast?
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			int devOrientation = getResources().getConfiguration().orientation;
			orient.setText("Orientation: " + String.valueOf(devOrientation));

			try {
				float azimuth = event.values[0];
				float pitch = event.values[1];
				float roll = event.values[2];

				String roundedPitch = String.format("Up/Down: %.2f", pitch * 10);
				String roundedRoll = String.format("Flat: %.2f", roll * 10);
				String roundedAzimuth = String.format("Left/Right: %.2f", azimuth * 10);

				txtRot.setText(roundedPitch + "\n" + roundedRoll + "\n" + roundedAzimuth);

				if (pitch < 0.15) {
					dir.setText(R.string.dirForwards);
				} else if (pitch < 0.25) {
					dir.setText(R.string.dirNone);
				} else {
					dir.setText(R.string.dirBackwards);
				}
			} catch (Exception e) {
				txtRot.setText("Something went wrong. Oops.");
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		String xTouch = Float.toString(event.getX());
		String yTouch = Float.toString(event.getY());
		yPos.setText("Y Touch Coordinate: " + yTouch);
		xPos.setText("X Touch Coordinate: " + xTouch);
		return true;
	}
}
