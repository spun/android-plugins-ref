package com.spundev.appplugin.plugina

import com.spundev.appplugin.pluginapi.data.ApiPet
import com.spundev.appplugin.pluginapi.provider.ApiPetProvider
import com.spundev.appplugin.pluginapi.provider.ApiProviderContract

class PluginACatProvider : ApiPetProvider() {
    override fun onLoadRequested(initial: Boolean) {
        context?.let { context ->
            ApiProviderContract.getProviderClient(context, BuildConfig.PLUGIN_A_AUTHORITY)
        }?.apply {
            val name = catNames.random()
            val age = (1..20).random()
            val image = catImages.random()
            addPet(ApiPet(1L, name, age, "Cat", image))
        }
    }
}