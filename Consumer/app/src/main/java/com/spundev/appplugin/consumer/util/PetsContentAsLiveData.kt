package com.spundev.appplugin.consumer.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.spundev.appplugin.consumer.model.PetData
import com.spundev.appplugin.pluginapi.data.ApiPet

class PetsContentAsLiveData(
    val context: Context,
    private val contentUri: Uri
) : ContentProviderLiveData<List<PetData>>(context, contentUri) {

    override fun getContentProviderValue(): List<PetData> {

        val newProviderContent = mutableListOf<PetData>()
        context.contentResolver.acquireUnstableContentProviderClient(contentUri)?.run {
            try {
                val cursor = query(contentUri, listOf<String>().toTypedArray(), null, null, null)

                cursor?.use {
                    while (cursor.moveToNext()) {
                        val apiPet = ApiPet.fromCursor(cursor)
                        newProviderContent.add(PetData.fromApiPet(apiPet))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Cursor query exception: ")
            }
        }
        return newProviderContent
    }
}

private const val TAG = "PetsContentAsLiveData"