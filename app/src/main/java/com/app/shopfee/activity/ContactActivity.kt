package com.app.shopfee.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.R
import com.app.shopfee.adapter.ContactAdapter
import com.app.shopfee.constant.AboutUsConfig
import com.app.shopfee.databinding.ActivityContactBinding
import com.app.shopfee.model.Contact
import com.app.shopfee.utils.GlobalFunction

class ContactActivity : BaseActivity() {
    private var mContactAdapter: ContactAdapter? = null

    private var mContactActivityBining : ActivityContactBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContactActivityBining = ActivityContactBinding.inflate(layoutInflater)
        setContentView(mContactActivityBining!!.root)
        initToolbar()
        initData()
        initListener()
    }

    private fun initToolbar() {
        mContactActivityBining!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mContactActivityBining!!.toolbar.tvToolbarTitle.text = getString(R.string.contact)
        mContactActivityBining!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
    }


    private fun initData() {
        mContactActivityBining!!.tvAboutUsTitle.text = AboutUsConfig.ABOUT_US_TITLE
        mContactActivityBining!!.tvAboutUsContent.text = AboutUsConfig.ABOUT_US_CONTENT
        mContactActivityBining!!.tvAboutUsWebsite.text = AboutUsConfig.ABOUT_US_WEBSITE_TITLE
        mContactAdapter = ContactAdapter(this, getListContact(), object : ContactAdapter.ICallPhone {
            override fun onClickCallPhone() {
                GlobalFunction.callPhoneNumber(this@ContactActivity)
            }
        })
        val layoutManager = GridLayoutManager(this, 3)
        mContactActivityBining!!.rcvData.isNestedScrollingEnabled = false
        mContactActivityBining!!.rcvData.isFocusable = false
        mContactActivityBining!!.rcvData.layoutManager = layoutManager
        mContactActivityBining!!.rcvData.adapter = mContactAdapter
    }

    private fun initListener() {
        mContactActivityBining!!.layoutWebsite.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(AboutUsConfig.WEBSITE)
                )
            )
        }
    }

    private fun getListContact(): List<Contact> {
        val contactArrayList: MutableList<Contact> = ArrayList()
        contactArrayList.add(Contact(Contact.FACEBOOK, R.drawable.ic_facebook))
        contactArrayList.add(Contact(Contact.HOTLINE, R.drawable.ic_hotline))
        contactArrayList.add(Contact(Contact.GMAIL, R.drawable.ic_gmail))
        contactArrayList.add(Contact(Contact.SKYPE, R.drawable.ic_skype))
        contactArrayList.add(Contact(Contact.YOUTUBE, R.drawable.ic_youtube))
        contactArrayList.add(Contact(Contact.ZALO, R.drawable.ic_zalo))
        return contactArrayList
    }

    public override fun onDestroy() {
        super.onDestroy()
        mContactAdapter?.release()
    }
}