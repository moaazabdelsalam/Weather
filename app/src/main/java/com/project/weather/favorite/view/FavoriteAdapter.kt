package com.project.weather.favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.weather.R
import com.project.weather.databinding.FavoriteItemBinding
import com.project.weather.model.FavoriteLocation

class FavoriteAdapter :
    ListAdapter<FavoriteLocation, FavoriteAdapter.FavoriteViewHolder>(FavoriteDiffUtil()) {
    private lateinit var binding: FavoriteItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater: LayoutInflater = parent
            .context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FavoriteItemBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = getItem(position)
        holder.binding.apply {
            favoriteWeatherIcon.setImageResource(R.drawable.weather_icon_placeholder)
            favoriteCityTxtV.text = favorite.timezone
            favoriteWeatherDescriptionTxtV.text = favorite.main
            favoriteMaxTempTxtV.text = favorite.max.toInt().toString()
            favoriteMinTempTxtV.text = favorite.min.toInt().toString()
        }
    }

    class FavoriteViewHolder(val binding: FavoriteItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class FavoriteDiffUtil : DiffUtil.ItemCallback<FavoriteLocation>() {
    override fun areItemsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation) =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation) =
        oldItem == newItem

}