package com.app.guardian

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.multidex.MultiDex
import com.app.guardian.common.LocaleUtils
import com.app.guardian.common.SharedPreferenceManager
import com.app.guardian.common.ShowLogToast
import com.app.guardian.injection.appModules

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class GuardianApplication :Application(),Application.ActivityLifecycleCallbacks{
    var isMapAvailable = AtomicBoolean(true)
    var currentCountryCode: String? = null
    private val hasCheckedLocation = AtomicBoolean(false)
    private val lastLocation: Location? = null

    init {
        appContext = this
        instance = this

    }

    fun usingMockLocations(): Boolean {
        return USE_MOCK_LOCATION
    }


    override fun onCreate() {

        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@GuardianApplication)
            modules(appModules)
        }
        appContext = applicationContext
        currentContext =
            appContext
        SharedPreferenceManager.init(appContext)
        if (!isInitialized) initialize()
    }


    override fun onTerminate() {
        currentContext = appContext
        ShowLogToast.ShowLog(TAG, "TERMINATE_APP in onTerminate")
        clean()
        super.onTerminate()
    }

    override fun onLowMemory() {
        clean()
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {

        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            this.onBackground()
        } else {
            clean()
        }

        super.onTrimMemory(level)
    }

    fun onForeground() {
        isBackground = false
    }

    private fun prepareForBackground() {
        ShowLogToast.ShowLog(TAG, "prepareForBackground")
    }

    fun onBackground() {
        ShowLogToast.ShowLog(TAG, "Entering background")

        clean()

        prepareForBackground()


        isActivated = false
        isReady = false
        isBackground = true
    }
    //endregion

    fun onUIAvailable() {
        ShowLogToast.ShowLog(TAG, "Application UI is available")
        //        AppHelper.sendBroadcast("NOTIF_APP_UI_AVAILABLE", null);

        isUIAvailable = true

        ShowLogToast.ShowLog(TAG, "Check to see if Google Play Services are available")
        //        isGooglePlayServicesAvailable = AppHelper.checkIfGooglePlayServicesAvailable();
        ShowLogToast.ShowLog(TAG, "googlePlayServicesAvailable=$isGooglePlayServicesAvailable")

        ShowLogToast.ShowLog(TAG, "Initialize")
        //        if (!isInitialized) initialize();

        ShowLogToast.ShowLog(TAG, "Application is launched")
        //        AppHelper.sendBroadcast("NOTIF_APP_LAUNCHED", null);
    }

    //region ACTIVITY LIFECYCLE
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ShowLogToast.ShowLog(TAG, "########## onActivityCreated; activity=$activity")

        currentContext = activity

        // if activity getting created for first time, we must have UI now
        if (isUIAvailable == false) {
            onUIAvailable()
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        ShowLogToast.ShowLog(TAG, "########## onActivityDestroyed; activity=$activity")
    }

    override fun onActivityStarted(activity: Activity) {
        ShowLogToast.ShowLog(TAG, "########## onActivityStarted; activity=$activity")
        currentContext = activity
        if (isBackground) this.onForeground()
    }

    override fun onActivityStopped(activity: Activity) {
        ShowLogToast.ShowLog(TAG, "########## onActivityStopped; activity=$activity")
    }

    override fun onActivityResumed(activity: Activity) {
        ShowLogToast.ShowLog(TAG, "########## onActivityResumed; activity=$activity")
        currentContext = activity
    }

    override fun onActivityPaused(activity: Activity) {
        ShowLogToast.ShowLog(TAG, "########## onActivityPaused; activity=$activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        ShowLogToast.ShowLog(TAG, "########## onActivitySaveInstanceState; activity=$activity")
    }

    private fun clean() {

    }

    private fun initialize() {
        ShowLogToast.ShowLog(TAG, "Initialize reachability manager")
        isInitialized = true
    }

    fun switchPowerSaveMode(exitFromForeground: Boolean) {
        /* We should only do this if the app is in background */
        //        if (!exitFromForeground && isAppInForeground.get()) {
        //            return;
        //        }

        //        isAppInPowerSaveMode.set(true);

        //        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //        if (!BTData.getAutoConnectViaBluetooth()) {
        //            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
        //                if ("com.escortLive2.bluetooth.CobraBTRadarService".equals(service.service.getClassName())) {
        //                    Intent btService = new Intent(this, CobraBTRadarService.class);
        //                    btService.putExtra("Exit from foreground", exitFromForeground);
        //                    stopService(btService);
        //                    break;
        //                }
        //            }
        //        }

        // VK: Now remove the location updates
        // VK: Close every activity
        //        LocalBroadcastManager.getInstance(CobraApplication.getAppContext()).sendBroadcast(
        //                new Intent(ConstantCodes.CobraInternalMessages.APP_EXIT.name()));

    }

    override fun attachBaseContext(base: Context) {
        //super.attachBaseContext(base);
        super.attachBaseContext(LocaleUtils.onAttach(base, "en"))
        MultiDex.install(this)
    }

    companion object {

        val GPS_CHIP_DEBUG = false
        private val USE_MOCK_LOCATION = false
        var TAG = "Guradian"
        lateinit var appContext: Context
        lateinit var currentContext: Context
        var isUIAvailable = false
        var isServerAvailable = false
        var isBackground = false
        var isActivated = false
        var isInitialized = false
        var isReady = false
        var isConnectedToInternet = false
        var isConnectedToServer = false
        var isGooglePlayServicesAvailable = false
        var instance: GuardianApplication? = null

        @Synchronized
        fun sharedInstance(): GuardianApplication {
            if (instance == null) instance =
                GuardianApplication()
            return instance as GuardianApplication
        }
    }

}