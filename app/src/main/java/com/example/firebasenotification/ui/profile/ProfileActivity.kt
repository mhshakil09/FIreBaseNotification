package com.example.firebasenotification.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.constraintlayout.solver.state.State
import androidx.core.view.isVisible
import com.example.firebasenotification.R
import com.example.firebasenotification.databinding.ActivityProfileBinding
import com.example.firebasenotification.ui.login.LoginActivity
import com.example.firebasenotification.utils.SessionManager
import com.example.firebasenotification.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import timber.log.Timber

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null
    private var fcmToken: String? = ""

    //variables
    var username: String = ""
    var email : String = ""
    var password: String = ""
    var gender: String = ""
    var religion: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fcmToken = SessionManager.fcmToken

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("profile")

        loadProfile()
        initClick()
    }

    private fun initClick() {

        binding.logoutBtn.setOnClickListener {
            logout()
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.fab.setOnClickListener {
            toast("Work in progress")
        }
    }

    private fun logout() {
        unSubToTopics("users")
        unSubToTopics(email)
        unSubToTopics(religion)
        unSubToTopics(gender)
        SessionManager.clearSession()
    }

    private fun login() {
        /*subToTopics("users")
        subToTopics("test")
        subToTopics("hola")*/
        subToTopics("asd")
        Timber.d("subscribeTo $religion, $gender")
        if (religion.isNotEmpty()) { subToTopics(religion) }
        if (gender.isNotEmpty()) { subToTopics(gender) }
    }

    private fun subToTopics(topic: String): Boolean {
        var flag = false
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "$topic subscribed"
                flag = true
                if (!task.isSuccessful) {
                    flag = false
                    msg = "$topic subscription failed"
                }
                Timber.d(msg)
                toast(msg)
            }
        return flag
    }

    private fun unSubToTopics(topic: String) {
        Firebase.messaging.unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "$topic unsubscribed"
                if (!task.isSuccessful) {
                    msg = "$topic unsubscribe failed"
                }
                Timber.d(msg)
                toast(msg)
            }
    }

    @SuppressLint("SetTextI18n")
    private fun loadProfile() {
        val user = auth.currentUser
        val userReference = databaseReference?.child(user?.uid!!)


        email = user?.email.toString()
        binding.emailET.setText("Email: $email")
        binding.fcmToken.text = fcmToken

        userReference?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                username = snapshot.child("username").value.toString()
                password = snapshot.child("password").value.toString()
                gender = snapshot.child("gender").value.toString()

                binding.usernameET.setText("Name: $username")
                binding.passwordET.setText("Password: $password")
                binding.genderTV.setText("Gender: $gender")
                val temp = snapshot.child("religion").value.toString().toInt()
                religion = when (temp) {
                    1 -> {
                        "Islam"
                    }
                    2 -> {
                        "Christian"
                    }
                    3 -> {
                        "Hindu"
                    }
                    4 -> {
                        "Buddhist"
                    }
                    else -> {
                        "Others"
                    }
                }
                binding.religionTV.text = "Religion: $religion"

                SessionManager.createSession(username, email, religion, gender, password)
                binding.fcmToken.isVisible = SessionManager.fcmToken.isNotEmpty()
                login()

                binding.fab.isVisible = snapshot.child("admin").value.toString().toInt() == 1
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}