package com.app.shopfee.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.app.shopfee.R
import com.app.shopfee.activity.MainActivity
import com.app.shopfee.adapter.OrderPagerAdapter
import com.app.shopfee.model.TabOrder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class HistoryFragment : BaseFragment() {

    private var mView: View? = null
    private var viewPagerOrder: ViewPager2? = null
    private var tabOrder: TabLayout? = null

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)!!.setToolBar(false, getString(R.string.nav_order))


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_history, container, false)
        initUi()
        displayTabsOrder()
        return mView
    }



    private fun backToHomeScreen() {
        val mainActivity = activity as MainActivity? ?: return
        mainActivity.viewPager2?.currentItem = 0
    }

    private fun initUi() {
        viewPagerOrder = mView?.findViewById(R.id.view_pager_order)
        viewPagerOrder?.isUserInputEnabled = false
        tabOrder = mView?.findViewById(R.id.tab_order)
    }

    private fun displayTabsOrder() {
        val list: MutableList<TabOrder> = ArrayList()
        list.add(TabOrder(TabOrder.TAB_ORDER_PROCESS, getString(R.string.label_process)))
        list.add(TabOrder(TabOrder.TAB_ORDER_DONE, getString(R.string.label_done)))
        if (activity == null) return
        viewPagerOrder?.offscreenPageLimit = list.size
        val adapter = OrderPagerAdapter(activity!!, list)
        viewPagerOrder?.adapter = adapter
        TabLayoutMediator(
            tabOrder!!, viewPagerOrder!!
        ) { tab: TabLayout.Tab, position: Int -> tab.text = list[position].name.toLowerCase(Locale.getDefault()) }
            .attach()
    }
}