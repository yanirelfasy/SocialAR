package postpc2021.android.socialar.myPostsComponents

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import postpc2021.android.socialar.R

class MyPostItemGui(itemView: View): RecyclerView.ViewHolder(itemView) {
//    var profilePicture: ImageView = itemView.findViewById(R.id.profileImageView) as ImageView
    var contentSummary: TextView = itemView.findViewById(R.id.contentSummary)
    var deletePostButton: ImageButton = itemView.findViewById(R.id.deletePostImageButton)
    var likes: TextView =itemView.findViewById(R.id.numOfLikesTextView)
    /// TODO: add buttons and details to the fav item xml
}