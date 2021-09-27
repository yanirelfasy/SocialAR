package postpc2021.android.socialar.dataTypes

import android.net.Uri
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.core.GeoHash
import com.google.type.DateTime
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class PostData(
		val userID: String = "",
		val userName: String = "",
		val id: String = "",
		val profilePicture: String = "",
		val likeID: ArrayList<String> = ArrayList(),
		var mediaContent: ArrayList<String> = ArrayList(),
		val textContent: String = "",
		val creationDate: String = ""
) : Serializable {
}
