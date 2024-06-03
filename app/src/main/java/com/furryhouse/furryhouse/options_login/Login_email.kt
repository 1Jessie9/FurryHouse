package com.furryhouse.furryhouse.options_login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.furryhouse.furryhouse.MainActivity
import com.furryhouse.furryhouse.R
import com.furryhouse.furryhouse.SignIn_email
import com.furryhouse.furryhouse.databinding.ActivityLoginEmailBinding
import com.google.firebase.auth.FirebaseAuth

class Login_email : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espera por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnLogin.setOnClickListener {
            validInfo()
        }

        binding.textSignIn.setOnClickListener() {
            startActivity(Intent(this@Login_email, SignIn_email::class.java))
        }
    }

    private var email = ""
    private var password = ""

    private fun validInfo() {
        email = binding.textEmail.text.toString().trim()
        password = binding.textPassword.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.textEmail.error = "Correo eléctronico inválido"
            binding.textEmail.requestFocus()
        }else if(email.isEmpty()){
            binding.textEmail.error = "Ingresa el correo eléctronico"
            binding.textEmail.requestFocus()
        }else if(password.isEmpty()){
            binding.textPassword.error = "Ingresa la contraseña"
            binding.textPassword.requestFocus()
        }else {
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setMessage("Ingresando")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
                Toast.makeText(
                    this,
                    "Bienvenido(a)",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Inténtalo de nuevo. Las credenciales no son correctas",
                    Toast.LENGTH_SHORT).show()
            }
    }
}