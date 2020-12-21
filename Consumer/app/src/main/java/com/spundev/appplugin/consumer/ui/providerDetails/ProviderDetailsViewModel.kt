package com.spundev.appplugin.consumer.ui.providerDetails

import android.app.Application
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.spundev.appplugin.consumer.model.ProviderData
import com.spundev.appplugin.consumer.util.PetsContentAsLiveData
import com.spundev.appplugin.pluginapi.provider.ApiConstants.KEY_DESCRIPTION
import com.spundev.appplugin.pluginapi.provider.ApiConstants.METHOD_GET_DESCRIPTION
import com.spundev.appplugin.pluginapi.provider.ApiConstants.METHOD_REQUEST_LOAD
import com.spundev.appplugin.pluginapi.provider.ApiProviderContract
import kotlinx.coroutines.flow.MutableStateFlow


class ProviderDetailsViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    // Contains the info about the provider
    private val providerData = MutableStateFlow<ProviderData>(ProviderData())
    val providerDataLiveData = providerData.asLiveData()

    // Use current authority to form the contentUri
    val contentUri: Uri
        get() = ApiProviderContract.getContentUri(providerData.value.authority)

    // When we get the data from the fragment, get info and content from the provider.
    fun setProviderInfo(providerData: ProviderData) {
        this.providerData.value = providerData
        getProviderContentLiveData()
        getProviderDetails()
    }

    // Provider content provider data
    fun getProviderContentLiveData() = PetsContentAsLiveData(app, contentUri)

    // Get simple information about the provider
    fun getProviderDetails() {
        // Get description
        val response = sendCall(METHOD_GET_DESCRIPTION)
        val receivedDescription = response?.getString(KEY_DESCRIPTION)
        // Update data
        providerData.value = with(providerData.value) {
            ProviderData(
                authority,
                packageName,
                title,
                receivedDescription,
                settingsActivity
            )
        }
    }

    // Request content update. We are telling the plugin that it should fetch new content
    // and update its content provider.
    fun sendLoadRequest() {
        sendCall(METHOD_REQUEST_LOAD)
    }

    // Send call to content provider
    fun sendCall(method: String): Bundle? {
        return app.contentResolver.acquireUnstableContentProviderClient(contentUri)?.run {
            call(method, null, null)
        }
    }
}

private const val TAG = "ProviderDetailsViewMode"