package postpc2021.android.socialar


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.OnLocationCameraTransitionListener
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.CancelableCallback
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
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
	private var locations = ArrayList<Pair<Double, Double>>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar!!.hide()
		initializeMap(savedInstanceState)

		val profileImageButton = findViewById<ImageButton>(R.id.profileButton)
		profileImageButton.setImageResource(R.drawable.ic_profile_light)
		profileImageButton.setOnClickListener {
			val intent = Intent(this, ProfileActivity::class.java)
			startActivity(intent)
		}
		val changeViewButton = findViewById<ImageButton>(R.id.changeView)
		changeViewButton.setImageResource(android.R.drawable.ic_menu_search)
		changeViewButton.setOnClickListener{ view ->
			val fogBackground: ImageView = findViewById(R.id.fogBackground)
			if(this.mode == MapMode.FOG){
				this.mode = MapMode.TOP
				fogBackground.visibility = View.INVISIBLE
				mapboxMap?.setMaxZoomPreference(25.0)
				mapboxMap?.setMinZoomPreference(0.0)
				buildingPlugin!!.setVisibility(false)
				setCameraTrackingMode(CameraMode.NONE)
				changeViewButton.setImageResource(android.R.drawable.ic_menu_view)
			}
			else{
				this.mode = MapMode.FOG
				fogBackground.visibility = View.VISIBLE
				mapboxMap?.setMaxZoomPreference(18.8)
				mapboxMap?.setMinZoomPreference(15.0)
				buildingPlugin!!.setVisibility(true)
				setCameraTrackingMode(CameraMode.TRACKING_COMPASS)
				changeViewButton.setImageResource(android.R.drawable.presence_offline)
			}
		}
	}

	private fun getLocationsFromExtras()
	{
		val extras = intent.extras
		if(extras != null)
		{
			val favorites = extras.get("favorites")
			val myPosts = extras.get("myPosts")
			if(favorites != null)
			{
				this.locations = favorites as ArrayList<Pair<Double, Double>>

			}
			else if(myPosts != null)
			{
				this.locations = myPosts as ArrayList<Pair<Double, Double>>
			}
			else
			{
				return
			}
			val changeViewButton = findViewById<ImageButton>(R.id.changeView)
			changeViewButton.performClick()
			findViewById<ImageButton>(R.id.profileButton).visibility = View.GONE
			changeViewButton.visibility = View.GONE
			findViewById<AppCompatButton>(R.id.addMessage).visibility = View.GONE
		}
	}

	private fun setCameraTrackingMode(@CameraMode.Mode mode: Int) {
		val locationComponent = this.mapboxMap!!.locationComponent
		locationComponent.setCameraMode(mode, object : OnLocationCameraTransitionListener {
			override fun onLocationCameraTransitionFinished(@CameraMode.Mode cameraMode: Int) {
				if (mode != CameraMode.NONE) {
					locationComponent.zoomWhileTracking(17.17, 200, object : CancelableCallback {
						override fun onCancel() {
							// No impl
						}

						override fun onFinish() {
							locationComponent.tiltWhileTracking(59.0)
						}
					})
				} else {
					mapboxMap!!.animateCamera(CameraUpdateFactory.tiltTo(0.0), 500)
				}
			}

			override fun onLocationCameraTransitionCanceled(@CameraMode.Mode cameraMode: Int) {
				// No impl
			}
		})
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
			style.addImage(
					"marker",
					BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default)
			);
			enableLocationComponent(style)
//			val uiSettings = mapboxMap.uiSettings
//			uiSettings.isCompassEnabled = false
			getLocationsFromExtras()
			drawMarkersInMap(style)
		}
	}

	private fun drawMarkersInMap(style: Style){
		// Create symbol manager object.
		val symbolManager = SymbolManager(mapView!!, mapboxMap!!, style);
		symbolManager.iconAllowOverlap = true;
		symbolManager.textAllowOverlap = true;
		// Create a symbol at the specified location.
		for(location: Pair<Double, Double> in this.locations)
		{
			val symbol: SymbolOptions = SymbolOptions()
					.withLatLng(LatLng(location.first, location.second))
					.withIconImage("marker")
					.withIconSize(1.3f)
			symbolManager.create(symbol)
		}
//		val symbol: SymbolOptions = SymbolOptions()
//				.withLatLng(LatLng(32.687337, 84.381457))
//				.withIconImage("marker")
//				.withIconSize(1.3f)
//		symbolManager.create(symbol)
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
			locationComponent.renderMode = RenderMode.GPS
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