package com.project.weather.setting.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.weather.databinding.SettingItemBinding
import com.project.weather.model.Setting

class SettingAdapter(
    private val settingList: List<Setting>,
    private val listener: (Int) -> Unit
) :
    RecyclerView.Adapter<SettingAdapter.SettingViewHolder>() {
    private lateinit var binding: SettingItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val inflater: LayoutInflater = parent
            .context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = SettingItemBinding.inflate(inflater, parent, false)
        return SettingViewHolder(binding)
    }

    override fun getItemCount() = settingList.size

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val settingItem = settingList[position]
        binding.apply {
            settingTitleTxt.text = settingItem.title
            settingIcon.setImageResource(settingItem.icon)
            settingCard.setOnClickListener {
                listener(position)
            }
        }
    }

    class SettingViewHolder(val binding: SettingItemBinding) : RecyclerView.ViewHolder(binding.root)
}