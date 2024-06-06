package com.app.shopfee.fragment.admin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.activity.AddDrinkActivity
import com.app.shopfee.activity.DrinkDetailActivity
import com.app.shopfee.adapter.AdminDrinkAdapter
import com.app.shopfee.adapter.FilterAdapter
import com.app.shopfee.event.SearchKeywordEvent
import com.app.shopfee.listener.AdminIClickDrinkListener
import com.app.shopfee.model.Drink
import com.app.shopfee.model.Filter
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.GlobalFunction
import com.app.shopfee.utils.StringUtil
import com.app.shopfee.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList
import java.util.Locale
import com.app.shopfee.utils.GlobalFunction.startActivity

class AdminDrinkFragment : Fragment() {


    private var mView: View? = null
    private var rcvFilter: RecyclerView? = null
    private var rcvDrink: RecyclerView? = null
    private var listDrink: MutableList<Drink>? = null
    private var listDrinkDisplay: MutableList<Drink>? = null
    private var listDrinkKeyWord: MutableList<Drink>? = null
    private var listFilter: MutableList<Filter>? = null
    private var drinkAdapter: AdminDrinkAdapter? = null
    private var filterAdapter: FilterAdapter? = null
    private var categoryId = 0
    private var currentFilter: Filter? = null
    private var keyword: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_admin_drink, container, false)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        getDataArguments()
        initUi()
        initListener()
        getListFilter()
        getListDrink()
        return mView
    }

    private fun getDataArguments() {
        val bundle = arguments ?: return
        categoryId = bundle.getInt(Constant.CATEGORY_ID)
    }

    private fun initUi() {
        rcvFilter = mView?.findViewById(R.id.rcv_filter)
        rcvDrink = mView?.findViewById(R.id.rcv_drink)
        displayListDrink()
    }

    private fun initListener() {}
    private fun getListFilter() {
        listFilter = ArrayList()
        listFilter?.add(Filter(Filter.TYPE_FILTER_ALL, getString(R.string.filter_all)))
        listFilter?.add(Filter(Filter.TYPE_FILTER_RATE, getString(R.string.filter_rate)))
        listFilter?.add(Filter(Filter.TYPE_FILTER_PRICE, getString(R.string.filter_price)))
        listFilter?.add(
            Filter(
                Filter.TYPE_FILTER_PROMOTION,
                getString(R.string.filter_promotion)
            )
        )
        val linearLayoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.HORIZONTAL, false
        )
        rcvFilter?.layoutManager = linearLayoutManager
        currentFilter = listFilter?.get(0)
        currentFilter?.isSelected = true
        filterAdapter = FilterAdapter(activity, listFilter, object: FilterAdapter.IClickFilterListener {
            override fun onClickFilterItem(filter: Filter) {
                handleClickFilter(filter)
            }
        })
        rcvFilter?.adapter = filterAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleClickFilter(filter: Filter) {
        for (filterEntity in listFilter!!) {
            if (filterEntity.id == filter.id) {
                filterEntity.isSelected = true
                setListDrinkDisplay(filterEntity, keyword)
                currentFilter = filterEntity
            } else {
                filterEntity.isSelected = false
            }
        }
        if (filterAdapter != null) filterAdapter!!.notifyDataSetChanged()
    }

    private fun getListDrink() {
        if (activity == null) return
        MyApplication[activity].getDrinkDatabaseReference()
            ?.orderByChild(Constant.CATEGORY_ID)
            ?.equalTo(categoryId.toDouble())
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (listDrink != null) {
                        listDrink!!.clear()
                    } else {
                        listDrink = ArrayList()
                    }
                    for (dataSnapshot in snapshot.children) {
                        val drink = dataSnapshot.getValue(Drink::class.java)
                        if (drink != null) {
                            listDrink!!.add(0, drink)
                        }
                    }
                    setListDrinkDisplay(
                        Filter(
                            Filter.TYPE_FILTER_ALL,
                            getString(R.string.filter_all)
                        ), keyword
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun displayListDrink() {
        if (activity == null) return
        listDrinkDisplay = ArrayList()
        val linearLayoutManager = LinearLayoutManager(activity)
        rcvDrink?.layoutManager = linearLayoutManager

        drinkAdapter = AdminDrinkAdapter(listDrinkDisplay, object : AdminIClickDrinkListener {
            override fun onClickDrinkItem(drink: Drink) {
                val bundle = Bundle()
                bundle.putInt(Constant.DRINK_ID, drink.id.toInt())
                startActivity(activity, DrinkDetailActivity::class.java, bundle)
            }

            override fun onClickUpdateFood(drink: Drink?) {
                onClickEditFood(drink)
            }

            override fun onClickDeleteFood(drink: Drink?) {
                deleteFoodItem(drink)
            }

        })
        rcvDrink?.adapter = drinkAdapter
    }

    private fun onClickEditFood(drink: Drink?) {
        val bundle = Bundle()
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, drink)
        startActivity(activity!!, AddDrinkActivity::class.java, bundle)
    }

    private fun deleteFoodItem(drink: Drink?) {
        AlertDialog.Builder(activity)
            .setTitle(getString(R.string.msg_delete_title))
            .setMessage(getString(R.string.msg_confirm_delete))
            .setPositiveButton(getString(R.string.action_ok)) { _: DialogInterface?, _: Int ->
                if (activity == null) {
                    return@setPositiveButton
                }
                MyApplication[activity!!].getDrinkDatabaseReference()
                    ?.child(drink!!.id.toString())?.removeValue { _: DatabaseError?, _: DatabaseReference? ->
                        Toast.makeText(activity,
                            getString(R.string.msg_delete_movie_successfully), Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton(getString(R.string.action_cancel), null)
            .show()
    }

    private fun setListDrinkDisplay(filter: Filter, keyword: String?) {
        if (listDrink == null || listDrink!!.isEmpty()) return
        if (listDrinkKeyWord != null) {
            listDrinkKeyWord!!.clear()
        } else {
            listDrinkKeyWord = ArrayList()
        }
        if (listDrinkDisplay != null) {
            listDrinkDisplay!!.clear()
        } else {
            listDrinkDisplay = ArrayList()
        }
        if (!StringUtil.isEmpty(keyword)) {
            for (drink in listDrink!!) {
                if (Utils.getTextSearch(drink.name).toLowerCase(Locale.getDefault()).trim { it <= ' ' }
                        .contains(Utils.getTextSearch(keyword).toLowerCase(Locale.getDefault()).trim { it <= ' ' })) {
                    listDrinkKeyWord!!.add(drink)
                }
            }
            when (filter.id) {
                Filter.TYPE_FILTER_ALL -> listDrinkDisplay!!.addAll(
                    listDrinkKeyWord!!
                )
                Filter.TYPE_FILTER_RATE -> {
                    listDrinkDisplay!!.addAll(listDrinkKeyWord!!)
                    listDrinkDisplay!!.sortWith(Comparator { drink1: Drink, drink2: Drink ->
                        drink2.rate.compareTo(drink1.rate)
                    })
                }
                Filter.TYPE_FILTER_PRICE -> {
                    listDrinkDisplay!!.addAll(listDrinkKeyWord!!)
                    listDrinkDisplay!!.sortWith(Comparator { drink1: Drink, drink2: Drink ->
                        drink1.realPrice.compareTo(drink2.realPrice)
                    })
                }
                Filter.TYPE_FILTER_PROMOTION -> for (drink in listDrinkKeyWord!!) {
                    if (drink.sale > 0) listDrinkDisplay!!.add(drink)
                }
            }
        } else {
            when (filter.id) {
                Filter.TYPE_FILTER_ALL -> listDrinkDisplay!!.addAll(
                    listDrink!!
                )
                Filter.TYPE_FILTER_RATE -> {
                    listDrinkDisplay!!.addAll(listDrink!!)
                    listDrinkDisplay!!.sortWith(Comparator { drink1: Drink, drink2: Drink ->
                        drink2.rate.compareTo(drink1.rate)
                    })
                }
                Filter.TYPE_FILTER_PRICE -> {
                    listDrinkDisplay!!.addAll(listDrink!!)
                    listDrinkDisplay!!.sortWith(Comparator { drink1: Drink, drink2: Drink ->
                        drink1.realPrice.compareTo(drink2.realPrice)
                    })
                }
                Filter.TYPE_FILTER_PROMOTION -> for (drink in listDrink!!) {
                    if (drink.sale > 0) listDrinkDisplay!!.add(drink)
                }
            }
        }
        reloadListDrink()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun reloadListDrink() {
        if (drinkAdapter != null) drinkAdapter!!.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchKeywordEvent(event: SearchKeywordEvent) {
        keyword = event.keyword
        setListDrinkDisplay(currentFilter!!, keyword)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (filterAdapter != null) filterAdapter!!.release()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    companion object {
        fun newInstance(categoryId: Int): AdminDrinkFragment {
            val drinkFragment = AdminDrinkFragment()
            val bundle = Bundle()
            bundle.putInt(Constant.CATEGORY_ID, categoryId)
            drinkFragment.arguments = bundle
            return drinkFragment
        }
    }

}