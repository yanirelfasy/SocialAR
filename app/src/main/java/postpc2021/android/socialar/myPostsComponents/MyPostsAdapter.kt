package postpc2021.android.socialar.myPostsComponents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import postpc2021.android.socialar.FirebaseWrapper
import postpc2021.android.socialar.LimitedMapActivity
import postpc2021.android.socialar.MapActivity
import postpc2021.android.socialar.R
import postpc2021.android.socialar.dataTypes.MessageData
import java.util.ArrayList

class MyPostsAdapter(holder: MyPostsItemsHolderImpl?): RecyclerView.Adapter<MyPostItemGui>() {
    val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
    private var items: MyPostsItemsHolderImpl? = holder
    private var guiHolderList: MutableList<MyPostItemGui>? = ArrayList()
    var onDeleteCallBack: ((Int)->Unit)?=null
    private var clickedPosition: Int = -1
    lateinit var myContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostItemGui {
        myContext = parent.context
        val view: View =
                LayoutInflater.from(myContext).inflate(R.layout.my_post_item, parent,
                        false)
        view.setOnClickListener {
            val item: MessageData = items!!.getCurrentMessageItems()[this.clickedPosition]
            val intent = Intent(myContext, LimitedMapActivity::class.java)
            val tempList = ArrayList<MessageData>()
            tempList.add(item)
            intent.putExtra("messages", tempList)
            myContext.startActivity(intent)
        }
        return MyPostItemGui(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(myPostItemGuiHolder: MyPostItemGui, position: Int) {
        guiHolderList!!.add(myPostItemGuiHolder)
        myPostItemGuiHolder.deletePostButton.setOnClickListener(View.OnClickListener {
            val callback = onDeleteCallBack?:return@OnClickListener
            callback(myPostItemGuiHolder.adapterPosition)
        })
        this.clickedPosition = myPostItemGuiHolder.adapterPosition
        val postText = items!!.getCurrentMessageItems()[position].textContent
        var count = 14
        if(postText.length < 15)
        {
            count = postText.length - 1
        }
        myPostItemGuiHolder.contentSummary.text =
                postText.slice(0..count) + "..."
        myPostItemGuiHolder.likes.text =
                items!!.getCurrentMessageItems()[position].likeID.size.toString()
        myPostItemGuiHolder.updateProfilePicture(items!!.getCurrentMessageItems()[position].userID,
                fireBaseManager)
    }

    override fun getItemCount(): Int {
        if(items != null)
        {
            return items!!.getCurrentMessageItems()!!.size
        }
        else {
            return 0
        }
    }

    fun getViewAtPosition(position: Int): MyPostItemGui {
        return this.guiHolderList!![position]
    }
}