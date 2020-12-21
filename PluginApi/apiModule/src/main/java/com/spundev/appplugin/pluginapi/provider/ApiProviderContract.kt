package com.spundev.appplugin.pluginapi.provider

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.RemoteException
import android.provider.BaseColumns
import androidx.annotation.RequiresApi
import com.spundev.appplugin.pluginapi.data.ApiPet

/**
 * Contract between the consumer app and the providers, containing the definitions for all supported
 * URIs and columns as well as helper methods to make it easier to work with the provided data.
 */
object ApiProviderContract {
    /**
     * Retrieve the content URI for the given [ApiPetProvider], allowing you to build
     * custom queries, inserts, updates, and deletes using a [ContentResolver].
     *
     * This **does not** check for the validity of the [ApiPetProvider]. You can
     * use [PackageManager.resolveContentProvider] passing in the
     * authority if you need to confirm the authority is valid.
     *
     * @param authority The [ApiPetProvider] you need a content URI for
     * @return The content URI for the [ApiPetProvider]
     * @see ApiPetProvider.contentUri
     */
    @JvmStatic
    fun getContentUri(authority: String): Uri {
        return Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(authority)
            .build()
    }

    /**
     * Creates a new [ApiProviderClient] for accessing the given [ApiPetProvider]
     * from anywhere in your application.
     *
     * @param context Context used to construct the ProviderClient.
     * @param provider The [ApiPetProvider] you need a ProviderClient for.
     * @return a [ApiProviderClient] suitable for accessing the [ApiPetProvider].
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getProviderClient(
        context: Context,
        provider: Class<out ApiPetProvider>
    ): ApiProviderClient {
        val componentName = ComponentName(context, provider)
        val pm = context.packageManager
        val authority: String
        try {
            val info = pm.getProviderInfo(componentName, 0)
            authority = info.authority
        } catch (e: PackageManager.NameNotFoundException) {
            throw IllegalArgumentException(
                "Invalid ApiPetProvider: $componentName, is your provider disabled?", e
            )
        }
        return getProviderClient(context, authority)
    }

    /**
     * Creates a new [ApiProviderClient] for accessing the given [ApiPetProvider]
     * from anywhere in your application.
     *
     * @param context Context used to construct the ProviderClient.
     * @param Provider The subclass of [ApiPetProvider] you need a ProviderClient for.
     * @return a [ApiProviderClient] suitable for accessing the [ApiPetProvider].
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    inline fun <reified Provider : ApiPetProvider> getProviderClient(
        context: Context
    ): ApiProviderClient {
        return getProviderClient(context, Provider::class.java)
    }


    /**
     * Creates a new [ApiProviderClient] for accessing the given [ApiPetProvider]
     * from anywhere in your application.
     *
     * @param context Context used to construct the ProviderClient.
     * @param authority The [ApiPetProvider] you need a ProviderClient for.
     * @return a [ApiProviderClient] suitable for accessing the [ApiPetProvider].
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getProviderClient(context: Context, authority: String): ApiProviderClient {
        val contentUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(authority)
            .build()
        return object : ApiProviderClient {

            override val contentUri: Uri
                get() = contentUri

            override fun addPet(pet: ApiPet): Uri? {
                val contentResolver = context.contentResolver
                return contentResolver.insert(contentUri, pet.toContentValues())
            }

            override fun addPet(pets: Iterable<ApiPet>): List<Uri> {
                val contentResolver = context.contentResolver
                val operations = ArrayList<ContentProviderOperation>()
                for (pet in pets) {
                    operations.add(
                        ContentProviderOperation.newInsert(contentUri)
                            .withValues(pet.toContentValues())
                            .build()
                    )
                }
                return try {
                    val results = contentResolver.applyBatch(authority, operations)
                    results.mapNotNull { result -> result.uri }
                } catch (ignored: OperationApplicationException) {
                    emptyList()
                } catch (ignored: RemoteException) {
                    emptyList()
                }
            }
        }
    }

    /**
     * Constants and helper methods for working with [ApiPet]s.
     * These are the minimum number of columns expected from of each item of the content provider.
     * This contract guaranties that the consumer app knows how and what to access when establishes
     * a connection with the provider.
     */
    object Pet {
        // Unique ID of the pet
        @Suppress("unused", "ObjectPropertyName")
        const val _ID: String = BaseColumns._ID

        // The name of the pet
        const val NAME: String = "name"

        // The age of the pet
        const val AGE: String = "age"

        // The species of the pet
        const val SPECIES: String = "species"

        // The image url of the pet
        const val IMAGE_URL: String = "image_url"
    }
}