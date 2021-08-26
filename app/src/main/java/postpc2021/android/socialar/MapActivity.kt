package postpc2021.android.socialar


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin


enum class MapMode {
	FOG, TOP
}

class MapActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener  {
	private var mapView: MapView? = null
	private var buildingPlugin: BuildingPlugin? = null
	private var permissionsManager: PermissionsManager? = null
	private var mapboxMap : MapboxMap? = null
	private var mode = MapMode.FOG

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar!!.hide()
		initializeMap(savedInstanceState)

		val changeViewButton = findViewById<ImageButton>(R.id.changeView)
		changeViewButton.setOnClickListener{ view ->
			var position: CameraPosition? = null
			val currentCameraPosition = mapboxMap?.cameraPosition
			val currentZoom = currentCameraPosition?.zoom
			val fogBackground: ImageView = findViewById(R.id.fogBackground)
			if(this.mode == MapMode.FOG){
				this.mode = MapMode.TOP
				fogBackground.visibility = View.INVISIBLE
				position = CameraPosition.Builder().tilt(0.0).zoom(currentZoom!!).target(currentCameraPosition.target).build()
				mapboxMap?.setMaxZoomPreference(25.0)
				buildingPlugin!!.setVisibility(false)

			}
			else{
				this.mode = MapMode.FOG
				fogBackground.visibility = View.VISIBLE
				position = CameraPosition.Builder().tilt(59.0).zoom(currentZoom!!).target(currentCameraPosition.target).build()
				mapboxMap?.setMaxZoomPreference(18.8)
				buildingPlugin!!.setVisibility(true)
			}
			mapboxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
		}
	}

	fun initializeMap(savedInstanceState: Bundle?){
		// Mapbox access token is configured here. This needs to be called either in your application
		// object or in the same activity which contains the mapview.
		Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

		// This contains the MapView in XML and needs to be called after the access token is configured.
		setContentView(R.layout.activity_map)
		mapView = findViewById(R.id.mapView)
		mapView?.onCreate(savedInstanceState)
		mapView?.getMapAsync(this)
	}

	override fun onMapReady(mapboxMap: MapboxMap) {
		this.mapboxMap = mapboxMap;
		mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
			// Using the Mapbox Building Plugin to easily display 3D extrusions on the map
			buildingPlugin = BuildingPlugin(mapView!!, mapboxMap, style)
			buildingPlugin!!.setVisibility(this.mode == MapMode.FOG)
			enableLocationComponent(style)
			val uiSettings = mapboxMap.uiSettings
			uiSettings.isCompassEnabled = false
		}
	}

	private fun enableLocationComponent(loadedMapStyle: Style) {
		// Check if permissions are enabled and if not request
		if (PermissionsManager.areLocationPermissionsGranted(this)) {
			// Enable the most basic pulsing styling by ONLY using
			// the `.pulseEnabled()` method
			val customLocationComponentOptions = LocationComponentOptions.builder(this)
					.pulseEnabled(true)
					.build()

			// Get an instance of the component
			val locationComponent = this.mapboxMap!!.locationComponent

			// Activate with options
			locationComponent.activateLocationComponent(
					LocationComponentActivationOptions.builder(this, loadedMapStyle)
							.locationComponentOptions(customLocationComponentOptions)
							.build())

			// Enable to make component visible
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return
			}
			locationComponent.isLocationComponentEnabled = true

			// Set the component's camera mode
			locationComponent.cameraMode = CameraMode.TRACKING_COMPASS

			// Set the component's render mode
			locationComponent.renderMode = RenderMode.COMPASS
		} else {
			permissionsManager = PermissionsManager(this)
			permissionsManager!!.requestLocationPermissions(this)
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
	}

	override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
		Toast.makeText(this, "We need location permission", Toast.LENGTH_LONG).show()
	}

	override fun onPermissionResult(granted: Boolean) {
		if (granted) {
			mapboxMap!!.getStyle { style -> enableLocationComponent(style) }
		} else {
			Toast.makeText(this, "No permission for location", Toast.LENGTH_LONG).show()
			finish()
		}
	}

	// Add the mapView lifecycle to the activity's lifecycle methods
	public override fun onResume() {
		super.onResume()
		mapView!!.onResume()
	}

	override fun onStart() {
		super.onStart()
		mapView!!.onStart()
	}

	override fun onStop() {
		super.onStop()
		mapView!!.onStop()
	}

	public override fun onPause() {
		super.onPause()
		mapView!!.onPause()
	}

	override fun onLowMemory() {
		super.onLowMemory()
		mapView!!.onLowMemory()
	}

	override fun onDestroy() {
		super.onDestroy()
		mapView!!.onDestroy()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		mapView!!.onSaveInstanceState(outState)
	}
}