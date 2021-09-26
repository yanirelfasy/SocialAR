package postpc2021.android.socialar

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.wikitude.architect.ArchitectJavaScriptInterfaceListener
import com.wikitude.architect.ArchitectView
import org.json.JSONException
import org.json.JSONObject
import postpc2021.android.socialar.arComponents.ArchitectViewExtension

class MessageActionsMiddleware(activity: Activity, architectView: ArchitectView) : ArchitectViewExtension(activity, architectView), ArchitectJavaScriptInterfaceListener {

	override fun onCreate() {
		/*
         * The ArchitectJavaScriptInterfaceListener has to be added to the Architect view after ArchitectView.onCreate.
         * There may be more than one ArchitectJavaScriptInterfaceListener.
         */
		architectView.addArchitectJavaScriptInterfaceListener(this)
	}

	override fun onDestroy() {
		// The ArchitectJavaScriptInterfaceListener has to be removed from the Architect view before ArchitectView.onDestroy.
		architectView.removeArchitectJavaScriptInterfaceListener(this)
	}

	override fun onJSONObjectReceived(jsonObject: JSONObject?) {
		val poiDetailIntent = Intent(activity, MessageActionsActivity::class.java)
		try {
			when (jsonObject?.getString("action")) {
				"present_poi_details" -> {
					poiDetailIntent.putExtra("id", jsonObject.getString("id"))
					activity.startActivity(poiDetailIntent)
				}
			}
		} catch (e: JSONException) {
			activity.runOnUiThread { Toast.makeText(activity, "Unable to parse JSON", Toast.LENGTH_LONG).show() }
			e.printStackTrace()
		}
	}

}