package com.spundev.appplugin.consumer.model

import com.spundev.appplugin.pluginapi.data.ApiPet

data class PetData(val id: Long, val name: String, val age: Int, val species: String) {

    val description: String
        get() {
            return "$name is a $age years old $species"
        }

    companion object {
        fun fromApiPet(apiPet: ApiPet): PetData {
            return PetData(
                id = apiPet.id,
                name = apiPet.name,
                age = apiPet.age,
                species = apiPet.species
            )
        }
    }
}