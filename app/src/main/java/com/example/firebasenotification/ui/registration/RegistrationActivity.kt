package com.example.firebasenotification.ui.registration

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasenotification.databinding.ActivityRegistrationBinding
import com.example.firebasenotification.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    private lateinit var auth: FirebaseAuth
    private var databaseReference: DatabaseReference? = null
    private var database: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.reference?.child("profile")

        religionSpinner()
        register()
    }

    private fun register() {
        binding.registrationBtn.setOnClickListener {
            val gender: RadioButton = findViewById(binding.genderSelect.checkedRadioButtonId)
            //toast("${binding.religionSpinner.selectedItemId}")
            /*
            we can also send INT value to store and check it easily
            0 --> "Select Religion..."
            1 --> "Islam"
            2 --> "Christian"
            3 -->  "Hindu"
            4 --> "Buddhist"
            5 --> "Others"
            */

            if (binding.usernameET.text.toString().isEmpty()) {
                binding.usernameET.error = "Enter your name"
                return@setOnClickListener
            }
            if (binding.emailET.text.toString().isEmpty()) {
                binding.emailET.error = "Enter your email"
                return@setOnClickListener
            }
            if (binding.religionSpinner.selectedItemId.toInt() == 0) {
                toast("Enter your Religion")
                return@setOnClickListener
            }
            if (binding.passwordET.text.toString().isEmpty()) {
                binding.passwordET.error = "Enter password"
                return@setOnClickListener
            }
            if (binding.passwordConfirmET.text.toString().isEmpty()) {
                binding.passwordConfirmET.error = "Enter password again"
                return@setOnClickListener
            }
            if (binding.passwordET.text.toString() != binding.passwordConfirmET.text.toString()) {
                binding.passwordConfirmET.error = "password not matched"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(
                binding.emailET.text.toString(),
                binding.passwordConfirmET.text.toString()
            )
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val currentUser = auth.currentUser
                        val currentUserDB = databaseReference?.child(currentUser?.uid!!)
                        currentUserDB?.child("username")?.setValue(binding.usernameET.text.toString())
                        currentUserDB?.child("email")?.setValue(binding.emailET.text.toString())
                        currentUserDB?.child("gender")?.setValue(gender.text)
                        currentUserDB?.child("religion")?.setValue(binding.religionSpinner.selectedItemId)
                        currentUserDB?.child("admin")?.setValue(0)
                        currentUserDB?.child("password")
                            ?.setValue(binding.passwordConfirmET.text.toString())
                        toast("Registration Success")
                        finish()
                    } else {
                        toast("Registration Failed")

                        try {
                            throw it.exception!!
                        } // if user enters wrong email.
                        catch (weakPassword: FirebaseAuthWeakPasswordException) {
                            //Log.d(TAG, "onComplete: weak_password")

                            // TODO: take your actions!
                            toast("weak password (minimum 8 required)")
                        } // if user enters wrong password.
                        catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                            //Log.d(TAG, "onComplete: malformed_email")

                            // TODO: Take your action
                            toast("email format not correct")
                        } catch (existEmail: FirebaseAuthUserCollisionException) {
                            //Log.d(TAG, "onComplete: exist_email")

                            // TODO: Take your action
                            toast("email already exists")
                        } catch (e: Exception) {
                            //Log.d(TAG, "onComplete: " + e.message)
                            toast("something went wrong")
                        }



                    }
                }


        }
    }

    private fun religionSpinner() {
        val options = arrayOf("Select Religion...", "Islam", "Christian", "Hindu", "Buddhist", "Others")

        binding.religionSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, options)

        binding.religionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.religionSpinner.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

}