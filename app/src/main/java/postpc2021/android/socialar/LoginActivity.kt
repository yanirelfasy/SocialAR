package postpc2021.android.socialar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.mapboxsdk.maps.MapView
import postpc2021.android.socialar.arComponents.ARView

class LoginActivity : AppCompatActivity() {
    private val fireBaseManager = FirebaseWrapper.getInstance().fireBaseManager
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var providers: List<AuthUI.IdpConfig>

    private lateinit var emailEditText: EditText
    private lateinit var passEditText: EditText
    private lateinit var signUpText: TextView
    private lateinit var loginButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.loginEmailEditText)
        passEditText = findViewById(R.id.loginPassEditText)
        signUpText = findViewById(R.id.loginSignUPTextView)
        loginButton = findViewById(R.id.loginButton)


        signUpText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            finish()
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passEditText.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userID = firebaseAuth.currentUser?.uid
                        fireBaseManager.setUserID(userID!!)
                        val intent = Intent(this, ARView::class.java)
                        finish()
                        startActivity(intent)
                    }
                }.addOnFailureListener {
                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(applicationContext, "Please insert a valid email and password", Toast.LENGTH_LONG).show()
            }
        }


//        providers = arrayListOf(
//            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.GoogleBuilder().build()
//        )
//
//        val signInIntent = AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .build()
//        signInLauncher.launch(signInIntent)


    }


    /**
     * Result from sign in is received here
     * */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            fireBaseManager.setUserID(userID!!)
            setupUser()
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }


    private fun setupUser() {
        val userRef = fireBaseManager.getUserDoc()
        userRef.get().addOnSuccessListener { document ->
            // Case: User exists
            if (document != null) {
                signedIn()
            }// Case: User does not yet exist in database
            else {
                userRef.set(hashMapOf(fireBaseManager.getUserID() to "userID"))
                signUp()
            }
        }
    }


    private fun signedIn() {
        val intent = Intent(this, MapActivity::class.java)
        finish()
        startActivity(intent)
    }

    private fun signUp() {
//        val intent = Intent(this, UserDetailsActivity::class.java)
        finish()
        startActivity(intent)
    }


    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }


}