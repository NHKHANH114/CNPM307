package com.app.shopfee.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.databinding.ActivityFeedbackBinding
import com.app.shopfee.model.Feedback
import com.app.shopfee.prefs.DataStoreManager
import com.app.shopfee.utils.GlobalFunction
import com.app.shopfee.utils.StringUtil
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class FeedbackActivity : BaseActivity() {


    private var mActivityFeedbackBinding : ActivityFeedbackBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityFeedbackBinding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(mActivityFeedbackBinding!!.root)
        initToolbar()
        initData()
    }

    private fun initToolbar() {
        mActivityFeedbackBinding!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
        mActivityFeedbackBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mActivityFeedbackBinding!!.toolbar.tvToolbarTitle.text = getString(R.string.feedback)
    }



    private fun initData() {
        mActivityFeedbackBinding!!.edtEmail.setText(DataStoreManager.user?.email)
        mActivityFeedbackBinding!!.tvSendFeedback.setOnClickListener { onClickSendFeedback() }
    }

    private fun onClickSendFeedback() {
        val strName = mActivityFeedbackBinding!!.edtName.text.toString()
        val strPhone = mActivityFeedbackBinding!!.edtPhone.text.toString()
        val strEmail = mActivityFeedbackBinding!!.edtEmail.text.toString()
        val strComment = mActivityFeedbackBinding!!.edtComment.text.toString()
        if (StringUtil.isEmpty(strName)) {
            GlobalFunction.showToastMessage(this, getString(R.string.name_require))
        } else if (StringUtil.isEmpty(strComment)) {
            GlobalFunction.showToastMessage(this, getString(R.string.comment_require))
        } else {
            showProgressDialog(true)
            val feedback = Feedback(strName, strPhone, strEmail, strComment)
            MyApplication[this].getFeedbackDatabaseReference()
                ?.child(System.currentTimeMillis().toString())
                ?.setValue(
                    feedback
                ) { _: DatabaseError?, _: DatabaseReference? ->
                    showProgressDialog(false)
                    sendFeedbackSuccess()
                }
        }
    }

    private fun sendFeedbackSuccess() {
        GlobalFunction.hideSoftKeyboard(this)
        GlobalFunction.showToastMessage(this, getString(R.string.msg_send_feedback_success))
        mActivityFeedbackBinding!!.edtName.setText("")
        mActivityFeedbackBinding!!.edtPhone.setText("")
        mActivityFeedbackBinding!!.edtComment.setText("")
    }
}