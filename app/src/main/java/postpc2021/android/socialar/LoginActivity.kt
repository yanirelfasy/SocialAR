package postpc2021.android.socialar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
    }

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var listener: FirebaseAuth.AuthStateListener
    lateinit var providers: List<AuthUI.IdpConfig>



    private fun init(){
        providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    fun emailLink() {
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setAndroidPackageName( /* yourPackageName= */
                "...",  /* installIfNotAvailable= */
                true,  /* minimumVersion= */
                null)
            .setHandleCodeInApp(true) // This must be set to true
            .setUrl("https://google.com") // This URL needs to be whitelisted
            .build()

        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder()
                .enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings)
                .build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_email_link]
    }







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



    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }




}