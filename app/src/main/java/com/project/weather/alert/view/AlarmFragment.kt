package com.project.weather.alert.view

import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.project.weather.R
import com.project.weather.SharedViewModel
import com.project.weather.alert.AlarmScheduler
import com.project.weather.alert.MyAlarmScheduler
import com.project.weather.constants.Constants
import com.project.weather.databinding.FragmentAlarmBinding
import com.project.weather.favorite.viewmodel.FavoriteViewModel
import com.project.weather.favorite.viewmodel.FavoriteViewModelFactory
import com.project.weather.local.ConcreteLocalSource
import com.project.weather.model.AlertItem
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.State
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo
import com.project.weather.utils.collectLatestFlowOnLifecycle
import com.project.weather.utils.convertLocalDateTimeToString
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var alertAdapter: AlertAdapter
    private lateinit var scheduler: AlarmScheduler

    private var listener: (FavoriteLocation) -> Unit = { location ->
        if (location.isScheduled)
            dismiss(location)
        else
            showDateTimePickerAndSchedule(location)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        if (ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestNotificationPermission()
        }

        collectLatestFlowOnLifecycle(favoriteViewModel.favoriteList) { state ->
            when (state) {
                is State.Failure -> showOnFailure()
                State.Loading -> {}
                is State.Success -> state.data?.let { showOnSuccess(it) }
            }
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ), Constants.NOTIFICATION_PERMISSION_ID
            )
        }
    }

    private fun showOnFailure() {
        binding.alertRecyclerV.visibility = View.INVISIBLE
        binding.noDataAnimation.visibility = View.VISIBLE
        binding.noDataAnimation.playAnimation()
    }

    private fun showOnSuccess(list: List<FavoriteLocation>) {
        binding.noDataAnimation.apply {
            pauseAnimation()
            visibility = View.GONE
        }
        alertAdapter.submitList(list)
        val layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation)
        binding.alertRecyclerV.apply {
            visibility = View.VISIBLE
            layoutAnimation = layoutAnimationController
            scheduleLayoutAnimation()
        }
    }

    private fun init() {
        scheduler = MyAlarmScheduler(requireContext())
        alertAdapter = AlertAdapter(requireContext(), listener)
        binding.alertRecyclerV.adapter = alertAdapter

        val favoriteViewModelFactory = FavoriteViewModelFactory(
            Repo.getInstance(
                WeatherClient,
                ConcreteLocalSource.getInstance(requireContext())
            ),
            ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        )
        favoriteViewModel =
            ViewModelProvider(
                requireActivity(),
                favoriteViewModelFactory
            )[FavoriteViewModel::class.java]
    }

    private fun showDateTimePickerAndSchedule(location: FavoriteLocation) {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = today
        val startDate = calendar.timeInMillis

        val constraints: CalendarConstraints = CalendarConstraints.Builder()
            .setOpenAt(startDate)
            .setStart(startDate)
            .build()
        val datePicker = MaterialDatePicker
            .Builder
            .datePicker()
            .setTitleText("Select date")
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener { date ->
            val localDateTime = convertToLocalDateTime(date)
            Log.i("TAG", "showDatePicker: $localDateTime")
            val timePicker: MaterialTimePicker = MaterialTimePicker
                .Builder()
                .setTitleText("Select a time")
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()
            timePicker.show(requireActivity().supportFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener {
                Log.i("TAG", "showTimePicker: ${timePicker.hour}:${timePicker.minute}")
                timePicker.hour     // returns the selected hour
                timePicker.minute   // returns the selected minute

                val mCalendar = Calendar.getInstance()
                mCalendar.timeInMillis = datePicker.selection!!

                val selectedDateTime = LocalDateTime.of(
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH) + 1, // Months are 0-based
                    mCalendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.hour,
                    timePicker.minute
                )
                val alertItem = AlertItem(
                    selectedDateTime,
                    location.cityName,
                    location.lat,
                    location.lon
                )
                favoriteViewModel.addAlert(location, convertLocalDateTimeToString(selectedDateTime))
                scheduler.schedule(alertItem)
            }
        }
        datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")

    }

    private fun dismiss(location: FavoriteLocation) {
        val alertItem = AlertItem(
            LocalDateTime.now(),
            location.cityName,
            location.lat,
            location.lon
        )
        scheduler.cancel(alertItem)
        favoriteViewModel.dismissAlert(location)
    }

    private fun convertToLocalDateTime(epochMillis: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
    }
}
