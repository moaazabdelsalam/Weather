package com.project.weather.repo

import android.content.Context
import android.util.Log
import com.project.weather.constants.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferenceRepo private constructor(context: Context) : PreferenceRepoInterface {
    private val TAG = "TAG PrefRepo"

    private val sharedPreference =
        context.getSharedPreferences("Setting Pref", Context.MODE_PRIVATE)
    private val editor = sharedPreference.edit()

    private val _homeLocationSourceValue: MutableStateFlow<String?> =
        MutableStateFlow(getHomeLocationSource())
    override val homeLocationSourceValue: StateFlow<String?>
        get() = _homeLocationSourceValue

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

    override fun setHomeLocationSource(source: String) {
        editor.putString(Constants.PREF_LOCATION_SOURCE, source)
        if (editor.commit())
            _homeLocationSourceValue.value = source
        else
            Log.i(TAG, "setHomeLocationSource: error")
    }

    private fun getHomeLocationSource() =
        sharedPreference.getString(Constants.PREF_LOCATION_SOURCE, null)
}