package com.example.firebasenotification.ui_java.java_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.firebasenotification.databinding.ActivityJavaLoginBinding;
import com.example.firebasenotification.ui_java.java_profile.JavaProfileActivity;
import com.example.firebasenotification.ui_java.java_registration.JavaRegistrationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class JavaLoginActivity extends AppCompatActivity {

    private ActivityJavaLoginBinding binding;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJavaLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        fcmCall();
        checkLoggedIn();
        login();
    }

    private void login() {

        binding.loginBtn.setOnClickListener(v -> {
            if (binding.emailET.toString().isEmpty()) {
                binding.emailET.setError("Enter your email");
            } else if (binding.passwordET.toString().isEmpty()) {
                binding.passwordET.setError("Enter password");
            } else {
                auth.signInWithEmailAndPassword(binding.emailET.getText().toString(), binding.passwordET.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(JavaLoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(JavaLoginActivity.this, JavaProfileActivity.class));
                                finish();
                            } else {
                                Toast.makeText(JavaLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.registrationBtn.setOnClickListener(v ->
                startActivity(new Intent(JavaLoginActivity.this, JavaRegistrationActivity.class))
        );

    }

    private void checkLoggedIn() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, JavaProfileActivity.class));
            finish();
        }
    }

    private void fcmCall() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("FirebaseToken", token);

                    Toast.makeText(JavaLoginActivity.this, token, Toast.LENGTH_SHORT).show();

                    // Log and toast

                });
    }


}