package com.example.recipebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recipebook.databinding.ActivityHomeScreenBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HomeScreen extends AppCompatActivity {
    private ActivityHomeScreenBinding binding;
    private ArrayList<BookRecipe> allRecipes = new ArrayList<>();
    private ArrayList<BookRecipe> filteredRecipes = new ArrayList<>();
    private RecipeAdpter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        binding.img.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               Intent i = new Intent(HomeScreen.this, ProfileActivity.class);
                                               startActivity(i);
                                           }
                                       }

        );
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        setupRecyclerView();
        setupSearchView();
        setupTabLayout();

        loadAllRecipes();

        binding.fbAddRecipe.setOnClickListener(v -> {
            startActivity(new Intent(HomeScreen.this, AddRecipe.class));
        });
    }

    private void setupRecyclerView() {
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdpter(filteredRecipes);
        binding.rv.setAdapter(adapter);
    }

    private void loadAllRecipes() {
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allRecipes.clear();
                    Set<String> categories = new HashSet<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        BookRecipe recipe = document.toObject(BookRecipe.class);
                        if (recipe != null) {
                            recipe.id = document.getId();
                            allRecipes.add(recipe);
                            if (recipe.category != null && !recipe.category.trim().isEmpty()) {
                                categories.add(recipe.category.trim());
                            }
                        }
                    }
                    updateCategoriesTab(categories);
                    filterData(); // عرض كل الوصفات مبدئياً
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeScreen.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCategoriesTab(Set<String> categories) {
        binding.tabLayout.removeAllTabs();
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("الكل"));

        for (String category : categories) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(category));
        }
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData();
                return true;
            }
        });
    }

    private void filterData() {
        String currentQuery = binding.searchView.getQuery().toString().toLowerCase(Locale.getDefault());
        String selectedCategory = "الكل";
        TabLayout.Tab selectedTab = binding.tabLayout.getTabAt(binding.tabLayout.getSelectedTabPosition());
        if (selectedTab != null && selectedTab.getText() != null) {
            selectedCategory = selectedTab.getText().toString();
        }

        filteredRecipes.clear();

        for (BookRecipe recipe : allRecipes) {
            boolean matchesCategory = selectedCategory.equals("الكل") || (recipe.category != null && recipe.category.equalsIgnoreCase(selectedCategory));
            boolean matchesQuery = recipe.title != null && recipe.title.toLowerCase(Locale.getDefault()).contains(currentQuery);

            if (matchesCategory && matchesQuery) {
                filteredRecipes.add(recipe);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllRecipes(); // لتحديث البيانات عند العودة للشاشة
    }
}
