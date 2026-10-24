
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView recipeTitle = findViewById(R.id.recipeTitle);
        TextView recipeIngredients =
                findViewById(R.id.recipeIngredients);
        TextView recipeInstructions =
                findViewById(R.id.recipeInstructions);

        databaseHelper = new DatabaseHelper(this);

        long recipeId = getIntent().getLongExtra("recipe_id", -1);

        if (recipeId == -1) {
            finish();
            return;
        }

        try (Cursor cursor = databaseHelper.getRecipeById(recipeId)) {
            if (cursor.moveToFirst()) {
                recipeTitle.setText(cursor.getString(1));
                recipeInstructions.setText(cursor.getString(2));
            } else {
                finish();
                return;
            }
        }

        StringBuilder ingredients = new StringBuilder();

        try (Cursor cursor =
                     databaseHelper.getRecipeIngredients(recipeId)) {

            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                double quantity = cursor.getDouble(1);
                String unit = cursor.getString(2);

                ingredients.append("• ")
                        .append(name)
                        .append(" — ")
                        .append(quantity)
                        .append(" ")
                        .append(unit)
                        .append("\n");
            }
        }

        recipeIngredients.setText(ingredients.toString());
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
