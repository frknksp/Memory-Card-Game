package com.example.memorygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.memorygame.databinding.ActivityLoginBinding
import com.example.memorygame.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        var currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.girisbutton.setOnClickListener{
            var email = binding.girisemail.text.toString()
            var password = binding.girisparola.text.toString()
            if (TextUtils.isEmpty(email)){
                binding.girisemail.error = "E-mail adresi boş bırakılamaz!"
                return@setOnClickListener
            }
            else if (TextUtils.isEmpty(password)){
                binding.girisparola.error = "Parola boş bırakılamaz!"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){
                    if (it.isSuccessful){
                        intent = Intent(applicationContext,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(applicationContext,"Giriş yapılamadı, e-mail veya parola hatalı.",Toast.LENGTH_LONG).show()
                    }
                }
        }
        binding.girisyeniuyelik.setOnClickListener{
            intent = Intent(applicationContext,Signup::class.java)
            startActivity(intent)
            finish()
        }
    }
}