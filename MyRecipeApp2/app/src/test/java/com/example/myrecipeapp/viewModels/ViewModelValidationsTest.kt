import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.myrecipeapp.database.*
import com.example.myrecipeapp.viewModels.RecipeViewModel
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class ViewModelValidationsTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RecipeViewModel
    private lateinit var repository: RecipeRepository

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var stepsObserver: Observer<List<String>>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = RecipeViewModel(repository)
        stepsObserver = mock<Observer<List<String>>>()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Test for validating ingredient name
    @Test
    fun validateIngredientName_returnsTrueForNonEmptyIngredientName() {
        // GIVEN: A non-empty ingredient name
        val ingredientName = "Flour"

        // WHEN: The validateIngredientName method is called
        val result = viewModel.validateIngredientName(ingredientName)

        // THEN: Ensure that the result is true (valid name)
        assertTrue("Ingredient name validation failed for a valid name.", result)
    }

    @Test
    fun validateIngredientName_returnsFalseForEmptyIngredientName() {
        // GIVEN: An empty ingredient name
        val ingredientName = ""

        // WHEN: The validateIngredientName method is called
        val result = viewModel.validateIngredientName(ingredientName)

        // THEN: Ensure that the result is false (invalid name)
        assertFalse("Ingredient name validation passed for an empty name.", result)
    }

    // Test for validating step description
    @Test
    fun validateStepDescription_returnsTrueForNonEmptyStepDescription() {
        // GIVEN: A valid step description
        val description = "Mix the batter."

        // WHEN: The validateStepDescription method is called
        val result = viewModel.validateStepDescription(description)

        // THEN: Ensure that the result is true (valid description)
        assertTrue("Step description validation failed for a valid description.", result)
    }

    @Test
    fun validateStepDescription_returnsFalseForEmptyStepDescription() {
        // GIVEN: An empty step description
        val description = ""

        // WHEN: The validateStepDescription method is called
        val result = viewModel.validateStepDescription(description)

        // THEN: Ensure that the result is false (invalid description)
        assertFalse("Step description validation passed for an empty description.", result)
    }

    // Test for validating recipe fields
    @Test
    fun validateRecipeFields_returnsTrueForAllValidFields() {
        // GIVEN: Valid recipe fields
        val result = viewModel.validateRecipeFields(
            recipeName = "Pancakes",
            prepTime = "15 min",
            description = "Delicious pancakes",
            ingredientsCount = 3,
            stepsCount = 5
        )

        // WHEN: The validateRecipeFields method is called

        // THEN: Ensure that the result is true (valid fields)
        assertTrue("Recipe fields validation failed for all valid inputs.", result)
    }

    @Test
    fun validateRecipeFields_returnsFalseForEmptyRecipeName() {
        // GIVEN: Empty recipe name
        val result = viewModel.validateRecipeFields(
            recipeName = "",
            prepTime = "15 min",
            description = "Delicious pancakes",
            ingredientsCount = 3,
            stepsCount = 5
        )

        // WHEN: The validateRecipeFields method is called

        // THEN: Ensure that the result is false (invalid fields)
        assertFalse("Recipe fields validation passed with an empty recipe name.", result)
    }

    @Test
    fun validateRecipeFields_returnsFalseForEmptyPrepTime() {
        // GIVEN: Empty prep time
        val result = viewModel.validateRecipeFields(
            recipeName = "Pancakes",
            prepTime = "",
            description = "Delicious pancakes",
            ingredientsCount = 3,
            stepsCount = 5
        )

        // WHEN: The validateRecipeFields method is called

        // THEN: Ensure that the result is false (invalid fields)
        assertFalse("Recipe fields validation passed with an empty prep time.", result)
    }

    @Test
    fun validateRecipeFields_returnsFalseForEmptyDescription() {
        // GIVEN: Empty description
        val result = viewModel.validateRecipeFields(
            recipeName = "Pancakes",
            prepTime = "15 min",
            description = "",
            ingredientsCount = 3,
            stepsCount = 5
        )

        // WHEN: The validateRecipeFields method is called

        // THEN: Ensure that the result is false (invalid fields)
        assertFalse("Recipe fields validation passed with an empty description.", result)
    }

    @Test
    fun validateRecipeFields_returnsFalseForZeroIngredients() {
        // GIVEN: Zero ingredients count
        val result = viewModel.validateRecipeFields(
            recipeName = "Pancakes",
            prepTime = "15 min",
            description = "Delicious pancakes",
            ingredientsCount = 0,
            stepsCount = 5
        )

        // WHEN: The validateRecipeFields method is called

        // THEN: Ensure that the result is false (invalid fields)
        assertFalse("Recipe fields validation passed with zero ingredients.", result)
    }

    @Test
    fun validateRecipeFields_returnsFalseForZeroSteps() {
        // GIVEN: Zero steps count
        val result = viewModel.validateRecipeFields(
            recipeName = "Pancakes",
            prepTime = "15 min",
            description = "Delicious pancakes",
            ingredientsCount = 3,
            stepsCount = 0
        )

        // WHEN: The validateRecipeFields method is called

        // THEN: Ensure that the result is false (invalid fields)
        assertFalse("Recipe fields validation passed with zero steps.", result)
    }
}
