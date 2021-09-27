package postpc2021.android.socialar.arComponents

import android.app.Activity
import android.location.Location
import android.location.LocationListener
import com.wikitude.architect.ArchitectView
import org.json.JSONArray
import org.json.JSONObject
import postpc2021.android.socialar.FireBaseManager
import postpc2021.android.socialar.MessageData
import java.util.*
import kotlin.collections.ArrayList

class PoiDataFromApplicationModelExtension(activity: Activity?, architectView: ArchitectView?) : ArchitectViewExtension(activity, architectView), LocationListener {
	private val fireBaseManager = FireBaseManager()

	/** If the POIs were already generated and sent to JavaScript.  */
	private var injectedPois = false

	/**
	 * When the first location was received the POIs are generated and sent to the JavaScript code,
	 * by using architectView.callJavascript.
	 */
	override fun onLocationChanged(location: Location) {
		if (!injectedPois) {
			this.fireBaseManager.getMessagesByPoIandRange(location.latitude, location.longitude, 200.0, ::sendPoiToHandler);
		}
	}

	override fun onProviderEnabled(provider: String) {}
	override fun onProviderDisabled(provider: String) {}
	private fun sendPoiToHandler(messages: ArrayList<MessageData>): Unit{
		val pois = JSONArray()
		val USER_HEIGHT = -32768f

		val ATTR_ID = "id"
		val ATTR_LATITUDE = "latitude"
		val ATTR_LONGITUDE = "longitude"
		val ATTR_ALTITUDE = "altitude"
		val LIKES = "likeID"
		val CONTENT = "textContent"
		val USER_ID = "userID"
		val CREATION_DATE = "creationDate"
		val MEDIA = "mediaContent"

		for (message in messages){
			val poiInformation = HashMap<String?, String?>()
			poiInformation[ATTR_ID] = message.id
			poiInformation[ATTR_LATITUDE] = message.latitude.toString()
			poiInformation[ATTR_LONGITUDE] = message.longitude.toString()
			poiInformation[ATTR_ALTITUDE] = USER_HEIGHT.toString()
			pois.put(JSONObject(poiInformation as Map<*, *>))
		}

		architectView.callJavascript("World.loadPoisFromJsonData($pois)") // Triggers the loadPoisFromJsonData function
		injectedPois = true // don't load pois again
	}
}