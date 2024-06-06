package com.app.shopfee.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.shopfee.R
import com.app.shopfee.activity.AdminFeedbackActivity
import com.app.shopfee.activity.AdminMainActivity
import com.app.shopfee.activity.AdminReportActivity
import com.app.shopfee.activity.ChangePasswordActivity
import com.app.shopfee.activity.LoginActivity
import com.app.shopfee.databinding.FragmentAdminAcountBinding
import com.app.shopfee.fragment.BaseFragment
import com.app.shopfee.prefs.DataStoreManager.Companion.user
import com.google.firebase.auth.FirebaseAuth
import com.app.shopfee.utils.GlobalFunction.startActivity
class AdminAcountFragment : BaseFragment() {

    override fun initToolbar() {
        if (activity != null) {
            (activity as AdminMainActivity?)!!.setToolBar(true, getString(R.string.nav_account))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val mFragmentAdminAcountBinding = FragmentAdminAcountBinding.inflate(inflater, container, false)
        mFragmentAdminAcountBinding.tvEmail.text = user!!.email
        mFragmentAdminAcountBinding.layoutSignOut.setOnClickListener { onClickSignOut() }
        mFragmentAdminAcountBinding.layoutChangePassword.setOnClickListener { onClickChangePassword() }
        mFragmentAdminAcountBinding.layoutReport.setOnClickListener { onclickReport() }
        mFragmentAdminAcountBinding.layoutFeedback.setOnClickListener { onClickFeedBack() }
        return mFragmentAdminAcountBinding.root
    }


    private fun onClickChangePassword() {
        startActivity(activity!!, ChangePasswordActivity::class.java)
    }

    private fun onClickFeedBack(){
        startActivity(activity!!, AdminFeedbackActivity::class.java)
    }

    private fun onclickReport(){
        startActivity(activity!!, AdminReportActivity::class.java)
    }
    private fun onClickSignOut() {
        if (activity == null) {
            return
        }
        FirebaseAuth.getInstance().signOut()
        user = null
        startActivity(activity!!, LoginActivity::class.java)
        activity!!.finishAffinity()
    }
}