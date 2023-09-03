package com.example.recipeapplive

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.recipeapplive.databinding.ActivitySigninBinding
import com.example.recipeapplive.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private  lateinit var binding: ActivitySignupBinding
    private  lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()


        binding.gotosignin.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }



        binding.signupbutton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmpassET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, SigninActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
//                Toast.makeText(this, "All Good", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Empty Fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}