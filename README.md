# Smart Pantry Manager

**Mobile App Development 700 | Richfield Graduate Institute of Technology**

## 1. Project Overview

Smart Pantry Manager is a native Android application developed using Java, XML and SQLite. The application helps users manage household pantry ingredients, monitor expiry dates, discover recipes they can prepare using available ingredients and reduce unnecessary food waste.

The application operates locally using SQLite, allowing users to access and manage their pantry without requiring a continuous internet connection.

## 2. Problem Statement

Food waste is a common household problem caused by poor ingredient tracking, forgotten expiry dates and ineffective meal planning.

Smart Pantry Manager addresses these challenges by providing a centralised mobile application that allows users to monitor available ingredients and identify meals they can prepare without purchasing additional ingredients.

The application prioritises strict recipe matching, ensuring that users are only recommended recipes when all required ingredients are available in sufficient quantities.

## 3. Technologies Used

| Technology | Purpose |
|---|---|
| Java | Core application logic and Android development |
| XML | User interface layouts |
| Android Studio | Integrated development environment |
| SQLite | Local persistent database |
| SQLiteOpenHelper | Database creation and management |
| ListView | Displaying pantry items and recipes |
| ArrayAdapter | Connecting database results to list interfaces |
| Android Activities | Implementing application screens |
| Intents | Navigation and data passing |
| SharedPreferences | Persistent user settings |
| Git and GitHub | Version control and source management |

## 4. Core Features

### 4.1 Pantry Management

Users can:

- Add new pantry ingredients.
- View stored ingredients.
- Edit existing ingredient information.
- Delete ingredients that are no longer available.
- Specify ingredient names, quantities, measurement units and optional expiry dates.
- Access previously saved data after reopening the application.

### 4.2 Recipe Management

The application contains 20 predefined recipes stored in the SQLite database.

Each recipe includes:

- Recipe name.
- Required ingredient list.
- Required ingredient quantities.
- Measurement units.
- Preparation instructions.

### 4.3 Strict Recipe Matching

The application implements a strict ingredient-matching algorithm.

A recipe is suggested only when every ingredient required by that recipe is available in the user's pantry in sufficient quantity.

For example, if a recipe requires two eggs and 50 ml of milk, the recipe will only appear when both requirements are satisfied.

The algorithm supports basic ingredient-name normalisation and compatible measurement-unit conversions, including grams to kilograms and millilitres to litres.

Recipes with missing or insufficient ingredients are excluded from the suggested list.

### 4.4 Recipe Details

Users can select a suggested recipe to view its complete ingredient requirements and preparation instructions.

### 4.5 Expiry Monitoring

Users can enter optional expiry dates for pantry ingredients.

The application identifies ingredients as:

- Expired.
- Expiring today.
- Expiring within three days.
- Fresh.

Expiry warnings are displayed within the pantry interface.

### 4.6 Settings

The Settings screen allows users to enable or disable expiry warnings.

User preferences are stored using SharedPreferences and remain available when the application is reopened.

## 5. Application Screens

The application contains the following screens:

1. **Main Activity:** Provides navigation to the application's primary features.
2. **Pantry Activity:** Displays pantry ingredients and allows users to add, edit and delete records.
3. **Suggested Recipes Activity:** Displays recipes that satisfy strict ingredient-matching requirements.
4. **Recipe Detail Activity:** Displays ingredient requirements and preparation instructions for a selected recipe.
5. **Settings Activity:** Provides persistent expiry-warning preferences and application information.

## 6. Database Implementation

Smart Pantry Manager uses SQLite for local persistent storage.

The database is managed using the `DatabaseHelper` class, which extends `SQLiteOpenHelper`.

### Database Tables

**Pantry**

Stores the user's available ingredients.

Fields include:

- id
- name
- quantity
- unit
- expiry_date

**Recipes**

Stores predefined recipe information.

Fields include:

- id
- name
- instructions

**Recipe Ingredients**

Stores the ingredients required for each recipe.

Fields include:

- id
- recipe_id
- ingredient_name
- quantity
- unit

The recipe_ingredients table links recipe records to their required ingredients through the recipe_id field.

### CRUD Operations

The application supports:

- **Create:** Insert new ingredients into SQLite.
- **Read:** Retrieve and display ingredient records.
- **Update:** Modify existing ingredient information.
- **Delete:** Remove ingredients from the database.

## 7. Why SQLite Was Selected

SQLite was selected because it provides lightweight, reliable, local data persistence without requiring external servers or internet connectivity.

The database integrates directly with Android through SQLiteOpenHelper and supports SQL-based CRUD operations.

For a household pantry application, SQLite provides an appropriate balance between simplicity, performance and offline functionality.

## 8. Project Structure

```text
SmartPantryManager/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/smartpantrymanager/
│           │   ├── MainActivity.java
│           │   ├── PantryActivity.java
│           │   ├── DatabaseHelper.java
│           │   ├── SuggestedRecipesActivity.java
│           │   ├── RecipeDetailActivity.java
│           │   └── SettingsActivity.java
│           ├── res/layout/
│           │   ├── activity_main.xml
│           │   ├── activity_pantry.xml
│           │   ├── activity_suggested_recipes.xml
│           │   ├── activity_recipe_detail.xml
│           │   └── activity_settings.xml
│           └── AndroidManifest.xml
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 9. Installation and Setup Instructions

### Prerequisites

- Android Studio with Android SDK installed.
- Java Development Kit compatible with the project's Gradle configuration.
- Android emulator or physical Android device.
- Git, if cloning the repository.

### Setup Procedure

1. Clone the GitHub repository:

   `git clone https://github.com/mekhailbagwandin-a11y/SmartPantryManager.git`

2. Open Android Studio.
3. Select **Open** and choose the SmartPantryManager project directory.
4. Allow Gradle to synchronise and resolve dependencies.
5. Start an Android emulator or connect a compatible Android device.
6. Select the `app` run configuration.
7. Click **Run** to build and launch the application.

On first use, SQLite creates the database and initialises the predefined recipe collection.

## 10. Application Testing

The application has been manually tested using an Android emulator.

Testing covered:

- Adding pantry ingredients.
- Viewing stored ingredient records.
- Editing and deleting ingredients.
- Validating ingredient inputs.
- Navigating between application screens.
- Matching recipes against pantry ingredient quantities.
- Excluding recipes with missing ingredients.
- Opening recipe details.
- Saving expiry-warning preferences.
- Displaying expiry warnings.

## 11. Limitations and Future Improvements

Potential future enhancements include:

- Barcode scanning for ingredient entry.
- Improved ingredient-name normalisation.
- Automated background expiry notifications.
- Cloud backup