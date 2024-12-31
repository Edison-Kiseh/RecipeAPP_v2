package com.example.myrecipeapp.database

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class RecipeRepository(private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("recipes")) {
    private val FirebaseDB: FirebaseDB = FirebaseDB()

    open fun fetchAllRecipes(callback: (List<RecipeTable>) -> Unit) {
        dbRef.get().addOnSuccessListener { dataSnapshot ->
            val recipeList = mutableListOf<RecipeTable>()

            for (snapshot in dataSnapshot.children) {
                val recipe = snapshot.getValue(RecipeTable::class.java)
                if (recipe != null) {
                    recipe.recipeId = snapshot.key?.toLongOrNull() ?: 0L
                    recipeList.add(recipe)
                }
            }

            callback(recipeList)
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Error fetching recipes: ${e.message}")
            callback(emptyList())
        }
    }

    open fun getRecipeById(recipeId: Long, callback: (RecipeTable?) -> Unit) {
        dbRef.child(recipeId.toString()).get().addOnSuccessListener { dataSnapshot ->
            val recipe = dataSnapshot.getValue(RecipeTable::class.java)
            if (recipe != null) {
                recipe.recipeId = recipeId
            }
            callback(recipe)
        }.addOnFailureListener { e ->
            Log.e("Repository", "Error fetching recipe: ${e.message}")
            callback(null)
        }
    }

    open fun fetchSteps(recipeId: Long, callback: (List<String>) -> Unit) {
        dbRef.child(recipeId.toString()).child("steps").get().addOnSuccessListener { snapshot ->
            val stepsList = mutableListOf<String>()

            for (stepSnapshot in snapshot.children) {
                val stepDescription = stepSnapshot.child("description").getValue(String::class.java)
                if (stepDescription != null) {
                    stepsList.add(stepDescription)
                }
            }

            callback(stepsList)
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Error fetching steps: ${e.message}")
            callback(emptyList())
        }
    }

    open fun fetchIngredients(recipeId: Long, callback: (List<String>) -> Unit) {
        dbRef.child(recipeId.toString()).child("ingredients").get().addOnSuccessListener { snapshot ->
            val ingredientsList = mutableListOf<String>()

            for (ingredientSnapshot in snapshot.children) {
                val ingredientName = ingredientSnapshot.child("name").getValue(String::class.java)
                if (ingredientName != null) {
                    ingredientsList.add(ingredientName)
                }
            }

            callback(ingredientsList)
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Error fetching ingredients: ${e.message}")
            callback(emptyList())
        }
    }

    open fun addRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        FirebaseDB.addRecipeWithIngredients(recipe, ingredients, steps)
    }

    open fun updateRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        FirebaseDB.updateRecipeWithIngredients(recipe, ingredients, steps)
    }

    open fun deleteRecipe(recipeId: Long): Boolean {
        return try {
            FirebaseDB.deleteRecipe(recipeId)
            true
        } catch (e: Exception) {
            Log.e("Repository", "Error deleting recipe: ${e.message}")
            false
        }
    }
}
