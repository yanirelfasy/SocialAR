package postpc2021.android.socialar.myPostsComponents

import android.content.Context
import android.content.Intent
import android.widget.Toast
import postpc2021.android.socialar.FirebaseWrapper
import postpc2021.android.socialar.dataTypes.MessageData
import postpc2021.android.socialar.dataTypes.PostData
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class MyPostsItemsHolderImpl(context: Context): Serializable {
    private val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
    private var myMessagesList: List<MessageData>? = null
    private var myPostsList: MutableList<PostData>? = ArrayList()
    private var myContext = context

    init{
        initFromFB()
    }

    private fun initFromFB() {
        myMessagesList = fireBaseManager.getMessagesByUser(fireBaseManager.getUserID())
        Toast.makeText(myContext, "here", Toast.LENGTH_SHORT).show()
    }

    private fun sendFBChanged(action: String, old_position: Int) {
        val broadcast = Intent("my_posts_sp_changed")
        broadcast.putExtra(action, old_position)
        this.myContext.sendBroadcast(broadcast)
    }

    fun getCurrentMessageItems(): ArrayList<MessageData> {
        return myMessagesList as ArrayList<MessageData>
    }

    fun deleteMyPost(messageData: MessageData) {
        fireBaseManager.deleteMessage(messageData)
        sendFBChanged("deleteMyPost", myMessagesList!!.indexOf(messageData))
        initFromFB()
    }

    fun saveState(): Serializable {
        val state = MyPostsListState()
        state.messagesData = this.myMessagesList
        return state
    }

    fun loadState(prevState: Serializable?) {
        if (prevState !is MyPostsListState) {
            return  // ignore
        }
        myMessagesList = prevState.messagesData
    }

//    fun getMyPostsLocationsList(): ArrayList<Pair<Double, Double>>?
//    {
//        val locations = ArrayList<Pair<Double, Double>>()
//        for (item: MyPostsItem in myPostsList!!)
//        {
//            locations.add(Pair(item.getLongitude(), item.getLatitude()))
//        }
//        return locations
//    }

    private class MyPostsListState : Serializable {
        var messagesData: List<MessageData>? = null
    }
}