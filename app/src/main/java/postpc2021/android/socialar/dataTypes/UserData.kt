package postpc2021.android.socialar.dataTypes

import android.net.Uri
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.core.GeoHash
import com.google.type.DateTime
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class UserData(
		val userID: String = "",
		val userName: String = "",
		val profilePicture: String = "",
		val dateOfBirth: String = "",
		val joinDate: String = "",
		val messages: ArrayList<String> = ArrayList()
) : Serializable {
}
