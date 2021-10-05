package postpc2021.android.socialar.favoritesComponents

import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class FavoriteItemsHolderImpl(context: Context) : Serializable {
    private var favoritesList: MutableList<FavoriteItem>? = ArrayList()
    private var sp = context.getSharedPreferences("favorites_list_db", Context.MODE_PRIVATE)
    private var favoritesIDSet: MutableSet<String>? = null
    private var myContext = context

    init {
        initFromSP()
    }


    private fun initFromSP() {
        favoritesIDSet = sp.getStringSet("favoritesIDSet", HashSet<String>())
        if (favoritesIDSet != null) {
            for (id in favoritesIDSet!!) {
                val favItem = FavoriteItem()
                favItem.setContentSummary(sp.getString(id + "_content_summary", "").toString())
                favItem.setId(UUID.fromString(id))
                favoritesList!!.add(favItem)
            }
        }
    }


    private fun sendSPChanged(action: String, old_position: Int) {
        val broadcast = Intent("favorites_sp_changed")
        broadcast.putExtra(action, old_position)
        this.myContext.sendBroadcast(broadcast)
    }

    fun updateSP(favItem: FavoriteItem) {
        favoritesIDSet!!.add(favItem.getId().toString())
        val editor = sp.edit()
        editor.putStringSet("favoritesIDSet", favoritesIDSet)
        editor.putString(favItem.getId().toString() + "_content_summary", favItem.getContent())

        /// TODO: update SP DB with favItem detail
        editor.apply()

    }

    private fun removeFromSP(favItem: FavoriteItem) {
        favoritesIDSet!!.remove(favItem.getId().toString())
        val editor = sp!!.edit()
        editor.remove(favItem.getId().toString() + "_content_summary")
        editor.putStringSet("favoritesIDSet", favoritesIDSet)
        editor.apply()
    }


    fun getCurrentFavsItems(): List<FavoriteItem>? {
        return favoritesList
    }

    fun addNewFavObject(fav: FavoriteItem): Boolean {
        var realFav: FavoriteItem? = null
        for (favFromList in favoritesList!!) {
            if (favFromList.getId().toString() == fav.getId().toString()) {
                realFav = fav
                break
            }
        }
        if (realFav == null) {
            favoritesList!!.add(0, fav)
            updateSP(fav)
            sendSPChanged("newFav", favoritesList!!.indexOf(fav))
            return true
        } else {
            Toast.makeText(myContext, "Favorite already exists", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun deleteFav(fav: FavoriteItem) {
        removeFromSP(fav)
        sendSPChanged("deleteFav", favoritesList!!.indexOf(fav))
        favoritesList!!.remove(fav)
    }

    fun saveState(): Serializable {
        val state = FavsListState()
        state.favs = this.favoritesList
        return state
    }

    fun loadState(prevState: Serializable?) {
        if (prevState !is FavsListState) {
            return  // ignore
        }
        favoritesList = prevState.favs
    }

    fun getFavoritesLocationsList(): ArrayList<Pair<Double, Double>>? {
        val locations = ArrayList<Pair<Double, Double>>()
        for (item: FavoriteItem in favoritesList!!) {
            locations.add(Pair(item.getLatitude(), item.getLongitude()))
        }
        return locations
    }

    private class FavsListState : Serializable {
        var favs: MutableList<FavoriteItem>? = null
    }
}