package com.app.shopfee.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.shopfee.R
import com.app.shopfee.activity.MainActivity
import com.app.shopfee.databinding.FragmentStroreBinding


class StoreFragment : BaseFragment() {


    private var mFragmentStoreFragment : FragmentStroreBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mFragmentStoreFragment = FragmentStroreBinding.inflate(inflater, container, false)
        mFragmentStoreFragment!!.btnOderNow.setOnClickListener {
            backToHomeScreen()
        }
        return mFragmentStoreFragment!!.root
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)!!.setToolBar(false, getString(R.string.nav_store))
        }
    }

    private fun backToHomeScreen() {
        val mainActivity = activity as MainActivity? ?: return
        mainActivity.viewPager2?.currentItem = 0
    }


}