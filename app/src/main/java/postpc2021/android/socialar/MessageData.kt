package postpc2021.android.socialar

import android.net.Uri
import java.io.Serializable
import java.util.*

data class MessageData(
    val userID: String = "", // Id of the user that left the message
    val longitude: Float = 0f, // The longitude
    val latitude: Float = 0f, // The latitude
    val textContent: String = "", // Text content of the message
    val mediaContent : List<Uri>? = null, // List of media file Uri's
    val likeID: List<String>? = null // List of UserIds of those who liked the message
    ) : Serializable {
        val id: String = UUID.randomUUID().toString() // Id of the message
}
