package postpc2021.android.socialar

import android.content.Context
import android.net.Uri
import android.widget.Toast

import androidx.core.net.toUri

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryBounds
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import postpc2021.android.socialar.dataTypes.MessageData
import postpc2021.android.socialar.dataTypes.PostData
import postpc2021.android.socialar.dataTypes.UserData
import java.io.File
import kotlin.reflect.KFunction1
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import kotlin.collections.ArrayList


class FireBaseManager(val context: Context) {
    private val userIDField = "userID"
    private val userCollection = "users"
    private val messageCollection = "messages"

    private var userID = ""

    private var _messagesAroundView = ArrayList<MessageData>()
    private var messagesAroundViewLiveData: MutableLiveData<List<MessageData>> = MutableLiveData<List<MessageData>>()
    val messagesAroundViewLiveDataPublic: LiveData<List<MessageData>> = messagesAroundViewLiveData


    val storage = Firebase.storage

    var db = FirebaseFirestore.getInstance()

    init {
        messagesAroundViewLiveData.value = ArrayList(_messagesAroundView)
    }

    fun getMessagesAroundView(): List<MessageData> {
        return ArrayList(_messagesAroundView)
    }

    fun onMessagesAroundViewReady(messages: ArrayList<MessageData>): Unit {
        _messagesAroundView = messages
        messagesAroundViewLiveData.value = ArrayList(_messagesAroundView)
    }

    fun setMessagesAroundView(latitude: Double, longitude: Double) {
        this.getMessagesByPoIandRange(latitude, longitude, 500.0, ::onMessagesAroundViewReady)
    }


    fun setUserID(userID: String) {
        this.userID = userID
    }

    fun getUserID(): String {
        return this.userID
    }

    /**
     * Uploads a file specified by path to the database and returns a download uri for it
     * */
    fun uploadMedia(path: String, storageLoc: String, callBack: (String) -> Unit) {
        val storageRef = storage.reference
//        val file = Uri.fromFile(File(path))
        // Todo
        val mediaRef = storageRef.child("images/$storageLoc")
        val uploadTask = mediaRef.putFile(path.toUri())
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
//                downloadUri = mediaRef.downloadUrl.toString()
                callBack(downloadUri)
            }

        }
    }


//    /**
//     * Upload a list of photos specified by their file paths, and return a list of download Uris for them
//     * */
//    fun uploadPhotos(paths: ArrayList<String>, storageLoc: String): ArrayList<String> {
//        val photoUris = ArrayList<String>()
//        for (path in paths) {
//            uploadMedia(path, storageLoc, photoUris)
//        }
//        return photoUris
//    }


    /**
     * Returns a list containing all message data of the specified userID
     * */
    fun getMessagesByUser(userID: String, callBack: (ArrayList<MessageData>) -> Unit) {
        val messages = ArrayList<MessageData>()
        val tasks = mutableListOf<Task<DocumentSnapshot>>()
        db.collection(userCollection).document(userID).get().addOnSuccessListener {
            val userData = it.toObject(UserData::class.java)
            if (userData != null) {
                val messageIDs = userData.messages
                for (messageId in messageIDs) {
                    val messageTask = db.collection(messageCollection).document(messageId).get()
                    tasks.add(messageTask)
                }
            }
//            for (msgID in messagesIDs as ArrayList<*>)
//            {
//                db.collection(messageCollection).document(msgID.toString()).get().addOnSuccessListener { inner_it ->
//                    val newData = inner_it.toObject(MessageData::class.java)
//                    messages.add(newData!!)
//                }
//            }
        }

        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            it.addOnCompleteListener {
                for (task in tasks) {
                    val message = task.result.toObject(MessageData::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                callBack(messages)
            }
        }


    }

    /**
     * Given a point of interest (latitude, longitude) and a radius in meters, returns a list of all messages within that range
     * */
    fun getMessagesByPoIandRange(latitude: Double, longitude: Double, radiusInMeters: Double, onDone: KFunction1<ArrayList<MessageData>, Unit>) {
        val messages = ArrayList<MessageData>()
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
            it.addOnCompleteListener {
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
                onDone(messages)
            }
        }
    }

    /**
     * Upload a message with raw data
     * */
    fun uploadMessage(latitude: Double, longitude: Double, textContent: String, mediaContent: ArrayList<String>, likeID: ArrayList<String>) {
        val messageData = MessageData(userID, latitude, longitude, textContent, mediaContent, likeID)
        uploadMessage(messageData)
    }

    /**
     *   Upload a message
     *
     * */
    fun uploadMessage(messageData: MessageData, callBack: () -> Unit = {}) {
        db.collection(messageCollection).document(messageData.id).set(messageData)
            .addOnSuccessListener {
                // On success, update users message array with message ID
                val userRef = db.collection(userCollection).document(userID)
                userRef.update("messages", FieldValue.arrayUnion(messageData.id))
                callBack()
            }
            .addOnFailureListener { }
    }

    fun deleteMessage(messageData: MessageData, callBack: () -> Unit = {}) {
        db.collection(messageCollection).document(messageData.id).delete()
            .addOnSuccessListener {
                // On success, update users message array with message ID
                val userRef = db.collection(userCollection).document(userID)
                userRef.update("messages", FieldValue.arrayRemove(messageData.id))
                callBack()
            }
            .addOnFailureListener { }
    }


    fun getUserDetails(userID: String, callBack: KFunction1<UserData, Unit>) {
        val userDocRef = db.collection(userCollection).document(userID)
        userDocRef.get().addOnSuccessListener { userDoc ->
            val userData: UserData? = userDoc.toObject(UserData::class.java)
            callBack(userData!!)
        }
    }

    fun getPostDetailsFromMessage(messageID: String?, callBack: KFunction1<PostData, Unit>) {
        val docRef = db.collection(messageCollection).document(messageID!!)
        docRef.get()
            .addOnSuccessListener { document ->
                val messageData: MessageData? = document.toObject(MessageData::class.java)
                val userDocRef = db.collection(userCollection).document(messageData!!.userID)
                userDocRef.get().addOnSuccessListener { userDoc ->
                    val userData: UserData? = userDoc.toObject(UserData::class.java)
                    callBack(
                        PostData(
                            userData!!.userID,
                            userData.userName,
                            messageData.id,
                            userData.profilePicture,
                            messageData.likeID,
                            messageData.mediaContent,
                            messageData.textContent,
                            messageData.creationDate
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                // TODO: add on fail handler
            }

    }


    fun updateUserDetails(userData: UserData, callBack: () -> Unit) {
        val userRef = db.collection(userCollection).document(userID)
        userRef.set(userData, SetOptions.merge()).addOnSuccessListener {
            callBack()
        }
    }

    fun getUserDoc(): DocumentReference {
        return db.collection(userCollection).document(userID)
    }

    fun signUpUser(callBack: () -> Unit) {
        if (userID.isNotBlank()) {
            db.collection(userCollection).document(userID).set(hashMapOf(userID to userIDField)).addOnSuccessListener { callBack() }
        }
    }

    fun hasUserCompletedSignUp(callBack: (Boolean) -> Unit) {
        db.collection(userCollection).document(userID).get().addOnSuccessListener {
            val document = it.toObject(UserData::class.java)
            callBack(document?.userName != null)
        }
    }
}



