package com.project.weather.repo

import android.content.Context
import android.util.Log
import com.project.weather.constants.Constants
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferenceRepo private constructor(val context: Context) : PreferenceRepoInterface {
    private val TAG = "TAG PrefRepo"

    private val sharedPreference =
        context.getSharedPreferences("Setting Pref", Context.MODE_PRIVATE)
    private val editor = sharedPreference.edit()

    private val _homeLocationSourceValue: MutableStateFlow<String?> =
        MutableStateFlow(getHomeLocationSource())
    override val homeLocationSourceValue: StateFlow<String?>
        get() = _homeLocationSourceValue
    private val _temperatureUnitValue: MutableStateFlow<String?> =
        MutableStateFlow(getTemperatureUnit())
    override val temperatureUnitValue: StateFlow<String?>
        get() = _temperatureUnitValue
    private val _speedUnitValue: MutableStateFlow<String?> =
        MutableStateFlow(getSpeedUnit())
    override val speedUnitValue: StateFlow<String?>
        get() = _speedUnitValue

    companion object {
        @Volatile
        private var _instance: PreferenceRepo? = null
        fun getInstance(context: Context): PreferenceRepo {
            return _instance ?: synchronized(this) {
                val instance = PreferenceRepo(context)
                _instance = instance
                instance
            }
        }
    }

    private fun getHomeLocationSource() =
        sharedPreference.getString(Constants.PREF_LOCATION_SOURCE, Constants.PREF_LOCATION_GPS)

    private fun getTemperatureUnit() =
        sharedPreference.getString(Constants.PREF_TEMP_UNIT, Constants.PREF_TEMP_C)

    private fun getSpeedUnit() =
        sharedPreference.getString(Constants.PREF_SPEED_UNIT, Constants.PREF_SPEED_METER)

    override fun setHomeLocationSource(source: String) {
        editor.putString(Constants.PREF_LOCATION_SOURCE, source)
        if (editor.commit())
            _homeLocationSourceValue.value = source
        else
            Log.i(TAG, "setHomeLocationSource: error")
    }

    override fun setLanguage(language: String) {
        Lingver.getInstance().setLocale(context, language)
    }

    override fun setTemperatureUnit(unit: String) {
        editor.putString(Constants.PREF_TEMP_UNIT, unit)
        if (editor.commit())
            _temperatureUnitValue.value = unit
        else
            Log.i(TAG, "setTemperatureUnit: error")
    }

    override fun setSpeedUnit(unit: String) {
        editor.putString(Constants.PREF_SPEED_UNIT, unit)
        if (editor.commit())
            _speedUnitValue.value = unit
        else
            Log.i(TAG, "setSpeedUnit: error")
    }
}