package com.spundev.appplugin.pluginapi.data

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface ApiPetDao {

    @Query("SELECT * FROM pets_table WHERE _id = :petId ORDER BY _id DESC")
    fun getPet(petId: Long): Cursor

    @Query("SELECT * FROM pets_table ORDER BY _id DESC")
    fun getAllPets(): Cursor

    /**
     * Inserts a pet into the table.
     *
     * @param pet A new pet.
     * @return The row ID of the newly inserted pet.
     */
    @Insert
    fun insert(pet: ApiPet): Long

    /**
     * Update a pet. The pet is identified by the row ID.
     *
     * @param pet The pet to update.
     * @return A number of pets updated. This should always be `1`.
     */
    @Update
    fun update(pet: ApiPet): Int

    /**
     * Delete a pet by the ID.
     *
     * @param id The row ID.
     * @return A number of pets deleted. This should always be {@code 1}.
     */
    @Query("DELETE FROM pets_table WHERE _id = :id")
    fun deleteById(id: Long): Int

    @Query("DELETE FROM pets_table")
    suspend fun deleteAll()
}