package com.project.weather.favorite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.weather.databinding.FragmentFavoriteBinding
import com.project.weather.model.FavoriteLocation

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteAdapter = FavoriteAdapter()
        binding.favoriteRecyclerV.adapter = favoriteAdapter

        val favList = listOf(
            FavoriteLocation(
                "Africa/Cairo",
                "Clear",
                "Clear sky",
                "",
                18.7,
                32.1
            )
        )
        favoriteAdapter.submitList(favList)
    }
}