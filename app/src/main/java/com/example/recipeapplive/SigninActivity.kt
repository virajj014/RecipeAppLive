package com.example.recipeapplive

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.recipeapplive.databinding.ActivitySigninBinding
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private  lateinit var binding: ActivitySigninBinding
    private  lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()


        binding.gotosignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


        binding.signinbutton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this, "User Logged In Successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this, "Empty Fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        if(currentUser !=null){
            Toast.makeText(this, "Signed in as: ${currentUser.email}", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        else{
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show()
        }
    }
}