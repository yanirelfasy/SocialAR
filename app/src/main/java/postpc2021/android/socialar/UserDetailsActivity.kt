package postpc2021.android.socialar

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.*


class UserDetailsActivity : AppCompatActivity() {


    private val PICK_IMAGE = 1
    lateinit var datePicker: DatePickerDialog
    lateinit var birthdayText: EditText
    lateinit var profilePicView: ImageView
    lateinit var nameText: EditText
    lateinit var joinDate: String
    lateinit var doneButton: Button
    var profilePicUri: String = ""
    val clndr = Calendar.getInstance()
    var birthdayDate: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)
        init()
    }

    private fun init() {
        birthdayText = findViewById(R.id.birthdayText)
        profilePicView = findViewById(R.id.picButton)
        nameText = findViewById(R.id.nameText)
        joinDate = clndr.time.toString()
        setupBirthdayPicker()
        setupProfilePicture()
        doneButton = findViewById(R.id.doneButton)

        doneButton.setOnClickListener {


            // Check the following:
            // Name is not blank
            // Birthday is not blank
            //
            if (nameText.text.isNotBlank() && birthdayText.text.isNotBlank()) {
                birthdayDate = birthdayText.text.toString()
                val intent = Intent(this, MapActivity::class.java)
                finish()
                startActivity(intent)
            }
        }
    }


    private fun setupBirthdayPicker() {
        birthdayText.inputType = InputType.TYPE_NULL
        birthdayText.setOnClickListener {
            val day = clndr.get(Calendar.DAY_OF_MONTH)
            val month = clndr.get(Calendar.MONTH)
            val cyear = clndr.get(Calendar.YEAR) - 15
            datePicker = DatePickerDialog(
                this,
                { _, year, monthOfYear, dayOfMonth -> birthdayText.setText("${dayOfMonth}/${(monthOfYear + 1)}/$year") }, cyear, month, day
            )

            datePicker.show()
        }

    }


    private fun setupProfilePicture() {
        pr
        profilePicView.setOnClickListener {
            openGallery()
        }
    }


    private fun openGallery() {
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(galleryIntent, PICK_IMAGE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                val imageUri: Uri? = data?.data
                if (imageUri != null) {
                    profilePicView.setImageURI(imageUri)
                }
            }

        }
    }


}