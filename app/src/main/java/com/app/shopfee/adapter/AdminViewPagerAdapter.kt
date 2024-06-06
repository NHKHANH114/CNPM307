package com.app.shopfee.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.shopfee.fragment.admin.AdminAcountFragment
import com.app.shopfee.fragment.admin.AdminHomeFragment
import com.app.shopfee.fragment.admin.AdminOderFragment


class AdminViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdminHomeFragment()
            1 -> AdminOderFragment()
            2 -> AdminAcountFragment()
            else -> AdminHomeFragment()
        }
    }
}