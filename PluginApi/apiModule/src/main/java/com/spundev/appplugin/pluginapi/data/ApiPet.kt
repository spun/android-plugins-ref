package com.spundev.appplugin.pluginapi.data

import android.content.ContentValues
import android.database.Cursor
import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.spundev.appplugin.pluginapi.provider.ApiPetProvider
import com.spundev.appplugin.pluginapi.provider.ApiProviderContract

@Entity(tableName = ApiPet.TABLE_NAME)
class ApiPet(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ApiProviderContract.Pet._ID)
    val id: Long = 0L,
    @ColumnInfo(name = ApiProviderContract.Pet.NAME)
    val name: String,
    @ColumnInfo(name = ApiProviderContract.Pet.AGE)
    val age: Int,
    @ColumnInfo(name = ApiProviderContract.Pet.SPECIES)
    val species: String,
    @ColumnInfo(name = ApiProviderContract.Pet.IMAGE_URL)
    val imageUrl: String
) {
    companion object {
        const val TABLE_NAME = "pets_table"

        @JvmStatic
        fun fromContentValues(values: ContentValues): ApiPet {
            // val id = values.getAsLong(ApiProviderContract.Pet._ID)
            val name = values.getAsString(ApiProviderContract.Pet.NAME)
            val age = values.getAsInteger(ApiProviderContract.Pet.AGE)
            val species = values.getAsString(ApiProviderContract.Pet.SPECIES)
            val imageUrl = values.getAsString(ApiProviderContract.Pet.IMAGE_URL)
            return ApiPet(name = name, age = age, species = species, imageUrl = imageUrl)
        }

        /**
         * Converts the current row of the given Cursor to an ApiPet object. The
         * assumption is that this Cursor was retrieve from a [ApiPetProvider]
         * and has the columns listed in [ApiProviderContract.Pet].
         *
         * @param data A Cursor retrieved from a [ApiPetProvider], already
         * positioned at the correct row you wish to convert.
         * @return a valid ApiPet with values filled in from the
         * [ApiProviderContract.Pet] columns.
         */
        @JvmStatic
        fun fromCursor(data: Cursor): ApiPet = ApiPet(
            data.getLong(data.getColumnIndex(BaseColumns._ID)),
            data.getString(data.getColumnIndex(ApiProviderContract.Pet.NAME)),
            data.getInt(data.getColumnIndex(ApiProviderContract.Pet.AGE)),
            data.getString(data.getColumnIndex(ApiProviderContract.Pet.SPECIES)),
            data.getString(data.getColumnIndex(ApiProviderContract.Pet.IMAGE_URL))
        )
    }

    internal fun toContentValues() = ContentValues().apply {
        put(ApiProviderContract.Pet._ID, id)
        put(ApiProviderContract.Pet.NAME, name)
        put(ApiProviderContract.Pet.AGE, age)
        put(ApiProviderContract.Pet.SPECIES, species)
        put(ApiProviderContract.Pet.IMAGE_URL, imageUrl)
    }
}