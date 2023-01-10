package com.example.memorygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import com.example.memorygame.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    private  lateinit var auth: FirebaseAuth
    var databaseReference:DatabaseReference?=null
    var database:FirebaseDatabase?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference =database?.reference!!.child("profile")


        binding.uyekaydetbtn.setOnClickListener {
            var uyeadsoyad = binding.uyeadsoyad.text.toString()
            var email = binding.uyeemail.text.toString()
            var password = binding.uyeparola.text.toString()
            if (TextUtils.isEmpty(uyeadsoyad)){
                binding.uyeadsoyad.error = "Lütfen ad soyad giriniz"
                return@setOnClickListener
            }else if (TextUtils.isEmpty(email)){
                binding.uyeemail.error = "Lütfen e-mail giriniz"
                return@setOnClickListener
            }
            else if (TextUtils.isEmpty(password)){
                binding.uyeparola.error = "Lütfen parola giriniz"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){ task ->
                    if (task.isSuccessful){
                        intent = Intent(applicationContext,Login::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@Signup,"Kayıt başarılı",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this@Signup,"Kayıt başarısız",Toast.LENGTH_LONG).show()
                    }

                }

    }

        //giriş sayfasına gitme
        binding.uyegirisbtn.setOnClickListener{
            intent = Intent(applicationContext,Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}