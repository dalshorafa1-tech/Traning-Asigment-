package com.example.recipebook;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.databinding.ActivityEditRecipeBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditRecipe extends AppCompatActivity {
    ActivityEditRecipeBinding binding;
    FirebaseFirestore db;
    BookRecipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        // استقبال البيانات الحالية للوصفة
        recipe = (BookRecipe) getIntent().getSerializableExtra("recipe");

        if (recipe != null) {
            populateFields();
        }

        binding.btUp.setOnClickListener(v -> {
            updateRecipe();
        });
    }

    private void populateFields() {
        binding.edTitleEd.setText(recipe.title);
        binding.edCateEd.setText(recipe.category);
        
        if (recipe.ingredients != null) {
            binding.edIngreEd.setText(String.join(", ", recipe.ingredients));
        }
        
        if (recipe.steps != null) {
            binding.edStepEd.setText(String.join(", ", recipe.steps));
        }
    }

    private void updateRecipe() {
        String updatedTitle = binding.edTitleEd.getText().toString().trim();
        String updatedCategory = binding.edCateEd.getText().toString().trim();
        String ingredientsRaw = binding.edIngreEd.getText().toString().trim();
        String stepsRaw = binding.edStepEd.getText().toString().trim();

        if (updatedTitle.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> ingredientsList = Arrays.asList(ingredientsRaw.split("\\s*,\\s*"));
        List<String> stepsList = Arrays.asList(stepsRaw.split("\\s*,\\s*"));

        Map<String, Object> updates = new HashMap<>();
        updates.put("title", updatedTitle);
        updates.put("category", updatedCategory);
        updates.put("ingredients", ingredientsList);
        updates.put("steps", stepsList);

        // تحديث البيانات في Firestore باستخدام المعرف (ID)
        db.collection("books").document(recipe.id)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditRecipe.this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // العودة بعد النجاح
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
