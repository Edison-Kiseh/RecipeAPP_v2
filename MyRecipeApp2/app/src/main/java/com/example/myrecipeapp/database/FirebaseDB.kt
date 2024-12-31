package com.example.myrecipeapp.database

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

open class FirebaseDB(private val dbRef: DatabaseReference) {

    // Constructor for production use
    constructor() : this(FirebaseDatabase.getInstance().getReference("recipes"))

    // Method to add a recipe with ingredients and steps
    open fun addRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        // Create a unique key for this recipe
        val recipeKey = recipe.recipeId

        // Count the number of ingredients and steps
        val ingredientsCount = ingredients.size
        val stepsCount = steps.size

        // Create a map to structure the recipe data
        val recipeData = mapOf(
            "name" to recipe.name,
            "prepTime" to recipe.prepTime,
            "description" to recipe.description,
            "ingredientCount" to ingredientsCount, // Store the count instead of individual quantities
            "stepCount" to stepsCount,
            "ingredients" to ingredients.map { ingredient ->
                mapOf("name" to ingredient.name) // Only the name of each ingredient
            },
            "steps" to steps.mapIndexed { index, step ->
                mapOf("stepNumber" to index + 1, "description" to step.name)
            }
        )

        // Write the recipe data to the database
        dbRef.child(recipeKey.toString()).setValue(recipeData)
            .addOnSuccessListener {
                Log.d("Firebase", "Recipe added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to add recipe: ${e.message}")
            }
    }

    // Method to update an existing recipe with new ingredients and steps
    open fun updateRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        // Use the recipe's unique key to locate it in the database
        val recipeKey = recipe.recipeId

        // Count the number of ingredients and steps
        val ingredientsCount = ingredients.size
        val stepsCount = steps.size

        // Create a map to update the recipe data
        val updatedRecipeData = mapOf(
            "name" to recipe.name,
            "prepTime" to recipe.prepTime,
            "description" to recipe.description,
            "ingredientCount" to ingredientsCount, // Store the count instead of individual quantities
            "stepCount" to stepsCount,
            "ingredients" to ingredients.map { ingredient ->
                mapOf("name" to ingredient.name) // Only the name of each ingredient
            },
            "steps" to steps.mapIndexed { index, step ->
                mapOf("stepNumber" to index + 1, "description" to step.name)
            }
        )

        // Update the recipe in the database
        dbRef.child(recipeKey.toString()).setValue(updatedRecipeData)
            .addOnSuccessListener {
                Log.d("Firebase", "Recipe updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to update recipe: ${e.message}")
            }
    }

    // Method to delete a recipe from the database
    open fun deleteRecipe(recipeKey: Long) {
        dbRef.child(recipeKey.toString()).removeValue()
            .addOnSuccessListener {
                Log.d("Firebase", "Recipe deleted successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete recipe: ${e.message}")
            }
    }

}
