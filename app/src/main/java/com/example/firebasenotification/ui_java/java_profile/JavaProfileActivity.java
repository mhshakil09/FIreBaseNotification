package com.example.firebasenotification.ui_java.java_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.firebasenotification.R;
import com.example.firebasenotification.databinding.ActivityJavaProfileBinding;
import com.example.firebasenotification.databinding.ActivityRegistrationBinding;
import com.example.firebasenotification.ui_java.java_login.JavaLoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import timber.log.Timber;

public class JavaProfileActivity extends AppCompatActivity {

    private ActivityJavaProfileBinding binding;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;

    //variables
    String username = "";
    String email = "";
    String password = "";
    String gender = "";
    String religion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJavaProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("profile");

        loadProfile();
        initClick();
    }

    private void initClick() {
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                auth.signOut();
                startActivity(new Intent(JavaProfileActivity.this, JavaLoginActivity.class));
                finish();
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(JavaProfileActivity.this, "Work in progress", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {

        unSubToTopics("users");
        //unSubToTopics(email);
        unSubToTopics(religion);
        unSubToTopics(gender);
    }

    private void login() {
        subToTopics("users");
        /*subToTopics("test");
        subToTopics("hola");*/
        subToTopics("asd");
        Timber.d("subscribeTo " + religion + " " + gender);
        if (!religion.isEmpty()) { subToTopics(religion); }
        if (!gender.isEmpty()) { subToTopics(gender); }
    }

    private void subToTopics(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(task -> {
            String msg = topic +" subscribed";
            if (!task.isSuccessful()) {
                msg = topic +" subscription failed";
            }
            Timber.d(msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void unSubToTopics(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnCompleteListener(task -> {
            String msg = topic +" unsubscribed";
            if (!task.isSuccessful()) {
                msg = topic +" unsubscribe failed";
            }
            Timber.d(msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadProfile() {
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        DatabaseReference userReference = databaseReference.child(user.getUid());

        //binding.fcmToken.setText(fcmToken);

        userReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                password = Objects.requireNonNull(snapshot.child("password").getValue()).toString();
                gender = Objects.requireNonNull(snapshot.child("gender").getValue()).toString();

                binding.usernameET.setText("Name: "+ username);
                binding.emailET.setText("Email: " + email);
                binding.passwordET.setText("Password: " + password);
                binding.genderTV.setText("Gender: " + gender);

                int temp = Integer.parseInt(Objects.requireNonNull(snapshot.child("religion").getValue()).toString());

                switch (temp) {
                    case 1:
                        religion = "Islam";
                        break;
                    case 2:
                        religion = "Christian";
                        break;
                    case 3:
                        religion = "Hindu";
                        break;
                    case 4:
                        religion = "Buddhist";
                        break;
                    default:
                        religion = "Others";
                        break;
                }
                binding.religionTV.setText("Religion: " + religion);

                //SessionManager.createSession(username, email, religion, gender, password);
                //binding.fcmToken.isVisible = SessionManager.fcmToken.isNotEmpty();
                login();
                if (Integer.parseInt(snapshot.child("admin").getValue().toString()) == 1) {
                    binding.fab.setVisibility(View.VISIBLE);
                } else {
                    binding.fab.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
    
    
}