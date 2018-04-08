package com.sample.huutho.utils.ui

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.AnkoLogger


/**
 * Created by HuuTho on 4/1/2018.
 */
abstract class BaseActivity : AppCompatActivity() , AnkoLogger{

    /**
     * Support for onBackPressed() in fragment
     */
    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        var handled = false
        for (f in fragmentList) {
            if (f is BaseFragment) {
                handled = f.onBackPressed()
                if (handled) {
                    break
                }
            }
        }

        if (!handled) {
            super.onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            supportFragmentManager.fragments.forEach { it.onActivityResult(requestCode, resultCode, data) }
        }
    }
}