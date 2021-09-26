package postpc2021.android.socialar

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MessageActionsActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		supportActionBar!!.hide()
		this.setContentView(R.layout.activity_message_actions)

		val extras = intent.extras
		Toast.makeText(this, extras!!.getString("id"), Toast.LENGTH_SHORT).show()
	}
}