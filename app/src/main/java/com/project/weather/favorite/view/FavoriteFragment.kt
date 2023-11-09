package com.project.weather.favorite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.project.weather.R
import com.project.weather.SharedViewModel
import com.project.weather.databinding.FragmentFavoriteBinding
import com.project.weather.favorite.viewmodel.FavoriteViewModel
import com.project.weather.favorite.viewmodel.FavoriteViewModelFactory
import com.project.weather.local.ConcreteLocalSource
import com.project.weather.model.FavoriteLocation
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo
import com.project.weather.utils.collectLatestFlowOnLifecycle


class FavoriteFragment : Fragment() {
    private val TAG = "TAG FavoriteFragment"

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private var favoriteList: MutableList<FavoriteLocation> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        attachSwipeToDeleteToRV()

        binding.openMapBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_favoriteFragment_to_mapFragment)
        }

        collectLatestFlowOnLifecycle(favoriteViewModel.addToFavoriteState) { resultState ->
            when {
                resultState != null && resultState > -1L -> Toast.makeText(
                    requireContext(),
                    "successfully added to favorite",
                    Toast.LENGTH_SHORT
                ).show()

                resultState == -1L -> Toast.makeText(
                    requireContext(),
                    "something went wrong please try again later",
                    Toast.LENGTH_SHORT
                ).show()

                else -> {}
            }
        }
        collectLatestFlowOnLifecycle(favoriteViewModel.favoriteList) {
            favoriteAdapter.submitList(it)
            favoriteList = it.toMutableList()
        }
    }

    private fun init() {
        val favoriteViewModelFactory = FavoriteViewModelFactory(
            Repo.getInstance(
                WeatherClient,
                ConcreteLocalSource.getInstance(requireContext())
            ),
            ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory)[FavoriteViewModel::class.java]
        favoriteAdapter = FavoriteAdapter()
        binding.favoriteRecyclerV.adapter = favoriteAdapter

    }

    private fun attachSwipeToDeleteToRV() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedLocation = favoriteList[viewHolder.adapterPosition]

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Remove from favorite")
                    .setMessage("Sure you want to remove ${deletedLocation.timezone} from favorite?")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Ok") { dialog, _ ->
                        favoriteViewModel.deleteLocationFromFavorite(deletedLocation)
                        dialog.dismiss()

                        Snackbar.make(
                            binding.favoriteRecyclerV,
                            "${deletedLocation.timezone} removed",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAnchorView(R.id.bottomNav)
                            .show()
                    }
                    .show()
            }
        }).attachToRecyclerView(binding.favoriteRecyclerV)
    }
}