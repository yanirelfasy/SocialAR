package postpc2021.android.socialar.favoritesComponents

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import postpc2021.android.socialar.R

class FavoriteItemGui(/// TODO: add buttons and details to the fav item xml
        var itemView: View): RecyclerView.ViewHolder(itemView) {
//    var profilePicture: ImageView = itemView.findViewById(R.id.profileImageView) as ImageView
    var contentSummary: TextView = itemView.findViewById(R.id.contentSummary)
    var deleteFavButton: ImageButton = itemView.findViewById(R.id.deleteFavImageButton)
}