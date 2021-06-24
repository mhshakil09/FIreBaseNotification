package com.example.firebasenotification.ui.login

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasenotification.BuildConfig
import com.example.firebasenotification.R
import com.example.firebasenotification.databinding.ActivityLoginBinding
import com.example.firebasenotification.ui.profile.ProfileActivity
import com.example.firebasenotification.ui.registration.RegistrationActivity
import com.example.firebasenotification.utils.SessionManager
import com.example.firebasenotification.utils.toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

class LoginActivity : AppCompatActivity() {

    private lateinit var  binding:ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG) {
            binding.emailET.setText("test@gmail.com")
            binding.passwordET.setText("123456789")
        }

        auth = FirebaseAuth.getInstance()
        SessionManager.init(this)
        fcmCall()
        checkLoggedIn()
        login()
    }

    private fun login() {
        binding.loginBtn.setOnClickListener {
            if (binding.emailET.toString().isEmpty()) {
                binding.emailET.error = "Enter your email"
            } else if (binding.passwordET.toString().isEmpty()) {
                binding.passwordET.error = "Enter password"
            } else {
                auth.signInWithEmailAndPassword(binding.emailET.text.toString(), binding.passwordET.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            toast("Logged In")
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        } else {
                            toast("Authentication failed.")
                        }
                    }
            }

        }

        binding.registrationBtn.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun checkLoggedIn() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun fcmCall() {
        Firebase.messaging.isAutoInitEnabled = true

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token.toString()
            Log.d("FirebaseToken", msg)
            if (!token.isNullOrEmpty()) {
                SessionManager.fcmToken = msg
            }
            toast(msg)
        })
    }
}