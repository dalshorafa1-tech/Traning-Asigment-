package com.example.recipebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recipebook.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<BookRecipe> userRecipes = new ArrayList<>();
    private RecipeAdpter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginScreen.class));
            finish();
            return;
        }

        setupRecyclerView();
        loadUserProfile();
        loadUserRecipes();

        binding.imgLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent i = new Intent(ProfileActivity.this, LoginScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void setupRecyclerView() {
        binding.rvUserRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdpter(userRecipes);
        binding.rvUserRecipes.setAdapter(adapter);
    }

    private void loadUserProfile() {
        String userId = mAuth.getUid();
        if (userId != null) {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            String imageBase64 = documentSnapshot.getString("image");

                            binding.tvUserName.setText(name);
                            binding.tvUserEmail.setText(email);

                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                try {
                                    byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    binding.imageView3.setImageBitmap(decodedByte);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadUserRecipes() {
        String userEmail = mAuth.getCurrentUser().getEmail();
        if (userEmail != null) {
            db.collection("books")
                    .whereEqualTo("userEmail", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        userRecipes.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            BookRecipe recipe = document.toObject(BookRecipe.class);
                            if (recipe != null) {
                                recipe.id = document.getId();
                                userRecipes.add(recipe);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Error loading recipes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
