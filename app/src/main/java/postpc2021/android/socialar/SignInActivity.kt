package postpc2021.android.socialar

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.firebase.ui.auth.util.ExtraConstants
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AppCompatActivity() {

    val AUTH_REQUEST_CODE = 424242
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var listener: FirebaseAuth.AuthStateListener
    lateinit var providers: List<AuthUI.IdpConfig>

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        if (listener != null) {
            firebaseAuth.removeAuthStateListener(listener)
        }
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()

    }

    private fun init() {
        providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        firebaseAuth = FirebaseAuth.getInstance()
        listener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user = p0.currentUser
                //Case: User already logged in
                if (user != null) {

                } else {
                    //Case: Start login process
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), AUTH_REQUEST_CODE)
                }
            }
        }

    }


}