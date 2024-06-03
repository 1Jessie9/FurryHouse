package com.furryhouse.furryhouse

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.furryhouse.furryhouse.databinding.ActivitySignInEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class SignIn_email : AppCompatActivity() {

    private lateinit var binding: ActivitySignInEmailBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnSignIn.setOnClickListener{
            validInfo()
        }
    }

    private var email = ""
    private var password = ""
    private var passwordConfirm = ""

    private fun validInfo() {
        email = binding.textEmail.text.toString().trim()
        password = binding.textPassword.text.toString().trim()
        passwordConfirm = binding.textPasswordConfirm.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.textEmail.error = "Correo eléctronico inválido"
            binding.textEmail.requestFocus()
        }else if(email.isEmpty()){
            binding.textEmail.error = "Ingresa el correo eléctronico"
            binding.textEmail.requestFocus()
        }else if(password.isEmpty()){
            binding.textPassword.error = "Ingresa la contraseña"
            binding.textPassword.requestFocus()
        }else if(passwordConfirm.isEmpty()){
            binding.textPasswordConfirm.error = "Repita la contraseña"
            binding.textPasswordConfirm.requestFocus()
        }else if(password != passwordConfirm){
            binding.textPasswordConfirm.error = "Las contraseñas no coinciden"
            binding.textPasswordConfirm.requestFocus()
        }else {
            registerUser()
        }
    }

    private fun registerUser() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                pushInfoBD()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Inténtalo de nuevo. No se creó cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun pushInfoBD() {
        progressDialog.setMessage("Guardando información")

        val time = Constants.getTimeDevice()
        val emailUser = firebaseAuth.currentUser!!.email
        val uidUser = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["names"] = ""
        hashMap["numberPhone"] = ""
        hashMap["urlPicture"] = ""
        hashMap["timeRegister"] = time
        hashMap["online"] = true
        hashMap["email"] = "${emailUser}"
        hashMap["uid"] = "${uidUser}"
        hashMap["supplier"] = "email"
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
}