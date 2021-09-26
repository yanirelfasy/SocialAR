package postpc2021.android.socialar.arComponents

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wikitude.architect.ArchitectStartupConfiguration
import com.wikitude.architect.ArchitectView
import com.wikitude.common.camera.CameraSettings.CameraResolution
import postpc2021.android.socialar.MessageActionsMiddleware
import postpc2021.android.socialar.R


open class ARView : AppCompatActivity() {
	var architectView: ArchitectView? = null
	var config = ArchitectStartupConfiguration()
	private var poiExtension: PoiDataFromApplicationModelExtension? = null
	private var geoExtension: GeoExtension? = null
	private var messageActionsMiddleware: MessageActionsMiddleware? = null

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