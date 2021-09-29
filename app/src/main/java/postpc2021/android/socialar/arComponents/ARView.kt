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


open class ARView : AppCompatActivity() {
	var architectView: ArchitectView? = null
	var config = ArchitectStartupConfiguration()
	private var poiExtension: PoiDataFromApplicationModelExtension? = null
	private var geoExtension: GeoExtension? = null
	private var messageActionsMiddleware: MessageActionsMiddleware? = null
	val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
	var profileViewID = 0

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
		// create container for buttons
		val containerLayout = RelativeLayout(this)
		containerLayout.setLayoutParams(RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT))

		// add newMessageButton
		val addMessageButton = AppCompatButton(this)
		val addMessageParams = RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		)
		addMessageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
		addMessageParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
		addMessageButton.background = AppCompatResources.getDrawable(this, R.drawable.button_primary)
		addMessageButton.text = "+"
		addMessageButton.layoutParams = addMessageParams
		addMessageButton.setOnClickListener{
			val intent = Intent(this, NewMessageActivity::class.java)
			val requestCode = 424242
			startActivityForResult(intent, requestCode)
		}
		containerLayout.addView(addMessageButton)


		// add profileButton
		val profileButton = ImageButton(this)
		this.profileViewID = View.generateViewId()
		profileButton.id = this.profileViewID
		fireBaseManager.getUserDetails(fireBaseManager.getUserID(), ::setUserData)
		val profileButtonParams = RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		)
		profileButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
		profileButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
		profileButton.background = AppCompatResources.getDrawable(this, R.drawable.button_primary)
		profileButton.layoutParams = profileButtonParams
		profileButton.setOnClickListener {
			val intent = Intent(this, ProfileActivity::class.java)
			startActivity(intent)
		}
		containerLayout.addView(profileButton)

		// add changeView button
		val changeViewButton = ImageButton(this)
		val changeViewParams = RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		)
		changeViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
		changeViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
		changeViewButton.background = AppCompatResources.getDrawable(this, R.drawable.button_primary)
		changeViewButton.layoutParams = changeViewParams
		changeViewButton.setImageResource(android.R.drawable.ic_dialog_map)
		changeViewButton.setOnClickListener {
			val intent = Intent(this, MapActivity::class.java)
			startActivity(intent)
		}
		containerLayout.addView(changeViewButton)

		// add container with all new buttons to the content view
		addContentView(containerLayout, RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT))
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
//			val userid = this.getSharedPreferences("usr_id",
//					Context.MODE_PRIVATE).getString("usr_id", "").toString()
//			val postcontent = data!!.getStringExtra("postcontent").toString()
//			val newMessageData = MessageData(userid, location.latitude, location.longitude, postcontent)
//			this.fireBaseManager.uploadMessage(newMessageData, true)
//			this.locations.add(newMessageData)

		}
	}

	fun setUserData(userData: UserData){
		val profileImageButton = findViewById<ImageButton>(this.profileViewID)
		if(!userData.profilePicture.isEmpty()){
			DownloadImageTask(findViewById(this.profileViewID))
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