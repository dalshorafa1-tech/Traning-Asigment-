package com.example.recipebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class Signup extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private ActivitySignupBinding binding;
    protected FirebaseAuth mAuth;
    FirebaseFirestore f;
    private String encodedImage = ""; // لتخزين الصورة كـ String

    // تعريف Launcher لاختيار الصورة وتحويلها لـ String
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    try {
                        binding.imageView3.setImageURI(uri);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        encodedImage = encodeImage(bitmap); // تحويل الصورة لـ String
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        f = FirebaseFirestore.getInstance();
        setupCountrySpinner();

        binding.imageView3.setOnClickListener(v -> mGetContent.launch("image/*"));

        binding.tvTraLo.setOnClickListener(v -> {
            Intent i = new Intent(Signup.this, LoginScreen.class);
            startActivity(i);
        });

        binding.btCreate.setOnClickListener(v -> {
            String email = binding.edEmSu.getText().toString().trim();
            String pass = binding.edPasSu.getText().toString().trim();
            String name = binding.edNameSu.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty() || name.isEmpty()) {
                Toast.makeText(Signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("email", email);
                    user.put("country", binding.spCountry.getSelectedItem().toString());
                    user.put("image", encodedImage); // إضافة الصورة كـ String (Base64)

                    f.collection("users").document(mAuth.getUid())
                            .set(user)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(Signup.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Signup.this, LoginScreen.class));
                                    finish();
                                }
                            });
                } else {
                    Toast.makeText(Signup.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // دالة تحويل الصورة (Bitmap) إلى String (Base64)
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void setupCountrySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spCountry.setAdapter(adapter);
    }
}