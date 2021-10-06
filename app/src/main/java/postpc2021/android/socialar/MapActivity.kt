package postpc2021.android.socialar


import android.Manifest
import android.content.Context
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
import androidx.lifecycle.Observer
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
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
import postpc2021.android.socialar.arComponents.ARView
import postpc2021.android.socialar.dataTypes.MessageData
import postpc2021.android.socialar.dataTypes.UserData


enum class MapMode {
	FOG, TOP
}

class MapActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener  {
	private var mapView: MapView? = null
	private var buildingPlugin: BuildingPlugin? = null
	private var permissionsManager: PermissionsManager? = null
	private var mapboxMap : MapboxMap? = null
	private var mode = MapMode.FOG
	private var locations = ArrayList<MessageData>()
	val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
	private var userData: UserData? = null


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar!!.hide()
		initializeMap(savedInstanceState)

		val addMessageButton = findViewById<AppCompatButton>(R.id.addMessage)
		addMessageButton.setOnClickListener {
			val intent = Intent(this, NewMessageActivity::class.java)
			val requestCode = 424242
			startActivityForResult(intent, requestCode)
		}
		val changeViewButton = findViewById<ImageButton>(R.id.changeView)
		val profileImageButton = findViewById<ImageButton>(R.id.profileButton)
		val arImageButton = findViewById<ImageButton>(R.id.arView)

		profileImageButton.setOnClickListener {
			val intent = Intent(this, ProfileActivity::class.java)
			startActivity(intent)
		}
		arImageButton.setImageResource(android.R.drawable.ic_menu_camera)
		arImageButton.setOnClickListener {
			val intent = Intent(this, ARView::class.java)
			startActivity(intent)
		}
		fireBaseManager.getUserDetails(fireBaseManager.getUserID(), ::setUserData)
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
				changeViewButton.setImageResource(android.R.drawable.ic_menu_search)
			}
		}
	}

	fun setUserData(userData: UserData){
		val profileImageButton = findViewById<ImageButton>(R.id.profileButton)
		if(!userData.profilePicture.isEmpty()){
			DownloadImageTask(findViewById(R.id.profileButton))
					.execute(userData.profilePicture);
		}
		else{
			profileImageButton.setImageResource(R.drawable.ic_profile_light)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		this.onNewMessageActivityResult(requestCode, resultCode, data)
	}

	private fun onNewMessageActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
		val requestCodeVerify = 424242
		if(requestCode == requestCodeVerify && resultCode == RESULT_OK)
		{
			val location = this.mapboxMap!!.locationComponent.lastKnownLocation!!
			val userid = this.fireBaseManager.getUserID()
			val postcontent = data!!.getStringExtra("postcontent").toString()
			val newMessageData = MessageData(userid, location.latitude, location.longitude, postcontent)
			this.fireBaseManager.uploadMessage(newMessageData)
			this.locations.add(newMessageData)

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
			val uiSettings = mapboxMap.uiSettings
			uiSettings.isCompassEnabled = false
			val locationComponent = this.mapboxMap!!.locationComponent
			val symbolManager = SymbolManager(mapView!!, mapboxMap, style);
			symbolManager.iconAllowOverlap = true;
			symbolManager.textAllowOverlap = true;
			fireBaseManager.setMessagesAroundView(locationComponent.lastKnownLocation!!.latitude, locationComponent.lastKnownLocation!!.longitude)
			fireBaseManager.messagesAroundViewLiveDataPublic.observe(this, Observer {
				drawMarkersInMap(symbolManager)
			})
		}
		val that = this
		mapboxMap.addOnMoveListener(object : MapboxMap.OnMoveListener {
			override fun onMoveBegin(detector: MoveGestureDetector) {
			}

			override fun onMove(detector: MoveGestureDetector) {
			}

			override fun onMoveEnd(detector: MoveGestureDetector) {
				fireBaseManager.setMessagesAroundView(mapboxMap.cameraPosition.target.latitude, mapboxMap.cameraPosition.target.longitude)
			}
		})
	}

	private fun drawMarkersInMap(symbolManager: SymbolManager){
		// Create symbol manager object.
		var symbol: SymbolOptions
		this.locations = fireBaseManager.getMessagesAroundView() as ArrayList<MessageData>
		symbolManager.deleteAll()
		// Create a symbol at the specified location.
		for(location: MessageData in this.locations)
		{
			symbol = SymbolOptions()
					.withLatLng(LatLng(location.latitude, location.longitude))
					.withIconImage("marker")
					.withTextAnchor("PinText")
					.withIconSize(1.3f)
			symbolManager.create(symbol)
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