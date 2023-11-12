package com.project.weather.setting.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.project.weather.R
import com.project.weather.SharedViewModel
import com.project.weather.SharedViewModelFactory
import com.project.weather.constants.Constants
import com.project.weather.databinding.FragmentSettingBinding
import com.project.weather.repo.PreferenceRepo

class SettingFragment : Fragment() {
    private val TAG = "TAG SettingFragment"
    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(PreferenceRepo.getInstance(requireContext()))
        )[SharedViewModel::class.java]

        binding.changeToGps.setOnClickListener {
            sharedViewModel.setHomeLocationSource(Constants.PREF_LOCATION_GPS)
        }
        binding.changeToMap.setOnClickListener {
            Log.i(TAG, "open map")
            Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_mapFragment)
            sharedViewModel.setHomeLocationSource(Constants.PREF_LOCATION_MAP)
        }
    }
}