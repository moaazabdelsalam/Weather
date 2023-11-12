package com.project.weather.setting.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.weather.R
import com.project.weather.SharedViewModel
import com.project.weather.SharedViewModelFactory
import com.project.weather.constants.Constants
import com.project.weather.databinding.FragmentSettingBinding
import com.project.weather.model.Setting
import com.project.weather.repo.PreferenceRepo

class SettingFragment : Fragment() {
    private val TAG = "TAG SettingFragment"
    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var _view: View
    private val locationSources =
        arrayOf<String>(Constants.PREF_LOCATION_GPS, Constants.PREF_LOCATION_MAP)
    private val listener: (Int) -> Unit = { position ->
        when (position) {
            0 -> showLocationDialogue()
            1 -> showSpeedDialogue()
            2 -> showTemperatureDialogue()
            3 -> showLanguageDialogue()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _view = view
        val settingList = listOf(
            Setting("Location source", R.drawable.ic_location),
            Setting("Speed unit", R.drawable.ic_speed),
            Setting("Temperature unit", R.drawable.ic_temperature),
            Setting("Language", R.drawable.ic_language)
        )
        val settingAdapter = SettingAdapter(settingList, listener)
        binding.settingRecyclerV.adapter = settingAdapter

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(PreferenceRepo.getInstance(requireContext()))
        )[SharedViewModel::class.java]
    }

    private fun showLocationDialogue() {
        var checkedItem = -1
        when (sharedViewModel.homeLocationSource.value) {
            Constants.PREF_LOCATION_GPS -> checkedItem = 0
            Constants.PREF_LOCATION_MAP -> checkedItem = 1
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.location_source))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(locationSources, checkedItem) { dialog, selected ->
                when (selected) {
                    0 -> {
                        sharedViewModel.setHomeLocationSource(Constants.PREF_LOCATION_GPS)
                        dialog.dismiss()
                    }

                    1 -> {
                        Navigation.findNavController(_view)
                            .navigate(R.id.action_settingFragment_to_mapFragment)
                        sharedViewModel.setHomeLocationSource(Constants.PREF_LOCATION_MAP)
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    private fun showSpeedDialogue() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.speed_unit))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            // Single-choice items (initialized with checked item)
            /*.setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                // Respond to item chosen
            }*/
            .show()
    }

    private fun showTemperatureDialogue() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.temp_unit))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }
            // Single-choice items (initialized with checked item)
            /*.setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                // Respond to item chosen
            }*/
            .show()
    }

    private fun showLanguageDialogue() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.language))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to neutral button press
            }
            // Single-choice items (initialized with checked item)
            /*.setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                // Respond to item chosen
            }*/
            .show()
    }
}