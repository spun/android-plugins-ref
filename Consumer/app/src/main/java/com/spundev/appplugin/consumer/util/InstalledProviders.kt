package com.spundev.appplugin.consumer.util

import android.content.*
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import com.spundev.appplugin.pluginapi.provider.ApiPetProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


fun ProviderInfo.getComponentName() = ComponentName(packageName, name)

private fun getProviders(context: Context, packageName: String? = null): List<ProviderInfo> {
    val queryIntent = Intent(ApiPetProvider.ACTION_PET_PROVIDER)
    if (packageName != null) {
        queryIntent.`package` = packageName
    }
    val pm = context.packageManager
    return pm.queryIntentContentProviders(queryIntent, PackageManager.GET_META_DATA).filterNotNull()
        .map {
            it.providerInfo
        }.filter {
            it.enabled
        }
}

/**
 * Get a [Flow] for the list of currently installed [ApiPetProvider] instances.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun getInstalledProviders(context: Context): Flow<List<ProviderInfo>> = callbackFlow {
    val currentProviders = HashMap<ComponentName, ProviderInfo>()
    val packageChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent?.data == null) {
                return
            }
            val packageName = intent.data?.schemeSpecificPart
            when (intent.action) {
                Intent.ACTION_PACKAGE_ADDED -> {
                    getProviders(context, packageName).forEach { providerInfo ->
                        currentProviders[providerInfo.getComponentName()] = providerInfo
                    }
                }
                Intent.ACTION_PACKAGE_CHANGED, Intent.ACTION_PACKAGE_REPLACED -> {
                    currentProviders.entries.removeAll { it.key.packageName == packageName }
                    getProviders(context, packageName).forEach { providerInfo ->
                        currentProviders[providerInfo.getComponentName()] = providerInfo
                    }
                }
                Intent.ACTION_PACKAGE_REMOVED -> {
                    currentProviders.entries.removeAll { it.key.packageName == packageName }
                }
            }
            sendBlocking(currentProviders.values.toList())
        }
    }
    val packageChangeFilter = IntentFilter().apply {
        addDataScheme("package")
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_CHANGED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
    }
    context.registerReceiver(packageChangeReceiver, packageChangeFilter)
    // Populate the initial set of providers
    getProviders(context).forEach { providerInfo ->
        currentProviders[providerInfo.getComponentName()] = providerInfo
    }
    send(currentProviders.values.toList())

    awaitClose {
        context.unregisterReceiver(packageChangeReceiver)
    }
}