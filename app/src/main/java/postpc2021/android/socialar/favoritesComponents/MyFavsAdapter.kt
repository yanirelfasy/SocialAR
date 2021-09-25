package postpc2021.android.socialar.favoritesComponents

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import postpc2021.android.socialar.R
import java.util.ArrayList

class MyFavsAdapter(holder: FavoriteItemsHolderImpl?): RecyclerView.Adapter<FavoriteItemGui>() {
    private var items: FavoriteItemsHolderImpl? = holder
    private var guiHolderList: MutableList<FavoriteItemGui>? = ArrayList()
    var onDeleteCallBack: ((Int)->Unit)?=null
    lateinit var myContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteItemGui {
        myContext = parent.context
        val view: View =
                LayoutInflater.from(myContext).inflate(R.layout.favorite_item, parent,
                        false)
        view.setOnClickListener {
//            val intent = Intent(myContext, FavoritesMapActivity::class.java)
//            myContext.startActivity(intent)
            /// TODO: open Map Activity with list of posts
            Toast.makeText(myContext, "Clicked!", Toast.LENGTH_SHORT).show()
        }
        return FavoriteItemGui(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(favItemGuiHolder: FavoriteItemGui, position: Int) {
        val item: FavoriteItem = items!!.getCurrentFavsItems()!![position]
        guiHolderList!!.add(favItemGuiHolder)
        favItemGuiHolder.deleteFavButton.setOnClickListener(View.OnClickListener {
            val callback = onDeleteCallBack?:return@OnClickListener
            callback(favItemGuiHolder.adapterPosition)
        })
    }

    override fun getItemCount(): Int {
        if(items != null)
        {
            return items!!.getCurrentFavsItems()!!.size
        }
        else {
            return 0
        }
    }

    fun getViewAtPosition(position: Int): FavoriteItemGui {
        return this.guiHolderList!![position]
    }
}