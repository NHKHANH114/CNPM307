package com.app.shopfee.fragment.admin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import com.app.shopfee.utils.GlobalFunction.startActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.activity.AddDrinkActivity
import com.app.shopfee.activity.AdminMainActivity
import com.app.shopfee.activity.DrinkDetailActivity
import com.app.shopfee.adapter.AdminCategoryPagerAdapter
import com.app.shopfee.adapter.BannerViewPagerAdapter
import com.app.shopfee.databinding.FragmentAdminHomeBinding
import com.app.shopfee.event.SearchKeywordEvent
import com.app.shopfee.fragment.BaseFragment
import com.app.shopfee.listener.IClickDrinkListener
import com.app.shopfee.model.Category
import com.app.shopfee.model.Drink
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList
import java.util.Locale


class AdminHomeFragment : BaseFragment() {
    private var mAdminHomeFragment : FragmentAdminHomeBinding? = null
    private var listDrinkFeatured: MutableList<Drink>? = null
    private var listCategory: MutableList<Category>? = null


//    private val mHandlerBanner = Handler(Looper.getMainLooper())
//    private val mRunnableBanner = Runnable {
//        if (listDrinkFeatured == null || listDrinkFeatured!!.isEmpty()) {
//            return@Runnable
//        }
//        if (mAdminHomeFragment!!.viewPagerDrinkFeaturedAdmin.currentItem == listDrinkFeatured!!.size - 1) {
//            mAdminHomeFragment!!.viewPagerDrinkFeaturedAdmin.currentItem = 0
//            return@Runnable
//        }
//        mAdminHomeFragment!!.viewPagerDrinkFeaturedAdmin.currentItem = mAdminHomeFragment!!.viewPagerDrinkFeaturedAdmin.currentItem + 1
//
//        mAdminHomeFragment!!.btnAddDrinkAdmin.setOnClickListener {  onClickAddFood()}
//    }


    override fun initToolbar() {
        if (activity != null) {
            (activity as AdminMainActivity?)!!.setToolBar(true, getString(R.string.nav_home))


        }
    }
    private fun onClickAddFood() {
        startActivity(activity!!, AddDrinkActivity::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mAdminHomeFragment = FragmentAdminHomeBinding.inflate(inflater, container, false)
        //initUi()
        //initListener()
        getListDrinkBanner()
        getListCategory()
        mAdminHomeFragment!!.btnAddDrinkAdmin.setOnClickListener {  onClickAddFood()}
        return mAdminHomeFragment!!.root
    }

//    private fun initListener() {
//        mAdminHomeFragment!!.edtSearchNameAdmin.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable) {
//                val strKey = s.toString().trim { it <= ' ' }
//                if (strKey == "" || strKey.isEmpty()) {
//                    searchDrink()
//                }
//            }
//        })
//        mAdminHomeFragment!!.imgSearchAdmin.setOnClickListener { searchDrink() }
//        mAdminHomeFragment!!.edtSearchNameAdmin.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                searchDrink()
//                return@setOnEditorActionListener true
//            }
//            false
//        }
//    }

    private fun getListDrinkBanner() {
        if (activity == null) return
        MyApplication[activity].getDrinkDatabaseReference()
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (listDrinkFeatured != null) {
                        listDrinkFeatured!!.clear()
                    } else {
                        listDrinkFeatured = ArrayList()
                    }
                    for (dataSnapshot in snapshot.children) {
                        val drink = dataSnapshot.getValue(Drink::class.java)
                        if (drink != null && drink.isFeatured) {
                            listDrinkFeatured!!.add(drink)
                        }
                    }
                    //displayListBanner()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

//    private fun displayListBanner() {
//        val adapter = BannerViewPagerAdapter(listDrinkFeatured, object : IClickDrinkListener {
//            override fun onClickDrinkItem(drink: Drink) {
//                val bundle = Bundle()
//                bundle.putInt(Constant.DRINK_ID, drink.id.toInt())
//                startActivity(activity, DrinkDetailActivity::class.java, bundle)
//            }
//        })
//        mAdminHomeFragment!!.viewPagerDrinkFeaturedAdmin.adapter = adapter
//        mAdminHomeFragment!!.indicatorDrinkFeaturedAdmin.setViewPager(mAdminHomeFragment!!.viewPagerDrinkFeaturedAdmin)
//        mAdminHomeFragment!!.viewPagerDrinkFeaturedAdmin.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                mHandlerBanner.removeCallbacks(mRunnableBanner)
//                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
//            }
//        })
//    }

    private fun getListCategory() {
        if (activity == null) return
        MyApplication[activity].getCategoryDatabaseReference()
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (listCategory != null) {
                        listCategory!!.clear()
                    } else {
                        listCategory = ArrayList()
                    }
                    for (dataSnapshot in snapshot.children) {
                        val category = dataSnapshot.getValue(
                            Category::class.java
                        )
                        if (category != null) {
                            listCategory!!.add(category)
                        }
                    }
                    displayTabsCategory()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun displayTabsCategory() {
        if (activity == null || listCategory == null || listCategory!!.isEmpty()) return
        mAdminHomeFragment!!.viewPagerCategoryAdmin.offscreenPageLimit = listCategory!!.size
        val adapter = AdminCategoryPagerAdapter(activity!!, listCategory)
        mAdminHomeFragment!!.viewPagerCategoryAdmin.adapter = adapter
        TabLayoutMediator(
            mAdminHomeFragment!!.tabCategoryAdmin, mAdminHomeFragment!!.viewPagerCategoryAdmin
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = listCategory!![position].name?.toLowerCase(Locale.getDefault())
        }
            .attach()
    }

//    private fun searchDrink() {
//        val strKey = mAdminHomeFragment!!.edtSearchNameAdmin.text.toString().trim { it <= ' ' }
//        EventBus.getDefault().post(SearchKeywordEvent(strKey))
//        Utils.hideSoftKeyboard(activity)
//    }
}