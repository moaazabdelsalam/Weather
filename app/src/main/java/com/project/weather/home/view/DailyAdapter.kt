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
import com.project.weather.databinding.DailyItemBinding
import com.project.weather.model.Daily
import com.project.weather.utils.celsiusToFahrenheit
import com.project.weather.utils.celsiusToKelvin
import com.project.weather.utils.getDateAndTime
import com.project.weather.utils.getIconLink
import kotlinx.coroutines.flow.MutableStateFlow

class DailyAdapter(private val context: Context) :
    ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtil()) {
    private lateinit var binding: DailyItemBinding
    val tempUnit: MutableStateFlow<String?> = MutableStateFlow(null)

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
            Glide.with(context)
                .load(getIconLink(daily.weather[0].icon))
                .placeholder(R.drawable.weather_icon_placeholder)
                .into(dailyWeatherIcon)
            dailyWeatherDescriptionTxtV.text = daily.weather[0].description
            when (tempUnit.value) {
                Constants.PREF_TEMP_C -> {
                    dailyTempUnitTxtV.text = context.resources.getString(R.string.celsius)
                    dailyMaxTempTxtV.text = daily.temp.max.toInt().toString()
                    dailyMinTempTxtV.text = daily.temp.min.toInt().toString()
                }

                Constants.PREF_TEMP_K -> {
                    dailyTempUnitTxtV.text = context.resources.getString(R.string.kelvin)
                    dailyMaxTempTxtV.text = celsiusToKelvin(daily.temp.max).toString()
                    dailyMinTempTxtV.text = celsiusToKelvin(daily.temp.min).toString()
                }

                Constants.PREF_TEMP_F -> {
                    dailyTempUnitTxtV.text = context.resources.getString(R.string.fahrenheit)
                    dailyMaxTempTxtV.text = celsiusToFahrenheit(daily.temp.max).toString()
                    dailyMinTempTxtV.text = celsiusToFahrenheit(daily.temp.min).toString()
                }
            }
        }
    }

    class DailyViewHolder(val binding: DailyItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class DailyDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily) = oldItem === newItem

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily) = oldItem == newItem
}