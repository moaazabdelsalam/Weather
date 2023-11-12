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
import java.util.Locale

class SettingFragment : Fragment() {
    private val TAG = "TAG SettingFragment"
    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var _view: View
    private val locationSources =
        arrayOf(Constants.PREF_LOCATION_GPS, Constants.PREF_LOCATION_MAP)
    private val languages =
        arrayOf("English", "العربية")
    private val tempUnits =
        arrayOf(Constants.PREF_TEMP_C, Constants.PREF_TEMP_K, Constants.PREF_TEMP_F)
    private val speedUnits =
        arrayOf(Constants.PREF_SPEED_METER, Constants.PREF_SPEED_MILE)

    private val listener: (Int) -> Unit = { position ->
        when (position) {
            0 -> handelLocationDialogue()
            1 -> handelSpeedDialogue()
            2 -> handelTemperatureDialogue()
            3 -> handelLanguageDialogue()
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
            Setting(resources.getString(R.string.location_source), R.drawable.ic_location),
            Setting(resources.getString(R.string.speed_unit), R.drawable.ic_speed),
            Setting(resources.getString(R.string.temp_unit), R.drawable.ic_temperature),
            Setting(resources.getString(R.string.language), R.drawable.ic_language)
        )
        val settingAdapter = SettingAdapter(settingList, listener)
        binding.settingRecyclerV.adapter = settingAdapter

        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory(PreferenceRepo.getInstance(requireContext()))
        )[SharedViewModel::class.java]
    }

    private fun handelLocationDialogue() {
        var checkedItem = -1
        when (sharedViewModel.homeLocationSource.value) {
            Constants.PREF_LOCATION_GPS -> checkedItem = 0
            Constants.PREF_LOCATION_MAP -> checkedItem = 1
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.location_source))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
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

    private fun handelSpeedDialogue() {
        var checkedItem = -1
        when (sharedViewModel.speedUnitValue.value) {
            Constants.PREF_SPEED_METER -> checkedItem = 0
            Constants.PREF_SPEED_MILE -> checkedItem = 1
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.speed_unit))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(speedUnits, checkedItem) { dialog, selected ->
                when (selected) {
                    0 -> {
                        sharedViewModel.setSpeedUnit(Constants.PREF_SPEED_METER)
                        dialog.dismiss()
                    }

                    1 -> {
                        sharedViewModel.setSpeedUnit(Constants.PREF_SPEED_MILE)
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    private fun handelTemperatureDialogue() {
        var checkedItem = -1
        when (sharedViewModel.temperatureUnitValue.value) {
            Constants.PREF_TEMP_C -> checkedItem = 0
            Constants.PREF_TEMP_K -> checkedItem = 1
            Constants.PREF_TEMP_F -> checkedItem = 2
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.temp_unit))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(tempUnits, checkedItem) { dialog, selected ->
                when (selected) {
                    0 -> {
                        sharedViewModel.setTemperatureUnit(Constants.PREF_TEMP_C)
                        dialog.dismiss()
                    }

                    1 -> {
                        sharedViewModel.setTemperatureUnit(Constants.PREF_TEMP_K)
                        dialog.dismiss()
                    }

                    2 -> {
                        sharedViewModel.setTemperatureUnit(Constants.PREF_TEMP_F)
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    private fun handelLanguageDialogue() {
        var checkedItem = -1
        when (Locale.getDefault().language) {
            "en" -> checkedItem = 0
            "ar" -> checkedItem = 1
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.language))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(languages, checkedItem) { dialog, selected ->
                when (selected) {
                    0 -> {
                        sharedViewModel.setLanguage(Constants.PREF_LANGUAGE_EN)
                        dialog.dismiss()
                        requireActivity().recreate()
                    }

                    1 -> {
                        sharedViewModel.setLanguage(Constants.PREF_LANGUAGE_AR)
                        dialog.dismiss()
                        requireActivity().recreate()
                    }
                }
            }
            .show()
    }
}