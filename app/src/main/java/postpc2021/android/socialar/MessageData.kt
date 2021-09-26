package postpc2021.android.socialar

import android.net.Uri
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.core.GeoHash
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class MessageData(
    val userID: String = "", // Id of the user that left the message
    val latitude: Double = 0.0, // The latitude
    val longitude: Double = 0.0, // The longitude
    val textContent: String = "", // Text content of the message
    var mediaContent: ArrayList<String> = ArrayList(), // List of media file download Uri's
    val likeID: ArrayList<String> = ArrayList(), // List of UserIds of those who liked the message
) : Serializable {
    val geoHash: String = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))// Geohash for the location, used for querying
    val id: String = UUID.randomUUID().toString() // Id of the message
}
