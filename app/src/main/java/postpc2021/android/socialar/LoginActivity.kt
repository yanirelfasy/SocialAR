package postpc2021.android.socialar

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var emailEditText: EditText
    private lateinit var passEditText: EditText
    private lateinit var signUpText: TextView
    private lateinit var loginButton: Button
    private lateinit var loadingProgressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_login)
        init()
    }



    private fun init() {

        emailEditText = findViewById(R.id.loginEmailEditText)
        passEditText = findViewById(R.id.loginPassEditText)
        signUpText = findViewById(R.id.loginSignUPTextView)
        loginButton = findViewById(R.id.loginButton)
        loadingProgressBar = findViewById(R.id.loading)
        loadingProgressBar.visibility = View.GONE


        signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            finish()
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passEditText.text.toString()
            if (email.isNotBlank() && password.isNotBlank()) {
                userSignIn(email, password)
            } else {
                loadingProgressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Please insert a valid email and password", Toast.LENGTH_LONG).show()
            }
        }


    }


    private fun userSignIn(email: String, password: String) {
        loadingProgressBar.visibility = View.VISIBLE
        loadingProgressBar.bringToFront()
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadingProgressBar.visibility = View.GONE
                val userID = firebaseAuth.currentUser?.uid
                fireBaseManager.setUserID(userID!!)
                fireBaseManager.hasUserCompletedSignUp(::nextActivity)

            }
        }.addOnFailureListener {
            loadingProgressBar.visibility = View.GONE
            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }


    private fun nextActivity(hasCompletedSignUp: Boolean) {
        if (hasCompletedSignUp) {
            val intent = Intent(this, MapActivity::class.java)
            finish()
            startActivity(intent)
        } else {
            val intent = Intent(this, UserDetailsActivity::class.java)
            finish()
            startActivity(intent)
        }
    }


}