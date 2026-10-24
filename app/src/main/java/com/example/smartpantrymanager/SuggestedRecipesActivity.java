
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView recipeList;
    private TextView emptyMessage;

    private final ArrayList<String> recipeNames = new ArrayList<>();
    private final ArrayList<Long> recipeIds = new ArrayList<>();

    private static class Amount {
        double quantity;
        String dimension;

        Amount(double quantity, String dimension) {
            this.quantity = quantity;
            this.dimension = dimension;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);

        recipeList = findViewById(R.id.recipeList);
        emptyMessage = findViewById(R.id.emptyMessage);

        recipeList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(
                    this, RecipeDetailActivity.class
            );

            intent.putExtra("recipe_id", recipeIds.get(position));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private String normalizeName(String value) {
        String name = value.trim().toLowerCase(Locale.ROOT);

        if (name.equals("tomatoes")) return "tomato";
        if (name.equals("potatoes")) return "potato";
        if (name.equals("eggs")) return "egg";
        if (name.equals("bananas")) return "banana";
        if (name.equals("apples")) return "apple";

        if (name.endsWith("ies") && name.length() > 3) {
            return name.substring(0, name.length() - 3) + "y";
        }

        if (name.endsWith("s")
                && !name.endsWith("ss")
                && !name.equals("oats")
                && name.length() > 3) {
            return name.substring(0, name.length() - 1);
        }

        return name;
    }

    private Amount convertAmount(double quantity, String unit) {
        String u = unit.trim().toLowerCase(Locale.ROOT);

        switch (u) {
            case "kg":
            case "kilogram":
            case "kilograms":
                return new Amount(quantity * 1000, "mass");

            case "g":
            case "gram":
            case "grams":
                return new Amount(quantity, "mass");

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return new Amount(quantity * 1000, "volume");

            case "ml":
            case "millilitre":
            case "millilitres":
                return new Amount(quantity, "volume");

            case "pcs":
            case "pc":
            case "piece":
            case "pieces":
            case "unit":
            case "units":
                return new Amount(quantity, "count");

            case "slice":
            case "slices":
                return new Amount(quantity, "slices");

            default:
                return new Amount(quantity, u);
        }
    }

    private String keyFor(String name, String dimension) {
        return normalizeName(name) + "|" + dimension;
    }

    private Map<String, Double> getPantryAmounts() {
        Map<String, Double> pantry = new HashMap<>();

        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(1);
                double quantity = cursor.getDouble(2);
                String unit = cursor.getString(3);

                Amount amount = convertAmount(quantity, unit);
                String key = keyFor(name, amount.dimension);

                pantry.put(key,
                        pantry.getOrDefault(key, 0.0) + amount.quantity);
            }
        }

        return pantry;
    }

    private boolean canMakeRecipe(
            long recipeId, Map<String, Double> pantry) {

        boolean hasRequirements = false;

        try (Cursor cursor =
                     databaseHelper.getRecipeIngredients(recipeId)) {

            while (cursor.moveToNext()) {
                hasRequirements = true;

                String ingredient = cursor.getString(0);
                double requiredQuantity = cursor.getDouble(1);
                String unit = cursor.getString(2);

                Amount required = convertAmount(requiredQuantity, unit);
                String key = keyFor(ingredient, required.dimension);

                double available = pantry.getOrDefault(key, 0.0);

                if (available + 0.000001 < required.quantity) {
                    return false;
                }
            }
        }

        return hasRequirements;
    }

    private void loadSuggestedRecipes() {
        recipeNames.clear();
        recipeIds.clear();

        Map<String, Double> pantry = getPantryAmounts();

        try (Cursor cursor = databaseHelper.getAllRecipes()) {
            while (cursor.moveToNext()) {
                long recipeId = cursor.getLong(0);
                String recipeName = cursor.getString(1);

                if (canMakeRecipe(recipeId, pantry)) {
                    recipeIds.add(recipeId);
                    recipeNames.add(recipeName);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeNames
        );

        recipeList.setAdapter(adapter);

        if (recipeNames.isEmpty()) {
            emptyMessage.setText(
                    "No recipes match your pantry yet.\n" +
                            "Add more ingredients to unlock recipes."
            );
            emptyMessage.setVisibility(TextView.VISIBLE);
            recipeList.setVisibility(ListView.GONE);
        } else {
            emptyMessage.setVisibility(TextView.GONE);
            recipeList.setVisibility(ListView.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
