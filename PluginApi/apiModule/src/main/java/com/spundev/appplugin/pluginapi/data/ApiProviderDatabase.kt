package com.spundev.appplugin.pluginapi.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * The Room database.
 */
// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [ApiPet::class], version = 1, exportSchema = false)
abstract class ApiProviderDatabase : RoomDatabase() {

    abstract fun apiPetDao(): ApiPetDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time. 
        @Volatile
        private var INSTANCE: ApiProviderDatabase? = null

        fun getDatabase(context: Context): ApiProviderDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApiProviderDatabase::class.java,
                    "plugin_provider_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}