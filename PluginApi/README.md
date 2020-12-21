# How to create a plugin app

## 1. Make your app use the api module

```gradle
// Add to settings.gradle
include ':apiModule'
project(':apiModule').projectDir = new File(settingsDir, '../PluginApi/apiModule')
```

```gradle
// Add to you app build.gradle
implementation project(path: ':apiModule')
```

## 2. Create your provider

```kotlin
class PluginACatProvider : ApiPetProvider() {
    override fun onLoadRequested(initial: Boolean) {
        [...]
    }
}
```

The `onLoadRequested` method will be called when the consumer app sends the call to the plugin. Inside this method, the plugin app should fetch for updates using its own sources or start a worker if the plugin is going to update its data periodically.

## 3. Add the provider to the `AndroidManifest.xml`

```xml
<provider
    android:name=".PluginACatProvider"
    android:authorities="${pluginAAuthority}"
    android:description="@string/source_description"
    android:exported="true"
    android:label="@string/source_title"
    android:permission="com.spundev.appplugin.api.ACCESS_PROVIDER">
    <intent-filter>
        <action android:name="com.spundev.appplugin.api.PetProvider" />
    </intent-filter>
    <meta-data
        android:name="settingsActivity"
        android:value=".SettingsActivity" />
</provider>
```

At this point, the plugin should appear as an available provider inside the consumer app.
