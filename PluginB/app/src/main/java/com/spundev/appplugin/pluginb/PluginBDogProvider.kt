package com.spundev.appplugin.pluginb

import com.spundev.appplugin.pluginapi.data.ApiPet
import com.spundev.appplugin.pluginapi.provider.ApiPetProvider
import com.spundev.appplugin.pluginapi.provider.ApiProviderContract

class PluginBMediaProvider : ApiPetProvider() {

    override fun onLoadRequested(initial: Boolean) {
        context?.let { context ->
            ApiProviderContract.getProviderClient(context, BuildConfig.PLUGIN_B_AUTHORITY)
        }?.apply {
            val name = dogNames.random()
            val age = (1..15).random()
            val image = dogImages.random()
            addPet(ApiPet(1L, name, age, "Dog", image))
        }
    }
}
