package com.app.shopfee.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.activity.*
import com.app.shopfee.model.Address
import com.app.shopfee.prefs.DataStoreManager
import com.app.shopfee.utils.GlobalFunction
import com.app.shopfee.utils.StringUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : BaseFragment() {

    private var mView: View? = null
    private var layoutAddress: LinearLayout? = null
    private var layoutFeedback: LinearLayout? = null
    private var layoutContact: LinearLayout? = null
    private var layoutChangePassword: LinearLayout? = null
    private var layoutSignOut: LinearLayout? = null
    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)!!.setToolBar(false, getString(R.string.nav_account))


        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_account, container, false)
        initToolbar()
        initUi()
        initListener()
        return mView
    }

//    private fun initToolbar() {
//        val imgToolbarBack = mView?.findViewById<ImageView>(R.id.img_toolbar_back)
//        val tvToolbarTitle = mView?.findViewById<TextView>(R.id.tv_toolbar_title)
//        imgToolbarBack!!.visibility = View.VISIBLE
//        imgToolbarBack.setOnClickListener { backToHomeScreen() }
//        tvToolbarTitle?.text = getString(R.string.nav_account)
//    }

    private fun backToHomeScreen() {
        val mainActivity = activity as MainActivity? ?: return
        mainActivity.viewPager2?.currentItem = 0
    }

    private fun initUi() {
        val tvUsername = mView?.findViewById<TextView>(R.id.tv_username)
        val userEmail = DataStoreManager.user?.email
        val userName = getUsernameFromEmail(userEmail!!)
        tvUsername?.text = userName

        layoutFeedback = mView?.findViewById(R.id.layout_feedback)
        layoutAddress = mView?.findViewById(R.id.layout_add_address)
        layoutContact = mView?.findViewById(R.id.layout_contact)
        layoutChangePassword = mView?.findViewById(R.id.layout_change_password)
        layoutSignOut = mView?.findViewById(R.id.layout_sign_out)
    }

    fun getUsernameFromEmail(email: String): String {
        return email.substringBefore('@')
    }
    private fun initListener() {
        layoutFeedback?.setOnClickListener {
            GlobalFunction.startActivity(
                activity, FeedbackActivity::class.java
            )
        }
        layoutContact?.setOnClickListener {
            GlobalFunction.startActivity(
                activity, ContactActivity::class.java
            )
        }
        layoutChangePassword?.setOnClickListener {
            GlobalFunction.startActivity(
                activity, ChangePasswordActivity::class.java
            )
        }
        layoutSignOut?.setOnClickListener { onClickSignOut() }

        layoutAddress?.setOnClickListener {
            GlobalFunction.startActivity(
                activity, AddressActivity::class.java
            )
        }
    }

    private fun onClickSignOut() {
        if (activity == null) return
        FirebaseAuth.getInstance().signOut()
        DataStoreManager.user = null
        GlobalFunction.startActivity(activity, LoginActivity::class.java)
        activity!!.finishAffinity()
    }

    @SuppressLint("InflateParams, MissingInflatedId")
    fun onClickAddAddress() {
        val viewDialog = layoutInflater.inflate(R.layout.layout_bottom_sheet_add_address, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(viewDialog)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        // init ui
        val edtName = viewDialog.findViewById<TextView>(R.id.edt_name)
        val edtPhone = viewDialog.findViewById<TextView>(R.id.edt_phone)
        val edtAddress = viewDialog.findViewById<TextView>(R.id.edt_address)
        val tvCancel = viewDialog.findViewById<TextView>(R.id.tv_cancel)
        val tvAdd = viewDialog.findViewById<TextView>(R.id.tv_add)

        // Set listener
        tvCancel.setOnClickListener { bottomSheetDialog.dismiss() }
        tvAdd.setOnClickListener {
            val strName = edtName.text.toString().trim { it <= ' ' }
            val strPhone = edtPhone.text.toString().trim { it <= ' ' }
            val strAddress = edtAddress.text.toString().trim { it <= ' ' }
            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strPhone) || StringUtil.isEmpty(
                    strAddress
                )
            ) {
                GlobalFunction.showToastMessage(requireContext(), getString(R.string.message_enter_infor))
            } else {
                val id = System.currentTimeMillis()
                val address = Address(id, strName, strPhone, strAddress)
                MyApplication[requireContext()].getAddressDatabaseReference()
                    ?.child(id.toString())
                    ?.setValue(address) { _, _ ->
                        GlobalFunction.showToastMessage(
                            requireContext(),
                            getString(R.string.msg_add_address_success)
                        )
                        GlobalFunction.hideSoftKeyboard(requireActivity())
                        bottomSheetDialog.dismiss()
                    }
            }
        }
        bottomSheetDialog.show()
    }


}