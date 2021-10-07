package postpc2021.android.socialar.myPostsComponents

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import postpc2021.android.socialar.DownloadImageTask
import postpc2021.android.socialar.FireBaseManager
import postpc2021.android.socialar.FirebaseWrapper
import postpc2021.android.socialar.R
import postpc2021.android.socialar.dataTypes.UserData

class MyPostItemGui(itemView: View): RecyclerView.ViewHolder(itemView) {
    var profilePicture: ImageView = itemView.findViewById(R.id.profilePicture) as ImageView
    var contentSummary: TextView = itemView.findViewById(R.id.contentSummary)
    var deletePostButton: ImageButton = itemView.findViewById(R.id.deletePostImageButton)
    var likes: TextView =itemView.findViewById(R.id.numOfLikesTextView)
    /// TODO: add buttons and details to the fav item

    fun updateProfilePicture(userID: String, fireBaseManager: FireBaseManager)
    {
        fireBaseManager.getUserDetails(userID, ::setUserData)
    }

    fun setUserData(userData: UserData){
        if(!userData.profilePicture.isEmpty()){
            DownloadImageTask(profilePicture)
                    .execute(userData.profilePicture);
        }
        else{
            profilePicture.setImageResource(R.drawable.ic_profile_light)
        }
    }
}