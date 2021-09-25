package postpc2021.android.socialar.myPostsComponents
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


class MyPostsActivity : AppCompatActivity() {
    var holder: MyPostsItemsHolderImpl? = null
    private var receiver: BroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        this.title = "My Posts"
        holder = MyPostsItemsHolderImpl(this.applicationContext)
        val fav = MyPostsItem()
        holder!!.addNewMyPostObject(fav)
        val mapButton = findViewById<FloatingActionButton>(R.id.seeItemsOnMapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("myPosts", holder!!.getMyPostsLocationsList()) /// TODO: change to firebase key for relevant data
            this.startActivity(intent)
        }
        val adapter = MyPostsAdapter(holder)
        val myPostsRecyclerView = findViewById<RecyclerView>(R.id.my_posts_recyclerView)
        myPostsRecyclerView.adapter = adapter
        myPostsRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter.onDeleteCallBack = {position ->
            holder!!.deleteMyPost(holder!!.getCurrentPostsItems()!![position])
        }
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action != null && action == "my_posts_sp_changed") {
                    if (intent.getIntExtra("newMyPost", -1) != -1) {
                        adapter.notifyItemInserted(0)
                    }
                    else if (intent.getIntExtra("deleteMyPost", -1) != -1) {
                        adapter.notifyItemRemoved(intent.getIntExtra("deleteMyPost",
                                -1))
                    }
                }
            }
        }
        registerReceiver(receiver, IntentFilter("my_posts_sp_changed"))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("myPostsList", holder!!.saveState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        holder!!.loadState(savedInstanceState.getSerializable("myPostsList"))
    }
}
