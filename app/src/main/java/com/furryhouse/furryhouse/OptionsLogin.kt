package com.furryhouse.furryhouse

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.furryhouse.furryhouse.databinding.ActivityOptionsLoginBinding
import com.furryhouse.furryhouse.options_login.Login_email
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class OptionsLogin : AppCompatActivity() {

    private  lateinit var binding: ActivityOptionsLoginBinding
    private lateinit var  firebaseAuth: FirebaseAuth

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espera por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        verifySession()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.loginEmail.setOnClickListener {
            startActivity(Intent(this@OptionsLogin, Login_email::class.java))
        }

        binding.loginGoogle.setOnClickListener {
            googleLogin()
        }
    }

    private fun googleLogin() {
        val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignInIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                authenticationGoogle(idToken)
            } catch (e: ApiException) {
                Toast.makeText(
                    this,
                    "Google sign in failed: ${e.statusCode}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticationGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {resultAuth->
                if(resultAuth.additionalUserInfo!!.isNewUser) {
                    pushInfoBD()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pushInfoBD() {
        progressDialog.setMessage("Guardando información")

        val time = Constants.getTimeDevice()
        val emailUser = firebaseAuth.currentUser!!.email
        val uidUser = firebaseAuth.uid
        val namesUser = firebaseAuth.currentUser?.displayName

        val hashMap = HashMap<String, Any>()
        hashMap["names"] = "${namesUser}"
        hashMap["numberPhone"] = ""
        hashMap["urlPicture"] = ""
        hashMap["timeRegister"] = time
        hashMap["online"] = true
        hashMap["email"] = "${emailUser}"
        hashMap["uid"] = "${uidUser}"
        hashMap["supplier"] = "google"
        hashMap["bornDate"] = ""

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(uidUser!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Inténtalo de nuevo. No se creó cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun verifySession(){
        if(firebaseAuth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}