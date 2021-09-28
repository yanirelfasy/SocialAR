package postpc2021.android.socialar

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.firebase.ui.auth.AuthUI
import postpc2021.android.socialar.dataTypes.UserData
import postpc2021.android.socialar.favoritesComponents.FavoritesActivity
import postpc2021.android.socialar.myPostsComponents.MyPostsActivity

class ProfileActivity : AppCompatActivity() {

    val firebase: FireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
    private fun initFromSP() {
        this.title = "Profile"
        firebase.getUserDetails(firebase.getUserID(), ::updateUserData)
    }

    private fun updateUserData(userData: UserData) {
        val profilePicture = findViewById<ImageView>(R.id.profileImageView)
        val userName = findViewById<TextView>(R.id.userName)
        userName.text = userData.userName
        DownloadImageTask(profilePicture)
            .execute(userData.profilePicture);

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
            firebase.setUserID("")
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                    startActivity(intent)
                }

        }

    }




}