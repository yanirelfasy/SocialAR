package postpc2021.android.socialar.myPostsComponents

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.tasks.Tasks
import postpc2021.android.socialar.FirebaseWrapper
import postpc2021.android.socialar.dataTypes.MessageData
import postpc2021.android.socialar.dataTypes.PostData
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class MyPostsItemsHolderImpl(context: Context): Serializable {
    private val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
    private var myMessagesList: MutableList<MessageData>? = null
    private var myPostsList: MutableList<PostData>? = ArrayList()
    private var myContext = context

    init{
        initFromFB()
    }

    private fun initFromFB() {
        fireBaseManager.getMessagesIDsByUser(fireBaseManager.getUserID(), ::getMessagesDataCallBack)
        Toast.makeText(myContext, "here", Toast.LENGTH_SHORT).show()
    }

    private fun getMessagesDataCallBack(messages: ArrayList<String>)
    {
        for (msgID in messages)
        {
            fireBaseManager.getMessageDataByMessageID(msgID, ::updateNewMessage)
        }
    }

    private fun updateNewMessage(messageData: MessageData)
    {
        if(myMessagesList == null)
        {
            myMessagesList = ArrayList()
        }
        this.myMessagesList!!.add(messageData)
        sendFBChanged("newMyPost", 0)
    }

    private fun sendFBChanged(action: String, old_position: Int) {
        val broadcast = Intent("my_posts_sp_changed")
        broadcast.putExtra(action, old_position)
        this.myContext.sendBroadcast(broadcast)
    }

    fun getCurrentMessageItems(): ArrayList<MessageData> {
        if(myMessagesList == null)
        {
            return ArrayList()
        }
        else
        {
            return myMessagesList as ArrayList<MessageData>
        }
    }

    fun deleteMyPost(messageData: MessageData) {
        fireBaseManager.deleteMessage(messageData)
        sendFBChanged("deleteMyPost", myMessagesList!!.indexOf(messageData))
        this.myMessagesList!!.remove(messageData)
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

    private class MyPostsListState : Serializable {
        var messagesData: MutableList<MessageData>? = null
    }
}