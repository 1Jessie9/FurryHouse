package com.furryhouse.furryhouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.furryhouse.furryhouse.Fragments.FragmentAccount
import com.furryhouse.furryhouse.Fragments.FragmentHome
import com.furryhouse.furryhouse.Fragments.FragmentPartners
import com.furryhouse.furryhouse.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var  firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        verifySession()
        getFragmentHome()

        binding.BottomNV.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.tab_home->{
                    getFragmentHome()
                    true
                }

                R.id.tab_patners->{
                    getFragmentPartners()
                    true
                }

                R.id.tab_account->{
                    getFragmentAccount()
                    true
                }

                else->{
                    false
                }
            }
        }
    }

    private fun verifySession(){
        if(firebaseAuth.currentUser == null){
            startActivity(Intent(this, OptionsLogin::class.java))
            finishAffinity()
        }
    }

    private fun getFragmentHome() {
        binding.TituloRL.text = "Home"
        val fragment = FragmentHome()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentHome")
        fragmentTransition.commit()
    }

    private fun getFragmentPartners() {
        binding.TituloRL.text = "Padrinos"
        val fragment = FragmentPartners()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentPartners")
        fragmentTransition.commit()
    }

    private fun getFragmentAccount() {
        binding.TituloRL.text = "Cuenta"
        val fragment = FragmentAccount()
        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(binding.FragmentL1.id, fragment, "FragmentAccount")
        fragmentTransition.commit()
    }
}