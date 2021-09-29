package postpc2021.android.socialar

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
		isARCompatible()
	}

	private fun isARCompatible() {
		val availability = ArCoreApk.getInstance().checkAvailability(this)
		if (availability.isTransient) {
			// Continue to query availability at 5Hz while compatibility is checked in the background.
			Handler().postDelayed({
				isARCompatible()
			}, 200)
		}
		if (availability.isSupported) {
			permissionManager.checkPermissions(this@MainActivity, permissions, PermissionManager.WIKITUDE_PERMISSION_REQUEST, object : PermissionManagerCallback {
				override fun permissionsGranted(requestCode: Int) {
					val intent = Intent(this@MainActivity, LoginActivity::class.java)
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
					startActivity(intent)
				}

				override fun permissionsDenied(deniedPermissions: Array<String>) {
					Toast.makeText(this@MainActivity, "PERMISSION DENIED" + Arrays.toString(deniedPermissions), Toast.LENGTH_SHORT).show()
				}

				override fun showPermissionRationale(requestCode: Int, strings: Array<String>) {
					val alertBuilder = AlertDialog.Builder(this@MainActivity)
					alertBuilder.setCancelable(true)
					alertBuilder.setTitle("WE NEED PERMISSIONS")
					alertBuilder.setMessage(("PERMISSIONS!") + Arrays.toString(permissions))
					alertBuilder.setPositiveButton(android.R.string.ok) { dialog, which -> permissionManager.positiveRationaleResult(requestCode, permissions) }
					val alert = alertBuilder.create()
					alert.show()
				}
			})
		} else { // The device is unsupported or unknown.
			Toast.makeText(this, "THIS DEVICE DOESN'T SUPPORT AR FUNCTIONS", Toast.LENGTH_SHORT).show()
		}
	}
}