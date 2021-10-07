package postpc2021.android.socialar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.ar.core.ArCoreApk
import com.wikitude.architect.ArchitectView
import com.wikitude.common.devicesupport.Feature
import com.wikitude.common.permission.PermissionManager
import com.wikitude.common.permission.PermissionManager.PermissionManagerCallback
import postpc2021.android.socialar.arComponents.ARView
import java.util.*

enum class Feature {
	geo, image_tracking
}

class MainActivity : AppCompatActivity(){
	private val permissionManager = ArchitectView.getPermissionManager()
	var arFeatures: EnumSet<Feature>? = EnumSet.allOf(Feature::class.java)

	val permissions: Array<String> = PermissionUtil.getPermissionsForArFeatures(arFeatures)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA),
				1)

	}

	override fun onRequestPermissionsResult(requestCode: Int,
											permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		when (1) {
			requestCode -> {
				// If request is cancelled, the result arrays are empty.
				if ((grantResults.isNotEmpty() &&
								grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
					val intent = Intent(this@MainActivity, LoginActivity::class.java)
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
					startActivity(intent)
					// Permission is granted. Continue the action or workflow
					// in your app.
				} else {
					// Explain to the user that the feature is unavailable because
					// the features requires a permission that the user has denied.
					// At the same time, respect the user's decision. Don't link to
					// system settings in an effort to convince the user to change
					// their decision.
					ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA),
							1)
					return
				}
			}

			// Add other 'when' lines to check for other
			// permissions this app might request.
			else -> {
				// Ignore all other requests.
			}

		}

	}

}