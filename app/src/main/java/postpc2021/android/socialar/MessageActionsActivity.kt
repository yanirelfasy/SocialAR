package postpc2021.android.socialar

import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import postpc2021.android.socialar.dataTypes.MessageData
import postpc2021.android.socialar.dataTypes.PostData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MessageActionsActivity : AppCompatActivity() {

	val firebase: FireBaseManager = FireBaseManager();

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar!!.hide()
		this.setContentView(R.layout.activity_message_actions)

		val extras = intent.extras
		firebase.getPostDetailsFromMessage(extras!!.getString("id"), ::setMessageData)

	}

	@RequiresApi(Build.VERSION_CODES.O)
	fun setMessageData(data: PostData): Unit{
		findViewById<TextView>(R.id.userName).text = data.userName
		findViewById<TextView>(R.id.likes).text = "${data.likeID.size} Likes"
		findViewById<TextView>(R.id.date).text = LocalDate.parse(data.creationDate, DateTimeFormatter.ISO_DATE).toString()
		DownloadImageTask(findViewById<ImageView>(R.id.profilePicture))
				.execute(data.profilePicture);
	}
}