package postpc2021.android.socialar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {


    private val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var emailEditText: EditText
    private lateinit var passEditText: EditText
    private lateinit var loginText: TextView
    private lateinit var signUpButton: Button
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_sign_up)

        init()
    }

    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.signUpEmailEditText)
        passEditText = findViewById(R.id.signUpPassEditText)
        loginText = findViewById(R.id.signUpLoginTextView)
        signUpButton = findViewById(R.id.signUpButton)
        loadingProgressBar = findViewById(R.id.loading)
        loadingProgressBar.visibility = View.GONE

        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(intent)
        }
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passEditText.text.toString()
            loadingProgressBar.visibility = View.VISIBLE
            if (email.isNotBlank() && password.isNotBlank()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loadingProgressBar.visibility = View.GONE
                        val userID = firebaseAuth.currentUser?.uid
                        fireBaseManager.setUserID(userID!!)
                        fireBaseManager.signUpUser(::nextActivity)
                    }
                }.addOnFailureListener {
                    loadingProgressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_LONG).show()
                }
            } else {
                loadingProgressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Please insert a valid email and password", Toast.LENGTH_LONG).show()
            }

        }


    }


    private fun nextActivity() {
        val intent = Intent(this, UserDetailsActivity::class.java)
        finish()
        startActivity(intent)
    }


}