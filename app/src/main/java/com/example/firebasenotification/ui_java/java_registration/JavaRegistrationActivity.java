package com.example.firebasenotification.ui_java.java_registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.firebasenotification.R;
import com.example.firebasenotification.databinding.ActivityJavaLoginBinding;
import com.example.firebasenotification.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class JavaRegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("profile");

        religionSpinner();
        register();
    }

    private void register() {
        binding.registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton gender = findViewById(binding.genderSelect.getCheckedRadioButtonId());
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

                if (binding.usernameET.getText().toString().isEmpty()) {
                    binding.usernameET.setError("Enter your name");
                    return;
                }
                if (binding.emailET.getText().toString().isEmpty()) {
                    binding.emailET.setError("Enter your email");
                    return;
                }
                if (binding.religionSpinner.getSelectedItemId() == 0) {
                    Toast.makeText(JavaRegistrationActivity.this,"Enter your Religion", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.passwordET.getText().toString().isEmpty()) {
                    binding.passwordET.setError("Enter password");
                    return;
                }
                if (binding.passwordConfirmET.getText().toString().isEmpty()) {
                    binding.passwordConfirmET.setError("Enter password again");
                    return;
                }
                if (!binding.passwordET.getText().toString().equals(binding.passwordConfirmET.getText().toString())) {
                    binding.passwordConfirmET.setError("password not matched");
                    return;
                }

                auth.createUserWithEmailAndPassword(
                        binding.emailET.getText().toString(),
                        binding.passwordConfirmET.getText().toString()
                ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = auth.getCurrentUser();
                            assert currentUser != null;
                            DatabaseReference currentUserDB = databaseReference.child(currentUser.getUid());

                            currentUserDB.child("username").setValue(binding.usernameET.getText().toString());
                            currentUserDB.child("email").setValue(binding.emailET.getText().toString());
                            currentUserDB.child("gender").setValue(gender.getText());
                            currentUserDB.child("religion").setValue(binding.religionSpinner.getSelectedItemId());
                            currentUserDB.child("admin").setValue(0);
                            currentUserDB.child("password").setValue(binding.passwordConfirmET.getText().toString());
                            Toast.makeText(JavaRegistrationActivity.this,"Registration Success", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });



            }
        });
    }

    private void religionSpinner() {
        String[] options = {"Select Religion...", "Islam", "Christian", "Hindu", "Buddhist", "Others"};

        binding.religionSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options));

        binding.religionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.religionSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}