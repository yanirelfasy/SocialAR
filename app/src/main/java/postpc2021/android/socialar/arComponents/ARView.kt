package postpc2021.android.socialar.arComponents

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wikitude.architect.ArchitectStartupConfiguration
import com.wikitude.architect.ArchitectView
import com.wikitude.common.camera.CameraSettings.*
import postpc2021.android.socialar.R


open class ARView : AppCompatActivity() {
	var architectView: ArchitectView? = null
	var config = ArchitectStartupConfiguration()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar!!.hide()
		architectView = ArchitectView(this)
		config.licenseKey = getString(R.string.wikitude_license_key)
		config.cameraResolution = CameraResolution.HD_1280x720
		architectView!!.onCreate(config) // create ArchitectView with configuration
		setContentView(architectView)
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
	}





	override fun onPostCreate(savedInstanceState: Bundle?) {
		super.onPostCreate(savedInstanceState)
		if(architectView != null){
			architectView!!.onPostCreate()
			try {
				Toast.makeText(this, "LOADING3", Toast.LENGTH_SHORT).show()
				architectView!!.load("demo3/index.html")
			}catch (e: Exception){
				Toast.makeText(this, "EXCEPTION", Toast.LENGTH_SHORT).show()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		architectView?.onDestroy()
	}

	override fun onPause() {
		super.onPause()
		architectView?.onPause()
	}

	override fun onResume() {
		super.onResume()
		architectView?.onResume()
	}

}