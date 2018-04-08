package com.sample.huutho.utils.ui

import android.support.v4.app.Fragment
import org.jetbrains.anko.AnkoLogger

/**
 * Created by HuuTho on 4/1/2018.
 */
abstract class BaseFragment : Fragment(), AnkoLogger {

    /**
     * Could handle back press.
     * @return true if back press was handled
     */
    fun onBackPressed(): Boolean {
        return false
    }

}