package com.spundev.appplugin.pluginb

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.spundev.appplugin.pluginapi.provider.ApiPetProvider

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (intent.hasExtra(ApiPetProvider.EXTRA_FROM_CONSUMER)) {
            val fromConsumer = intent.getBooleanExtra(ApiPetProvider.EXTRA_FROM_CONSUMER, false)
            Toast.makeText(this, "from consumer: $fromConsumer", Toast.LENGTH_LONG).show()
        }
    }
}
