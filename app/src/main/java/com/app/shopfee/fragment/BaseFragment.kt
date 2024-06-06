package com.app.shopfee.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.shopfee.R


abstract class BaseFragment : Fragment() {
    override fun onResume() {
        super.onResume()
        initToolbar()
    }

    protected abstract fun initToolbar()
}