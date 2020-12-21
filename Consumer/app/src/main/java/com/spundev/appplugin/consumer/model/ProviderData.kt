package com.spundev.appplugin.consumer.model

import android.content.ComponentName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProviderData(
    val authority: String = "unknown authority",
    val packageName: String = "unknown packageName",
    val title: String = "unknown title",
    val description: String? = null,
    val settingsActivity: ComponentName? = null,
    // val icon: Drawable,
    // val setupActivity: ComponentName?,
    // val selected: Boolean
) : Parcelable