package com.project.weather.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.weather.AndroidAlarmScheduler
import com.project.weather.databinding.FragmentAlarmBinding
import com.project.weather.model.AlarmItem
import java.time.LocalDateTime

class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scheduler = AndroidAlarmScheduler(requireContext())
        val alarmItem = AlarmItem(
            LocalDateTime.now().plusSeconds(5L),
            "El Mahalla El Kubra",
            30.9723517,
            31.1683002
        )
        val alarmItem2 = AlarmItem(
            LocalDateTime.now().plusSeconds(10L),
            "weather alert of cityName",
            0.9723517,
            31.1683002
        )
        scheduler.schedule(alarmItem)
        alarmItem2.let(scheduler::schedule)
        scheduler.cancel(alarmItem2)

        binding.alarmTxtV.text = "ALARM SET!!!!"
    }
}
