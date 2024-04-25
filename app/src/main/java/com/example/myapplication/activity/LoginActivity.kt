package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.signUp.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        }
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    fun loginUser() {
        val email: String = binding.etEmail.getText().toString()
        val password: String = binding.etPassword.getText().toString()
        val isValidate = validateDate(email, password)
        if (!isValidate) {
            return
        }
        loginAccountInFirebase(email, password)
    }

    fun loginAccountInFirebase(email: String?, password: String?) {
        val firebaseAuth = FirebaseAuth.getInstance()
        changeInProgress(true)
        firebaseAuth.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener { task ->
            changeInProgress(false)
            if (task.isSuccessful) {
                if (firebaseAuth.currentUser!!.isEmailVerified) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Utility.showToast(
                        this@LoginActivity,
                        "email not verified, Please verify your email."
                    )
                }
            } else {
                Utility.showToast(this@LoginActivity, task.exception!!.localizedMessage)
            }
        }
    }

    fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.visibility = View.VISIBLE
        }
    }

    fun validateDate(email: String?, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email is invalid "
            return false
        }
        if (password.length < 6) {
            binding.etPassword.error = "Password length is invalid"
            return false
        }
        return true
    }
}