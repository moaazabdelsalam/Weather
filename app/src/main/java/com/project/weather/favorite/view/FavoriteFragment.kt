package com.project.weather.favorite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import com.project.weather.model.State
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

        binding.openMapBtn.setOnClickListener {
            val action = FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment()
            action.source = "favorite"
            Navigation.findNavController(view).navigate(action)
        }

        collectLatestFlowOnLifecycle(favoriteViewModel.favoriteList) {state ->
            when(state) {
                is State.Failure -> showOnFailure()
                State.Loading -> {}
                is State.Success -> state.data?.let { showOnSuccess(it) }
            }
        }
        attachSwipeToDeleteToRV()
    }

    private fun showOnFailure() {
        binding.favoriteRecyclerV.visibility = View.INVISIBLE
        binding.openMapBtn.visibility = View.INVISIBLE
    }

    private fun showOnSuccess(list: List<FavoriteLocation>) {
        favoriteAdapter.submitList(list)
        favoriteList = list.toMutableList()
        val layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation)
        binding.favoriteRecyclerV.apply {
            visibility = View.VISIBLE
            layoutAnimation = layoutAnimationController
            scheduleLayoutAnimation()
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
            ViewModelProvider(requireActivity(), favoriteViewModelFactory)[FavoriteViewModel::class.java]
        favoriteAdapter = FavoriteAdapter(requireContext())
        binding.favoriteRecyclerV.adapter = favoriteAdapter
        binding.openMapBtn.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.from_bottom
            )
        )
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
                    .setMessage("Sure you want to remove ${deletedLocation.cityName} from favorite?")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Ok") { dialog, _ ->
                        favoriteViewModel.deleteLocationFromFavorite(deletedLocation)
                        dialog.dismiss()

                        Snackbar.make(
                            binding.favoriteRecyclerV,
                            "${deletedLocation.cityName} removed",
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