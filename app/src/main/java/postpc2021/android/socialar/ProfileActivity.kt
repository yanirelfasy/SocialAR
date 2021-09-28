package postpc2021.android.socialar

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import postpc2021.android.socialar.favoritesComponents.FavoritesActivity
import postpc2021.android.socialar.myPostsComponents.MyPostsActivity

class ProfileActivity : AppCompatActivity() {
    private fun initFromSP() {
        this.title = "Profile"
        /// TODO: get profile data from the device or from firebase
        val profilePicture = findViewById<ImageView>(R.id.profileImageView)
        profilePicture.setImageResource(R.drawable.profile)  /// TODO:: pull image from firestore and update here
        val userName = findViewById<TextView>(R.id.userName)
        userName.text = "Pablo Escobar" /// TODO: pull username from firestore
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.initFromSP()
        val favoritesButton = findViewById<Button>(R.id.favoritesButton)  /// TODO: change favorites logo
        favoritesButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, FavoritesActivity::class.java)
            startActivity(intent)
        }

        val myPostsButton = findViewById<Button>(R.id.myPostsButton)
        myPostsButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MyPostsActivity::class.java)
            startActivity(intent)
        }

        val logoutButton = findViewById<Button>(R.id.logoutButton) /// TODO: change logout logo
        logoutButton.setOnClickListener {
            /// TODO:  open login activity and delete data from shared preferences
        }

    }
}