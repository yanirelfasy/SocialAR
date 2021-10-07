package postpc2021.android.socialar.arComponents

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import com.wikitude.architect.ArchitectStartupConfiguration
import com.wikitude.architect.ArchitectView
import com.wikitude.common.camera.CameraSettings.CameraResolution
import postpc2021.android.socialar.*
import postpc2021.android.socialar.dataTypes.MessageData
import postpc2021.android.socialar.dataTypes.UserData
import java.util.zip.Inflater
import android.view.LayoutInflater





open class ARView : AppCompatActivity() {
	var architectView: ArchitectView? = null
	var config = ArchitectStartupConfiguration()
	private var poiExtension: PoiDataFromApplicationModelExtension? = null
	private var geoExtension: GeoExtension? = null
	private var messageActionsMiddleware: MessageActionsMiddleware? = null
	val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar!!.hide()
		architectView = ArchitectView(this)
		config.licenseKey = getString(R.string.wikitude_license_key)
		config.cameraResolution = CameraResolution.HD_1280x720
		architectView!!.onCreate(config) // create ArchitectView with configuration
		setContentView(architectView)
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		poiExtension = PoiDataFromApplicationModelExtension(this, architectView)
		geoExtension = GeoExtension(this, architectView)
		messageActionsMiddleware = MessageActionsMiddleware(this, architectView!!)
		geoExtension!!.setLocationListenerExtension(poiExtension)
		geoExtension!!.onCreate()
		poiExtension!!.onCreate()
		messageActionsMiddleware!!.onCreate()
		addButtons()
	}

	private fun addButtons()
	{
		val inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val childLayout = inflater.inflate(R.layout.activity_a_r_view, findViewById(R.id.constraintLayout))
		addContentView(childLayout, RelativeLayout.LayoutParams(
		RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT))
		val addMessageButton = findViewById<AppCompatButton>(R.id.addMessage)
		addMessageButton.setOnClickListener{
			val intent = Intent(this, NewMessageActivity::class.java)
			val requestCode = 424242
			startActivityForResult(intent, requestCode)
		}
		val profileButton = findViewById<ImageButton>(R.id.profileButton)
		fireBaseManager.getUserDetails(fireBaseManager.getUserID(), ::setUserData)
		profileButton.setOnClickListener {
			val intent = Intent(this, ProfileActivity::class.java)
			startActivity(intent)
		}
		val changeViewButton = findViewById<ImageButton>(R.id.mapView)
		changeViewButton.setOnClickListener {
			val intent = Intent(this, MapActivity::class.java)
			startActivity(intent)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		this.onNewMessageActivityResult(requestCode, resultCode, data)
	}

	private fun onNewMessageActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
		val requestCodeVerify = 424242
		// TODO: add new message on AR
		if(requestCode == requestCodeVerify && resultCode == RESULT_OK)
		{
//			val location = this.mapboxMap!!.locationComponent.lastKnownLocation!!
//			val userid = this.fireBaseManager.getUserID()
//			val postContent = data!!.getStringExtra("postContent").toString()
//			val mediaUris = data.getStringArrayListExtra("mediaUris") as ArrayList
//			val newMessageData = MessageData(userID=userid,
//					latitude=location.latitude,
//					longitude=location.longitude,
//					textContent=postContent,
//					mediaContent=mediaUris)
//			this.fireBaseManager.uploadMessage(newMessageData)
//			this.locations.add(newMessageData)

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


	override fun onPostCreate(savedInstanceState: Bundle?) {
		super.onPostCreate(savedInstanceState)
		if(architectView != null){
			architectView!!.onPostCreate()
			try {
				architectView!!.load("ARMessages/index.html")
				geoExtension!!.onPostCreate()
				poiExtension!!.onPostCreate()
			}catch (e: Exception){
				Toast.makeText(this, "EXCEPTION", Toast.LENGTH_SHORT).show()
			}
		}
	}

	override fun onDestroy() {
		geoExtension!!.onDestroy()
		poiExtension!!.onDestroy()
		messageActionsMiddleware!!.onDestroy()
		super.onDestroy()
		architectView?.onDestroy()
	}

	override fun onPause() {
		geoExtension!!.onPause()
		poiExtension!!.onPause()
		super.onPause()
		architectView?.onPause()
	}

	override fun onResume() {
		super.onResume()
		architectView?.onResume()
		geoExtension!!.onResume()
		poiExtension!!.onResume()
	}
}