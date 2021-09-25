package postpc2021.android.socialar.myPostsComponents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import postpc2021.android.socialar.MapActivity
import postpc2021.android.socialar.R
import postpc2021.android.socialar.favoritesComponents.FavoriteItem
import java.util.ArrayList

class MyPostsAdapter(holder: MyPostsItemsHolderImpl?): RecyclerView.Adapter<MyPostItemGui>() {
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
            val item: MyPostsItem = items!!.getCurrentPostsItems()!![this.clickedPosition]
            val intent = Intent(myContext, MapActivity::class.java)
            val locationPair: Pair<Double, Double> = Pair(item.getLongitude(), item.getLatitude())
            val tempList = ArrayList<Pair<Double, Double>>()
            tempList.add(locationPair)
            intent.putExtra("myPosts", tempList) /// TODO: change to firebase key for relevant data
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
    }

    override fun getItemCount(): Int {
        if(items != null)
        {
            return items!!.getCurrentPostsItems()!!.size
        }
        else {
            return 0
        }
    }

    fun getViewAtPosition(position: Int): MyPostItemGui {
        return this.guiHolderList!![position]
    }
}