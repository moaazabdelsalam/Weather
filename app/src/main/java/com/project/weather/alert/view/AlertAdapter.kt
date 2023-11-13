package com.project.weather.alert.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.weather.R
import com.project.weather.databinding.AlertItemBinding
import com.project.weather.model.FavoriteLocation

class AlertAdapter(val context: Context, val listener: (FavoriteLocation) -> Unit) :
    ListAdapter<FavoriteLocation, AlertAdapter.AlertViewHolder>(AlertDiffUtil()) {
    private lateinit var binding: AlertItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlertAdapter.AlertViewHolder {
        val inflater: LayoutInflater = parent
            .context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertItemBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertAdapter.AlertViewHolder, position: Int) {
        val item = getItem(position)
        binding.apply {
            alertIcon.setImageResource(if (item.isScheduled) R.drawable.ic_alarm else R.drawable.ic_alarm_off)
            alertCityTxt.text = item.cityName
            alertTimeTxt.text = item.timeString
            alertItemLayout.setOnClickListener {
                listener(item)
            }
        }
    }

    class AlertViewHolder(val binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class AlertDiffUtil : DiffUtil.ItemCallback<FavoriteLocation>() {
    override fun areItemsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation) =
        oldItem === newItem

    override fun areContentsTheSame(oldItem: FavoriteLocation, newItem: FavoriteLocation) =
        oldItem == newItem

}