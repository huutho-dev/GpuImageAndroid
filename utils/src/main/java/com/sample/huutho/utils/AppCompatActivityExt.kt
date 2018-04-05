package android.utils.nht.superutils.utils
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Created by ThoNh on 2/22/2018.
 */

/**
 * @param toolbarId If of toolbar in xml for Activity
 *
 * @param action function setup for actionBar
 *      setTitle(R.string.statistics_title)
 *      setHomeAsUpIndicator(R.drawable.ic_menu)
 *      setDisplayHomeAsUpEnabled(true)
 *      ...
 *
 * Maybe you need override function  onSupportNavigateUp() for handle onBackPress
 */
fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action
    }
}


/**
 * Request permission
 * @param permissions list permission need request
 * @param funcGrantPermission optional callback after permission granted -> call this function funcGrantPermission
 * @param funcDeniedPermission optional callback if user denied permissions -> call function funcDeniedPermission
 *
 * @howToUse requestPermission(this::grantedPermissionAndOpenCamera, this::deniedPermissionOpenCamera, Manifest.permission.CAMERA, "", "")
 *           private fun grantedPermissionAndOpenCamera(){request camera}
 *           private fun deniedPermissionOpenCamera(){toast for user}
 *
 * @howToUse requestPermission(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, ...)
 */
fun AppCompatActivity.requestPermission(funcGrantPermission: () -> Unit = {},
                                        funcDeniedPermission: () -> Unit = {},
                                        vararg permissions: String) {
    RxPermissions(this)
            .request(*permissions)
            .subscribe({ granted ->
                if (granted) { // Always true pre-M
                    Log.e("ThoNH", "requestPermission(${permissions}) -> :D")
                    funcGrantPermission.invoke()
                } else {
                    Log.e("ThoNH", "requestPermission(${permissions}) -> :((")
                    funcDeniedPermission.invoke()
                }
            })
}

/**
 * Request permission
 * @param permissions list permission need request
 * @param funcGrantPermission optional callback after permission granted -> call this function funcGrantPermission
 * @param funcDeniedPermission optional callback if user denied permissions -> call function funcDeniedPermission
 *
 * @howToUse requestPermission(this::grantedPermissionAndOpenCamera, this::deniedPermissionOpenCamera, Manifest.permission.CAMERA, "", "")
 *           private fun grantedPermissionAndOpenCamera(){request camera}
 *           private fun deniedPermissionOpenCamera(){toast for user}
 *
 * @howToUse requestPermission( permissions = Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, ...)
 */
fun Fragment.requestPermission(funcGrantPermission: () -> Unit = {},
                               funcDeniedPermission: () -> Unit = {},
                               vararg permissions: String) {
    RxPermissions(activity!!)
            .request(*permissions)
            .subscribe({ granted ->
                if (granted) { // Always true pre-M
                    Log.e("ThoNH", "requestPermission(${permissions}) -> :D")
                    funcGrantPermission.invoke()
                } else {
                    Log.e("ThoNH", "requestPermission(${permissions}) -> :((")
                    funcDeniedPermission.invoke()
                }
            })
}

