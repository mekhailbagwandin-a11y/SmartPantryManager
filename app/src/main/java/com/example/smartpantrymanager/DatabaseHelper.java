

package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Pantry ingredients table
        db.execSQL(
                "CREATE TABLE pantry (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT)"
        );

        createRecipeTables(db);
        seedRecipes(db);
    }

    private void createRecipeTables(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {

        // Preserve existing pantry records
        if (oldVersion < 2) {
            createRecipeTables(db);
            seedRecipes(db);
        }
    }

    // CREATE: Add ingredient
    public boolean addIngredient(String name,
                                 double quantity,
                                 String unit,
                                 String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        return db.insert("pantry", null, values) != -1;
    }

    // READ: Retrieve pantry ingredients
    public Cursor getAllIngredients() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id AS _id, name, quantity, unit, expiry_date " +
                        "FROM pantry ORDER BY name ASC",
                null
        );
    }

    // UPDATE: Modify ingredient
    public boolean updateIngredient(int id,
                                    String name,
                                    double quantity,
                                    String unit,
                                    String expiryDate) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        int rows = db.update(
                "pantry",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }

    // DELETE: Remove ingredient
    public boolean deleteIngredient(int id) {

        SQLiteDatabase db = getWritableDatabase();

        int rows = db.delete(
                "pantry",
                "id=?",
                new String[]{String.valueOf(id)}
        );

        return rows > 0;
    }

    // Retrieve every recipe
    public Cursor getAllRecipes() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id AS _id, name, instructions " +
                        "FROM recipes ORDER BY name ASC",
                null
        );
    }

    // Retrieve ingredients required by one recipe
    public Cursor getRecipeIngredients(long recipeId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT ingredient_name, quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id=?",
                new String[]{String.valueOf(recipeId)}
        );
    }

    // Retrieve details of one recipe
    public Cursor getRecipeById(long recipeId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id AS _id, name, instructions " +
                        "FROM recipes WHERE id=?",
                new String[]{String.valueOf(recipeId)}
        );
    }

    // Seed 20 recipes on database creation or upgrade
    private void seedRecipes(SQLiteDatabase db) {

        addRecipe(db, "Boiled Eggs",
                "Boil eggs in water for 8 to 10 minutes.",
                new String[]{"egg"},
                new double[]{2},
                new String[]{"pcs"});

        addRecipe(db, "Scrambled Eggs",
                "Beat eggs with milk and cook gently until set.",
                new String[]{"egg", "milk"},
                new double[]{2, 50},
                new String[]{"pcs", "ml"});

        addRecipe(db, "Toast",
                "Toast bread until golden and crispy.",
                new String[]{"bread"},
                new double[]{2},
                new String[]{"slices"});

        addRecipe(db, "Cheese Sandwich",
                "Place cheese between two slices of bread.",
                new String[]{"bread", "cheese"},
                new double[]{2, 30},
                new String[]{"slices", "g"});

        addRecipe(db, "Tomato Sandwich",
                "Slice tomato and place between bread slices.",
                new String[]{"bread", "tomato"},
                new double[]{2, 1},
                new String[]{"slices", "pcs"});

        addRecipe(db, "Egg Sandwich",
                "Cook eggs and place between bread slices.",
                new String[]{"bread", "egg"},
                new double[]{2, 2},
                new String[]{"slices", "pcs"});

        addRecipe(db, "Cheese Omelette",
                "Beat eggs, add cheese and cook in a pan.",
                new String[]{"egg", "cheese"},
                new double[]{2, 30},
                new String[]{"pcs", "g"});

        addRecipe(db, "Tomato Omelette",
                "Beat eggs, add chopped tomato and cook.",
                new String[]{"egg", "tomato"},
                new double[]{2, 1},
                new String[]{"pcs", "pcs"});

        addRecipe(db, "French Toast",
                "Dip bread in beaten egg and milk. Cook on a pan.",
                new String[]{"bread", "egg", "milk"},
                new double[]{2, 1, 50},
                new String[]{"slices", "pcs", "ml"});

        addRecipe(db, "Banana Smoothie",
                "Blend banana and milk until smooth.",
                new String[]{"banana", "milk"},
                new double[]{1, 200},
                new String[]{"pcs", "ml"});

        addRecipe(db, "Banana Oats",
                "Cook oats with milk and add sliced banana.",
                new String[]{"oats", "banana", "milk"},
                new double[]{50, 1, 150},
                new String[]{"g", "pcs", "ml"});

        addRecipe(db, "Plain Porridge",
                "Cook oats with milk until thick and soft.",
                new String[]{"oats", "milk"},
                new double[]{50, 200},
                new String[]{"g", "ml"});

        addRecipe(db, "Rice Bowl",
                "Cook rice until tender and serve.",
                new String[]{"rice"},
                new double[]{100},
                new String[]{"g"});

        addRecipe(db, "Tomato Rice",
                "Cook rice and mix with chopped tomatoes.",
                new String[]{"rice", "tomato"},
                new double[]{100, 1},
                new String[]{"g", "pcs"});

        addRecipe(db, "Cheese Pasta",
                "Cook pasta and stir in cheese.",
                new String[]{"pasta", "cheese"},
                new double[]{100, 40},
                new String[]{"g", "g"});

        addRecipe(db, "Tomato Pasta",
                "Cook pasta and combine with chopped tomato.",
                new String[]{"pasta", "tomato"},
                new double[]{100, 2},
                new String[]{"g", "pcs"});

        addRecipe(db, "Potato Mash",
                "Boil potatoes and mash with milk.",
                new String[]{"potato", "milk"},
                new double[]{2, 100},
                new String[]{"pcs", "ml"});

        addRecipe(db, "Potato and Egg",
                "Boil potatoes and eggs, then combine.",
                new String[]{"potato", "egg"},
                new double[]{2, 2},
                new String[]{"pcs", "pcs"});

        addRecipe(db, "Apple Oats",
                "Cook oats with milk and add chopped apple.",
                new String[]{"oats", "milk", "apple"},
                new double[]{50, 200, 1},
                new String[]{"g", "ml", "pcs"});

        addRecipe(db, "Banana Sandwich",
                "Slice banana and place between bread slices.",
                new String[]{"bread", "banana"},
                new double[]{2, 1},
                new String[]{"slices", "pcs"});
    }

    // Insert a recipe and its required ingredients
    private void addRecipe(SQLiteDatabase db,
                           String name,
                           String instructions,
                           String[] ingredients,
                           double[] quantities,
                           String[] units) {

        if (ingredients.length != quantities.length ||
                ingredients.length != units.length) {
            throw new IllegalArgumentException(
                    "Recipe ingredient data lengths do not match"
            );
        }

        ContentValues recipeValues = new ContentValues();

        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insertOrThrow(
                "recipes", null, recipeValues
        );

        for (int i = 0; i < ingredients.length; i++) {

            ContentValues ingredientValues =
                    new ContentValues();

            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put(
                    "ingredient_name", ingredients[i]
            );
            ingredientValues.put(
                    "quantity", quantities[i]
            );
            ingredientValues.put(
                    "unit", units[i]
            );

            db.insertOrThrow(
                    "recipe_ingredients",
                    null,
                    ingredientValues
            );
        }
    }
}
