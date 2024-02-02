package com.dalakoti.android.localeapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dalakoti.android.localeapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getPreferences(Context.MODE_PRIVATE)
        binding.rbEnglish.setOnClickListener {
            changeLanguage("en")
        }
        binding.rbHindi.setOnClickListener {
            changeLanguage("hi")
        }
        setRadioButtons()
    }

    private fun setRadioButtons() {
        when(sharedPref.getString("language","en")){
            "en"->{
                binding.radioGroup
            }
            "hi"->{
                binding.rbHindi.isSelected = true
            }
        }
    }

    private fun changeLanguage(language: String) {
        val currentLanguage = sharedPref.getString("language","en")
        if(currentLanguage == language) return
        sharedPref.edit().apply {
            putString("language", language)
            apply()
        }

    }
}
