package postpc2021.android.socialar

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk


class MainActivity : AppCompatActivity() {
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
			val intent = Intent(this, MapActivity::class.java)
			startActivity(intent)
		} else { // The device is unsupported or unknown.
			Toast.makeText(this, "THIS DEVICE DOESN'T SUPPORT AR FUNCTIONS", Toast.LENGTH_SHORT).show()
		}
	}
}