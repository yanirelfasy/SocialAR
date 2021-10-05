package postpc2021.android.socialar.favoritesComponents
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import postpc2021.android.socialar.MapActivity
import postpc2021.android.socialar.R


class FavoritesActivity : AppCompatActivity() {
    var holder: FavoriteItemsHolderImpl? = null
    private var receiver: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        this.title = "Favorites"
        holder = FavoriteItemsHolderImpl(this.applicationContext)
        val fav = FavoriteItem()
        holder!!.addNewFavObject(fav)
        val mapButton = findViewById<FloatingActionButton>(R.id.seeItemsOnMapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("favorites", holder!!.getFavoritesLocationsList()) /// TODO: change to firebase key for relevant data
            this.startActivity(intent)
        }
        val adapter = MyFavsAdapter(holder)
        val favoritesRecyclerView = findViewById<RecyclerView>(R.id.favorites_recyclerView)
        favoritesRecyclerView.adapter = adapter
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter.onDeleteCallBack = {position ->
            holder!!.deleteFav(holder!!.getCurrentFavsItems()!![position])
        }

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action != null && action == "favorites_sp_changed") {
                    if (intent.getIntExtra("newFav", -1) != -1) {
                        adapter.notifyItemInserted(0)
                    }
                    else if (intent.getIntExtra("deleteFav", -1) != -1) {
                        adapter.notifyItemRemoved(intent.getIntExtra("deleteFav",
                                -1))
                    }
                }
            }
        }
        registerReceiver(receiver, IntentFilter("favorites_sp_changed"))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("favsList", holder!!.saveState())

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        holder!!.loadState(savedInstanceState.getSerializable("favsList"))
    }
}
