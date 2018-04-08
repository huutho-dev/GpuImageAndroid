package com.sample.huutho.utils

import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


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


/**
 * Call func to take photo with camera
 * @param REQUEST_IMAGE_CAPTURE need to check onActivityForResult
 * @return String file path of photo after capture, using get Bitmap file onActivityResult
 *
 * *  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
 *      super.onActivityResult(requestCode, resultCode, data)
 *      if (resultCode == Activity.RESULT_OK) {
 *          if (requestCode == REQUEST_IMAGE_CAPTURE) {
 *             decodeFile(PATH_PHOTO,start = {}, success = { bmp: Bitmap -> image.setImageBitmap(bmp) }, failure = {})      // PATH_PHOTO = takePhotoFromCamera(REQUEST_GALLERY_PHOTO)
 *          }
 *      }
 *  }
 *
 */
fun AppCompatActivity.takePhotoFromCamera(REQUEST_IMAGE_CAPTURE: Int): String {

    try {
        fun createImageFile(): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date());
            val imageFileName = "JPEG_" + timeStamp + "_";
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            val image = File.createTempFile(imageFileName, ".jpg", storageDir)
            return image
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            val photoFile = createImageFile();

            val photoURI = FileProvider.getUriForFile(this, "${Config.APPLICATION_ID}.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            return photoFile.absolutePath
        }

    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return ""
}

/**
 * call func when u want take photo from gallery
 * @param REQUEST_GALLERY_PHOTO need to check in onActivityForResult
 *
 *  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
 *      super.onActivityResult(requestCode, resultCode, data)
 *      if (resultCode == Activity.RESULT_OK) {
 *          if (requestCode == REQUEST_GALLERY_PHOTO) {
 *              data?.data?.let {
 *              decodeStream(this, data.data, start = {}, success = {}, failure = {})
 *          }
 *      }
 *  }
 */
fun AppCompatActivity.takePhotoFromGallery(REQUEST_GALLERY_PHOTO: Int) {
    val photoPickerIntent = Intent(Intent.ACTION_PICK)
    photoPickerIntent.type = "image/*"
    startActivityForResult(photoPickerIntent, REQUEST_GALLERY_PHOTO)
}
