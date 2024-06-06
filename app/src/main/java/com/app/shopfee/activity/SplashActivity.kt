package com.app.shopfee.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.app.shopfee.R
import com.app.shopfee.prefs.DataStoreManager
import com.app.shopfee.prefs.DataStoreManager.Companion.user
import com.app.shopfee.utils.GlobalFunction
import com.app.shopfee.utils.GlobalFunction.gotoMainActivity
import com.app.shopfee.utils.StringUtil
import com.app.shopfee.utils.StringUtil.isEmpty


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ goToActivity() }, 2000)
    }

    private fun goToActivity() {
            if (user != null && !isEmpty(user!!.email)) {
                gotoMainActivity(this)
            } else {
                GlobalFunction.startActivity(this, LoginActivity::class.java)
            }
            finish()
        }
}