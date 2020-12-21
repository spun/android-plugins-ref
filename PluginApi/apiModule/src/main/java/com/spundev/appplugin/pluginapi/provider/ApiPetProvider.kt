@file:Suppress("RedundantSemicolon")

package com.spundev.appplugin.pluginapi.provider

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.util.Log
import com.spundev.appplugin.pluginapi.data.ApiPet
import com.spundev.appplugin.pluginapi.data.ApiPet.Companion.TABLE_NAME
import com.spundev.appplugin.pluginapi.data.ApiProviderDatabase
import com.spundev.appplugin.pluginapi.provider.ApiConstants.KEY_DESCRIPTION
import com.spundev.appplugin.pluginapi.provider.ApiConstants.KEY_VERSION
import com.spundev.appplugin.pluginapi.provider.ApiConstants.METHOD_GET_DESCRIPTION
import com.spundev.appplugin.pluginapi.provider.ApiConstants.METHOD_GET_VERSION
import com.spundev.appplugin.pluginapi.provider.ApiConstants.METHOD_REQUEST_LOAD
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

abstract class ApiPetProvider : ContentProvider(), ApiProviderClient {

    companion object {
        /**
         * The [Intent] action representing a pet provider. A provider should
         * declare an `<intent-filter>` for this action.
         */
        const val ACTION_PET_PROVIDER = "com.spundev.appplugin.api.PetProvider"

        /**
         * Boolean extra that will be set to true when the consumer starts provider settings
         *
         * Check for this extra in your activity if you need to adjust your UI depending on
         * whether or not the user came from the consumer.
         */
        const val EXTRA_FROM_CONSUMER = "com.spundev.appplugin.api.extra.FROM_CONSUMER_SETTINGS"
    }

    private lateinit var authority: String

    override fun onCreate(): Boolean {
        authority = contentUri.authority!!
        return true
    }

    /* TODO: This implementation of the content provider using room is extracted from the
        architecture-components-samples repository (PersistenceContentProviderSample).
        The code from the project ignores the parameters passed to the crud methods and could be a
        problem if for example a consumer app asks for results providing a projection, selection
        or sort order.
    */

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val context = context ?: return null
        val contentResolver = context.contentResolver
            ?: throw IllegalStateException("Called query() before onCreate()")

        val petDao = ApiProviderDatabase
            .getDatabase(context)
            .apiPetDao()

        val queryResult = if (uri != contentUri) {
            // If the uri is different, we assume the pet id has been included
            val id = uri.lastPathSegment ?: return null
            petDao.getPet(id.toLong())
        } else {
            petDao.getAllPets()
        }

        queryResult.setNotificationUri(contentResolver, uri)
        return queryResult;
    }

    override fun getType(uri: Uri): String? {
        return if (uri == contentUri) {
            "vnd.android.cursor.dir/vnd.$authority.$TABLE_NAME"
        } else {
            "vnd.android.cursor.item/vnd.$authority.$TABLE_NAME"
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val context = context ?: return null
        values?.let {
            val newPetId = ApiProviderDatabase
                .getDatabase(context)
                .apiPetDao()
                .insert(ApiPet.fromContentValues(values))
            context.contentResolver.notifyChange(uri, null)
            return ContentUris.withAppendedId(uri, newPetId)
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val context = context ?: return 0
        val count = ApiProviderDatabase
            .getDatabase(context)
            .apiPetDao()
            .deleteById(ContentUris.parseId(uri));
        context.contentResolver.notifyChange(uri, null);
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val context = context
        if (context == null || values == null) {
            return 0; }
        val pet = ApiPet.fromContentValues(values);
        val count = ApiProviderDatabase
            .getDatabase(context)
            .apiPetDao()
            .update(pet);
        context.contentResolver.notifyChange(uri, null);
        return count;
    }

    override val contentUri: Uri by lazy {
        val context = context
            ?: throw IllegalStateException("getContentUri() should not be called before onCreate()")
        ApiProviderContract.getProviderClient(context, javaClass).contentUri
    }


    override fun addPet(pet: ApiPet): Uri? {
        return insert(contentUri, pet.toContentValues())
    }

    override fun addPet(pets: Iterable<ApiPet>): List<Uri> {
        val operations = ArrayList<ContentProviderOperation>()
        for (pet in pets) {
            operations.add(
                ContentProviderOperation.newInsert(contentUri)
                    .withValues(pet.toContentValues())
                    .build()
            )
        }
        return try {
            applyBatch(operations).mapNotNull { result -> result.uri }
        } catch (e: OperationApplicationException) {
            if (Log.isLoggable(TAG, Log.INFO)) {
                Log.i(TAG, "addArtwork failed", e)
            }
            emptyList()
        }
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        val context = context ?: return null
        val token = Binder.clearCallingIdentity()
        try {
            when (method) {
                METHOD_GET_VERSION -> {
                    return Bundle().apply {
                        putInt(KEY_VERSION, 2)
                    }
                }
                METHOD_GET_DESCRIPTION -> {
                    return Bundle().apply {
                        putString(KEY_DESCRIPTION, getDescription())
                    }
                }
                METHOD_REQUEST_LOAD -> {
                    onLoadRequested(false)
                }
            }
            return null
        } finally {
            Binder.restoreCallingIdentity(token)
        }
    }

    abstract fun onLoadRequested(initial: Boolean)


    open fun getDescription(): String {
        val context = context ?: return ""
        return try {
            @SuppressLint("InlinedApi")
            val info = context.packageManager.getProviderInfo(
                ComponentName(context, javaClass),
                PackageManager.MATCH_DISABLED_COMPONENTS
            )
            if (info.descriptionRes != 0) context.getString(info.descriptionRes) else ""
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        Log.d(TAG, "openFile A: $uri")
        return openFile(uri, mode, null)
    }

    override fun openFile(
        uri: Uri,
        mode: String,
        signal: CancellationSignal?
    ): ParcelFileDescriptor? {
        Log.d(TAG, "openFile B: $uri")
        val context = context ?: return null
        // Step 1: Retrieve pet from our own content provider
        val pet = query(uri, null, null, null, null).use { data ->
            if (data == null || !data.moveToFirst()) {
                throw FileNotFoundException("Could not get persistent uri for $uri")
            }
            ApiPet.fromCursor(data)
        }


        // Step 2: Check if we downloaded the image previously by looking for a file with the pet id
        //  as the filename.
        val dir = context.getDir("petImages", Context.MODE_PRIVATE)
        val imageFile = File(dir, pet.id.toString())
        if (!imageFile.exists()) {
            // Step 3: Download the image from the pet image url and save it with the pet id as the filename
            val url = URL(pet.imageUrl)
            val urlConnection = url.openConnection() as HttpURLConnection
            val responseCode = urlConnection.responseCode
            if (responseCode !in 200..299) {
                throw IOException("HTTP error response $responseCode")
            }

            urlConnection.inputStream.use { input ->
                FileOutputStream(imageFile).use { output ->
                    input.copyTo(output)
                }
            }
        }

        // Step 4: From the created file, create the ParcelFileDescriptor and return
        return ParcelFileDescriptor.open(imageFile, ParcelFileDescriptor.parseMode(mode))
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        Log.d(TAG, "openAssetFile: $uri")
        val pfd = openFile(uri, mode, null)
        return AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH)
    }
}

private const val TAG = "ApiPetProvider"