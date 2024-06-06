package com.app.shopfee.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.app.shopfee.R
import com.app.shopfee.adapter.AdminViewPagerAdapter
import com.app.shopfee.databinding.ActivityAdminMainBinding

class AdminMainActivity : AppCompatActivity() {
    private var mActivityAdminMainBinding : ActivityAdminMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAdminMainBinding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(mActivityAdminMainBinding!!.root)
        mActivityAdminMainBinding!!.viewpager2.isUserInputEnabled = false
        val adminViewPagerAdapter = AdminViewPagerAdapter(this)
        mActivityAdminMainBinding!!.viewpager2.adapter = adminViewPagerAdapter
        mActivityAdminMainBinding!!.viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> mActivityAdminMainBinding!!.bottomNavigation.menu.findItem(R.id.nav_home).isChecked = true
                    1 -> mActivityAdminMainBinding!!.bottomNavigation.menu.findItem(R.id.nav_order).isChecked = true
                    2 -> mActivityAdminMainBinding!!.bottomNavigation.menu.findItem(R.id.nav_account).isChecked = true
                }
            }
        })

        mActivityAdminMainBinding!!.bottomNavigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> {
                    mActivityAdminMainBinding!!.viewpager2.currentItem = 0
                }
                R.id.nav_order -> {
                    mActivityAdminMainBinding!!.viewpager2.currentItem = 1
                }
                R.id.nav_account -> {
                    mActivityAdminMainBinding!!.viewpager2.currentItem = 2
                }
            }
            true
        }

    }
    override fun onBackPressed() {
        showConfirmExitApp()
    }

    private fun showConfirmExitApp() {
        MaterialDialog.Builder(this)
            .title(getString(R.string.app_name))
            .content(getString(R.string.msg_exit_app))
            .positiveText(getString(R.string.action_ok))
            .onPositive { _: MaterialDialog?, _: DialogAction? -> finishAffinity() }
            .negativeText(getString(R.string.action_cancel))
            .cancelable(false)
            .show()
    }

    fun setToolBar(isHome : Boolean, title: String?) {
        mActivityAdminMainBinding!!.toolbar.tvToolbarTitle.text = title
        if (isHome){
            mActivityAdminMainBinding!!.toolbar.imgCart.visibility = View.GONE
            return
        }
        mActivityAdminMainBinding!!.toolbar.imgCart.visibility = View.VISIBLE
    }

}