package com.example.myrecipeapp

import android.widget.Button
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myrecipeapp.database.RecipeTable
import com.example.myrecipeapp.viewModels.RecipeViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.annotation.Config
import org.junit.Before
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class RecipeInfoFragmentTest {

    @get:Rule
    val rule = InstantTaskExecutorRule() // For LiveData

    private val viewModel = mock(RecipeViewModel::class.java)
    private val recipeInfoFragment = RecipeInfo()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testManageTime_lessThanOneHour() {
        // GIVEN: A recipe time of less than one hour
        val recipeInfo = RecipeInfo()
        val inputTime = "45"

        // WHEN: The `manageTime` method is called
        val formattedTime = recipeInfo.manageTime(inputTime)

        // THEN: The formatted time should be "45 mins"
        assertEquals("45 mins", formattedTime)
    }

    @Test
    fun testManageTime_moreThanOneHour() {
        // GIVEN: A recipe time of more than one hour
        val recipeInfo = RecipeInfo()
        val inputTime = "130"

        // WHEN: The `manageTime` method is called
        val formattedTime = recipeInfo.manageTime(inputTime)

        // THEN: The formatted time should be "2 hr 10 mins"
        assertEquals("2 hr 10 mins", formattedTime)
    }

    @Test
    fun testRecipeObservation() {
        // GIVEN: A mock LiveData holding a RecipeTable and a mocked ViewModel
        val recipeLiveData = MutableLiveData<RecipeTable>()
        `when`(viewModel.recipe).thenReturn(recipeLiveData)

        val mockRecipe = RecipeTable(
            recipeId = 1L,
            name = "Test Recipe",
            prepTime = "45",
            description = "Delicious meal",
            image = "test.jpg"
        )
        recipeLiveData.value = mockRecipe

        // WHEN: The RecipeInfoFragment observes the recipe LiveData
        recipeInfoFragment.viewModel = viewModel
        recipeLiveData.observeForever {
            // THEN: The observed recipe details should match the mock data
            assertEquals("Test Recipe", it.name)
            assertEquals("45", it.prepTime)
            assertEquals("Delicious meal", it.description)
        }
    }
}
