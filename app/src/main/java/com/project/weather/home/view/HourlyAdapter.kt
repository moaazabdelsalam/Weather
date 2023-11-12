package com.project.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.weather.R
import com.project.weather.constants.Constants
import com.project.weather.databinding.HourlyItemBinding
import com.project.weather.model.Hourly
import com.project.weather.utils.getDateAndTime
import com.project.weather.utils.getIconLink

class HourlyAdapter(private val context: Context) :
    ListAdapter<Hourly, HourlyAdapter.HourlyViewHolder>(HourlyDiffUtil()) {
    private lateinit var binding: HourlyItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val inflater: LayoutInflater = parent
            .context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyItemBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val hourly = getItem(position)
        val hourlyDate = getDateAndTime(hourly.dt)
        holder.binding.apply {
            hourlyTimeTxtV.text =
                "${hourlyDate[Constants.TIME_KEY]} ${hourlyDate[Constants.AM_PM_KEY]}"
            //hourlyWeatherIcon.setImageResource(getIconDrawableId(hourly.weather[0].icon))
            Glide.with(context)
                .load(getIconLink(hourly.weather[0].icon))
                .placeholder(R.drawable.weather_icon_placeholder)
                .into(hourlyWeatherIcon)
            hourlyWeatherDescriptionTxtV.text = hourly.weather[0].description
            hourlyTempTxtV.text = hourly.temp.toInt().toString()
        }
    }

    class HourlyViewHolder(val binding: HourlyItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class HourlyDiffUtil : DiffUtil.ItemCallback<Hourly>() {
    override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly) = oldItem === newItem

    override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly) = oldItem == newItem
}