package com.app.shopfee.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.app.shopfee.R
import com.app.shopfee.databinding.ActivityChangePasswordBinding
import com.app.shopfee.model.User
import com.app.shopfee.prefs.DataStoreManager
import com.app.shopfee.utils.StringUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : BaseActivity() {

    private var mActivityChangePasswordBinding : ActivityChangePasswordBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityChangePasswordBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(mActivityChangePasswordBinding!!.root)
        initToolbar()
        initListener()
    }

    private fun initToolbar() {
        val imgToolbarBack = findViewById<ImageView>(R.id.img_toolbar_back)
        val tvToolbarTitle = findViewById<TextView>(R.id.tv_toolbar_title)
        imgToolbarBack.setOnClickListener { finish() }
        tvToolbarTitle.text = getString(R.string.change_password)
    }



    private fun initListener() {
        mActivityChangePasswordBinding!!.edtOldPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityChangePasswordBinding!!.edtOldPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_main)
                } else {
                    mActivityChangePasswordBinding!!.edtOldPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray)
                }
            }
        })
        mActivityChangePasswordBinding!!.edtNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityChangePasswordBinding!!.edtNewPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_main)
                } else {
                    mActivityChangePasswordBinding!!.edtNewPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray)
                }
            }
        })
        mActivityChangePasswordBinding!!.edtConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!StringUtil.isEmpty(s.toString())) {
                    mActivityChangePasswordBinding!!.edtConfirmPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_main)
                } else {
                    mActivityChangePasswordBinding!!.edtConfirmPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray)
                }
            }
        })
        mActivityChangePasswordBinding!!.btnChangePassword.setOnClickListener { onClickValidateChangePassword() }
    }

    private fun onClickValidateChangePassword() {
        val strOldPassword = mActivityChangePasswordBinding!!.edtOldPassword.text.toString().trim { it <= ' ' }
        val strNewPassword = mActivityChangePasswordBinding!!.edtNewPassword.text.toString().trim { it <= ' ' }
        val strConfirmPassword = mActivityChangePasswordBinding!!.edtConfirmPassword.text.toString().trim { it <= ' ' }
        if (StringUtil.isEmpty(strOldPassword)) {
            showToastMessage(getString(R.string.msg_old_password_require))
        } else if (StringUtil.isEmpty(strNewPassword)) {
            showToastMessage(getString(R.string.msg_new_password_require))
        } else if (StringUtil.isEmpty(strConfirmPassword)) {
            showToastMessage(getString(R.string.msg_confirm_password_require))
        } else if (DataStoreManager.user?.password != strOldPassword) {
            showToastMessage(getString(R.string.msg_old_password_invalid))
        } else if (strNewPassword != strConfirmPassword) {
            showToastMessage(getString(R.string.msg_confirm_password_invalid))
        } else if (strOldPassword == strNewPassword) {
            showToastMessage(getString(R.string.msg_new_password_invalid))
        } else {
            changePassword(strNewPassword)
        }
    }

    private fun changePassword(newPassword: String) {
        showProgressDialog(true)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        user.updatePassword(newPassword)
            .addOnCompleteListener { task: Task<Void?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    showToastMessage(getString(R.string.msg_change_password_successfully))
                    val userLogin: User? = DataStoreManager.user
                    userLogin?.password = newPassword
                    DataStoreManager.user = userLogin
                    mActivityChangePasswordBinding!!.edtOldPassword.setText("")
                    mActivityChangePasswordBinding!!.edtNewPassword.setText("")
                    mActivityChangePasswordBinding!!.edtConfirmPassword.setText("")
                }
            }
    }
}