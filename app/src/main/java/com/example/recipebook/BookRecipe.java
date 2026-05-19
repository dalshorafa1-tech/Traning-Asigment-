package com.example.recipebook;

import com.google.firebase.firestore.Exclude;
import java.io.Serializable;
import java.util.List;

public class BookRecipe implements Serializable {
    @Exclude
    public String id;
    public String title;
    public List<String> ingredients;
    public List<String> steps;
    public String imageUrl;
    public String category;
    public String userEmail;
    public String sourceUrl;

    public BookRecipe() {
    }

    public BookRecipe(String id, String title, List<String> ingredients, List<String> steps, String imageUrl, String category, String userEmail, String sourceUrl) {
        this.id = id;
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
        this.category = category;
        this.userEmail = userEmail;
        this.sourceUrl = sourceUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}

