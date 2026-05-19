package com.example.recipebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.databinding.ActivityLoginScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityLoginScreenBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        // تحميل بيانات "تذكرني"
        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            binding.edEmailLo.setText(sharedPreferences.getString(KEY_EMAIL, ""));
            binding.edPassLo.setText(sharedPreferences.getString(KEY_PASS, ""));
            binding.checkBox.setChecked(true);
        }

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edEmailLo.getText().toString().trim();
                String pass = binding.edPassLo.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginScreen.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            handleRememberMe(email, pass);
                            Toast.makeText(LoginScreen.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginScreen.this,HomeScreen.class));
                            finish();
                        } else {
                            Toast.makeText(LoginScreen.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        binding.tvTranSu.setOnClickListener(v -> startActivity(new Intent(LoginScreen.this,Signup.class)));
    }

    private void handleRememberMe(String email, String pass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (binding.checkBox.isChecked()) {
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASS, pass);
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            editor.clear();
        }
        editor.apply();
    }
}
