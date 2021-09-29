package postpc2021.android.socialar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class NewMessageActivity : Activity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        val sendPostButton = findViewById<ImageButton>(R.id.sendPostImageButton)
        val postTitleView = findViewById<TextInputEditText>(R.id.postTitleEditText)
        val postContentView = findViewById<TextInputEditText>(R.id.postContentEditText)
        val addMedia = findViewById<ImageButton>(R.id.addMediaImageButton)
        sendPostButton.setOnClickListener {
            if (postTitleView.text.toString() == "" || postContentView.text.toString() == "")
                Toast.makeText(this, "Please insert post title and content to continue!",
                        Toast.LENGTH_SHORT).show()
            else {
                val postTitle = postTitleView.text.toString()
                val postContent = postContentView.text.toString()
                val dataIntent = Intent()
                dataIntent.putExtra("posttitle", postTitle)
                dataIntent.putExtra("postcontent", postContent)
                setResult(RESULT_OK, dataIntent)
                finish()
            }
        }
    }
}