package postpc2021.android.socialar.myPostsComponents

import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.io.Serializable
import java.util.*


class MyPostsItemsHolderImpl(context: Context): Serializable {
    private var myPostsList: MutableList<MyPostsItem>? = ArrayList()
    private var sp = context.getSharedPreferences("my_posts_list_db", Context.MODE_PRIVATE)
    private var myPostsIDSet: MutableSet<String>? = null
    private var myContext = context
    init{
        initFromSP()
    }


    private fun initFromSP() {
        myPostsIDSet = sp.getStringSet("myPostsIDSet", HashSet<String>())
        if (myPostsIDSet != null) {
            for (id in myPostsIDSet!!) {
                val myPostItem = MyPostsItem()
                myPostItem.setContentSummary(sp.getString(id + "_content_summary", "").toString())
                myPostItem.setId(UUID.fromString(id))
                myPostsList!!.add(myPostItem)
            }
        }
    }



    private fun sendSPChanged(action: String, old_position: Int) {
        val broadcast = Intent("my_posts_sp_changed")
        broadcast.putExtra(action, old_position)
        this.myContext.sendBroadcast(broadcast)
    }

    fun updateSP(myPostItem: MyPostsItem) {
        myPostsIDSet!!.add(myPostItem.getId().toString())
        val editor = sp.edit()
        editor.putStringSet("myPostsIDSet", myPostsIDSet)
        editor.putString(myPostItem.getId().toString() + "_content_summary", myPostItem.getContent())
        /// TODO: update SP DB with favItem detail
        editor.apply()

    }

    private fun removeFromSP(myPostItem: MyPostsItem) {
        myPostsIDSet!!.remove(myPostItem.getId().toString())
        val editor = sp!!.edit()
        editor.remove(myPostItem.getId().toString() + "_content_summary")
        editor.putStringSet("myPostsIDSet", myPostsIDSet)
        editor.apply()
    }



    fun getCurrentPostsItems(): List<MyPostsItem>? {
        return myPostsList
    }

    fun addNewMyPostObject(myPost: MyPostsItem): Boolean {
        var realPost: MyPostsItem? = null
        for (postFromList in myPostsList!!)
        {
            if(postFromList.getId().toString() == myPost.getId().toString())
            {
                realPost = myPost
                break
            }
        }
        if(realPost == null) {
            myPostsList!!.add(0, myPost)
            updateSP(myPost)
            sendSPChanged("newMyPost", myPostsList!!.indexOf(myPost))
            return true
        }
        else
        {
            Toast.makeText(myContext, "Post already exists", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun deleteMyPost(myPost: MyPostsItem) {
        removeFromSP(myPost)
        sendSPChanged("deleteMyPost", myPostsList!!.indexOf(myPost))
        myPostsList!!.remove(myPost)
    }

    fun saveState(): Serializable {
        val state = MyPostsListState()
        state.myPosts = this.myPostsList
        return state
    }

    fun loadState(prevState: Serializable?) {
        if (prevState !is MyPostsListState) {
            return  // ignore
        }
        myPostsList = prevState.myPosts
    }

    private class MyPostsListState : Serializable {
        var myPosts: MutableList<MyPostsItem>? = null
    }
}