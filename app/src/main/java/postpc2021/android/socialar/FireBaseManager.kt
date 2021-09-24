package postpc2021.android.socialar

import android.net.Uri
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryBounds
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.ArrayList


class FireBaseManager {

    private val userCollection = "users"
    private val messageCollection = "messages"

    //TODO: Store this at a more suitable location
    private val userID = "currentUserID"


    val storage = Firebase.storage

    val db = FirebaseFirestore.getInstance()

    /**
     * Uploads a file specified by path to the database and returns a download uri for it
     * */
    fun uploadMedia(path: String, messageID: String): String {
        val storageRef = storage.reference
        val file = Uri.fromFile(File(path))
        // Todo
        val mediaRef = storageRef.child(messageID)
        val uploadTask = mediaRef.putFile(file)
        var downloadUri = ""

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            mediaRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUri = task.result.toString()
            }

        }
        return downloadUri
    }


    /**
     * Upload a list of photos specified by their file paths, and return a list of download Uris for them
     * */
    fun uploadPhotos(paths: ArrayList<String>, messageID: String): ArrayList<String> {
        val photoUris = ArrayList<String>()
        for (path in paths) {
            photoUris.add(uploadMedia(path, messageID))
        }
        return photoUris
    }

//    // TODO: If needed
//    fun downloadMedia(downloadURI : String, messageID: String) {
//        val storageRef = storage.reference
//        val gsReference = storage.getReferenceFromUrl(downloadURI)
//    }


    /**
     * Returns a list containing all message data of the specified userID
     * */
    fun getMessagesByUser(userID: String): List<MessageData> {
        val messages = mutableListOf<MessageData>()
        db.collection("$userCollection/$userID/messages")/*whereEqualTo("userID", userID)*/.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val message = document.toObject(MessageData::class.java)
                messages.add(message)
            }
        }

        return messages
    }

    /**
     * Given a point of interest (latitude, longitude) and a radius in meters, returns a list of all messages within that range
     * */
    fun getMessagesByPoIandRange(latitude: Double, longitude: Double, radiusInMeters: Double): List<MessageData> {
        val messages = mutableListOf<MessageData>()
        val center = GeoLocation(latitude, longitude)


        // First, query according to geohash
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeters)
        val tasks = mutableListOf<Task<QuerySnapshot>>()
        for (b: GeoQueryBounds in bounds) {
            val query = db.collection(messageCollection)
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash);

            tasks.add(query.get())
        }
        // Once complete, get rid of false positives while aggregating the matching results in the list
        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            for (task: Task<QuerySnapshot> in tasks) {
                val snapshot: QuerySnapshot = task.result
                for (doc: DocumentSnapshot in snapshot.documents) {
                    val lat: Double = doc.getDouble("latitude")!!
                    val lng: Double = doc.getDouble("longitude")!!
                    val docloc = GeoLocation(lat, lng)
                    val distanceInMeters: Double = GeoFireUtils.getDistanceBetween(docloc, center)
                    if (distanceInMeters <= radiusInMeters) {
                        messages.add(doc.toObject(MessageData::class.java)!!)
                    }
                }

            }
        }
        return messages
    }

    /**
     * Upload a message with raw data
     * */
    fun uploadMessage(latitude: Double, longitude: Double, textContent: String, mediaContent: ArrayList<String>, likeID: ArrayList<String>) {
        val messageData = MessageData(userID, latitude, longitude, textContent, mediaContent, likeID)
        uploadMessage(messageData)
    }

    /**
     *
     * */
    fun uploadMessage(messageData: MessageData, hasDLuris : Boolean = false) {
        // If media has not yet been uploaded
        // If media in the message contains photo file locations and not download uris
        if (!hasDLuris){
             messageData.mediaContent = uploadPhotos(messageData.mediaContent, messageData.id)
        }

        db.collection(messageCollection).document(messageData.id).set(messageData)
            .addOnSuccessListener {
                // On success, update users message array with message ID
                val userRef = db.collection(userCollection).document(userID)
                userRef.update("messages", FieldValue.arrayUnion(messageData.id))

            }
            .addOnFailureListener { }
    }


}