package com.example.myrecipeapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myrecipeapp.database.RecipeRepository
import com.example.myrecipeapp.databinding.FragmentHomeBinding
import com.example.myrecipeapp.utils.NetworkUtils
import com.example.myrecipeapp.viewModels.RecipeViewModel
import com.example.myrecipeapp.viewModels.RecipeViewModelFactory

class Home : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    lateinit var viewModel: RecipeViewModel
    lateinit var adapter: RecipeAdapter
    private lateinit var connectivityReceiver: ConnectivityReceiver
    public lateinit var navController: NavController

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val repository = RecipeRepository()
        val factory = RecipeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(RecipeViewModel::class.java)

        recyclerView = binding.recycleView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        navController = findNavController()
        adapter = RecipeAdapter(navController, listOf(), viewModel)
        recyclerView.adapter = adapter

        setupNotificationChannel()

        viewModel.recipes.observe(viewLifecycleOwner, Observer { recipes ->
            if (recipes.isEmpty()) {
                showAddRecipeNotification()
                binding.emptyMessageText.setText(R.string.empty_recipes_message)
                binding.emptyMessageText.visibility = View.VISIBLE
                binding.noodles.setImageResource(R.drawable.noodles)
                binding.noodles.visibility = View.VISIBLE
                hideLoadingAnimation()
            } else {
                binding.emptyMessageText.visibility = View.GONE
                binding.noodles.visibility = View.GONE
                adapter.updateData(recipes)
                hideLoadingAnimation()

                if (recipes.size == 1) {
                    showFirstRecipeAddedNotification()
                }
                else if (recipes.size >= 5) {
                    showFifthRecipeAddedNotification()
                }
            }
        })

        viewModel.fetchRecipes()
        checkNetworkAvailability()

        connectivityReceiver = ConnectivityReceiver()

        binding.fab.setOnClickListener {
            navController.navigate(R.id.action_home2_to_addRecipe)
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { adapter.filter(it) }
                return true
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(connectivityReceiver)
    }

    fun checkNetworkAvailability() {
        val isConnected = NetworkUtils.isNetworkAvailable(requireContext())

        if (!isConnected) {
            binding.fab.visibility = View.GONE
            binding.searchBar.visibility = View.GONE
            binding.emptyMessageText.visibility = View.VISIBLE
            binding.emptyMessageText.text = "No internet connection. Please turn on your network connection to display items or try again later."
            binding.noodles.visibility = View.VISIBLE
            binding.noodles.setImageResource(R.drawable.ic_connecton_error)
            hideLoadingAnimation()
        } else {
            showLoadingAnimation()
            showItems()
        }
    }

    private fun showItems() {
        binding.fab.visibility = View.VISIBLE
        binding.searchBar.visibility = View.VISIBLE
        binding.emptyMessageText.visibility = View.GONE
        binding.noodles.visibility = View.GONE
    }

    private fun showLoadingAnimation() {
        binding.loadingAnimation.visibility = View.VISIBLE
    }

    private fun hideLoadingAnimation() {
        binding.loadingAnimation.visibility = View.GONE
    }

    private fun setupNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Recipe Notifications"
            val channelDescription = "Notifications related to recipes"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(
                "RECIPE_CHANNEL",
                channelName,
                importance
            ).apply {
                description = channelDescription
            }

            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showAddRecipeNotification() {
        val notificationBuilder = NotificationCompat.Builder(requireContext(), "RECIPE_CHANNEL")
            .setSmallIcon(R.drawable.food)
            .setContentTitle("Get Cooking!")
            .setContentText("Your list is empty. Add a recipe to start cooking!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())
    }

    fun showFirstRecipeAddedNotification() {
        val notificationBuilder = NotificationCompat.Builder(requireContext(), "RECIPE_CHANNEL")
            .setSmallIcon(R.drawable.food)
            .setContentTitle("Congratulations!")
            .setContentText("You just added your first recipe. Happy Cooking!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notificationBuilder.build())
    }

    fun showFifthRecipeAddedNotification() {
        val notificationBuilder = NotificationCompat.Builder(requireContext(), "RECIPE_CHANNEL")
            .setSmallIcon(R.drawable.food)
            .setContentTitle("Your list is growing!")
            .setContentText("Select a recipe from your cook book and start cooking!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(3, notificationBuilder.build())
    }

    inner class ConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            checkNetworkAvailability()
        }
    }
}
