package com.example.recipebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipebook.databinding.ActivityAddRecipeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddRecipe extends AppCompatActivity {
    ActivityAddRecipeBinding binding;
    Uri imageUri;
    FirebaseFirestore db;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    binding.imageView3.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.imageView3.setOnClickListener(v -> mGetContent.launch("image/*"));

        binding.btAdd.setOnClickListener(v -> {
            saveRecipeWithBase64Image();
        });
    }

    private void saveRecipeWithBase64Image() {
        String title = binding.edTitle.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String base64Image = "";
        if (imageUri != null) {
            base64Image = convertImageToBase64(imageUri);
        }

        String ingredientsRaw = binding.edIngre.getText().toString().trim();
        String stepsRaw = binding.edStep.getText().toString();
        
        List<String> ingredientsList = Arrays.asList(ingredientsRaw.split("\\s*,\\s*"));
        List<String> stepsList = Arrays.asList(stepsRaw.split("\\s*,\\s*"));

        HashMap<String, Object> myrecipe = new HashMap<>();
        myrecipe.put("title", title);
        myrecipe.put("ingredients", ingredientsList);
        myrecipe.put("steps", stepsList);
        myrecipe.put("imageUrl", base64Image); // تخزين الصورة كنص Base64
        myrecipe.put("category", binding.edCate.getText().toString().trim());
        myrecipe.put("sourceUrl", binding.edUrl.getText().toString().trim());
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            myrecipe.put("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }

        db.collection("books").add(myrecipe).addOnSuccessListener(documentReference -> {
            Toast.makeText(AddRecipe.this, "Recipe Added!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private String convertImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // ضغط الصورة لتقليل حجم النص (Firestore لديه حد 1 ميجابايت للمستند)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            byte[] byteArray = outputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
