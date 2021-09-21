package postpc2021.android.socialar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.mapbox.mapboxsdk.style.layers.Property

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        this.title = "Profile"
        /// TODO: get profile data from the device or from firebase
        val profilePicture = findViewById<ImageView>(R.id.profileImageView)
        profilePicture.setVisibility(View.VISIBLE)
    }
}