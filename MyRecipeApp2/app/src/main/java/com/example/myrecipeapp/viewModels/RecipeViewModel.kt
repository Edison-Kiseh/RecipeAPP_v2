package com.example.myrecipeapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipeapp.database.IngredientsTable
import com.example.myrecipeapp.database.RecipeRepository
import com.example.myrecipeapp.database.RecipeTable
import com.example.myrecipeapp.database.StepsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableLiveData<List<RecipeTable>>()
    val recipes: LiveData<List<RecipeTable>> get() = _recipes

    // for validation the fields
    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> get() = _validationError

    fun fetchRecipes() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("ViewModel", "Fetch recipes function called")
            repository.fetchAllRecipes { recipeList ->
                _recipes.postValue(recipeList) // Update LiveData
                Log.d("ViewModel", "Fetched recipes: $recipeList")
            }
        }
    }

    private val _recipe = MutableLiveData<RecipeTable?>() // Now it can accept null values
    val recipe: LiveData<RecipeTable?> get() = _recipe

    fun fetchRecipeById(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getRecipeById(recipeId) { recipe ->
//                withContext(Dispatchers.Main) {
                if (recipe != null) {
                    _recipe.postValue(recipe)  // Update LiveData with the recipe
                } else {
                    Log.e("ViewModel", "Recipe not found")
                }
//                }
            }
        }
    }

    private val _ingredients = MutableLiveData<List<String>>()
    val ingredients: LiveData<List<String>> get() = _ingredients

    // Fetch Ingredients by RecipeId
    fun fetchIngredientsByRecipeId(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchIngredients(recipeId) { ingredientsList ->
//                withContext(Dispatchers.Main) {
                _ingredients.postValue(ingredientsList)
//                }
            }
        }
    }

    private val _steps = MutableLiveData<List<String>>()
    val steps: LiveData<List<String>> get() = _steps

    // Fetch Steps by RecipeId
    fun fetchStepsByRecipeId(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchSteps(recipeId) { stepsList ->
//                withContext(Dispatchers.Main) {
                _steps.postValue(stepsList)
//                }
            }
        }
    }

    fun validateIngredientName(ingredientName: String): Boolean {
        return ingredientName.isNotEmpty()
    }

    fun validateStepDescription(stepDescription: String): Boolean {
        return stepDescription.isNotEmpty()
    }

    fun validateRecipeFields(recipeName: String, prepTime: String, description: String, ingredientsCount: Int, stepsCount: Int): Boolean {
        return recipeName.isNotEmpty() && prepTime.isNotEmpty() && description.isNotEmpty() && ingredientsCount > 0 && stepsCount > 0
    }

    fun addRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        if (!validateRecipeFields(recipe.name, recipe.prepTime, recipe.description, ingredients.size, steps.size)) {
            _validationError.postValue("Recipe fields cannot be empty")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecipeWithIngredients(recipe, ingredients, steps)
            _validationError.postValue("No problems found") // Clear any error
        }
    }

    fun updateRecipeWithIngredients(recipe: RecipeTable, ingredients: List<IngredientsTable>, steps: List<StepsTable>) {
        if (!validateRecipeFields(recipe.name, recipe.prepTime, recipe.description, ingredients.size, steps.size)) {
            _validationError.postValue("Recipe fields cannot be empty")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRecipeWithIngredients(recipe, ingredients, steps)
            _validationError.postValue("No problems found") // Clear any error
        }
    }

    fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.deleteRecipe(recipeId)
            if (result) {
                _validationError.postValue("Recipe deleted successfully")
            } else {
                _validationError.postValue("Failed to delete recipe")
            }
        }
    }

    companion object {
        fun create(): RecipeViewModel {
            return RecipeViewModel(repository = RecipeRepository())
        }
    }
}