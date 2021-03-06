package postpc2021.android.socialar

import android.content.Context
import android.os.Message

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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.reflect.KFunction1
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

    fun getMessagesAroundView(): List<MessageData>{
        return ArrayList(_messagesAroundView)
    }

    fun onMessagesAroundViewReady(messages: ArrayList<MessageData>): Unit{
        _messagesAroundView = messages
        messagesAroundViewLiveData.value = ArrayList(_messagesAroundView)
    }

    fun setMessagesAroundView(latitude: Double, longitude: Double){
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
                mediaRef.downloadUrl.addOnSuccessListener {
                    callBack(it.toString())
                }
//                downloadUri = mediaRef.downloadUrl.toString()

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
    fun getMessagesIDsByUser(userID: String, callBack: (ArrayList<String>) -> Unit,
                             mode: String ="myPosts") {
        val messagesIDs = ArrayList<String>()
        db.collection(userCollection).document(userID).get().addOnSuccessListener {
            val messagesIDsObjects = if(mode == "myPosts") {
                it["messages"]
            } else {
                it["favorites"]
            }
            for (msgIDObject in messagesIDsObjects as ArrayList<*>)
            {
                messagesIDs.add(msgIDObject.toString())
            }
            callBack(messagesIDs)
        }
    }

    fun getMessageDataByMessageID(messageId: String, callBack: (MessageData) -> Unit)
    {
        db.collection(messageCollection).document(messageId).get().addOnSuccessListener {
            if(it.data != null) {
                callBack(it.toObject(MessageData::class.java)!!)
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

    fun deleteMessage(messageData: MessageData, callBack: () -> Unit = {}, mode: String = "myPosts") {
        if(mode == "myPosts") {
            db.collection(messageCollection).document(messageData.id).delete()
                    .addOnSuccessListener {
                        // On success, update users message array with message ID
                        val userRef = db.collection(userCollection).document(userID)
                        userRef.update("messages", FieldValue.arrayRemove(messageData.id))
                        callBack()
                    }
                    .addOnFailureListener { }
        }
        else
        {
            val userRef = db.collection(userCollection).document(userID)
            userRef.update("favorites", FieldValue.arrayRemove(messageData.id))
            callBack()
        }
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

    fun likeMessage(userID: String, messageID: String, callBack: (Boolean, String) -> Unit){
        db.collection(messageCollection).document(messageID).get().addOnSuccessListener {
            val message = it.toObject(MessageData::class.java)
            message!!.likeID.add(userID)
            db.collection(messageCollection).document(messageID).set(message).addOnSuccessListener {
                callBack(true, messageID)
            }
        }
    }

    fun dislikeMessage(userID: String, messageID: String, callBack: (Boolean, String) -> Unit){
        db.collection(messageCollection).document(messageID).get().addOnSuccessListener {
            val message = it.toObject(MessageData::class.java)
            message!!.likeID.remove(userID)
            db.collection(messageCollection).document(messageID).set(message).addOnSuccessListener {
                callBack(false, messageID)
            }
        }
    }

    fun isUserLikeMessage(userID: String, messageID: String, callBack: (Boolean, String) -> Unit){
        db.collection(messageCollection).document(messageID).get().addOnSuccessListener {
            val message = it.toObject(MessageData::class.java)
            callBack(message!!.likeID.contains(userID), messageID)
        }
    }

    fun isMessageUserFav(userID: String, messageID: String, callBack: (Boolean, String) -> Unit){
        db.collection(userCollection).document(userID).get().addOnSuccessListener {
            val user = it.toObject(UserData::class.java)
            callBack(user!!.favorites.contains(messageID), messageID)
        }
    }

    fun addToFavorites(userID: String, messageID: String, callBack: (Boolean, String) -> Unit){
        db.collection(userCollection).document(userID).get().addOnSuccessListener {
            val user = it.toObject(UserData::class.java)
            user!!.favorites.add(messageID)
            db.collection(userCollection).document(userID).set(user).addOnSuccessListener {
                callBack(true, messageID)
            }
        }
    }

    fun removeFromFavorites(userID: String, messageID: String, callBack: (Boolean, String) -> Unit){
        db.collection(userCollection).document(userID).get().addOnSuccessListener {
            val user = it.toObject(UserData::class.java)
            user!!.favorites.remove(messageID)
            db.collection(userCollection).document(userID).set(user).addOnSuccessListener {
                callBack(true, messageID)
            }
        }
    }
}



