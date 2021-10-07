package postpc2021.android.socialar

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import postpc2021.android.socialar.dataTypes.MessageData
import postpc2021.android.socialar.dataTypes.PostData
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MessageActionsActivity : AppCompatActivity() {

	val firebase = FirebaseWrapper.getInstance().fireBaseManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar!!.hide()
		this.setContentView(R.layout.activity_message_actions)

		val extras = intent.extras
		firebase.getPostDetailsFromMessage(extras!!.getString("id"), ::setMessageData)
		firebase.isUserLikeMessage(firebase.getUserID(), extras.getString("id")!!, ::setLikeButtonStatus)
		firebase.isMessageUserFav(firebase.getUserID(),  extras.getString("id")!!, ::setFavButtonStatus)

	}

	fun setLikeButtonStatus(isLikePressed: Boolean, messageID: String): Unit {
		val likeButton = findViewById<ImageButton>(R.id.likeButton)
		if(isLikePressed){
			likeButton.setImageResource(R.drawable.ic_dislike_light)
			likeButton.setOnClickListener {
				firebase.dislikeMessage(firebase.getUserID(), messageID, ::setLikeButtonStatus)
			}
		}
		else{
			likeButton.setImageResource(R.drawable.ic_like_empty)
			likeButton.setOnClickListener {
				firebase.likeMessage(firebase.getUserID(), messageID, ::setLikeButtonStatus)
			}
		}
	}

	fun setFavButtonStatus(isFavPressed: Boolean, messageID: String): Unit {
		val favButton = findViewById<ImageButton>(R.id.favButton)
		if(isFavPressed){
			favButton.setImageResource(android.R.drawable.btn_star_big_on)
			favButton.setOnClickListener {
				firebase.removeFromFavorites(firebase.getUserID(), messageID, ::setFavButtonStatus)
			}
		}
		else{
			favButton.setImageResource(android.R.drawable.btn_star_big_off)
			favButton.setOnClickListener {
				firebase.addToFavorites(firebase.getUserID(), messageID, ::setFavButtonStatus)
			}

		}
	}

	@RequiresApi(Build.VERSION_CODES.O)
	fun setMessageData(data: PostData): Unit{
		val date = SimpleDateFormat("MM-dd-yyyy").parse(data.creationDate)
		val formattedDate = SimpleDateFormat("MM-dd-yyyy").format(date!!)
		findViewById<TextView>(R.id.userName).text = data.userName
		findViewById<TextView>(R.id.likes).text = "${data.likeID.size} Likes"
		findViewById<TextView>(R.id.date).text = formattedDate.toString()
		findViewById<TextView>(R.id.messageContent).text = data.textContent
		if(data.mediaContent.size > 0){
			DownloadImageTask(findViewById(R.id.imagePlace)).execute(data.mediaContent[0])
		}
		DownloadImageTask(findViewById<ImageView>(R.id.profilePicture))
				.execute(data.profilePicture);
	}
}