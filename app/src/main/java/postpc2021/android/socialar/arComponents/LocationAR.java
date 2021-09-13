package postpc2021.android.socialar.arComponents;


import com.wikitude.architect.ArchitectView;

import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Objects;

/**
 * This Activity is (almost) the least amount of code required to use the
 * basic functionality for Geo AR.
 *
 * This Activity needs Manifest.permission.ACCESS_FINE_LOCATION permissions
 * in addition to the required permissions of the SimpleArActivity.
 */
public class LocationAR extends ARView implements LocationListener {

	/**
	 * Very basic location provider to enable location updates.
	 * Please note that this approach is very minimal and we recommend to implement a more
	 * advanced location provider for your app. (see https://developer.android.com/training/location/index.html)
	 */
	private LocationProvider locationProvider;

	/**
	 * Error callback of the LocationProvider, noProvidersEnabled is called when neither location over GPS nor
	 * location over the network are enabled by the device.
	 */
	private final LocationProvider.ErrorCallback errorCallback = new LocationProvider.ErrorCallback() {
		@Override
		public void noProvidersEnabled() {
			Toast.makeText(LocationAR.this, "Please enable GPS and Network positioning in your Settings and restart the Activity", Toast.LENGTH_LONG).show();
		}
	};

	/**
	 * The ArchitectView.SensorAccuracyChangeListener notifies of changes in the accuracy of the compass.
	 * This can be used to notify the user that the sensors need to be recalibrated.
	 *
	 * This listener has to be registered after onCreate and unregistered before onDestroy in the ArchitectView.
	 */
	private final ArchitectView.SensorAccuracyChangeListener sensorAccuracyChangeListener = new ArchitectView.SensorAccuracyChangeListener() {
		@Override
		public void onCompassAccuracyChanged(int accuracy) {
			if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) { // UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3
				Toast.makeText(LocationAR.this, "please re-calibrate compass by waving your device in a figure 8 motion.", Toast.LENGTH_LONG ).show();
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationProvider = new LocationProvider(this, this, errorCallback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationProvider.onResume();
		/*
		 * The SensorAccuracyChangeListener has to be registered to the Architect view after ArchitectView.onCreate.
		 * There may be more than one SensorAccuracyChangeListener.
		 */
		Objects.requireNonNull(this.getArchitectView()).registerSensorAccuracyChangeListener(sensorAccuracyChangeListener);
	}

	@Override
	protected void onPause() {
		locationProvider.onPause();
		super.onPause();
		// The SensorAccuracyChangeListener has to be unregistered from the Architect view before ArchitectView.onDestroy.
		Objects.requireNonNull(this.getArchitectView()).unregisterSensorAccuracyChangeListener(sensorAccuracyChangeListener);
	}

	/**
	 * The ArchitectView has to be notified when the location of the device
	 * changed in order to accurately display the Augmentations for Geo AR.
	 *
	 * The ArchitectView has two methods which can be used to pass the Location,
	 * it should be chosen by whether an altitude is available or not.
	 */
	@Override
	public void onLocationChanged(Location location) {
		float accuracy = location.hasAccuracy() ? location.getAccuracy() : 1000;
		if (location.hasAltitude()) {
			Objects.requireNonNull(this.getArchitectView()).setLocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), accuracy);
		} else {
			Objects.requireNonNull(this.getArchitectView()).setLocation(location.getLatitude(), location.getLongitude(), accuracy);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}
}
