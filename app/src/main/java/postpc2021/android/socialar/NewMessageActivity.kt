package postpc2021.android.socialar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText

class NewMessageActivity : Activity() {
    private val PICK_IMAGE = 1
    private val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
    private var mediaUris = ArrayList<String>()
    lateinit var loadingProgressBar: ProgressBar
    lateinit var sendPostButton: ImageButton
//    lateinit var postTitleView: TextInputEditText
    lateinit var postContentView: TextInputEditText
    lateinit var addMedia: ImageButton
    lateinit var deleteMedia: ImageButton
    lateinit var uploadingText: TextView
    lateinit var attachedMediaText: TextView
    lateinit var deletedMediaText: TextView
    lateinit var deleteMediaCardView: CardView
    lateinit var uploadingTextString: String
    lateinit var textForAttachedMediaText: String
    private var mediaCount = 0
    private var expectedMediaCount = 1
    private var totalAttachedMedia = 0


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        deleteMediaCardView = findViewById(R.id.deleteMediaCardView)
        loadingProgressBar = findViewById(R.id.newMediaProgressBar)
        sendPostButton = findViewById(R.id.sendPostImageButton)
//        postTitleView = findViewById(R.id.postTitleEditText)
        postContentView = findViewById(R.id.postContentEditText)
        addMedia = findViewById(R.id.addMediaImageButton)
        deleteMedia = findViewById(R.id.deleteMediaImageButton)
        uploadingText =findViewById(R.id.uploadingText)
        attachedMediaText =findViewById(R.id.numOfAttachedMediaText)
        deletedMediaText =findViewById(R.id.deleteMediaText)
        addMedia.setOnClickListener {
            openGallery()
        }
        deleteMedia.setOnClickListener {
            mediaUris = ArrayList<String>()
            totalAttachedMedia = 0
            deleteMediaCardView.visibility = View.GONE
            deletedMediaText.visibility = View.GONE
            totalAttachedMedia = 0
            textForAttachedMediaText = "No media files attached"
            attachedMediaText.text = textForAttachedMediaText

        }
        sendPostButton.setOnClickListener {
//            if (postTitleView.text.toString() == "" || postContentView.text.toString() == "")
            if (postContentView.text.toString() == "")
                Toast.makeText(this, "Please insert post content to continue!",
                        Toast.LENGTH_SHORT).show()
            else {
//                val postTitle = postTitleView.text.toString()
                val postContent = postContentView.text.toString()
                val dataIntent = Intent()
//                dataIntent.putExtra("posttitle", postTitle)
                dataIntent.putExtra("postContent", postContent)
                dataIntent.putExtra("mediaUris", mediaUris)
                setResult(RESULT_OK, dataIntent)
                finish()
            }
        }
        loadingProgressBar.visibility = View.GONE
        uploadingText.visibility = View.GONE
        deleteMediaCardView.visibility = View.GONE
        deletedMediaText.visibility = View.GONE

    }

    private fun openGallery() {
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(galleryIntent, PICK_IMAGE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            expectedMediaCount = 0
            mediaCount = 0
            loadingProgressBar.visibility = View.VISIBLE
            uploadingText.visibility = View.VISIBLE
            sendPostButton.isClickable = false
            addMedia.isClickable = false
            postContentView.isClickable = false
//            postTitleView.isClickable = false
            if (requestCode == PICK_IMAGE) {
                val imageUri: Uri? = data?.data
                val imageUris: ClipData? = data?.clipData
                if (imageUri != null) {
                    expectedMediaCount = 1
                    fireBaseManager.uploadMedia(imageUri.toString(), fireBaseManager.getUserID(), ::setMediaUri)
                }
                else if(imageUris != null)
                {
                    val count = imageUris.itemCount -1
                    expectedMediaCount = imageUris.itemCount
                    for(i: Int in 0..count)
                    {
                        fireBaseManager.uploadMedia(imageUris.getItemAt(i).uri.toString(),
                                fireBaseManager.getUserID(), ::setMediaUri)
                    }
                }
            }
            uploadingTextString = "Uploading media 1/$expectedMediaCount"
            uploadingText.text = uploadingTextString
        }
    }

    private fun setMediaUri(downloadUri: String)
    {
        mediaUris.add(downloadUri)
        mediaCount += 1
        uploadingTextString = "Uploading media ${mediaCount+1}/$expectedMediaCount"
        uploadingText.text = uploadingTextString
        if(mediaCount == expectedMediaCount) {
            mediaCount = 0
            loadingProgressBar.visibility = View.GONE
            uploadingText.visibility = View.GONE
            sendPostButton.isClickable = true
            addMedia.isClickable = true
            postContentView.isClickable = true
//            postTitleView.isClickable = true
            totalAttachedMedia += expectedMediaCount
            textForAttachedMediaText = "$totalAttachedMedia Media files attached"
            attachedMediaText.text = textForAttachedMediaText
            deleteMediaCardView.visibility = View.VISIBLE
            deletedMediaText.visibility = View.VISIBLE

        }
    }
}