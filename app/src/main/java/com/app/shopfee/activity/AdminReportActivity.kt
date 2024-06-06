package com.app.shopfee.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.shopfee.MyApplication
import com.app.shopfee.R
import com.app.shopfee.adapter.RevenueAdapter
import com.app.shopfee.databinding.ActivityAdminReportBinding
import com.app.shopfee.listener.IGetDateListener
import com.app.shopfee.listener.IOnSingleClickListener
import com.app.shopfee.model.DrinkOrder
import com.app.shopfee.model.Order
import com.app.shopfee.utils.Constant
import com.app.shopfee.utils.DateTimeUtils.convertDate2ToTimeStamp
import com.app.shopfee.utils.DateTimeUtils.convertTimeStampToDate_2
import com.app.shopfee.utils.GlobalFunction.showDatePicker
import com.app.shopfee.utils.StringUtil.isEmpty
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class AdminReportActivity : AppCompatActivity() {
    private var mAdminReportBinding : ActivityAdminReportBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdminReportBinding = ActivityAdminReportBinding.inflate(layoutInflater)
        setContentView(mAdminReportBinding!!.root)

        initToolbar()
        initListener()
        getListRevenue()
    }
    private fun initToolbar() {
        mAdminReportBinding!!.toolbar.imgToolbarBack.setOnClickListener { finish() }
        mAdminReportBinding!!.toolbar.tvToolbarTitle.text = getString(R.string.report)
        mAdminReportBinding!!.toolbar.imgToolbarBack.visibility = View.VISIBLE
        mAdminReportBinding!!.toolbar.imgCart.visibility = View.GONE
    }

    private fun initListener() {
        mAdminReportBinding!!.tvDateFrom.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showDatePicker(this@AdminReportActivity,
                    mAdminReportBinding!!.tvDateFrom.text.toString(), object :
                        IGetDateListener {
                        override fun getDate(date: String?) {
                            mAdminReportBinding!!.tvDateFrom.text = date
                            getListRevenue()
                        }
                    })
            }
        })
        mAdminReportBinding!!.tvDateTo.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                showDatePicker(this@AdminReportActivity,
                    mAdminReportBinding!!.tvDateTo.text.toString(), object :
                        IGetDateListener {
                        override fun getDate(date: String?) {
                            mAdminReportBinding!!.tvDateTo.text = date
                            getListRevenue()
                        }
                    })
            }
        })
    }
    private fun getListRevenue() {
        MyApplication[this].getOrderDatabaseReference()?.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list: MutableList<Order> = ArrayList()
                for (dataSnapshot in snapshot.children) {
                    val order = dataSnapshot.getValue(Order::class.java)!!
                    if (canAddOrder(order)) {
                        list.add(0, order)
                    }
                }
                handleDataHistories(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun canAddOrder(order: Order?): Boolean {
        if (order == null) {
            return false
        }
        if (order.status != 4) {
            return false
        }
        val strDateFrom = mAdminReportBinding!!.tvDateFrom.text.toString()
        val strDateTo = mAdminReportBinding!!.tvDateTo.text.toString()
        if (isEmpty(strDateFrom) && isEmpty(strDateTo)) {
            return true
        }
        val strDateOrder = convertTimeStampToDate_2(order.id)
        val longOrder = convertDate2ToTimeStamp(strDateOrder).toLong()
        if (isEmpty(strDateFrom) && !isEmpty(strDateTo)) {
            val longDateTo = convertDate2ToTimeStamp(strDateTo).toLong()
            return longOrder <= longDateTo
        }
        if (!isEmpty(strDateFrom) && isEmpty(strDateTo)) {
            val longDateFrom = convertDate2ToTimeStamp(strDateFrom).toLong()
            return longOrder >= longDateFrom
        }
        val longDateTo = convertDate2ToTimeStamp(strDateTo).toLong()
        val longDateFrom = convertDate2ToTimeStamp(strDateFrom).toLong()
        return longOrder in longDateFrom..longDateTo
    }
    private fun handleDataHistories(list: List<Order>?) {
        if (list == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(this)
        mAdminReportBinding!!.rcvOrderHistory.layoutManager = linearLayoutManager
        val revenueAdapter = RevenueAdapter(list)
        mAdminReportBinding!!.rcvOrderHistory.adapter = revenueAdapter

        // Calculate total
        val strTotalValue: String = "" + getTotalValues(list) + " " + Constant.CURRENCY
        mAdminReportBinding!!.tvTotalValue.text = strTotalValue

        val strtoralOder : String = "" + list.size + " Đơn hàng"
        mAdminReportBinding!!.tvTotalOrder.text = strtoralOder
    }
    private fun getTotalValues(list: List<Order>?): Int {
        if (list == null || list.isEmpty()) {
            return 0
        }
        var total = 0
        for (order in list) {
            total += order.total
        }
        return total
    }
}