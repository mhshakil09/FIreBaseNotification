package com.example.firebasenotification.ui_java.java_profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.firebasenotification.databinding.ActivityJavaProfile2Binding;

public class JavaProfile2Activity extends AppCompatActivity {

    private ActivityJavaProfile2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJavaProfile2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}