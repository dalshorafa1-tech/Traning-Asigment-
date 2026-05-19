package com.example.recipebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Recipedetails extends AppCompatActivity {

    ImageView imgRecipe;
    TextView tvTitle, tvCategory, tvIngredients, tvSteps, tvSourceUrl;
    ImageButton btnEdit, btnDelete;
    View ownerControls;
    BookRecipe currentRecipe;
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipedetails);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        imgRecipe = findViewById(R.id.img_recipe_details);
        tvTitle = findViewById(R.id.tv_title_details);
        tvCategory = findViewById(R.id.tv_category_details);
        tvIngredients = findViewById(R.id.tv_ingredients_details);
        tvSteps = findViewById(R.id.tv_steps_details);
        tvSourceUrl = findViewById(R.id.tv_source_url);
        btnEdit = findViewById(R.id.btn_edit_recipe);
        btnDelete = findViewById(R.id.btn_delete_recipe);
        ownerControls = findViewById(R.id.owner_controls);

        currentRecipe = (BookRecipe) getIntent().getSerializableExtra("recipe");

        if (currentRecipe != null) {
            displayRecipeDetails();
            checkOwnership();
        }

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Recipedetails.this, EditRecipe.class);
            intent.putExtra("recipe", currentRecipe);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            showDeleteDialog();
        });

        tvSourceUrl.setOnClickListener(v -> {
            if (currentRecipe.getSourceUrl() != null && !currentRecipe.getSourceUrl().isEmpty()) {
                String url = currentRecipe.sourceUrl;
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void checkOwnership() {
        if (mAuth.getCurrentUser() != null && currentRecipe.userEmail != null) {
            if (mAuth.getCurrentUser().getEmail().equals(currentRecipe.userEmail)) {
                ownerControls.setVisibility(View.VISIBLE);
            } else {
                ownerControls.setVisibility(View.GONE);
            }
        } else {
            ownerControls.setVisibility(View.GONE);
        }
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("حذف الوصفة")
                .setMessage("هل أنت متأكد أنك تريد حذف هذه الوصفة نهائياً؟")
                .setPositiveButton("حذف", (dialog, which) -> {
                    deleteRecipeFromFirestore();
                })
                .setNegativeButton("إلغاء", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteRecipeFromFirestore() {
        if (currentRecipe.id != null) {
            db.collection("books").document(currentRecipe.id)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(Recipedetails.this, "تم حذف الوصفة بنجاح", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Recipedetails.this, "فشل الحذف: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void displayRecipeDetails() {
        tvTitle.setText(currentRecipe.title);
        tvCategory.setText(currentRecipe.category);

        if (currentRecipe.sourceUrl != null && !currentRecipe.sourceUrl.isEmpty()) {
            tvSourceUrl.setVisibility(View.VISIBLE);
            tvSourceUrl.setText("المصدر: " + currentRecipe.sourceUrl);
        } else {
            tvSourceUrl.setVisibility(View.GONE);
        }

        if (currentRecipe.ingredients != null) {
            StringBuilder ingredientsText = new StringBuilder();
            for (String ingredient : currentRecipe.ingredients) {
                ingredientsText.append("• ").append(ingredient).append("\n");
            }
            tvIngredients.setText(ingredientsText.toString().trim());
        }

        if (currentRecipe.steps != null) {
            StringBuilder stepsText = new StringBuilder();
            for (int i = 0; i < currentRecipe.steps.size(); i++) {
                stepsText.append(i + 1).append(". ").append(currentRecipe.steps.get(i)).append("\n\n");
            }
            tvSteps.setText(stepsText.toString().trim());
        }

        if (currentRecipe.imageUrl != null && !currentRecipe.imageUrl.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(currentRecipe.imageUrl, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgRecipe.setImageBitmap(decodedByte);
            } catch (Exception e) {
                imgRecipe.setImageResource(R.drawable.o2);
            }
        }
    }
}
