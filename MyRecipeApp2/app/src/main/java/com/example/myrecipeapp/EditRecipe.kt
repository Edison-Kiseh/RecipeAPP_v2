package com.example.myrecipeapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.myrecipeapp.database.*
import com.example.myrecipeapp.databinding.FragmentEditRecipeBinding
import com.example.myrecipeapp.viewModels.RecipeViewModel
import com.example.myrecipeapp.viewModels.RecipeViewModelFactory

class EditRecipe : Fragment() {

    lateinit var binding: FragmentEditRecipeBinding
    private var stepCount = 0
    lateinit var viewModel: RecipeViewModel
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    // Use navigation arguments to get the recipe ID
    private val args: EditRecipeArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditRecipeBinding.inflate(inflater, container, false)

        binding.saveIngredients.setBackgroundColor(resources.getColor(R.color.SeaGreen))
        binding.saveRecipe.setBackgroundColor(resources.getColor(R.color.SeaGreen))
        binding.saveSteps.setBackgroundColor(resources.getColor(R.color.SeaGreen))

        viewModel = RecipeViewModel.create()

        binding.saveIngredients.setOnClickListener {
            addIngredient()
        }

        binding.saveSteps.setOnClickListener {
            addStep()
        }

        binding.saveRecipe.setOnClickListener {
            saveRecipe()
        }

        // Load the recipe data
        loadRecipeData(args.recipeId)

        return binding.root
    }

    fun addIngredient() {
        val ingredientName = binding.newCheckbox.text.toString().trim()
        if (viewModel.validateIngredientName(ingredientName)) {
            addIngredientLayout(ingredientName)
            binding.newCheckbox.text.clear()
        } else {
            Toast.makeText(requireContext(), "Please enter an ingredient name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addIngredientLayout(ingredientName: String) {
        val ingredientLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 4, 0, 16)
        }

        val ingredientTextView = TextView(requireContext()).apply {
            text = "• $ingredientName"
            setPadding(0, 4, 30, 0)
            setTextSize(16F)
        }

        val closeButton = ImageView(requireContext()).apply {
            setImageResource(R.drawable.x)
            layoutParams = LinearLayout.LayoutParams(48, 48)
            setOnClickListener {
                binding.ingredientsContainer.removeView(ingredientLayout)
            }
        }

        ingredientLayout.addView(ingredientTextView)
        ingredientLayout.addView(closeButton)
        binding.ingredientsContainer.addView(ingredientLayout)
    }


    fun addStep() {
        val stepDescription = binding.addStep.text.toString().trim()
        if (viewModel.validateStepDescription(stepDescription)) {
            stepCount++
            addStepLayout(stepCount, stepDescription)
            binding.addStep.text.clear()
        } else {
            Toast.makeText(requireContext(), "Please enter a step first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addStepLayout(stepNumber: Int, stepDescription: String) {
        val stepLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 4, 0, 16)
        }

        val stepTextView = TextView(requireContext()).apply {
            id = View.generateViewId()
            text = "Step $stepNumber: $stepDescription"
            setPadding(0, 4, 30, 0)
            setTextSize(16F)
        }

        val closeButton = ImageView(requireContext()).apply {
            setImageResource(R.drawable.x)
            layoutParams = LinearLayout.LayoutParams(48, 48)
            setOnClickListener {
                binding.stepsContainer.removeView(stepLayout)
                updateStepNumbers()
            }
        }

        stepLayout.addView(stepTextView)
        stepLayout.addView(closeButton)
        binding.stepsContainer.addView(stepLayout)
    }

    private fun updateStepNumbers() {
        for (i in 0 until binding.stepsContainer.childCount) {
            val stepLayout = binding.stepsContainer.getChildAt(i) as LinearLayout
            val stepTextView = stepLayout.getChildAt(0) as TextView
            val stepDescription = stepTextView.text.toString().substringAfter(": ")
            stepTextView.text = "Step ${i + 1}: $stepDescription"
        }
        stepCount = binding.stepsContainer.childCount
    }

    fun saveRecipe() {
        val recipeName = binding.recipeNameField.text.toString().trim()
        val prepTime = binding.prepTimeField.text.toString().trim()
        val description = binding.descriptionField.text.toString().trim()

        val ingredientsCount = binding.ingredientsContainer.childCount
        val stepsCount = binding.stepsContainer.childCount

        if (viewModel.validateRecipeFields(recipeName, prepTime, description, ingredientsCount, stepsCount)) {
            val foodImage: String = R.drawable.recipes.toString()

            val recipe = RecipeTable(
                recipeId = args.recipeId, // Using the existing recipe ID
                name = recipeName,
                description = description,
                prepTime = prepTime,
                image = foodImage,
                stepCount = binding.stepsContainer.childCount,
                ingredientCount = binding.ingredientsContainer.childCount,
            )

            val ingredients = mutableListOf<IngredientsTable>()
            for (i in 0 until ingredientsCount) {
                val ingredientLayout = binding.ingredientsContainer.getChildAt(i) as LinearLayout
                val ingredientTextView = ingredientLayout.getChildAt(0) as TextView
                val ingredientName = ingredientTextView.text.toString().trim().removePrefix("• ")
                ingredients.add(IngredientsTable(name = ingredientName, recipeId = args.recipeId))
            }

            val steps = mutableListOf<StepsTable>()
            for (i in 0 until stepsCount) {
                val stepLayout = binding.stepsContainer.getChildAt(i) as LinearLayout
                val stepTextView = stepLayout.getChildAt(0) as TextView
                val stepDescription = stepTextView.text.toString().trim().substringAfter(": ")
                steps.add(StepsTable(name = stepDescription, recipeId = args.recipeId))
            }

            viewModel.updateRecipeWithIngredients(recipe, ingredients, steps)

            Toast.makeText(requireContext(), "Recipe updated successfully!", Toast.LENGTH_SHORT).show()

            val navController = findNavController()
            val action = EditRecipeDirections.actionEditRecipeToRecipeInfo(args.recipeId)
            navController.navigate(action)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRecipeData(recipeId: Long) {
        val repository = RecipeRepository()
        val factory = RecipeViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(RecipeViewModel::class.java)

        viewModel.recipe.observe(viewLifecycleOwner, Observer { recipe ->
            if (recipe != null) {
                binding.recipeNameField.setText(recipe.name)
            }
            binding.prepTimeField.setText(recipe?.prepTime)
            binding.descriptionField.setText(recipe?.description)
        })

        viewModel.ingredients.observe(viewLifecycleOwner, Observer { ingredients ->
            binding.ingredientsContainer.removeAllViews()

            for (ingredient in ingredients) {
                addIngredientLayout(ingredient)
            }
        })

        viewModel.steps.observe(viewLifecycleOwner, Observer { steps ->
            binding.stepsContainer.removeAllViews()

            stepCount = 0

            for (step in steps) {
                stepCount++
                addStepLayout(stepCount, step)
            }
        })

        //fetching the data
        viewModel.fetchRecipeById(recipeId)
        viewModel.fetchStepsByRecipeId(recipeId)
        viewModel.fetchIngredientsByRecipeId(recipeId)
    }
}
