import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.myrecipeapp.database.*
import com.example.myrecipeapp.viewModels.RecipeViewModel
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import com.example.myrecipeapp.utils.getOrAwaitValue

@ExperimentalCoroutinesApi
class RecipeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RecipeViewModel
    private lateinit var repository: RecipeRepository

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var repositoryMock: RecipeRepository

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

    // Test for updating recipe with ingredients
    @Test
    fun updateRecipeWithIngredients_updatesRecipe_correctly() = runTest {
        // GIVEN: Recipe, ingredients, and steps
        val recipeId = 1L
        val updatedRecipe = RecipeTable(recipeId = recipeId, name = "Updated Recipe", prepTime = "30 mins", description = "Updated description")
        val ingredients = listOf(IngredientsTable(ingredientId = 1L, name = "Salt", recipeId = recipeId))
        val steps = listOf(StepsTable(stepId = 1L, name = "Step 1", recipeId = recipeId))

        // Mock repository
        repository.updateRecipeWithIngredients(updatedRecipe, ingredients, steps)

        // WHEN: Update the recipe
        viewModel.updateRecipeWithIngredients(updatedRecipe, ingredients, steps)
        advanceUntilIdle()

        // THEN: Verify validation error LiveData value
        val validationMessage = viewModel.validationError.getOrAwaitValue()
        assertEquals("No problems found", validationMessage)
    }

    // Test for deleting a recipe
    @Test
    fun deleteRecipe_deletesRecipe_correctly() = runTest {
        // GIVEN: A recipe ID and a mock repository that simulates successful deletion
        val recipeId = 1L
        val repositoryMock = mock<RecipeRepository>()
        val viewModel = RecipeViewModel(repositoryMock)

        `when`(repositoryMock.deleteRecipe(recipeId)).thenReturn(true)

        // Observe LiveData for validation error
        val validationObserver = mock<Observer<String?>>()
        viewModel.validationError.observeForever(validationObserver)

        // WHEN: Trigger the deletion of the recipe
        viewModel.deleteRecipe(recipeId)

        // THEN: Ensure LiveData is updated with the correct success message
        assertThat(viewModel.validationError.getOrAwaitValue(), `is`("Recipe deleted successfully"))

        verify(validationObserver).onChanged("Recipe deleted successfully")
    }

    // Test for fetching steps by recipe ID successfully
    @Test
    fun fetchStepsByRecipeId_fetchesSteps_correctly() = runTest {
        // GIVEN: A recipe ID and expected steps
        val recipeId = 1L
        val repositoryMock = mock<RecipeRepository>()
        val expectedSteps = listOf("Step 1", "Step 2", "Step 3")

        `when`(repositoryMock.fetchSteps(eq(recipeId), any())).doAnswer {
            val callback = it.getArgument<(List<String>) -> Unit>(1)
            callback.invoke(expectedSteps)
            Unit
        }

        val viewModel = RecipeViewModel(repositoryMock)

        // WHEN: The ViewModel fetches the steps for the recipe
        viewModel.fetchStepsByRecipeId(recipeId)
        advanceUntilIdle()

        // THEN: Verify LiveData value using getOrAwaitValue
        val actualSteps = viewModel.steps.getOrAwaitValue()
        assertEquals(expectedSteps, actualSteps)
    }


    // Test for fetching steps when no steps are available
    @Test
    fun fetchStepsByRecipeId_failsWhenNoSteps() = runTest {
        // GIVEN: A recipe ID and an empty list of steps
        val recipeId = 2L
        val repositoryMock = mock<RecipeRepository>()
        val expectedSteps = emptyList<String>()

        `when`(repositoryMock.fetchSteps(eq(recipeId), any())).doAnswer {
            val callback = it.getArgument<(List<String>) -> Unit>(1)
            callback.invoke(expectedSteps)
            Unit
        }

        val viewModel = RecipeViewModel(repositoryMock)

        // WHEN: The ViewModel fetches the steps for the recipe
        viewModel.fetchStepsByRecipeId(recipeId)
        advanceUntilIdle()

        // THEN: Verify LiveData value using getOrAwaitValue
        val actualSteps = viewModel.steps.getOrAwaitValue()
        assertEquals(expectedSteps, actualSteps)
    }

}
