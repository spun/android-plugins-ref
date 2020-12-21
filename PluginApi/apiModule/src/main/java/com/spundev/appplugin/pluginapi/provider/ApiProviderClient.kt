package com.spundev.appplugin.pluginapi.provider

import android.content.ContentResolver
import android.net.Uri
import com.spundev.appplugin.pluginapi.data.ApiPet

/**
 * Interface for interacting with a [ApiPetProvider]. Methods of this interface can
 * be used directly within a [ApiPetProvider] or you can get an instance via
 * [ApiProviderContract.getProviderClient].
 */
interface ApiProviderClient {
    /**
     * Retrieve the content URI for the [ApiPetProvider], allowing you to build
     * custom queries, inserts, updates, and deletes using a [ContentResolver].
     *
     * @return The content URI for the [ApiPetProvider]
     */
    val contentUri: Uri

    /**
     * Add a new pet to the [ApiPetProvider].
     *
     * @param pet The pet to add
     * @return The URI of the newly added pet or null if the insert failed
     */
    fun addPet(pet: ApiPet): Uri?

    /**
     * Add multiple pets as a batch operation to the [ApiPetProvider].
     *
     * @param pets The pets to add
     * @return The URIs of the newly added pets or an empty List if the insert failed.
     */
    fun addPet(pets: Iterable<ApiPet>): List<Uri>
}