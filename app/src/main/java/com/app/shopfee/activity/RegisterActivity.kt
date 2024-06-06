package com.app.shopfee.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.app.shopfee.R
import com.app.shopfee.databinding.ActivityRegisterBinding
import com.app.shopfee.model.User
import com.app.shopfee.prefs.DataStoreManager
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlobalFunction
import com.app.shopfee.utils.StringUtil
import com.app.shopfee.utils.StringUtil.isEmpty
import com.app.shopfee.utils.StringUtil.isValidEmail
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : BaseActivity() {

    private var mActivityRegisterBinding : ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mActivityRegisterBinding!!.root)
        mActivityRegisterBinding!!.layoutLogin.setOnClickListener { finish() }
        mActivityRegisterBinding!!.btnRegister.setOnClickListener { onClickValidateSignUp() }

    }



    private fun onClickValidateSignUp() {
        val strEmail = mActivityRegisterBinding!!.edtEmail.text.toString().trim { it <= ' ' }
        val strPassword = mActivityRegisterBinding!!.edtPassword.text.toString().trim { it <= ' ' }
        if (isEmpty(strEmail)) {
            Toast.makeText(this@RegisterActivity, getString(R.string.msg_email_require), Toast.LENGTH_SHORT).show()
        } else if (isEmpty(strPassword)) {
            Toast.makeText(this@RegisterActivity, getString(R.string.msg_password_require), Toast.LENGTH_SHORT).show()
        } else if (!isValidEmail(strEmail)) {
            Toast.makeText(this@RegisterActivity, getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT).show()
        } else {
            if (mActivityRegisterBinding!!.rdbAdmin.isChecked) {
                if (!strEmail.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                    Toast.makeText(this@RegisterActivity, getString(R.string.msg_email_invalid_admin), Toast.LENGTH_SHORT).show()
                } else {
                    signUpUser(strEmail, strPassword)
                    Toast.makeText(this@RegisterActivity, "Thanh cong", Toast.LENGTH_SHORT).show()
                }
                return
            }
            if (strEmail.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                Toast.makeText(this@RegisterActivity, getString(R.string.msg_email_invalid_user), Toast.LENGTH_SHORT).show()
            } else {
                signUpUser(strEmail, strPassword)
                Toast.makeText(this@RegisterActivity, "Thanh cong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpUser(email: String, password: String) {
        showProgressDialog(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult?> ->
                showProgressDialog(false)
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        val userObject = User(user.email, password)
                        if (user.email != null && user.email!!.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                            userObject.isAdmin = true
                        }
                        DataStoreManager.user = userObject
                        //gotoMainActivity(this)
                        //finishAffinity()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, getString(R.string.msg_sign_up_error),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}