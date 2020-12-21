package com.spundev.appplugin.consumer.ui.imageDialog

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageDialogViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    // Contains the image we receive from the provider
    private val providerImage = MutableStateFlow<Bitmap?>(null)
    val providerImageLiveData: LiveData<Bitmap?> = providerImage.asLiveData()

    // Because we (as a consumer app) may not know how to request the pet image in some cases
    // (asset file, authentication with a remote server, etc), we delegate the request to the
    // plugin app and get the file as a Bitmap instead of asking for an url.
    fun requestImage(imageUri: String) {

        val contentUri = imageUri.toUri()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    app.contentResolver.openFileDescriptor(contentUri, "r")
                        ?.use { parcelFileDescriptor ->
                            val fileDescriptor = parcelFileDescriptor.fileDescriptor
                            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                            providerImage.value = bitmap
                        }
                } catch (e: Exception) {
                    Log.e(TAG, "requestImage: failed to parse image uri", e)
                }
            }
        }
    }
}

private const val TAG = "ImageDialogViewModel"
