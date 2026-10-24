
package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private ListView pantryList;
    private ArrayAdapter<String> adapter;

    private final ArrayList<String> ingredients = new ArrayList<>();
    private final ArrayList<Integer> ingredientIds = new ArrayList<>();

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        databaseHelper = new DatabaseHelper(this);

        preferences = getSharedPreferences(
                SettingsActivity.PREFS_NAME,
                MODE_PRIVATE
        );

        pantryList = findViewById(R.id.pantryList);
        Button btnAdd = findViewById(R.id.btnAddIngredient);

        btnAdd.setOnClickListener(v -> showIngredientDialog(-1));

        pantryList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    int ingredientId = ingredientIds.get(position);
                    showOptionsDialog(ingredientId);
                }
        );

        loadIngredients();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            loadIngredients();
        }
    }

    private void loadIngredients() {
        ingredients.clear();
        ingredientIds.clear();

        boolean alertsEnabled = preferences.getBoolean(
                SettingsActivity.KEY_EXPIRY_ALERTS,
                true
        );

        try (Cursor cursor = databaseHelper.getAllIngredients()) {

            while (cursor.moveToNext()) {

                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double quantity = cursor.getDouble(2);
                String unit = cursor.getString(3);
                String expiry = cursor.getString(4);

                ingredientIds.add(id);

                StringBuilder display = new StringBuilder();

                display.append(name)
                        .append(" - ")
                        .append(quantity)
                        .append(" ")
                        .append(unit);

                if (expiry != null && !expiry.isEmpty()) {

                    display.append("\nExpiry: ").append(expiry);

                    if (alertsEnabled) {
                        display.append("\n")
                                .append(getExpiryStatus(expiry));
                    }
                }

                ingredients.add(display.toString());
            }
        }

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                ingredients
        );

        pantryList.setAdapter(adapter);
    }

    private String getExpiryStatus(String expiryDate) {

        try {
            LocalDate expiry = LocalDate.parse(expiryDate);
            LocalDate today = LocalDate.now();

            long daysRemaining = ChronoUnit.DAYS.between(
                    today, expiry
            );

            if (daysRemaining < 0) {
                return "EXPIRED - Do not use";
            }

            if (daysRemaining == 0) {
                return "WARNING: Expires today";
            }

            if (daysRemaining <= 3) {
                return "WARNING: Expires in "
                        + daysRemaining + " day(s)";
            }

            return "Fresh - " + daysRemaining
                    + " days remaining";

        } catch (DateTimeParseException e) {
            return "Invalid expiry date";
        }
    }

    private void showOptionsDialog(int ingredientId) {

        String[] options = {
                "Edit Ingredient",
                "Delete Ingredient"
        };

        new AlertDialog.Builder(this)
                .setTitle("Manage Ingredient")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        showIngredientDialog(ingredientId);
                    } else {
                        confirmDelete(ingredientId);
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showIngredientDialog(int ingredientId) {

        boolean editing = ingredientId != -1;

        EditText nameInput = new EditText(this);
        nameInput.setHint("Ingredient Name");

        EditText quantityInput = new EditText(this);
        quantityInput.setHint("Quantity");
        quantityInput.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        EditText unitInput = new EditText(this);
        unitInput.setHint("Unit (g, ml, pcs, kg, L)");

        EditText expiryInput = new EditText(this);
        expiryInput.setHint("Expiry Date YYYY-MM-DD (Optional)");
        expiryInput.setSingleLine(true);

        if (editing) {

            boolean found = false;

            try (Cursor cursor =
                         databaseHelper.getAllIngredients()) {

                while (cursor.moveToNext()) {

                    if (cursor.getInt(0) == ingredientId) {

                        nameInput.setText(cursor.getString(1));

                        quantityInput.setText(
                                String.valueOf(cursor.getDouble(2))
                        );

                        unitInput.setText(cursor.getString(3));

                        String expiry = cursor.getString(4);

                        expiryInput.setText(
                                expiry == null ? "" : expiry
                        );

                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                Toast.makeText(this,
                        "Ingredient not found",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        layout.addView(nameInput);
        layout.addView(quantityInput);
        layout.addView(unitInput);
        layout.addView(expiryInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(
                        editing ? "Edit Ingredient" : "Add Ingredient"
                )
                .setView(layout)
                .setPositiveButton("Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(unused -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String name = nameInput.getText()
                                .toString().trim();

                        String qtyText = quantityInput.getText()
                                .toString().trim();

                        String unit = unitInput.getText()
                                .toString().trim();

                        String expiry = expiryInput.getText()
                                .toString().trim();

                        if (name.isEmpty()) {
                            nameInput.setError("Name is required");
                            return;
                        }

                        if (qtyText.isEmpty()) {
                            quantityInput.setError(
                                    "Quantity is required"
                            );
                            return;
                        }

                        if (unit.isEmpty()) {
                            unitInput.setError("Unit is required");
                            return;
                        }

                        double quantity;

                        try {
                            quantity = Double.parseDouble(qtyText);
                        } catch (NumberFormatException e) {
                            quantityInput.setError(
                                    "Enter a valid quantity"
                            );
                            return;
                        }

                        if (!Double.isFinite(quantity)
                                || quantity <= 0) {

                            quantityInput.setError(
                                    "Quantity must be greater than zero"
                            );
                            return;
                        }

                        if (!expiry.isEmpty()) {
                            try {
                                LocalDate parsed =
                                        LocalDate.parse(expiry);

                                if (!parsed.toString().equals(expiry)) {
                                    expiryInput.setError(
                                            "Use YYYY-MM-DD"
                                    );
                                    return;
                                }

                            } catch (DateTimeParseException e) {
                                expiryInput.setError(
                                        "Invalid date. Use YYYY-MM-DD"
                                );
                                return;
                            }
                        }

                        boolean success;

                        if (editing) {
                            success = databaseHelper.updateIngredient(
                                    ingredientId,
                                    name,
                                    quantity,
                                    unit,
                                    expiry
                            );
                        } else {
                            success = databaseHelper.addIngredient(
                                    name,
                                    quantity,
                                    unit,
                                    expiry
                            );
                        }

                        if (success) {

                            Toast.makeText(this,
                                    editing ?
                                            "Ingredient Updated" :
                                            "Ingredient Added",
                                    Toast.LENGTH_SHORT).show();

                            loadIngredients();
                            dialog.dismiss();

                        } else {
                            Toast.makeText(this,
                                    "Unable to save ingredient",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialog.show();
    }

    private void confirmDelete(int ingredientId) {

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete this ingredient?"
                )
                .setPositiveButton("Delete", (dialog, which) -> {

                    boolean deleted =
                            databaseHelper.deleteIngredient(ingredientId);

                    if (deleted) {
                        Toast.makeText(this,
                                "Ingredient Deleted",
                                Toast.LENGTH_SHORT).show();

                        loadIngredients();

                    } else {
                        Toast.makeText(this,
                                "Unable to delete ingredient",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}
