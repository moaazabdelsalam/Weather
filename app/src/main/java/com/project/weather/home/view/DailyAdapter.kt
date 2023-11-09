package com.project.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.weather.R
import com.project.weather.constants.Constants
import com.project.weather.databinding.DailyItemBinding
import com.project.weather.model.Daily
import com.project.weather.utils.getDateAndTime
import com.project.weather.utils.getIconDrawableId

class DailyAdapter : ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    private lateinit var binding: DailyItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val inflater: LayoutInflater = parent
            .context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = DailyItemBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val daily = getItem(position)
        val date = getDateAndTime(daily.dt)
        holder.binding.apply {
            dailyDateTxtV.text =
                "${date[Constants.DAY_OF_WEEK_KEY]}, ${date[Constants.MONTH_KEY]} ${date[Constants.DAY_OF_MONTH_KEY]}"
            dailyWeatherIcon.setImageResource(getIconDrawableId(daily.weather[0].icon))
            dailyWeatherDescriptionTxtV.text = daily.weather[0].main
            dailyMaxTempTxtV.text = daily.temp.max.toInt().toString()
            dailyMinTempTxtV.text = daily.temp.min.toInt().toString()
        }
    }

    class DailyViewHolder(val binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class DailyDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily) = oldItem === newItem

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily) = oldItem == newItem
}