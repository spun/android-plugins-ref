package com.spundev.appplugin.consumer.ui.availableProviders

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.spundev.appplugin.consumer.model.ProviderData
import com.spundev.appplugin.consumer.util.getInstalledProviders
import kotlinx.coroutines.flow.map

class AvailableProvidersViewModel(
    app: Application
) : AndroidViewModel(app) {

    private val pm: PackageManager = app.packageManager

    val availableProvidersList =
        getInstalledProviders(app).map { providerInfoList: List<ProviderInfo> ->
            providerInfoList.map { providerInfo ->
                ProviderData(
                    authority = providerInfo.authority,
                    packageName = providerInfo.packageName,
                    title = providerInfo.loadLabel(pm).toString(),
                    description = "",
                    settingsActivity = providerInfo.metaData?.getString("settingsActivity")?.run {
                        // From platform source code. Replacement of [ComponentName.createRelative] (Api 23)
                        val fullName = if (this[0] == '.') {
                            // Relative to the package. Prepend the package name.
                            providerInfo.packageName + this
                        } else {
                            // Fully qualified package name.
                            this
                        }
                        ComponentName(providerInfo.packageName, fullName)
                    }
                )
            }
        }.asLiveData()
}