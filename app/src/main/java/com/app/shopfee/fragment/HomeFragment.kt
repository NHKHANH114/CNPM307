package com.app.shopfee.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.activity.AdminMainActivity
import com.app.shopfee.activity.DrinkDetailActivity
import com.app.shopfee.activity.MainActivity
import com.app.shopfee.adapter.BannerViewPagerAdapter
import com.app.shopfee.adapter.CategoryPagerAdapter
import com.app.shopfee.databinding.FragmentHomeBinding
import com.app.shopfee.event.SearchKeywordEvent
import com.app.shopfee.listener.IClickDrinkListener
import com.app.shopfee.model.Category
import com.app.shopfee.model.Drink
import com.app.shopfee.prefs.DataStoreManager
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlobalFunction
import com.app.shopfee.utils.Utils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import me.relex.circleindicator.CircleIndicator3
import org.greenrobot.eventbus.EventBus
import java.util.*

class HomeFragment : BaseFragment() {



    private var mFragmentHomeBinding : FragmentHomeBinding? = null
    private var listDrinkFeatured: MutableList<Drink>? = null
    private var listCategory: MutableList<Category>? = null
    private val mHandlerBanner = Handler(Looper.getMainLooper())
    private val mRunnableBanner = Runnable {
        if (listDrinkFeatured == null || listDrinkFeatured!!.isEmpty()) {
            return@Runnable
        }
        if (mFragmentHomeBinding!!.viewPagerDrinkFeatured.currentItem == listDrinkFeatured!!.size - 1) {
            mFragmentHomeBinding!!.viewPagerDrinkFeatured.currentItem = 0
            return@Runnable
        }
        mFragmentHomeBinding!!.viewPagerDrinkFeatured.currentItem = mFragmentHomeBinding!!.viewPagerDrinkFeatured.currentItem + 1
    }

    override fun initToolbar() {
        if (activity != null) {
            (activity as MainActivity?)!!.setToolBar(true, getString(R.string.nav_home))


        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        mFragmentHomeBinding!!.viewPagerCategory.isUserInputEnabled = false
        initListener()
        getListDrinkBanner()
        getListCategory()
        val userEmail = DataStoreManager.user?.email
        val userName = getUsernameFromEmail(userEmail!!)
        mFragmentHomeBinding!!.txtBanner.text = "Xin chào! " + userName
        return mFragmentHomeBinding!!.root
    }


    fun getUsernameFromEmail(email: String): String {
        return email.substringBefore('@')
    }
    private fun initListener() {
        mFragmentHomeBinding!!.edtSearchName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey == "" || strKey.isEmpty()) {
                    searchDrink()
                }
            }
        })
        mFragmentHomeBinding!!.imgSearch.setOnClickListener { searchDrink() }
        mFragmentHomeBinding!!.edtSearchName.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDrink()
                return@setOnEditorActionListener true
            }
            false
        }
    }

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
                    displayListBanner()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun displayListBanner() {
        val adapter = BannerViewPagerAdapter(listDrinkFeatured, object : IClickDrinkListener {
            override fun onClickDrinkItem(drink: Drink) {
                val bundle = Bundle()
                bundle.putInt(Constant.DRINK_ID, drink.id.toInt())
                GlobalFunction.startActivity(activity, DrinkDetailActivity::class.java, bundle)
            }
        })
        mFragmentHomeBinding!!.viewPagerDrinkFeatured.adapter = adapter
        mFragmentHomeBinding!!.indicatorDrinkFeatured.setViewPager(mFragmentHomeBinding!!.viewPagerDrinkFeatured)
        mFragmentHomeBinding!!.viewPagerDrinkFeatured.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandlerBanner.removeCallbacks(mRunnableBanner)
                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
            }
        })
    }

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
        mFragmentHomeBinding!!.viewPagerCategory.offscreenPageLimit = listCategory!!.size
        val adapter = CategoryPagerAdapter(activity!!, listCategory)
        mFragmentHomeBinding!!.viewPagerCategory.adapter = adapter
        TabLayoutMediator(
            mFragmentHomeBinding!!.tabCategory, mFragmentHomeBinding!!.viewPagerCategory
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = listCategory!![position].name?.toLowerCase(Locale.getDefault())
        }
            .attach()
    }

    private fun searchDrink() {
        val strKey = mFragmentHomeBinding!!.edtSearchName.text.toString().trim { it <= ' ' }
        EventBus.getDefault().post(SearchKeywordEvent(strKey))
        Utils.hideSoftKeyboard(activity)
    }


}