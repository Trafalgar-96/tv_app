package com.mobapphome.androidappupdater.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import com.mobapphome.androidappupdater.AAUpdaterRestricterDlg
import com.mobapphome.androidappupdater.AAUpdaterDlg
import com.mobapphome.androidappupdater.DownloadApk
import com.mobapphome.androidappupdater.R
import java.lang.Exception

@SuppressLint("StaticFieldLeak")
object AAUpdaterController {
    var urlService: String? = null
    var versionCode = ""
    private const val sharedPrefFile = "version_code"
    var updateInfoResolver: IUpdateInfoResolver? = null
    var sharedPref: SharedPreferences? = null
    var fontName: String? = null
    private var act: FragmentActivity? = null
    private var initCalled = false

    private var btnInfoVisibility: Boolean = false
    private var btnInfoMenuItemTitle: String? = null
    private var btnInfoActionURL: String? = null

    /**
     * Initializes MAHAndroidUpdater library
     * @param act Activity which init calls
     * *
     * @param urlService Url for services which data about update has placed.
     * *
     * @param updateInfoResolver Object wich implementing it you can get data from your own structed web service.
     * *
     * @param btnInfoVisibility If true shows info button
     * *
     * @param btnInfoMenuItemTitle Title of menu item for info button
     * *
     * @param btnInfoActionURL Url to open when clicking to info button or info menu item
     */
    @Throws(NullPointerException::class)
    @JvmStatic
    @JvmOverloads
    fun init(
            act: FragmentActivity,
            btnInfoVisibility: Boolean = false,
            btnInfoMenuItemTitle: String = act.getString(R.string.android_app_upd_info_popup_text),
            //btnInfoActionURL: String = Constants.MAH_UPD_GITHUB_LINK,
            updateInfoResolver: IUpdateInfoResolver? = null) {
        if (initCalled) {
            return
        }

        AAUpdaterController.btnInfoVisibility = btnInfoVisibility
        AAUpdaterController.btnInfoMenuItemTitle = btnInfoMenuItemTitle
        AAUpdaterController.btnInfoActionURL = btnInfoActionURL

        urlService = DownloadApk.UPDATE_DOMAIN + "update_info.json"
        AAUpdaterController.updateInfoResolver = updateInfoResolver
        AAUpdaterController.act = act

        if (urlService == null && updateInfoResolver == null) {
            throw NullPointerException("At least one of these urlService or updateInfoResolver variables " + "\n has to be set in init(final Activity act, String urlService, IUpdateInfoResolver updateInfoResolver) method")
        }

        if (urlService != null && updateInfoResolver != null) {
            throw RuntimeException("Can't use urlService and updateInfoResolver at the same time, choose one")
        }

        sharedPref = act.getPreferences(Context.MODE_PRIVATE)

        val updater = Updater()
        val sharedPreferences: SharedPreferences = act.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        updater.updaterListiner = object : UpdaterListener {

            override fun onResponse(programInfo: ProgramInfo?, errorStr: String) {
                when {
                    programInfo == null -> Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "MAhUpdater Program info is null")
                    !programInfo.isRunMode -> Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "MAHUpdter run mode is false")
                    programInfo.uriCurrent == null -> Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "MAHUpdter uri_current is null in service. check service")
                    programInfo.versionCodeCurrent == -1 -> Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "MAHUpdter version_code_current is -1 in service. check service")
                    else -> {
                        //try to save version code in shared preferences
                        // this will help for checking update from settings
                        try {
                            val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                            editor.putInt("version_code",programInfo.versionCodeCurrent)
                            editor.apply()
                            editor.commit()
                        }catch (e:Exception){
                            Log.i("error : ","cannot save version code in shared preferences.")
                        }

                        versionCode = programInfo.versionCodeCurrent.toString()
                        Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "Uri current from service = " + programInfo.uriCurrent + "  Uri from package" + act.applicationContext.packageName)
                        Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "Version from service = " + programInfo.versionCodeCurrent + "  Version from package" + getVersionCode(act))

                        val isRestrictedDlg = if (getVersionCode(act) < programInfo.versionCodeMin) true else false

                        when {
                            programInfo.uriCurrent != act.applicationContext.packageName ->
                                when {
                                    checkPackageIfExists(act, programInfo.uriCurrent) -> showRestricterDlg(act, DlgModeEnum.OPEN_NEW, programInfo)
                                    isRestrictedDlg -> showRestricterDlg(act, DlgModeEnum.INSTALL, programInfo)
                                    else -> showUpdaterDlg(act, DlgModeEnum.INSTALL, programInfo)
                                }
                            getVersionCode(act) < programInfo.versionCodeCurrent ->
                                when {
                                    isRestrictedDlg -> showRestricterDlg(act, DlgModeEnum.UPDATE, programInfo)
                                    else -> showUpdaterDlg(act, DlgModeEnum.UPDATE, programInfo)
                                }
                            else -> Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "MAHUpdater: There are not any update in service")
                        }
                    }
                }
            }
        }

        updater.updateProgramList(act)
        //initCalled = true
    }

    @Throws(NullPointerException::class)
    fun callUpdate() {
        if (urlService == null && updateInfoResolver == null) {
            return
            //throw new NullPointerException("urlService not set call init(final Activity act, String urlService) constructor");
        }

        val updater = Updater()
        updater.updaterListiner = null /*object : UpdaterListener {

            override fun onResponse(programInfo: ProgramInfo?, errorStr: String) {
            }
        }*/

        updater.updateProgramList(act!!)
    }

    @JvmStatic
    @JvmOverloads
    fun end() {
        Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "MAHUpdater end called")
        callUpdate()
        initCalled = false
    }

    @JvmStatic
    @JvmOverloads
    fun version(): String {
        return versionCode
    }

    @JvmStatic
    fun testUpdaterDlg(act: FragmentActivity) {
        val prInfoTest = ProgramInfo(updateInfo = "Update info test mode.")
        showUpdaterDlg(act, DlgModeEnum.TEST, prInfoTest)
    }

    @JvmStatic
    fun testRestricterDlg(act: FragmentActivity) {
        val prInfoTest = ProgramInfo(updateInfo = "Update info test mode. ")
        showRestricterDlg(act, DlgModeEnum.TEST, prInfoTest)
    }

    private fun showUpdaterDlg(act: FragmentActivity, mode: DlgModeEnum, programInfo: ProgramInfo) {
        showDlg(act,
                AAUpdaterDlg.newInstance(programInfo, mode, btnInfoVisibility, btnInfoMenuItemTitle, btnInfoActionURL),
                "fragment_android_updater_dlg")
    }

    private fun showRestricterDlg(act: FragmentActivity, mode: DlgModeEnum, programInfo: ProgramInfo) {
        showDlg(act,
                AAUpdaterRestricterDlg.newInstance(programInfo, mode, btnInfoVisibility, btnInfoMenuItemTitle, btnInfoActionURL),
                "fragment_restricter_dlg")
    }

    private fun showDlg(activity: FragmentActivity, frag:
    Fragment, fragTag: String) {

        if (!activity.isFinishing) {
            val fragmentManager = activity.supportFragmentManager
            val fr = fragmentManager.findFragmentByTag(fragTag)
            if (fr != null && !fr.isHidden) {
                Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "showDlg  dismissed")
                (fr as DialogFragment).dismissAllowingStateLoss()
            }

            val transaction = fragmentManager.beginTransaction()
            transaction.add(frag, fragTag)
            transaction.commitAllowingStateLoss()
        }
    }
}
