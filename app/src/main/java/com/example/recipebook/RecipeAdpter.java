package com.example.recipebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipeAdpter extends RecyclerView.Adapter<RecipeAdpter.MyHolder> {

    ArrayList<BookRecipe> bookRecipes;

    public RecipeAdpter(ArrayList<BookRecipe> bookRecipes) {
        this.bookRecipes = bookRecipes;
    }

    @NonNull
    @Override
    public RecipeAdpter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipeitem, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdpter.MyHolder holder, int position) {
        BookRecipe bookRecipe = bookRecipes.get(position);
        holder.title.setText(bookRecipe.title);
        
        if (bookRecipe.ingredients != null && !bookRecipe.ingredients.isEmpty()) {
            StringBuilder ingredientsStr = new StringBuilder();
            for (int i = 0; i < bookRecipe.ingredients.size(); i++) {
                ingredientsStr.append(bookRecipe.ingredients.get(i));
                if (i < bookRecipe.ingredients.size() - 1) ingredientsStr.append(", ");
            }
            holder.ingredients.setText("Ingredients: " + ingredientsStr.toString());
        } else {
            holder.ingredients.setText("No ingredients");
        }

        // تحويل النص (Base64) إلى صورة وعرضها
        if (bookRecipe.imageUrl != null && !bookRecipe.imageUrl.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(bookRecipe.imageUrl, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.image.setImageBitmap(decodedByte);
            } catch (Exception e) {
                e.printStackTrace();
                holder.image.setImageResource(R.drawable.o2);
            }
        } else {
            holder.image.setImageResource(R.drawable.o2);
        }

        // عند الضغط على العنصر ينتقل إلى صفحة التفاصيل
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), Recipedetails.class);
            intent.putExtra("recipe", bookRecipe);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookRecipes != null ? bookRecipes.size() : 0;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView title, ingredients;
        ImageView image;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            ingredients = itemView.findViewById(R.id.tv_ingre);
            image = itemView.findViewById(R.id.imageView4);
        }
    }
}
